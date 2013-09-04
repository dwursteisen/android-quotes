
package com.github.dwursteisen.quotes;

import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import java.util.Random;

@EActivity(R.layout.activity_main)
public class HomeActivity extends Activity {

    private static final String[] QUOTES = {
            "His ignorance is encyclopedic",
            "Political correctness is tyranny with manners.",
            "Sex and religion are closer to each other than either might prefer."
    };

    private static final Random RANDOMIZER = new Random();
    private static final int MAX_QUOTE_INDEX = 1000;

    private int currentQuote = 0;
    private int quoteIndex = 0;

    @ViewById
    TextView quote;


    @AfterViews
    void updateQuoteText() {
        quote.setText(QUOTES[currentQuote]);
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
        return (Math.abs(quoteIndex) % QUOTES.length + QUOTES.length) % QUOTES.length;
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
