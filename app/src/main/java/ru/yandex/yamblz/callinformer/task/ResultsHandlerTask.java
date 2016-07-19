package ru.yandex.yamblz.callinformer.task;

import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yamblz.callinformer.model.SearchResult;
import ru.yandex.yamblz.callinformer.util.GoogleSearchJSONDeserializer;
import ru.yandex.yamblz.callinformer.util.function.Consumer;

/**
 * Created by root on 7/19/16.
 */
public class ResultsHandlerTask extends AsyncTask<String, Void, SearchResult> {

    private final Consumer<SearchResult> onGetResult;
    private final Runnable onNoResult;

    public ResultsHandlerTask(Consumer<SearchResult> onGetResult, Runnable onNoResult) {

        this.onGetResult = onGetResult;
        this.onNoResult = onNoResult;
    }

    @Override
    protected SearchResult doInBackground(String... strings) {
        List<SearchResult> results = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<ArrayList<SearchResult>>() {
                }.getType(), new GoogleSearchJSONDeserializer())
                .create().fromJson(strings[0], new TypeToken<ArrayList<SearchResult>>() {
                }.getType());

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    protected void onPostExecute(SearchResult searchResult) {
        super.onPostExecute(searchResult);
        if(searchResult == null) {
            onNoResult.run();
        } else {
            onGetResult.accept(searchResult);
        }
    }
}
