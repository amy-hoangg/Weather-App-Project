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
     * Performs a search for a location's weather data, and updates all the necessary variables with seperate methods.
     * @returns nothing
     */
    public void search();


}
