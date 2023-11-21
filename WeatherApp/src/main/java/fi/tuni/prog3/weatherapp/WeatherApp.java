package fi.tuni.prog3.weatherapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

// This version is being maintained by Abu

/**
 * JavaFX Sisu
 */
public class WeatherApp extends Application {

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

        Scene scene = new Scene(root, 500, 700);
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
        centerHBox.getChildren().addAll(getTopButtonBox(), getTopHBox(), getBottomHBox());

        return centerHBox;
    }

    private HBox getTopButtonBox(){
        // Creating top box for buttons
        HBox topHBox = new HBox();
        topHBox.setPrefHeight(50);
        topHBox.setStyle("-fx-background-color: #05de29;");

        //creating unit toggle button

        Button unitButton = getUnitToggleButton();
        unitButton.setMinWidth(60);

        // Creating search button
        Button SearchButton = new Button("Search");

        // Creating favourites button

        Button FavButton = new Button("Favourites");

        // Creating spacer and adjusting button layout

        Region spacer = new Region();

        HBox.setMargin(SearchButton, new Insets(0, 10, 0, 0));
        HBox.setMargin(spacer, new Insets(0, 280, 0, 0));
        HBox.setMargin(unitButton, new Insets(0, 0, 0, 5));
        
        topHBox.getChildren().addAll(unitButton, spacer, SearchButton, FavButton);

        return topHBox;
    }

    private HBox getTopHBox() {
        // Creating a HBox for the left side.
        HBox leftHBox = new HBox();
        leftHBox.setPrefHeight(330);
        leftHBox.setStyle("-fx-background-color: #8fc6fd;");

        
        // Creating top label
        Label topBoxTitle = new Label();
        topBoxTitle.setFont(Font.font("arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
        topBoxTitle.setText("Today's weather in ");

         // Adding search for location textbox

        TextField locField = new TextField();
        locField.setMaxWidth(100);
        locField.setPromptText("Enter your city: ");

        // This label will have the location name
        Label locLabel = new Label();
        locLabel.setMinWidth(80);

        // Location search button

        Button locButton = new Button("Search for city");

        locButton.setOnAction(event -> {
            String location = locField.getText();

            String locOutput = new String(location);
            locLabel.setFont(Font.font("arial", FontWeight.BOLD, FontPosture.REGULAR, 12));

            locLabel.setText(locOutput);
        });

        HBox.setMargin(locField, new Insets(0, 5, 0, 5));



        leftHBox.getChildren().addAll(topBoxTitle, locLabel, locField, locButton);

        return leftHBox;
    }

    private HBox getBottomHBox() {
        // Creating a VBox for the right side.
        HBox rightHBox = new HBox();
        rightHBox.setPrefHeight(330);
        rightHBox.setStyle("-fx-background-color: #b1c2d4;");

        rightHBox.getChildren().add(new Label("Bottom Panel"));

        return rightHBox;
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

    //Unit toggle button functionality

    private Button getUnitToggleButton(){
        Button unitButton = new Button("Imperial");

        unitButton.setOnAction((ActionEvent event) -> {
            if(unitButton.getText() == "Imperial"){
                unitButton.setText("Metric");
            }

            else if(unitButton.getText() == "Metric"){
                unitButton.setText("Imperial");
            }
        });

        return unitButton;

    }

    private String getLocation(){
        return "Test";
    }
}