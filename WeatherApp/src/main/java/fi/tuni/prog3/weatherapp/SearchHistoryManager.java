package fi.tuni.prog3.weatherapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryManager {
    private List<SearchHistoryEntry> searchHistory;
    private static final String HISTORY_FILE = "history.json";

    public SearchHistoryManager() {
        this.searchHistory = new ArrayList<>();
        loadSearchHistory();
    }

    public void addToHistory(String city_loc) {
        LocalDateTime timestamp = LocalDateTime.now();
        SearchHistoryEntry entry = new SearchHistoryEntry(timestamp, city_loc);
        searchHistory.add(entry);
        saveSearchHistory();
    }

    private void saveSearchHistory() {
        try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting() // Use pretty printing for better formatting
                    .create();

            writer.write("[\n"); // Start of JSON array

            boolean isFirstEntry = true;
            for (SearchHistoryEntry entry : searchHistory) {
                if (entry.getCity_loc() != null && !entry.getCity_loc().isEmpty()) {
                    // Save only if the location (city_loc) is not null or empty
                    String json = gson.toJson(entry);

                    if (!isFirstEntry) {
                        writer.write(",\n"); // Add comma and new line if it's not the first entry
                    } else {
                        isFirstEntry = false;
                    }

                    writer.write(json); // Write the already serialized JSON string
                }
            }

            writer.write("\n]"); // End of JSON array
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
    
    private void loadSearchHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
                Type listType = new TypeToken<List<SearchHistoryEntry>>() {
                }.getType();
                searchHistory = gson.fromJson(reader, listType);
                if (searchHistory == null) {
                    searchHistory = new ArrayList<>(); // Initialize if loadedHistory is null
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Log the error or handle it appropriately
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                // Log the error or handle it appropriately
            }
        } else {
            searchHistory = new ArrayList<>(); // File doesn't exist, initialize empty list
        }
    }

    public List<SearchHistoryEntry> getSearchHistory() {
        return searchHistory;
    }
}
