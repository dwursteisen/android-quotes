package com.github.dwursteisen.quotes.model;

/**
 * Created by david.wursteisen on 04/09/13.
 */
public class QuotesData {

    public Quote[] quotes;

    public static class Quote {
        public String quote;
        public Author author;
    }

    public static class Author {
        public String name;
        public String subtitle;
    }
}
