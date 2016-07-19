package ru.yandex.yamblz.callinformer.util.animation;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * Created by root on 7/17/16.
 */
public class AnimationUtils {

    public static final void makeAnimation(View view, Animation animation, final Runnable onStartFunction, int duration, final Runnable ... onEndFunctions) {
        animation.setDuration(duration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(onStartFunction != null) {
                    onStartFunction.run();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // fix flick's bug
                animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                animation.setDuration(1);
                view.startAnimation(animation);

                for(Runnable onEndFunction: onEndFunctions) {
                    onEndFunction.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

}
