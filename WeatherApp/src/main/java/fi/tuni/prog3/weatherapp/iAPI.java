/*
hello
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package fi.tuni.prog3.weatherapp;

/**
 * Interface for extracting data from the OpenWeatherMap API.
 */
public interface iAPI {
    
    /**
     * Returns weather data as a Wetaherdata-type object based on location that is saved during app runtime.
     * @param loc Name of the location for which data should be fetched.
     * @return String.
     */
    public String getWeatherData();

    /**
     * Returns a forecast for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String.
     */
    public String getForecast(double lat, double lon);
}
