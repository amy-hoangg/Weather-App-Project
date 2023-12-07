package fi.tuni.prog3.weatherapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WeatherApp extends Application {

    private String unit = "metric";
    private String lang = "en";

    // For error handling
    private Boolean LOCATION_NOT_FOUND = false;

    // Container for current city weather data
    private Map<String, CurrentWeatherData> current_history = new HashMap<>();
    private Map<String, HourlyWeatherData> hourly_history = new HashMap<>();
    private Map<String, DailyWeatherData> daily_history = new HashMap<>();

    // Container for cached images to reduce memory usage
    // The key is the weather status icon id (for example "04n")
    private Map<String, Image> imageCache = new HashMap<>();

    // Placeholder image is used often so load it once here to reduce
    // memory usage
    private static Image placeholderImage;

    // Color profile of the program
    private String main_color = "#06cccc";
    private String accent_color = "#dcfaf9";

    // Favourites are stored in these
    private List<String> favourites = new ArrayList<String>();
    private ComboBox<String> favouritesBox = favouritesDropBox();

    private static String api_key_Abu = "88a91051d6699b4cb230ff1ff2ebb3b1";

    private HBox bottomHBox = new HBox();
    private HBox dailyHbox = new HBox();
    private ScrollPane middleScrollPane = new ScrollPane();

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
    private boolean isMapShown;
    private boolean isForecastShown;
    private boolean isHistoryShown;

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
        /*
         * var quitButton = getQuitButton();
         * BorderPane.setMargin(quitButton, new Insets(10, 10, 0, 10));
         * root.setBottom(quitButton);
         * BorderPane.setAlignment(quitButton, Pos.TOP_RIGHT);
         */

        Scene scene = new Scene(root, 650, 900);
        stage.setScene(scene);
        stage.setTitle("WeatherApp");
        stage.show();

        // Connecting X-button to quit-button
        // stage.setOnCloseRequest(event -> quitButton.fire());
        stage.setOnCloseRequest(event -> getQuitButton().fire());
    }

    public static void main(String[] args) {
        initImages();
        launch();
    }

    private static void initImages() {
        placeholderImage = new Image(WeatherApp.class.getResourceAsStream("/weather_types/placeholder.gif"));
    }

    private VBox getCenterVBox() {
        // Creating an HBox.
        VBox centerHBox = new VBox(10);

        // Add all boxes and scrollpane to centerbox
        centerHBox.getChildren().addAll(getFirstNavBar(), getTodayBox(), getSecondNavBar(),
                getMiddleScrollPane(), getBottomScrollPane(), getBottomHBox());

        return centerHBox;
    }

    private HBox getFirstNavBar() {
        HBox topHBox = new HBox();
        topHBox.setPadding(new Insets(5, 5, 0, 5));
        topHBox.setPrefHeight(50);
        topHBox.setStyle("-fx-background-color: " + main_color);

        Button unitButton = getUnitToggleButton();
        unitButton.setMinWidth(60);

        locField = new TextField();
        locField.setMaxWidth(100);
        locField.setPromptText("Enter your city: ");

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

        // Empty favourites-button
        Button clearFavs = new Button("Clear favourites");
        clearFavs.setMinWidth(60);

        clearFavs.setOnAction(event -> {
            favourites.clear();
            updateFavouritesComboBox();
            isFavourite();

            updateFavBoxText();
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

        topHBox.getChildren().addAll(unitButton, locField, locButton, favouritesDropBox(), langButton(), spacer2,
                clearFavs);

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
        todayBox.setPrefHeight(300);
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

    private HBox getSecondNavBar() {
        HBox secondNavBar = new HBox(); // Create an HBox for the second navigation bar
        secondNavBar.setAlignment(Pos.CENTER_LEFT);
        // secondNavBar.setSpacing(10);
        // secondNavBar.setPadding(new Insets(5, 10, 5, 10));
        secondNavBar.setMinHeight(Control.USE_PREF_SIZE); // Set minimum height to prefer size

        Button forecastButton = new Button("Forecast");
        Button historyButton = new Button("History");
        Button mapButton = new Button("Map");

        forecastButton.setOnAction(e -> showForecastContent());
        mapButton.setOnAction(e -> showMapContent());
        historyButton.setOnAction(e -> showHistoryContent());

        // Add buttons to the HBox
        secondNavBar.getChildren().addAll(forecastButton, historyButton, mapButton);

        return secondNavBar;
    }

    private ScrollPane getMiddleScrollPane() {
        dailyHbox.setPrefHeight(300);
        dailyHbox.setStyle("-fx-background-color: white;");
        dailyHbox.setSpacing(10);
        dailyHbox.setPadding(new Insets(10, 5, 0, 5));

        middleScrollPane.setContent(dailyHbox);
        middleScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        middleScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return middleScrollPane;
    }

    private VBox createDayColumn(int index, String city, DailyWeatherData dailyWeatherData) {
        VBox dayColumn = new VBox();
        dayColumn.setAlignment(Pos.TOP_CENTER);
        dayColumn.setSpacing(10);
        VBox.setVgrow(dayColumn, Priority.NEVER);

        String temp_type;
        if (unit.equals("metric")) {
            temp_type = "°C";
        } else {
            temp_type = "°F";
        }

        String temperatureMin;
        String temperatureMax;
        String dayOfWeek;
        String dayOfMonth;

        DailyWeatherData.WeatherData currentDayWeatherData = dailyWeatherData.getList().get(index);

        // Extracting day information from timestamp
        long timestamp = currentDayWeatherData.getDt();
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZoneId zoneId = ZoneId.of("GMT"); // Adjust timezone if necessary
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zoneId);
        dayOfWeek = dateTime.getDayOfWeek().toString();
        dayOfMonth = dateTime.format(DateTimeFormatter.ofPattern("d.M"));

        // Get temperature details
        temperatureMin = String.format("%.0f", currentDayWeatherData.getTemp().getMin());
        temperatureMax = String.format("%.0f", currentDayWeatherData.getTemp().getMax());

        // Get weather status
        String weatherStatus = "No data"; // Default value
        List<DailyWeatherData.Weather> weatherList = currentDayWeatherData.getWeather();
        if (!weatherList.isEmpty()) {
            weatherStatus = weatherList.get(0).getDescription();

            // Retrieve the weather icon for the status
            String weatherIcon = weatherList.get(0).getIcon();
            Image weatherImage = imageCache.get(weatherIcon);
            if (weatherImage == null) {
                String imagePath = "/weather_types/" + weatherIcon + ".gif";
                weatherImage = new Image(getClass().getResourceAsStream(imagePath));
                imageCache.put(weatherIcon, weatherImage); // Cache the image
            }

            ImageView weatherIconView = new ImageView(weatherImage);
            weatherIconView.setFitHeight(25); // Set the height as needed
            weatherIconView.setFitWidth(25); // Set the width as needed
            dayColumn.getChildren().add(weatherIconView);
        }

        // Elements to display weather data
        Label dayOfWeekLabel = new Label(dayOfWeek);
        Label dayOfMonthLabel = new Label(dayOfMonth);
        Label minTempLabel = new Label("Min: " + temperatureMin + " " + temp_type);
        Label maxTempLabel = new Label("Max: " + temperatureMax + " " + temp_type);
        Label weatherStatusLabel = new Label(weatherStatus);

        // Set font and style
        Font labelFont = Font.font("Montserrat", FontWeight.MEDIUM, FontPosture.REGULAR, 15);
        dayOfWeekLabel.setFont(labelFont);
        dayOfMonthLabel.setFont(labelFont);
        minTempLabel.setFont(labelFont);
        maxTempLabel.setFont(labelFont);
        weatherStatusLabel.setFont(labelFont);

        // Add labels to VBox
        dayColumn.getChildren().addAll(dayOfWeekLabel, dayOfMonthLabel, minTempLabel, maxTempLabel, weatherStatusLabel);

        return dayColumn;
    }

    private void updateDailyColumns() {
        dailyHbox.getChildren().clear();

        DailyWeatherData dailyWeatherData;
        try {
            // Call the getWeatherData function to retrieve daily weather data
            response = getWeatherData(city_loc, api_key_Abu, "daily");

            // Parse the response and handle the data as needed
            Gson gson = new Gson();
            dailyWeatherData = gson.fromJson(response, DailyWeatherData.class);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (city_loc != null) {
            // Create a column for each day
            for (int i = 0; i < 7; i++) {
                VBox dayColumn = createDayColumn(i, city_loc, dailyWeatherData);
                dailyHbox.getChildren().add(dayColumn);
            }
        }
    }

    private void showForecastContent() {
        isMapShown = false;
        isForecastShown = true;
        isHistoryShown = false;
        middleScrollPane.setContent(dailyHbox);
    }

    private void showMapContent() {
        isMapShown = true;
        isForecastShown = false;
        isHistoryShown = false;
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.loadContent(getHTMLContent(city_loc));

        VBox mapContent = new VBox(webView);

        middleScrollPane.setContent(mapContent);

        // Trigger change event for layer select after setting initial value
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                webEngine.executeScript(
                        "var layerSelect = document.getElementById('layer-select'); layerSelect.dispatchEvent(new Event('change'));");
            }
        });
    }

    private String getHTMLContent(String city) {
        String temp_type;
        if (unit.equals("metric")) {
            temp_type = "°C";
        } else {
            temp_type = "°F";
        }

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>OpenWeatherMap and OpenStreetMap</title>\n" +
                "  <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                "  <style>\n" +
                "    #map {\n" +
                "      height: 80vh; /* Adjust as needed */\n" +
                "      width: 100%;\n" +
                "    }\n" +
                "    .button-container {\n" +
                "      position: absolute;\n" +
                "      top: 10px;\n" +
                "      left: 10px;\n" +
                "      z-index: 1000;\n" +
                "      background-color: white;\n" +
                "      padding: 5px;\n" +
                "      border-radius: 5px;\n" +
                "      box-shadow: 0 2px 5px rgba(0,0,0,0.2);\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"map\"></div>\n" +
                "<div class=\"button-container\">\n" +
                "  <select id=\"layer-select\">\n" +
                "    <option value=\"temp_new\">Temperature</option>\n" +
                "    <option value=\"clouds_new\">Clouds</option>\n" +
                "    <option value=\"precipitation_new\">Precipitation</option>\n" +
                "  </select>\n" +
                "</div>\n" +
                "<script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                "<script>\n" +
                "  var map = L.map('map').setView([0, 0], 2); // Set the initial view\n" +
                "  var currentLayer;\n" +
                "  var layerSelect = document.getElementById('layer-select');\n" +
                "  var initialLayer = 'temp_new'; // Set the initial layer (Temperature)\n" +
                "  layerSelect.value = initialLayer; // Set the dropdown value\n" +
                "  layerSelect.addEventListener('change', function() {\n" +
                "    var selectedLayer = layerSelect.value;\n" +
                "    if (currentLayer) {\n" +
                "      map.removeLayer(currentLayer);\n" +
                "    }\n" +
                "    currentLayer = L.tileLayer('https://tile.openweathermap.org/map/' + selectedLayer + '/{z}/{x}/{y}.png?appid="
                + api_key_Abu + "', {\n" +
                "      attribution: 'Map data &copy; <a href=\"https://openweathermap.org\">OpenWeatherMap</a>'\n" +
                "    }).addTo(map);\n" +
                "  });\n" +
                "  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "    maxZoom: 19,\n" +
                "    attribution: '© OpenStreetMap contributors'\n" +
                "  }).addTo(map);\n" +
                "  var city = '" + city + "';\n" + // Get the city/location from input
                "  var url = 'https://api.openweathermap.org/data/2.5/weather?q=' + city + '&appid=" + api_key_Abu
                + "&units=" + unit + "&lang=" + lang + "';\n" +
                "  fetch(url)\n" +
                "    .then(response => response.json())\n" +
                "    .then(data => {\n" +
                "      var lat = data.coord.lat;\n" +
                "      var lon = data.coord.lon;\n" +
                "      map.setView([lat, lon], 10); // Set map view to the coordinates of the searched location\n" +
                "      L.marker([lat, lon]).addTo(map)\n" +
                "        .bindPopup('<b>" + city + "</b><br>Temperature: ' + (data.main.temp).toFixed(1) + '"
                + temp_type + "').openPopup();\n" +
                "    });\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";

        return htmlContent;
    }

    private void showHistoryContent() {
        isMapShown = false;
        isForecastShown = false;
        isHistoryShown = true;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("history.json"));
            StringBuilder historyJson = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                historyJson.append(line).append("\n");
            }
            bufferedReader.close();

            Gson gson = new Gson();
            List<Map<String, Object>> historyList = gson.fromJson(historyJson.toString(),
                    new TypeToken<List<Map<String, Object>>>() {}.getType());

            VBox historyContent = new VBox();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            for (Map<String, Object> entry : historyList) {
                StringBuilder entryContent = new StringBuilder();
                if (entry.containsKey("timestamp")) {
                    String timestamp = entry.get("timestamp").toString();
                    entryContent.append("Timestamp: ").append(timestamp).append("\n");

                    if (entry.size() > 1) {
                        for (Map.Entry<String, Object> detail : entry.entrySet()) {
                            if (!detail.getKey().equals("timestamp")) {
                                entryContent.append(detail.getKey()).append(": ").append(detail.getValue()).append("\n");
                            }
                        }
                    }

                    Label entryLabel = new Label(entryContent.toString());
                    historyContent.getChildren().add(entryLabel);
                }
            }

            middleScrollPane.setContent(historyContent);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to read history data");
            alert.showAndWait();
        }
    }    
       
    private HBox getBottomHBox() {
        HBox bottomHBox = new HBox(10);
        bottomHBox.setPrefHeight(100);
        bottomHBox.setStyle("-fx-background-color: white;");
        bottomHBox.setAlignment(Pos.BOTTOM_RIGHT);

        // This box has two elements, the quit button and the quote box
        var quitButton = getQuitButton();
        VBox quoteVBox = getQuoteVBox();

        quitButton.setStyle("; -fx-border-color: " + main_color + "; -fx-border-width: 2px;");


        // Make quoteVBox take up all available horizontal space
        HBox.setHgrow(quoteVBox, Priority.ALWAYS);
        quitButton.setAlignment(Pos.BOTTOM_RIGHT);
        bottomHBox.getChildren().addAll(quoteVBox, quitButton);

        return bottomHBox;
    }

    private VBox getQuoteVBox() {
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
        bottomHBox.setPadding(new Insets(10, 5, 0, 5));
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
        hourColumn.setSpacing(12);
        VBox.setVgrow(hourColumn, Priority.NEVER);
        // hourColumn.setPadding(new Insets(5,5,5,5));

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
            temp_type = " °C";
            // Meters per second
            speed_type = " m/s";
        } else {
            temp_type = " °F";
            // Miles per hour
            speed_type = " mph";
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
            for (int i = 0; i < 24; i++) {
                VBox hourColumn = createHourColumn(i, city_loc, hourlyWeatherData);
                bottomHBox.getChildren().add(hourColumn);
            }
        }

    }

    public String getWeatherData(String city, String api_key_Abu, String timespan) throws IOException {
        String apiUrl;
        if (timespan.equals("hourly")) {
            apiUrl = "https://pro.openweathermap.org/data/2.5/forecast/hourly?q=" + city + "&appid=" + api_key_Abu
                    + "&units=" + unit + "&lang=" + lang;
        } else if (timespan == "daily") {
            apiUrl = "https://pro.openweathermap.org/data/2.5/forecast/daily?q=" + city + "&appid=" + api_key_Abu
                    + "&units="
                    + unit + "&lang=" + lang;
        } else {
            apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + api_key_Abu + "&units="
                    + unit
                    + "&lang=" + lang;
        }

        URL url = new URL(apiUrl);

        // Opening HTML connection
        URLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty(api_key_Abu, apiUrl);

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

                // If daily weather:
                DailyWeatherData dailyWeatherData = gson.fromJson(response, DailyWeatherData.class);

                // Saving generated dailyWeatherData object to a container for later accessing
                daily_history.put(dailyWeatherData.getCity().getName(), dailyWeatherData);
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

    private boolean isFavourite() {
        if (favourites.contains(city_loc)) {
            updateStarImage("/icons/star.png");
            return true;
        } else {
            updateStarImage("/icons/empty_star.png");
            return false;
        }
    }

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

        // This updates the text in case box is emptied
        updateFavBoxText();
    }

    private void updateStarImage(String imageUrl) {
        starImage = new Image(getClass().getResourceAsStream(imageUrl));
        favStar.setFitWidth(20);
        favStar.setFitHeight(20);
        favStar.setImage(starImage);
        favButton.setGraphic(favStar);
    }

    private void updateLocLabel() {
        city_locText.setFont(locFont);
        city_locText.setText(city_loc);
    }

    private void updateDescriptionLabel() {
        CurrentWeatherData todaysData = current_history.get(city_loc);

        if (todaysData != null) {
            {
                String rawString = todaysData.getWeather().get(0).getDescription();

                if (lang.equals("zh_cn")) { // Only format if allowed
                    descriptionText.setText(rawString);

                }

                else if (lang.equals("ar")) {
                    descriptionText.setText((rawString) + ".");

                }

                else {
                    descriptionText.setText(rawString.substring(0, 1).toUpperCase() + rawString.substring(1) + ".");

                }
            }
        }
    }

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

    private void updateWeatherImage() {
        CurrentWeatherData todaysData = current_history.get(city_loc);

        if (todaysData != null) {
            String weatherStatus = todaysData.getWeather().get(0).getIcon();
            String imagePath = "/weather_types/" + weatherStatus + ".gif";

            CurrentWeatherImage = new Image(getClass().getResourceAsStream(imagePath));
            weatherImage.setImage(CurrentWeatherImage);

        }
    }

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

    private void updateWindSpeed() {
        CurrentWeatherData todaysData = current_history.get(city_loc);

        if (todaysData != null) {
            Double windDouble = todaysData.getWind().getSpeed();

            switch (unit) {
                case "metric":
                    windSpeed = String.format("༄ %.1f", windDouble) + " m/s";

                    break;

                case "imperial":
                    windSpeed = String.format("༄ %.1f", windDouble) + " mph";

                    break;
            }

            windText.setText(windSpeed);

        }
    }

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
                    city_loc = favourites.get(favourites.size() - 1);
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

    private void loadFavourites() {

        // Load language
        try (BufferedReader reader = new BufferedReader(new FileReader("last_language.txt"))) {

            lang = reader.readLine();

            if (lang == null || lang.trim().isEmpty()) {
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

    public void search() {
        try {
            getWeatherData(city_loc, api_key_Abu, "current");
            updateLocLabel();
            updateDescriptionLabel();
            updateWeatherImage();
            updateTemperText();
            updateFeelsText();
            updateWindSpeed();
            isFavourite();
            updateHourlyColumns();
            updateDailyColumns();
            if (isMapShown) {
                showMapContent();
            }
            if (isHistoryShown) {
                showHistoryContent();
            }
            SearchHistoryManager historyManager = new SearchHistoryManager();
            historyManager.addToHistory(city_loc);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Button getUnitToggleButton() {
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

            search();
        });

        return unitButton;

    }

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

                search();

            }
        });

        return langBox;
    }

    private ComboBox<String> favouritesDropBox() {
        try {
            // Initialize favouritesBox only if it's not already initialized
            if (favouritesBox == null) {
                favouritesBox = new ComboBox<>();
            }
    
            // Add selected favourite to search box
            favouritesBox.setOnAction(event -> {
                String selectedFavourite = favouritesBox.getValue();
                if (selectedFavourite != null) {
                    locField.setText(selectedFavourite);
                    locButton.fire();
                }
            });
    
            // Check if favourites is null or empty before setting items
            if (favouritesBox.getItems().isEmpty() && favourites != null && !favourites.isEmpty()) {
                // Siphon favourites here
                favouritesBox.getItems().setAll(favourites);
            }
    
            return favouritesBox;
        } catch (Exception e) {
            e.printStackTrace(); // or log the exception
        }
        return favouritesBox;
    }
    

    /*
     * private ComboBox<String> favouritesDropBox() {
     * 
     * // Initialize favouritesBox only if it's not already initialized
     * if (favouritesBox == null) {
     * favouritesBox = new ComboBox<>();
     * }
     * 
     * // Add selected favourite to search box
     * favouritesBox.setOnAction(event -> {
     * String selectedFavourite = favouritesBox.getValue();
     * if (selectedFavourite != null) {
     * locField.setText(selectedFavourite);
     * locButton.fire();
     * }
     * 
     * });
     * 
     * if (favouritesBox.getItems().isEmpty()) {
     * // Siphon favourites here
     * favouritesBox.getItems().setAll(favourites);
     * }
     * 
     * return favouritesBox;
     * 
     * }
     */

    // This method updates the items in the ComboBox
    private void updateFavouritesComboBox() {
        favouritesDropBox().getItems().clear();
        favouritesDropBox().getItems().setAll(favourites);

        // This updates the text in case box is emptied
        updateFavBoxText();
    }

    // This method updates the favoruites combobox text if it is
    private void updateFavBoxText() {

        if (favouritesBox.getItems().isEmpty() || favourites.isEmpty()) {
            favouritesBox.setPromptText("Favourites");
        }

    }

    // Getter methods for unit testing
    public String getApiKey() {
        return this.api_key_Abu;
    }

    public String getLocation() {
        return this.city_loc;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getLang() {
        return this.lang;
    }

    public ComboBox<String> getLangBox() {
        return this.langBox;
    }

    public ComboBox<String> getFavBox() {
        return this.favouritesBox;
    }

    public TextField getLocField() {
        return locField;
    }

    public Label getLocLabel() {
        return locLabel;
    }

    public Text getTemperText() {
        return temperText;
    }

    public Text getFeelsText() {
        return feelsText;
    }

    public Text getWindText() {
        return windText;
    }

    public Text getDescriptionText() {
        return descriptionText;
    }

    public ImageView getWeatherImage() {
        return weatherImage;
    }

}
