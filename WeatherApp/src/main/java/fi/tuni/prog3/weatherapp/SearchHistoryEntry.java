package fi.tuni.prog3.weatherapp;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class SearchHistoryEntry {
    @SerializedName("timestamp")
    private LocalDateTime timestamp;
    @SerializedName("city_loc")
    private String city_loc;

    public SearchHistoryEntry(LocalDateTime timestamp, String city_loc) {
        this.timestamp = timestamp;
        this.city_loc = city_loc;
    }

    // Getters and setters (make sure they are public)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getCity_loc() {
        return city_loc;
    }

    public void setCity_loc(String city_loc) {
        this.city_loc = city_loc;
    }
}
