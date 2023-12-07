module fi.tuni.progthree.weatherapp {
    requires transitive javafx.controls;
    exports fi.tuni.prog3.weatherapp;
    requires com.google.gson;
    requires javafx.web;
    requires java.base;
    opens fi.tuni.prog3.weatherapp to com.google.gson;
}

