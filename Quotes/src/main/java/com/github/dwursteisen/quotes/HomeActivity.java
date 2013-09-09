
package com.github.dwursteisen.quotes;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.b50.gesticulate.SwipeDetector;
import com.github.dwursteisen.quotes.analytics.Tracker;
import com.github.dwursteisen.quotes.model.QuotesData;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.annotations.ViewById;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

@EActivity(R.layout.activity_main)
public class HomeActivity extends Activity {


    private static final String ACTIVITY_TAG = "HomeActivity";

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

    private GestureDetector detector;

    @AfterViews
    void initAfterViews() {
        detector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                SwipeDetector detector = new SwipeDetector(e1, e2, velocityX, velocityY);
                if (detector.isDownSwipe()) {
                    Log.d(ACTIVITY_TAG, "down swipe");
                    tracker.createEvent("quote", "down_swipe").andSendIt();
                } else if (detector.isLeftSwipe()) {
                    Log.d(ACTIVITY_TAG, "left swipe");
                    tracker.createEvent("quote", "left_swipe").andSendIt();

                } else if (detector.isRightSwipe()) {
                    Log.d(ACTIVITY_TAG, "right swipe");
                    tracker.createEvent("quote", "right_swipe").andSendIt();

                } else if (detector.isUpSwipe()) {
                    Log.d(ACTIVITY_TAG, "up swipe");
                    tracker.createEvent("quote", "up_swipe").andSendIt();
                }
                return false;
            }
        });

        quote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        updateQuoteText();
    }



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
    public void quoteClicked() {
        spyEvent("quote");
    }

    @LongClick
    public void quoteLongClicked() {
        spyEvent("quote_long");
    }

    @Click
    public void avatarClicked() {
        spyEvent("avatar");
    }

    @LongClick
    public void avatarLongClicked() {
        spyEvent("avatar_long");
    }

    @Click
    public void authorNameClicked() {
        spyEvent("author_name");
    }

    @LongClick
    public void authorNameLongClicked() {
        spyEvent("author_name_long");
    }

    @Click
    public void nextClicked() {
        currentQuote = secureQuoteIndex(++quoteIndex);
        updateQuoteText();
        spyEvent("next");
    }

    @LongClick
    public void nextLongClicked() {
        spyEvent("next_long");
    }

    private int secureQuoteIndex(int quoteIndex) {
        return (Math.abs(quoteIndex) % jsonQuotes.quotes.length + jsonQuotes.quotes.length) % jsonQuotes.quotes.length;
    }

    @Click
    public void previousClicked() {
        currentQuote = secureQuoteIndex(--quoteIndex);
        updateQuoteText();
        spyEvent("previous");
    }

    @LongClick
    public void previousLongClicked() {
        spyEvent("previous_long");
    }

    @Click
    public void randomClicked() {
        randomQuote();
        updateQuoteText();
        spyEvent("random");
    }

    @LongClick
    public void randomLongClicked() {
        spyEvent("random");
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

    private void spyEvent(String event) {
        Log.d("Event", event + " was clicked");
        tracker.createEvent("home", event + "_clicked").andSendIt();
    }

}
