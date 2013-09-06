package com.github.dwursteisen.quotes.analytics;

import android.app.Activity;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * Created by david.wursteisen on 06/09/13.
 */
public class Tracker {

    private final Context context;


    public Tracker(Context context) {
        this.context = context;
    }

    public Tracker start(Activity activity) {
        EasyTracker.getInstance(context).activityStart(activity);  // Add this method.
        return this;
    }

    public Tracker stop(Activity activity) {
        EasyTracker.getInstance(context).activityStop(activity);  // Add this method.
        return this;
    }

    public EventBuilder createEvent(String category, String action) {
        return new EventBuilder(context, category, action);
    }


    public static class EventBuilder {

        private final Context context;
        private final String category;
        private final String action;
        private final String label;
        private final Long value;

        public EventBuilder(Context context, String category, String action) {
            this(context, category, action, null, null);
        }

        public EventBuilder(Context context, String category, String action, String label, Long value) {
            this.context = context;
            this.category = category;
            this.action = action;
            this.label = label;
            this.value = value;
        }

        public void andSendIt() {
            EasyTracker easyTracker = EasyTracker.getInstance(context);
            if (easyTracker == null) {
                return;
            }

            // MapBuilder.createEvent().build() returns a Map of event fields and values
            // that are set and sent with the hit.
            easyTracker.send(MapBuilder
                    .createEvent(category,     // Event category (required)
                            action,  // Event action (required)
                            label,   // Event label
                            value)            // Event value
                    .build()
            );
        }


    }
}
