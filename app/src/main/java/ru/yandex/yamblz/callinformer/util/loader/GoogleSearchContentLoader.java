package ru.yandex.yamblz.callinformer.util.loader;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by root on 7/16/16.
 */
public class GoogleSearchContentLoader extends ContentLoader {

    private static final String API_KEY = "AIzaSyD8AOmlfNXNkoLPWCISxJoATMyXsrg884Y";
    private static final String APP_ID = "015745406250162702014:hbrkjx0ro1m";
    private static final String FIELDS = "items(title,snippet,link)";

    private final String URL;

    private final Context context;
    private final String query;
    private final Response.Listener<String> onResponse;
    private final Response.ErrorListener onError;

    private GoogleSearchContentLoader(Builder builder) {
        super(builder.context);
        this.context = builder.context;
        this.query = builder.query;
        this.onResponse = builder.onResponse;
        this.onError = builder.onError;

        URL = new Uri.Builder().scheme("https")
                .authority("www.googleapis.com")
                .appendPath("customsearch")
                .appendPath("v1")
                .appendQueryParameter("key", API_KEY)
                .appendQueryParameter("cx", APP_ID)
                .appendQueryParameter("fields", FIELDS)
                .appendQueryParameter("q", query)
                .build().toString();

    }

    @Override
    public void load() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, onResponse, onError);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        requestQueue.start();
    }

    public static class Builder {

        private final Context context;
        private final String query;

        private Response.Listener<String> onResponse = (response -> {});
        private Response.ErrorListener onError = (error -> {});

        public Builder(Context context, String query) {
            this.context = context;
            this.query = query;
        }

        public Builder onResponse(Response.Listener<String> onResponse) {
            this.onResponse = onResponse;
            return this;
        }

        public Builder onError(Response.ErrorListener onError) {
            this.onError = onError;
            return this;
        }

        public GoogleSearchContentLoader build() {
            return new GoogleSearchContentLoader(this);
        }

    }

}
