package fi.tuni.prog3.weatherapp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Quotes {
    private static final String QUOTES_FILE = "quotes.txt";
    private List<String> quotes;
    private Random random;

    public Quotes() {
        loadQuotes();
        random = new Random();
    }

    private void loadQuotes() {
        quotes = new ArrayList<>();

        try (InputStream inputStream = getClass().getClassLoader().
                                    getResourceAsStream(QUOTES_FILE);
             Scanner scanner = new Scanner(inputStream)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                // Remove number and dot from line
                line = line.substring(line.indexOf(" ") + 1);
                
                quotes.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRandomQuote() {
        int randomIndex = random.nextInt(quotes.size());
        return quotes.get(randomIndex);
    }
}
