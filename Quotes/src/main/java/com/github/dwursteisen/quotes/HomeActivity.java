
package com.github.dwursteisen.quotes;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dwursteisen.quotes.analytics.Tracker;
import com.github.dwursteisen.quotes.model.QuotesData;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

@EActivity(R.layout.activity_main)
public class HomeActivity extends Activity {


    private static final Random RANDOMIZER = new Random();

    private static final String QUOTES_DATA = "quotes.js";

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
    private Tracker tracker;

    @AfterViews
    void updateQuoteText() {
        QuotesData.Quote quoteObj = jsonQuotes.quotes[currentQuote];
        quote.setText(quoteObj.quote);
        authorName.setText(quoteObj.author.name);
        authorRole.setText(quoteObj.author.subtitle);

        try {
            if (quoteObj.author.avatar != null) {
                avatar.setImageDrawable(getImage(quoteObj.author.avatar));
            } else {
                avatar.setImageResource(R.drawable.ic_launcher);
            }
        } catch (IOException e) {
            avatar.setImageResource(R.drawable.ic_launcher);
        }
    }

    private Drawable getImage(String filename) throws IOException {
        // get input stream
        InputStream ims = getAssets().open(filename);
        // load image as Drawable
        Drawable d = Drawable.createFromStream(ims, null);

        return d;
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

        tracker = new Tracker(this);
        randomQuote();
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

        tracker.createEvent("home", "next_clicked").andSendIt();
    }

    private int secureQuoteIndex(int quoteIndex) {
        return (Math.abs(quoteIndex) % jsonQuotes.quotes.length + jsonQuotes.quotes.length) % jsonQuotes.quotes.length;
    }

    @Click
    public void previousClicked() {
        currentQuote = secureQuoteIndex(--quoteIndex);
        updateQuoteText();
        System.err.println("previous was clicked");

        tracker.createEvent("home", "previous_clicked").andSendIt();
    }

    @Click
    public void randomClicked() {
        randomQuote();
        updateQuoteText();
        System.err.println("random was clicked");

        tracker.createEvent("home", "random_clicked").andSendIt();
    }

    private void randomQuote() {
        quoteIndex = RANDOMIZER.nextInt(100);
        currentQuote = secureQuoteIndex(quoteIndex);
    }

    @Override
    public void onStart() {
        super.onStart();
        tracker.start(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        tracker.stop(this);
    }

}
