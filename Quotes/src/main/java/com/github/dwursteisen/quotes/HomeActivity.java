
package com.github.dwursteisen.quotes;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dwursteisen.quotes.model.QuotesData;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

@EActivity(R.layout.activity_main)
public class HomeActivity extends Activity {


    private static final Random RANDOMIZER = new Random();
    public static final String QUOTES_DATA = "quotes.js";

    private int currentQuote = 0;
    private int quoteIndex = 0;

    private QuotesData jsonQuotes;

    @ViewById
    TextView quote;

    @ViewById
    TextView authorName;

    @ViewById
    TextView authorRole;

    @ViewById
    ImageView avatar;

    @AfterViews
    void updateQuoteText() {
        QuotesData.Quote quoteObj = jsonQuotes.quotes[currentQuote];
        quote.setText(quoteObj.quote);
        authorName.setText(quoteObj.author.name);
        authorRole.setText(quoteObj.author.subtitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Reader is = new BufferedReader(new InputStreamReader(this.getAssets().open(QUOTES_DATA)));
            jsonQuotes = new Gson().fromJson(is, QuotesData.class);
        } catch (IOException e) {
            throw new RuntimeException("Damn. Got a problem when opening quotes !", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater();
        return true;
    }

    @Click
    public void nextClicked() {
        currentQuote = secureQuoteIndex(++quoteIndex);
        updateQuoteText();
        System.err.println("next was clicked");
    }

    private int secureQuoteIndex(int quoteIndex) {
        return (Math.abs(quoteIndex) % jsonQuotes.quotes.length + jsonQuotes.quotes.length) % jsonQuotes.quotes.length;
    }

    @Click
    public void previousClicked() {
        currentQuote = secureQuoteIndex(--quoteIndex);
        updateQuoteText();
        System.err.println("previous was clicked");
    }

    @Click
    public void randomClicked() {
        quoteIndex = RANDOMIZER.nextInt(100);
        currentQuote = secureQuoteIndex(quoteIndex);
        updateQuoteText();
        System.err.println("random was clicked");
    }

}
