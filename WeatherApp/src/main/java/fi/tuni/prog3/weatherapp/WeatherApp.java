package fi.tuni.prog3.weatherapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

// This version is being maintained by Abu
// Latest update: API call is working

/**
 * JavaFX Sisu
 */
public class WeatherApp extends Application {

    String api_key_Abu = "88a91051d6699b4cb230ff1ff2ebb3b1";
    // String api_key_Hans = "83d2b0a2d2140939c7f59d054de6a413";

    // Container for city data
    Map<String, WeatherData> history = new HashMap<>();

    // This displays location name
    private Label locLabel;
    private String city_loc;
    private Font titleFont;
    private Font locFont;
    private Text city_locText;

    @Override
    public void start(Stage stage) {

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

        Scene scene = new Scene(root, 600, 800);
        stage.setScene(scene);
        stage.setTitle("WeatherApp");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private VBox getCenterVBox() {
        // Creating an HBox.
        VBox centerHBox = new VBox(10);

        // Adding two VBox to the HBox.
        centerHBox.getChildren().addAll(getTopButtonBox(), getTopHBox(),
                getMiddleBox(), getBottomScrollPane());

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

        // Creating favourites button

        Button FavButton = new Button("Favourites");

        // Adding search for location textbox

        TextField locField = new TextField();
        locField.setMaxWidth(100);
        locField.setPromptText("Enter your city: ");

        // Location search button

        Button locButton = new Button("Search for city");

        locButton.setOnAction(event -> {
            city_loc = locField.getText();

            // API CALL HAPPENS HERE!!!
            try {
                getWeatherData(city_loc.toLowerCase(), api_key_Abu);
            } catch (IOException e) {

                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            updateLocLabel(); // Updates location name

        });

        // Pressing enter is equal to pressing search button
        locField.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("Enter")) {
                locButton.fire();
            }
        });

        // Creating spacer and adjusting button layout

        Region spacer = new Region();

        HBox.setMargin(locField, new Insets(0, 10, 0, 0));
        HBox.setMargin(spacer, new Insets(0, 280, 0, 0));
        HBox.setMargin(unitButton, new Insets(0, 10, 0, 5));

        topHBox.getChildren().addAll(unitButton, locField, locButton, FavButton);

        return topHBox;
    }

    private HBox getTopHBox() {
        // Creating a HBox for the left side.
        HBox leftHBox = new HBox();
        leftHBox.setPrefHeight(330);
        leftHBox.setStyle("-fx-background-color: #327aed;");

        // Creating custom text font
        titleFont = Font.loadFont(
                "file:///C:/Opiskelu/Prog3_project/group3163/WeatherApp/src/main/java/custom_fonts/snownly/SNOWNLY.ttf",
                35);

        // Creating top label
        Label topBoxTitle = new Label();
        topBoxTitle.setPadding(new Insets(5, 5, 5, 5));
        Text todaysWeather = new Text("Today's weather in ");
        todaysWeather.setFont(titleFont);
        todaysWeather.setStroke(Color.GREEN);
        todaysWeather.setFill(Color.BLACK);
        todaysWeather.setStrokeWidth(1.3);
        // Shadow effects
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(-3.0);
        // Extra text effect
        todaysWeather.setEffect(shadow);
        // Change label looks
        topBoxTitle.setTextFill(Color.SKYBLUE);
        topBoxTitle.setMinWidth(169);
        topBoxTitle.setGraphic(todaysWeather);

        // City name and its graphics
        locFont = Font.loadFont(
                "file:///C:/Opiskelu/Prog3_project/group3163/WeatherApp/src/main/java/custom_fonts/revorioum/Revo.ttf",
                50);

        String city_loc = "";
        city_locText = new Text(city_loc);
        city_locText.setFont(locFont);
        city_locText.setStroke(Color.DARKGREEN);
        city_locText.setFill(Color.BLACK);
        city_locText.setStrokeWidth(1.3);
        // Shadow effects
        shadow.setOffsetY(5.0);
        // Extra text effect
        city_locText.setEffect(shadow);

        // This label will have the location name
        locLabel = new Label();
        locLabel.setMinWidth(80);
        locLabel.setTextFill(Color.BLACK);

        locLabel.setGraphic(city_locText);
        ;

        leftHBox.getChildren().addAll(topBoxTitle, locLabel);

        return leftHBox;
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

    public ScrollPane getBottomScrollPane() {
        // Creating a VBox for the bottom side.
        HBox bottomVBox = new HBox();
        bottomVBox.setPrefHeight(200);
        bottomVBox.setStyle("-fx-background-color: white;");

        bottomVBox.setSpacing(10);
        bottomVBox.setAlignment(Pos.CENTER);

        String[] hours = { "00", "01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
                "18", "19", "20", "21", "22", "23", "24" };

        // Create a column for each hour
        for (String hour : hours) {
            VBox hourColumn = createHourColumn(hour);
            bottomVBox.getChildren().add(hourColumn);
        }

        // Add scrollbar to bottom to scroll through hours
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(bottomVBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        return scrollPane;
    }

    private VBox createHourColumn(String hour) {
        VBox hourColumn = new VBox();
        hourColumn.setAlignment(Pos.CENTER);

        // TODO: Get actual weather data
        String weatherIcon = "???"; // Weather icon representing state of weather
        String temperature = "-42°C";
        String windDirection = "->"; // Arrow representing direction of wind
        String humidity = "420%";

        // Labels to display weather data
        Label hourLabel = new Label(hour);
        Label iconLabel = new Label(weatherIcon);
        Label tempLabel = new Label(temperature);
        Label windLabel = new Label(windDirection);
        Label humidityLabel = new Label(humidity);

        // Add labels to VBox
        hourColumn.getChildren().addAll(hourLabel, iconLabel, tempLabel, windLabel, humidityLabel);

        return hourColumn;
    }

    private VBox getWeatherBox() {
        VBox WeatherBox = new VBox();
        WeatherBox.setPrefHeight(300);
        WeatherBox.setStyle("-fx-background-color: #b1c2d4;");

        return WeatherBox;
    }

    // Update location label
    private void updateLocLabel() {
        city_locText.setFont(locFont);
        city_locText.setText(city_loc);
    }

    private Button getQuitButton() {
        // Creating a button.
        Button button = new Button("Quit");

        // Adding an event to the button to terminate the application.
        button.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });

        return button;
    }

    // Unit toggle button functionality

    private Button getUnitToggleButton() {
        Button unitButton = new Button("Imperial");

        unitButton.setOnAction((ActionEvent event) -> {
            if (unitButton.getText() == "Imperial") {
                unitButton.setText("Metric");
            }

            else if (unitButton.getText() == "Metric") {
                unitButton.setText("Imperial");
            }
        });

        return unitButton;

    }

    private String getWeatherData(String city, String apikey) throws IOException {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apikey;

        URL url = new URL(apiUrl);

        // Opening HTML connection
        URLConnection connection = url.openConnection();
        connection.setRequestProperty(apikey, apiUrl);

        // Establishing the readers
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder respoStringBuilder = new StringBuilder();
        String line;

        // Reading the response
        while ((line = br.readLine()) != null) {
            respoStringBuilder.append(line);
        }

        // Converting stringbuilder to string
        String response = respoStringBuilder.toString();

        // Using Gson to parse JSON
        Gson gson = new Gson();

        WeatherData weatherData = gson.fromJson(response, WeatherData.class);

        // Saving generated weatherData object to a container for later accessing
        history.put(weatherData.getName(), weatherData);

        // Test print

        System.out
                .println("Weather in " + weatherData.getName() + " " + weatherData.getWeather().get(0).getDescription()
                        + " " + String.format("%.2f", weatherData.getMain().getTemp()));

        return response;

    }

}