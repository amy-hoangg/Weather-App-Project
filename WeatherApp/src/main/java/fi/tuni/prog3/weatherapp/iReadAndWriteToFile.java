/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package fi.tuni.prog3.weatherapp;

/**
 * Interface with methods to read from a file and write to a file.
 */
public interface iReadAndWriteToFile {
    
    /**
     * Reads JSON from the given file.
     * @param fileName name of the file to read from.
     * @return true if the read was successful, otherwise false.
     * @throws Exception if the method e.g, cannot find the file. 
     */
    public String readFromFile(String fileName) throws Exception; 
    
    /**
     * Write the student progress as JSON into the given file.
     * @param fileName name of the file to write to.
     * @return true if the write was successful, otherwise false.
     * @throws Exception if the method e.g., cannot write to a file.
     */
    public boolean writeToFile(String fileName) throws Exception;
}
