package ru.yandex.yamblz.callinformer.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.yandex.yamblz.callinformer.R;
import ru.yandex.yamblz.callinformer.model.SearchResult;
import ru.yandex.yamblz.callinformer.util.GoogleSearchJSONDeserializer;
import ru.yandex.yamblz.callinformer.util.StringUtils;
import ru.yandex.yamblz.callinformer.util.animation.AnimationUtils;
import ru.yandex.yamblz.callinformer.util.loader.GoogleSearchContentLoader;

/**
 * Created by root on 7/16/16.
 */
public class CallingWidget {

    private static final int MAX_SNIPPET_LENGTH = 50;
    private static final int MAX_TITLE_LENGTH = 20;

    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    private ViewGroup widgetLayout, contentLayout;
    private Context context;

    private boolean isVisible = false;
    private float lastTouchX;
    private String phone;

    private AtomicBoolean isAnimating = new AtomicBoolean(false);

    public CallingWidget(Context context) {

        this.context = context;

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        widgetLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.call_widget_fragment, null);

        contentLayout = (ViewGroup) widgetLayout.getChildAt(0);

        setOnTouchListener();

    }

    private void setOnTouchListener() {
        contentLayout.setOnTouchListener((view, motionEvent) -> {
            final int action = motionEvent.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    final float x = motionEvent.getX();
                    lastTouchX = x;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    final float x = motionEvent.getX();
                    final float dx = x - lastTouchX;

                    contentLayout.setX(contentLayout.getX() + dx);
                    lastTouchX = x;

                    contentLayout.invalidate();

                    break;
                }

                case MotionEvent.ACTION_UP: {
                    if(Math.abs(contentLayout.getX()) > contentLayout.getWidth() / 3) {
                        close();
                    } else {
                        backToHome();
                    }
                }

            }
            return true;
        });
    }

    private void startAnimation() {
        isAnimating.set(true);
    }

    private void endAnimation() {
        isAnimating.set(false);
    }

    private void backToHome() {
        if(isAnimating.get()) {
            return;
        }
        AnimationUtils.makeAnimation(contentLayout, new TranslateAnimation(contentLayout.getX(),
                -contentLayout.getX(), 0, 0), 500, this::startAnimation, this::resetView);
    }

    public void show(String phone) {

        this.phone = phone;

        if(isVisible) {
            throw new IllegalStateException("Widget has already shown!");
        }

        fillViews();
        loadData();
        windowManager.addView(widgetLayout, params);

        AnimationUtils.makeAnimation(contentLayout, new AlphaAnimation(0, 1), 500, this::startAnimation, this::endAnimation);

        isVisible = true;

    }

    private void resetView() {
        contentLayout.setX(0);
        endAnimation();
    }

    private void loadData() {

        new GoogleSearchContentLoader.Builder(context, phone).onResponse((response) -> showData(response))
                .onError((error) -> {
                    Log.d("callingtest", error.getLocalizedMessage());
                    showLoadingError();
                }).build().load();
    }

    private void showData(String response) {

        List<SearchResult> results = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<ArrayList<SearchResult>>() {
                }.getType(), new GoogleSearchJSONDeserializer())
                .create().fromJson(response, new TypeToken<ArrayList<SearchResult>>() {
        }.getType());

        if(results.isEmpty()) {
            showNoResults();
        } else {
            showResult(results.get(0));
        }

    }

    private void showResult(SearchResult searchResult) {
        TextView title = (TextView) widgetLayout.findViewById(R.id.result_title);
        title.setText(StringUtils.trim(searchResult.getTitle(), MAX_TITLE_LENGTH));

        TextView snippet = (TextView) widgetLayout.findViewById(R.id.result_text);
        snippet.setText(StringUtils.trim(searchResult.getSnippet(), MAX_SNIPPET_LENGTH));

        Button openBrowser = (Button) widgetLayout.findViewById(R.id.open_browser);
        openBrowser.setOnClickListener((v) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchResult.getLink()));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
            close();
        });

        widgetLayout.findViewById(R.id.loading_layout).setVisibility(View.GONE);
        widgetLayout.findViewById(R.id.result_layout).setVisibility(View.VISIBLE);

    }

    private void showNoResults() {
        widgetLayout.findViewById(R.id.loading_layout).setVisibility(View.GONE);
        widgetLayout.findViewById(R.id.no_results_layout).setVisibility(View.VISIBLE);
    }

    private void showLoadingError() {
        widgetLayout.findViewById(R.id.loading_layout).setVisibility(View.GONE);
        widgetLayout.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
    }

    public void close() {
        if(isAnimating.get()) {
            return;
        }
        if(isVisible) {
            isVisible = false;
            AnimationUtils.makeAnimation(contentLayout, new TranslateAnimation(contentLayout.getX(),
                    contentLayout.getX() > 0 ? 1000 : -1000, 0, 0), 500, this::startAnimation,
                    this::removeFromManager);
        }
    }

    private void removeFromManager() {
        windowManager.removeView(widgetLayout);
        endAnimation();
    }

    private void fillViews() {
        TextView widgetTitle = (TextView) widgetLayout.findViewById(R.id.widget_title);
        widgetTitle.setText(Html.fromHtml(context.getString(R.string.widget_title, phone)));

        ImageButton closeButton = (ImageButton) widgetLayout.findViewById(R.id.close);
        closeButton.setOnClickListener((v) -> close());

        Button tryAgainButton = (Button) widgetLayout.findViewById(R.id.try_again);
        tryAgainButton.setOnClickListener((v) -> {
            widgetLayout.findViewById(R.id.error_layout).setVisibility(View.GONE);
            widgetLayout.findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
            loadData();
        });

    }

}
