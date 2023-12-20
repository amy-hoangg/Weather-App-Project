Programming 3 Group Project: Weather App
Overview

This repository contains the source code for a Weather App developed as part of the Programming 3 group project. The Weather App is designed to provide users with current and forecasted weather information for various locations.

Features

    Current Weather Data: Retrieve and display real-time weather data for a specific location, including temperature, wind speed, and visibility.
    Hourly Forecast: Display an hourly forecast for the next 24 hours, showing temperature, wind speed, and humidity.
    Daily Forecast: Provide a 7-day weather forecast with details on daily temperature, weather conditions, and more.
    Search History: Previously searched locations can be seen as timestamps in the history log.
    Favorites: Users can mark locations as favorites for easy access to frequently checked weather updates. Favourites can be deleted all at once or idividually.
    Internationalization: Support multiple languages for user interface elements. Ability to search for location in multiple languages regardless of the language selected.
    Units: Temperature and wind speed can be seen in either metric or imperial units.
    Memory: The program saves last searched location, last used units and last used language as defaults for next time the program is started.
    Error handling: The program handles errors during file processing and if location is not found.

    Additional requirements

        Weather map: Consists of a map base and map data obtained from OpenWeatherMap. The map can be zoomed in and out and also type of weather data can be selected between 
        temperature, clouds and precipitation.
        Location search history: Recently searched locations are kept in memory and stored in history.json.
        Multiple systems of units of measurement: Supports displaying weather data in SI and imperial units, fetched from OpenWeatherMap.

        Own features

            Quotes: Displays random weather related quote at the bottom of the GUI. Required implementing the Quotes class and quotes.txt file.
            Default setting: Last used language and last used units are saved and loaded when user opens the program next time.
            Multiple languages: The program can display weather data and run in multiple languages, and locations can be searched for in multiple languages regardless of the currently used language.
            GIFs: The weather icons are GIFs instead of still images
            Star button: Custom button that can both favourite and un-favourite a location, and it changes color based on that.
            One click empty favourites: All favourites can be deleted at once instead of one at a time.



Testing

    Abdurrahman Faig implemented a class for testing (Junit), but because of software version compatibility issues not related to the code itself, the unit tests refused to run. However, we did our own thorough testing and fixed the bugs and issues as they were
    discovered.

Components

The project is structured into several components:

    Weather App: The main application that interacts with the OpenWeatherMap API to fetch weather data and displays it to the user.
    WeatherData: The project has tree main classes for weather data. CurrentWeatherData, 
    DailyWeatherData and HourlyWeatherData.
    Search History Manager: Manages the search history functionality, storing and retrieving search entries with timestamps.
    Quotes: A class that provides random quotes, adding a motivational or interesting element to the application.

How to Run

    Clone this repository to your local machine.
    Open the project in your preferred Java IDE.
    Compile and run the WeatherApp class to start the Weather App.

Dependencies

    Gson: This project uses the Gson library for handling JSON serialization and deserialization.

Contributors with student numbers

    Abdurrahman Faig (H290733), Hans Ravna-Pieski (151247084), My Hoang (151395127)

Division of labour in the team

    Hans Ravna-Pieski:
        Implemented the HourlyWeatherData class for the hourly columns.
        Made the hourly columns to the GUI. 
        Implemented the Quotes class
        to display a random weather quote in GUI. 
        Positioned quit button.
        Made the UML class diagram in Dia. 
        Optimized image loading and 
        other minor fixes and adjustments.
        Manual testing and fixing bugs.
        README-file.
        Program translation.

    Abdurrahman Faig:
        Uppermost buttons of the program.
        API call logic for weather data, fundamental JSON class and GSON parsing logic.
        Unit change button, API unit logic, ability to change units and remember last unit.
        Favourites logic, star button that changes based on favourite status.
        Saving favourites to a file and loading them at the start of the program.
        Ability to clear saved favourites with a single button.
        Language selection, API language logic, saving last used language.
        All the content in the first box of the program that display's today's weather
        GIFs instead of still images.
        README-file.
        Quit-button logic that handles saving data, connecting quit-button to X-button.
        Manual testing and fixing bugs.
        Program translation.

    My Hoang:
        Second nav bar with 3 buttons: forecast, history, map.
        Middle scroll pane daily forecast.
        Save, load and display search history in middle scroll pane.
        Display map in middle scroll pane, API call for map.
        Implement 3 features forecast, history, map changing according to location, unit and language.
        Manual testing and fixing bugs.

## UML diagram of the application
![Alt text](prog3classdiagram.SVG)

