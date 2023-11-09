/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package fi.tuni.prog3.weatherapp;

/**
 * Interface for extracting data from the OpenWeatherMap API.
 */
public interface iAPI {
    
    /**
     * Returns coordinates for a location.
     * @param loc Name of the location for which coordinates should be fetched.
     * @return String.
     */
    public String lookUpLocation(String loc);
    
    /**
     * Returns the current weather for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String.
     */
    public String getCurrentWeather(double lat, double lon);

    /**
     * Returns a forecast for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String.
     */
    public String getForecast(double lat, double lon);
}
