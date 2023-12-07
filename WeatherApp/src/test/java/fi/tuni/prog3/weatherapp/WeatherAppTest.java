/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.weatherapp;

import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

/**
 *
 * @author Abdurrahman Faig
 */
public class WeatherAppTest {

    // New innstance of weatherApp
    WeatherApp weatherApp = new WeatherApp();

    
    public WeatherAppTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        
        WeatherApp weatherApp = new WeatherApp();

    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of start method, of class WeatherApp.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        Stage stage = null;
        WeatherApp instance = new WeatherApp();
        instance.start(stage);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class WeatherApp.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        WeatherApp.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateTemperText method, of class WeatherApp.
     * @throws IOException
     */
    @Test
    public void testSearchAndUpdateLabels() throws IOException {
        // Simulate a search and verify that labels are updated

        assertEquals("London", weatherApp.getLocation());
        assertNotNull(weatherApp.getWeatherData(weatherApp.getLocation(), weatherApp.getApiKey(), "current"));
        assertEquals("en", weatherApp.getLang());
        assertNotNull(weatherApp.getLangBox());
        assertNotNull(weatherApp.getLocField());
        assertNotNull(weatherApp.getLocLabel());
        assertNotNull(weatherApp.getTemperText());
        assertNotNull(weatherApp.getFeelsText());
        assertNotNull(weatherApp.getWindText());
        assertNotNull(weatherApp.getDescriptionText());
        assertNotNull(weatherApp.getWeatherImage());
    }

    @Test
    public void testUnitToggleButton() {
        // Simulate toggling unit button and check if unit is updated
        assertNotNull(weatherApp.getUnitToggleButton());

        assertEquals("Metric", weatherApp.getUnitToggleButton().getText());
        assertEquals("metric", weatherApp.getUnit());

        weatherApp.getUnitToggleButton().fire();
        assertEquals("Imperial", weatherApp.getUnitToggleButton().getText());
        assertEquals("imperial", weatherApp.getUnit());
    }
    
}
