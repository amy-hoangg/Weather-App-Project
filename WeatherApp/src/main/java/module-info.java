module fi.tuni.progthree.weatherapp {
    // Scrollpane was giving warning without "transitive"
    requires transitive javafx.controls;
    exports fi.tuni.prog3.weatherapp;
    requires com.google.gson;
    requires javafx.web;
}
