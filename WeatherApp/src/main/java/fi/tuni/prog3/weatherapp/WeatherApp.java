package fi.tuni.prog3.weatherapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

/**
 * JavaFX Sisu
 */
public class WeatherApp extends Application {

    String unit = "metric";
    String lang = "en";

    // For error handling
    Boolean LOCATION_NOT_FOUND = false;

    // Container for current city weather data
    Map<String, CurrentWeatherData> current_history = new HashMap<>();
    Map<String, HourlyWeatherData> hourly_history = new HashMap<>();
    // Map<String, DailyWeatherData> daily_history = new HashMap<>();

    // Container for cached images to reduce memory usage
    // The key is the weather status icon id (for example "04n")
    Map<String, Image> imageCache = new HashMap<>();

    // Placeholder image is used often so load it once here to reduce
    // memory usage
    private static Image placeholderImage;

    List<String> favourites = new ArrayList<String>();

    private static String api_key_Abu = "88a91051d6699b4cb230ff1ff2ebb3b1";

    private HBox bottomHBox = new HBox();

    // Different variables
    private String response;
    private Label locLabel;
    private Label temperLabel;
    private Label feelsLabel;
    private Label windLabel;
    private String city_loc;
    private String description;
    private String temperature;
    private String feelsLike;
    private String windSpeed;
    private Font titleFont;
    private Font locFont;
    private Font descFont;
    private Font def_font;
    private Text city_locText;
    private Text descriptionText;
    private Text temperText;
    private Text feelsText;
    private Text windText;
    private ImageView weatherImage;
    private ImageView favStar;
    private Image CurrentWeatherImage;
    private Image starImage;
    private Image emptyStarImage;
    private TextField locField;
    private Button locButton;
    private Button favButton;
    private ComboBox<String> langBox;

    @Override
    public void start(Stage stage) {
        System.setProperty("file.encoding", "UTF-8"); // Needed for non-latin letters, DON'T TOUCH THIS!!!
        // Default font for this app
        def_font = Font.font("Arial", 20);

        // Creating a new BorderPane.
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));

        // Adding HBox to the center of the BorderPane.
        root.setCenter(getCenterVBox());

        // Adding button to the BorderPane and aligning it to the right.
        var quitButton = getQuitButton();
        BorderPane.setMargin(quitButton, new Insets(10, 10, 0, 10));
        root.setBottom(quitButton);
        BorderPane.setAlignment(quitButton, Pos.TOP_RIGHT);

        Scene scene = new Scene(root, 700, 900);
        stage.setScene(scene);
        stage.setTitle("WeatherApp");
        stage.show();
    }

    public static void main(String[] args) {
        initImages();
        launch();
    }

    // Initialize placeholder image
    private static void initImages() {
        placeholderImage = new Image(WeatherApp.class.getResourceAsStream("/weather_types/placeholder.gif"));
    }

    private VBox getCenterVBox() {
        // Creating an HBox.
        VBox centerHBox = new VBox(10);

        // Add all boxes and scrollpane to centerbox
        centerHBox.getChildren().addAll(getTopButtonBox(), getTodayBox(),
                getMiddleBox(), getBottomScrollPane(), getBottomVBox());

        return centerHBox;
    }

    private HBox getTopButtonBox() {
        // Creating top box for buttons
        HBox topHBox = new HBox();
        topHBox.setPadding(new Insets(5, 5, 0, 5));
        topHBox.setPrefHeight(50);
        topHBox.setStyle("-fx-background-color: #05de29;");

        // creating unit toggle button

        Button unitButton = getUnitToggleButton();
        unitButton.setMinWidth(60);

        // Adding search for location textbox

        locField = new TextField();
        locField.setMaxWidth(100);
        locField.setPromptText("Enter your city: ");

        // Location search button

        locButton = new Button("Search for city");

        locButton.setOnAction(event -> {
            city_loc = locField.getText();

            search();

        });

        // Pressing enter is equal to pressing search button
        locField.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("Enter")) {
                locButton.fire();
            }
        });

        // EMpty favourites-button
        Button clearFavs = new Button("Clear favourites");
        clearFavs.setMinWidth(60);

        clearFavs.setOnAction(event -> {
            favourites.clear();
            updateFavouritesComboBox();
            isFavourite();
        });



        // Adjusting favourites dropbox size and other visual adjusting
        favouritesDropBox().setMinWidth(50);
        favouritesDropBox().setPromptText("Favourites");
        Region spacer = new Region();
        Region spacer2 = new Region();
        HBox.setMargin(locField, new Insets(0, 10, 0, 0));
        HBox.setMargin(spacer, new Insets(0, 280, 0, 0));
        HBox.setMargin(spacer2, new Insets(0, 30, 0, 0));
        HBox.setMargin(unitButton, new Insets(0, 10, 0, 5));
        HBox.setMargin(favouritesDropBox(), new Insets(0, 10, 0, 10));

        topHBox.getChildren().addAll(unitButton, locField, locButton, favouritesDropBox(), langButton(), spacer2, clearFavs);

        return topHBox;
    }

    private HBox getTodayBox() {

        // Creating favourite button that will save/unsave favourite locations
        favButton = new Button();
        favButton.setMaxSize(20, 20);
        favButton.setPadding(new Insets(10, 10, 10, 10));
        emptyStarImage = new Image(getClass().getResourceAsStream("/icons/empty_star.png"));
        favStar = new ImageView(emptyStarImage);
        favStar.setFitHeight(20);
        favStar.setFitWidth(20);
        favButton.setGraphic(favStar);

        favButton.setOnAction(event -> {
            toggleFavourite();
        });

        // Creating a HBox for today's weather.
        HBox todayBox = new HBox();
        todayBox.setPrefHeight(350);
        todayBox.setStyle("-fx-background-color: #FFFFFF;");

        // Creating vertical box that will store location, temperature, weather
        // description, images etc in seperate horizontal boxes
        VBox weatherDataBox = new VBox();
        weatherDataBox.setPrefHeight(330);
        weatherDataBox.setPadding(new Insets(10, 10, 10, 10));
        weatherDataBox.setStyle("-fx-background-color: #FFFFFF;");

        // Creating box for today's weather text
        HBox locationBox = new HBox();
        locationBox.setPrefHeight(15);

        // Creating custom text font
        titleFont = Font.font(def_font.getFamily(), FontWeight.BOLD, 30);

        // Creating top label
        Label topBoxTitle = new Label();
        topBoxTitle.setPadding(new Insets(5, 5, 5, 5));
        Text todaysWeather = new Text("Today's weather in ");
        todaysWeather.setFont(titleFont);
        todaysWeather.setStroke(Color.BLACK);
        todaysWeather.setFill(Color.BLACK);
        todaysWeather.setStrokeWidth(0.5);
        // Shadow effects

        // Extra text effect

        // Change label looks
        topBoxTitle.setTextFill(Color.SKYBLUE);
        topBoxTitle.setMinWidth(169);
        topBoxTitle.setGraphic(todaysWeather);

        // City name and its graphics
        locFont = Font.font(def_font.getFamily(), FontWeight.BOLD, 30);

        city_locText = new Text(city_loc);
        city_locText.setFont(locFont);
        city_locText.setStroke(Color.BLACK);
        city_locText.setFill(Color.BLACK);
        city_locText.setStrokeWidth(0.5);

        // Extra text effect

        // This label will have the location name
        locLabel = new Label();
        locLabel.setMinWidth(80);
        locLabel.setPadding(new Insets(5, 0, 0, 0));
        locLabel.setTextFill(Color.BLACK);
        locLabel.setGraphic(city_locText);

        // Creating a label for weather description
        descFont = Font.font(def_font.getFamily(), FontWeight.NORMAL, 30);

        Label descriptionLabel = new Label();
        descriptionLabel.setMinHeight(30);
        descriptionLabel.setTextFill(Color.BLACK);
        descriptionLabel.setPadding(new Insets(0, 0, 10, 0));

        descriptionText = new Text(description);
        descriptionText.setFill(Color.BLACK);
        descriptionText.setStrokeWidth(2);
        descriptionText.setFont(descFont);

        descriptionLabel.setGraphic(descriptionText);

        // Location text is stored here
        locationBox.getChildren().addAll(topBoxTitle, locLabel);

        // Creating horizontal box for weather image and temperature
        HBox symbolBox = new HBox();
        symbolBox.setPrefHeight(120);
        symbolBox.setStyle("-fx-background-color: #FFFFFF;");

        // Creating a weather status indicator gif
        weatherImage = new ImageView();
        weatherImage.setFitHeight(125);
        weatherImage.setFitWidth(125);

        // Placeholder image
        CurrentWeatherImage = new Image(getClass().getResourceAsStream("/weather_types/placeholder.gif"));
        weatherImage.setImage(CurrentWeatherImage);

        // Creating label for the temperature
        temperLabel = new Label();
        temperLabel.setMinHeight(50);
        temperLabel.setTextFill(Color.BLACK);
        temperLabel.setPadding(new Insets(10, 10, 10, 10));

        temperText = new Text();
        temperText.setText(temperature);
        temperText.setFill(Color.BLACK);
        temperText.setStroke(Color.BLACK);
        temperText.setStrokeWidth(1);
        temperText.setFont(Font.font(def_font.getFamily(), 50));

        temperLabel.setGraphic(temperText);

        // Creating label for feels like
        feelsLabel = new Label();
        feelsLabel.setMinHeight(50);
        feelsLabel.setTextFill(Color.BLACK);
        feelsLabel.setPadding(new Insets(10, 10, 10, 10));

        feelsText = new Text();
        feelsText.setText(feelsLike);
        feelsText.setFill(Color.BLACK);
        feelsText.setStroke(Color.BLACK);
        feelsText.setStrokeWidth(1);
        feelsText.setFont(Font.font(def_font.getFamily(), 20));

        feelsLabel.setGraphic(feelsText);

        // Creating label for wind speed
        windLabel = new Label();
        windLabel.setMinHeight(50);
        windLabel.setTextFill(Color.BLACK);
        windLabel.setPadding(new Insets(10, 10, 10, 10));

        windText = new Text();
        windText.setText(windSpeed);
        windText.setFill(Color.BLACK);
        windText.setStroke(Color.BLACK);
        windText.setStrokeWidth(0);
        windText.setFont(Font.font(def_font.getFamily(), 30));

        windLabel.setGraphic(windText);

        // Creating VBox for feels like and wind
        VBox feelsAndWindBox = new VBox();
        feelsAndWindBox.setPrefHeight(120);
        feelsAndWindBox.setStyle("-fx-background-color: #FFFFFF;");

        feelsAndWindBox.getChildren().addAll(feelsLabel, windLabel);

        symbolBox.getChildren().addAll(weatherImage, temperLabel, feelsAndWindBox);

        // Add seperate boxes under each other to the weatherDataBox
        weatherDataBox.getChildren().addAll(locationBox, descriptionLabel, symbolBox);
        // Add the vertical box to the first big box that display's today's weather
        todayBox.getChildren().addAll(weatherDataBox, favButton);

        // Load previously saved favourites
        loadFavourites();
        updateFavouritesComboBox();

        return todayBox;
    }

    private ScrollPane getMiddleBox() {
        // Creating a ScrollPane for the HBox.
        ScrollPane middleBox = new ScrollPane();
        middleBox.setPrefHeight(330);
        middleBox.setStyle("-fx-background-color: #b1c2d4;");
        middleBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Set horizontal scrollbar always visible

        // Creating an HBox for the right side.
        HBox rightHBox = new HBox();

        // Creating forecast button
        Button forecastButton = new Button("Forecast");
        HBox.setMargin(forecastButton, new Insets(5, 10, 5, 10));

        // Creating history button
        Button historyButton = new Button("History");
        HBox.setMargin(historyButton, new Insets(5, 10, 5, 0));

        // Creating map button
        Button mapButton = new Button("Map");
        HBox.setMargin(mapButton, new Insets(5, 10, 5, 0));

        rightHBox.getChildren().addAll(forecastButton, historyButton, mapButton);

        middleBox.setContent(rightHBox); // Set HBox as content of ScrollPane

        return middleBox;
    }

    private VBox getBottomVBox() {
        VBox bottomVBox = new VBox(10);
        bottomVBox.setPrefHeight(100);
        bottomVBox.setStyle("-fx-background-color: white;");
        bottomVBox.setAlignment(Pos.CENTER);

        // Get random quote from Quotes class
        Quotes quotes = new Quotes();
        String randomQuote = quotes.getRandomQuote();

        // Split quote to lines if it is too long to fit in one line
        randomQuote = splitStringIntoLines(randomQuote, 50);

        // Extract attribution from the quote
        String[] qutoeParts = randomQuote.split("~");
        String quotePart = qutoeParts[0].trim();
        String attributionPart = "~" + qutoeParts[1].trim();

        // Create custom text font
        titleFont = Font.font(def_font.getFamily(), FontPosture.ITALIC, 20);

        // Create bottom label for the Quote
        Label bottomBoxTitle = new Label();
        bottomBoxTitle.setPadding(new Insets(5, 5, 5, 5));
        bottomBoxTitle.setAlignment(Pos.CENTER);

        // Set random quote
        Text quoteText = new Text(quotePart);
        quoteText.setFont(titleFont);
        quoteText.setStroke(Color.BLACK);
        quoteText.setFill(Color.BLACK);
        quoteText.setStrokeWidth(0.5);

        // Set attribution on a new line
        Text attributionText = new Text(attributionPart);
        attributionText.setFont(titleFont);
        attributionText.setStroke(Color.BLACK);
        attributionText.setFill(Color.BLACK);
        attributionText.setStrokeWidth(0.5);

        bottomBoxTitle.setGraphic(new VBox(quoteText, attributionText));

        bottomVBox.getChildren().addAll(bottomBoxTitle);
        return bottomVBox;
    }

    private String splitStringIntoLines(String input, int maxCharacters) {
        if (input.length() <= maxCharacters) {
            return input; // No need to split, the string is short enough
        } else {
            int splitIndex = input.lastIndexOf(' ', maxCharacters);
            if (splitIndex == -1) {
                // If there are no spaces, split at the maxCharacters position
                splitIndex = maxCharacters;
            }
            return input.substring(0, splitIndex) + "\n" + input.substring(splitIndex).trim();
        }
    }

    private ScrollPane getBottomScrollPane() {
        bottomHBox.setPrefHeight(250);
        bottomHBox.setStyle("-fx-background-color: white;");

        bottomHBox.setSpacing(10);
        bottomHBox.setPadding(new Insets(10,5,0,5));
        // bottomHBox.setAlignment(Pos.CENTER);

        // Add scrollbar to bottom to scroll through hours
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(bottomHBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return scrollPane;
    }

    private VBox createHourColumn(int index, String city, HourlyWeatherData hourlyWeatherData) {
        VBox hourColumn = new VBox();
        hourColumn.setAlignment(Pos.TOP_CENTER);
        hourColumn.setMaxHeight(10);
        hourColumn.setSpacing(10);
        VBox.setVgrow(hourColumn, Priority.NEVER);
        //hourColumn.setPadding(new Insets(5,5,5,5));

        String temperature = "ERROR";
        String windSpeed = "ERROR";
        String humidity = "ERROR";

        HourlyWeatherData.WeatherData currentHourWeatherData = hourlyWeatherData.getList().get(index);

        // Get current hour
        String dateTime = currentHourWeatherData.getDt_txt();
        String[] dateTimeParts = dateTime.split(" ");
        String timePart = dateTimeParts[1];
        String[] timeParts = timePart.split(":");
        String hour = timeParts[0];

        // Specify unit type
        String temp_type;
        String speed_type;
        if (unit.equals("metric")) {
            temp_type = "°C";
            // Meters per second
            speed_type = "m/s";
        } else {
            temp_type = "°F";
            // Miles per hour
            speed_type = "m/h";
        }

        // Placeholder image
        Image currentHourWeatherImage = placeholderImage;

        // Set the weather data to variables
        if (hourlyWeatherData != null) {

            String weatherStatus = currentHourWeatherData.getWeather().get(0).getIcon();

            currentHourWeatherImage = imageCache.get(weatherStatus);

            if (currentHourWeatherImage == null) {
                String imagePath = "/weather_types/" + weatherStatus + ".gif";
                currentHourWeatherImage = new Image(getClass().getResourceAsStream(imagePath));

                // Put the loaded image into the cache
                imageCache.put(weatherStatus, currentHourWeatherImage);
            }

            double tempValue = currentHourWeatherData.getMain().getTemp();
            int roundedTemp = (int) Math.round(tempValue);

            temperature = String.format("%d" + temp_type, roundedTemp);

            int humidityValue = currentHourWeatherData.getMain().getHumidity();
            humidity = String.format("%d", humidityValue);

            // Set wind speed
            double speedValue = currentHourWeatherData.getWind().getSpeed();
            int roundedSpeed = (int) Math.round(speedValue);
            windSpeed = String.format("%d" + speed_type, roundedSpeed);
        }

        // Create an ImageView with the weather status icon
        ImageView weatherIconView = new ImageView(currentHourWeatherImage);
        weatherIconView.setFitHeight(25); // Set the height as needed
        weatherIconView.setFitWidth(25); // Set the width as needed

        // Elements to display weather data
        Label hourLabel = new Label(hour);
        Label tempLabel = new Label(temperature);
        Label windLabel = new Label(windSpeed);
        Label humidityLabel = new Label(humidity);

        // Set font and style
        Font labelFont = Font.font("Montserrat", FontWeight.MEDIUM, FontPosture.REGULAR, 15);
        hourLabel.setFont(labelFont);
        tempLabel.setFont(labelFont);
        windLabel.setFont(labelFont);
        humidityLabel.setFont(labelFont);

        // Add labels to VBox
        hourColumn.getChildren().addAll(hourLabel, weatherIconView, tempLabel, windLabel, humidityLabel);

        return hourColumn;
    }

    private void updateHourlyColumns() {
        // Clear existing columns
        bottomHBox.getChildren().clear();

        HourlyWeatherData hourlyWeatherData;
        try {
            // Call the getWeatherData function to retrieve hourly weather data
            response = getWeatherData(city_loc, api_key_Abu, "hourly");

            // Parse the response and handle the data as needed
            Gson gson = new Gson();
            hourlyWeatherData = gson.fromJson(response, HourlyWeatherData.class);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            return;
        }

        if (city_loc != null) {
            // Create a column for each hour
            for (int i = 0; i < 24; i++) {
                VBox hourColumn = createHourColumn(i, city_loc, hourlyWeatherData);
                bottomHBox.getChildren().add(hourColumn);
            }
        }

    }

    private String getWeatherData(String city, String apikey, String timespan) throws IOException {
        String apiUrl;
        if (timespan.equals("hourly")) {
            apiUrl = "https://pro.openweathermap.org/data/2.5/forecast/hourly?q=" + city + "&appid=" + apikey
                    + "&units=" + unit + "&lang=" + lang;
        } else if (timespan == "daily") {
            apiUrl = "https://pro.openweathermap.org/data/2.5/forecast/daily?q=" + city + "&appid=" + apikey + "&units="
                    + unit + "&lang=" + lang;
        } else {
            apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apikey + "&units=" + unit
                    + "&lang=" + lang;
        }

        URL url = new URL(apiUrl);

        // Opening HTML connection
        URLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty(apikey, apiUrl);

        // Check for possible errors
        int responseCode = ((HttpURLConnection) connection).getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Location found
            LOCATION_NOT_FOUND = false;

            // Establishing the readers
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder respoStringBuilder = new StringBuilder();
            String line;

            // Reading the response
            while ((line = br.readLine()) != null) {
                respoStringBuilder.append(line);
            }

            br.close();

            // Converting stringbuilder to string
            response = respoStringBuilder.toString();

            // Using Gson to parse JSON
            Gson gson = new Gson();

            if (timespan.equals("hourly")) {
                // Print the raw JSON response for trouble shooting
                // System.out.println("Raw JSON Response: " + response);

                // If hourly weather:
                HourlyWeatherData hourlyWeatherData = gson.fromJson(response, HourlyWeatherData.class);

                // Saving generated hourlyWeatherData object to a container for later accessing
                hourly_history.put(hourlyWeatherData.getCity().getName(), hourlyWeatherData);
            }

            else if (timespan.equals("daily")) {
                // TODO: Handle daily weather data parsing
            }

            else {
                // If current weather:
                CurrentWeatherData todaysWeatherData = gson.fromJson(response, CurrentWeatherData.class);
                // Update searched city's name
                city_loc = todaysWeatherData.getName();

                // Saving generated todaysWeatherData object to a container for later accessing
                current_history.put(todaysWeatherData.getName(), todaysWeatherData);

                // Test print for current weather
                String weatherTest = new String("Weather in " + todaysWeatherData.getName() + " "
                        + todaysWeatherData.getWeather().get(0).getDescription() + " "
                        + todaysWeatherData.getWeather().get(0).getMain() + " "
                        + " " + String.format("%.2f", todaysWeatherData.getMain().getTemp()));

                System.out.println(weatherTest);
            }
            return response;
        }

        else {
            LOCATION_NOT_FOUND = true;
            city_locText = new Text("CITY NOT FOUND");
            city_locText.setFont(locFont);
            city_locText.setStroke(Color.BLACK);
            city_locText.setFill(Color.BLACK);
            city_locText.setStrokeWidth(0.5);
            locLabel.setGraphic(city_locText);

            return null;

        }
    }

    // Check whether location is in favourites
    private boolean isFavourite() {
        if (favourites.contains(city_loc)) {
            updateStarImage("/icons/star.png");
            return true;
        } else {
            updateStarImage("/icons/empty_star.png");
            return false;
        }
    }

    // This function adds/removes favourites from list and updates star icon
    private void toggleFavourite() {
        if (isFavourite() && !LOCATION_NOT_FOUND) {
            favourites.remove(city_loc);
            updateStarImage("/icons/empty_star.png");
        }

        else if (!isFavourite() && !LOCATION_NOT_FOUND) {
            favourites.add(city_loc);
            updateStarImage("/icons/star.png");
        }

        // Updating the contents of favourites dropbox
        updateFavouritesComboBox();
    }

    // Update star's color when the button is toggled
    private void updateStarImage(String imageUrl) {
        starImage = new Image(getClass().getResourceAsStream(imageUrl));
        favStar.setFitWidth(20);
        favStar.setFitHeight(20);
        favStar.setImage(starImage);
        favButton.setGraphic(favStar);
    }

    // Update location label
    private void updateLocLabel() {
        city_locText.setFont(locFont);
        city_locText.setText(city_loc);
    }

    // Update description label
    private void updateDescriptionLabel() {
        CurrentWeatherData todaysData = current_history.get(city_loc);

        if (todaysData != null) {
            {
                String rawString = todaysData.getWeather().get(0).getDescription();

                if (!lang.equals("zh_cn") && !lang.equals("vi") && !lang.equals("ar")) { // Only format if allowed
                    descriptionText.setText(rawString.substring(0, 1).toUpperCase() + rawString.substring(1) + ".");
                }

                else {
                    descriptionText.setText(rawString);

                }
            }
        }
    }

    // Update temperature
    void updateTemperText() {
        CurrentWeatherData todaysData = current_history.get(city_loc);

        if (todaysData != null) {
            Double temperatureDouble = todaysData.getMain().getTemp();

            switch (unit) {
                case "metric":
                    temperature = String.format("%.1f", temperatureDouble) + " °C";

                    break;

                case "imperial":
                    temperature = String.format("%.1f", temperatureDouble) + " °F";

                    break;
            }

            temperText.setText(temperature);

        }
    }

    // This updates the gif of current weather
    private void updateWeatherImage() {
        CurrentWeatherData todaysData = current_history.get(city_loc);

        if (todaysData != null) {
            String weatherStatus = todaysData.getWeather().get(0).getIcon();
            String imagePath = "/weather_types/" + weatherStatus + ".gif";

            CurrentWeatherImage = new Image(getClass().getResourceAsStream(imagePath));
            weatherImage.setImage(CurrentWeatherImage);

        }
    }

    // This updates the feels like- text
    private void updateFeelsText() {
        CurrentWeatherData todaysData = current_history.get(city_loc);

        if (todaysData != null) {
            Double feelsDouble = todaysData.getMain().getFeelsTemp();

            switch (unit) {
                case "metric":
                    feelsLike = String.format("Truefeel %.1f", feelsDouble) + " °C";

                    break;

                case "imperial":
                    feelsLike = String.format("Truefeel %.1f", feelsDouble) + " °F";

                    break;
            }

            feelsText.setText(feelsLike);

        }
    }

    // This updates wind speed
    private void updateWindSpeed() {
        CurrentWeatherData todaysData = current_history.get(city_loc);

        if (todaysData != null) {
            Double windDouble = todaysData.getWind().getSpeed();

            switch (unit) {
                case "metric":
                    windSpeed = String.format("༄ %.1f", windDouble) + " kph";

                    break;

                case "imperial":
                    windSpeed = String.format("༄ %.1f", windDouble) + " mph";

                    break;
            }

            windText.setText(windSpeed);

        }
    }

    // This function exits the program, but also saves the existing favourites to a
    // txt file.
    private Button getQuitButton() {
        // Creating a button.
        Button button = new Button("Quit");

        // Adding an event to the button to terminate the application and save
        // favourites to a file.
        button.setOnAction((ActionEvent event) -> {
            saveFavourites();
            Platform.exit();
        });

        return button;
    }

    // This saves favourites to a txt file at the end of the session
    // It also saves current location to a seperate txt file
    // additionally, selected language is also saved
    private void saveFavourites() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("last_language.txt"))) {
            writer.write(lang);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("favourites.txt"))) {
            for (String location : favourites) {
                writer.write(location);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("last_location.txt"))) {
            if (!LOCATION_NOT_FOUND) {
                writer.write(city_loc);
                writer.close();
            }

            // If latest search was an error, set last favourite as the last search
            else {
                if (!favourites.isEmpty()) {
                    city_loc = favourites.get(favourites.size()-1);
                    writer.write(city_loc);
                    writer.close();
                }

                else {
                    city_loc = "";
                    writer.write(city_loc);
                    writer.close();
                }
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

    // This file loads favourites from a file, and it also loads last location and
    // the language
    private void loadFavourites() {

        // Load language
        try (BufferedReader reader = new BufferedReader(new FileReader("last_language.txt"))) {

            lang = reader.readLine();

            if(lang == null || lang.trim().isEmpty()){
                lang = "en";
            }
            
            langBox.setValue(lang);
            langBox.fireEvent(new ActionEvent(langBox, null));

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("favourites.txt"))) {

            String line;

            while ((line = reader.readLine()) != null) {
                favourites.add(line);
                // Change star icon for this
                starImage = new Image(getClass().getResourceAsStream("/icons/star.png"));
                favStar = new ImageView(starImage);
                favStar.setFitWidth(20);
                favStar.setFitHeight(20);
                favButton.setGraphic(favStar);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("last_location.txt"))) {

            city_loc = reader.readLine();

            search();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void search() {
        // API call for weekly/current weather happens here
        // Also a lot of other functions are activated each time search button is
        // pressed
        try {
            getWeatherData(city_loc, api_key_Abu, "current");
            updateLocLabel();
            updateDescriptionLabel();
            updateWeatherImage();
            updateTemperText();
            updateFeelsText();
            updateWindSpeed();
            isFavourite();

            // Update hourly columns
            updateHourlyColumns();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Unit toggle button functionality

    private Button getUnitToggleButton() {
        Button unitButton = new Button("Metric");

        unitButton.setOnAction((ActionEvent event) -> {
            if (unitButton.getText() == "Imperial") {
                unitButton.setText("Metric");
                unit = "metric";
            }

            else if (unitButton.getText() == "Metric") {
                unitButton.setText("Imperial");
                unit = "imperial";
            }
        });

        return unitButton;

    }

    // This changes language
    private ComboBox<String> langButton() {

        langBox = new ComboBox<>();
        // Add options to the ComboBox
        langBox.getItems().addAll("en", "fi", "fr", "tr", "az", "zh_cn", "vi", "de", "da", "sp", "ar");
        langBox.setValue("en");

        lang = langBox.getValue();

        langBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lang = langBox.getValue();

            }
        });

        return langBox;
    }

    // Favourites can be accessed here
    private ComboBox<String> favouritesBox = new ComboBox<>();

    private ComboBox<String> favouritesDropBox() {

        // Siphon favourites here
        favouritesBox.getItems().setAll(favourites);

        // Add selected favourite to search box
        favouritesBox.setOnAction(event -> {
            String selectedFavourite = favouritesBox.getValue();
            if (selectedFavourite != null) {
                locField.setText(selectedFavourite);
                locButton.fire();

            }

        });

        return favouritesBox;

    }

    // This method updates the items in the ComboBox
    private void updateFavouritesComboBox() {
        favouritesDropBox().getItems().clear();
        favouritesDropBox().getItems().setAll(favourites);
    }

}