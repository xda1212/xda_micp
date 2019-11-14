package com.tuanmai.tools.Utils.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Version : 1.0
 * Author: zzh
 * Created : 2018/6/23
 * Des :
 */
public class ScrollAnimUtils {

    public static void showBottom(View view, int duration) {
        if (view != null && view.getVisibility() != View.VISIBLE) {
            TranslateAnimation anim = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            anim.setDuration(duration);
            view.startAnimation(anim);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideBottom(View view, int duration) {
        if (view != null && view.getVisibility() == View.VISIBLE) {
            TranslateAnimation anim = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            anim.setDuration(duration);
            view.startAnimation(anim);
            view.setVisibility(View.GONE);
        }
    }


    public static void showOrderLayout(View view, int duration) {
        if (view != null && view.getVisibility() != View.VISIBLE) {
            TranslateAnimation anim = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            anim.setDuration(duration);
            view.startAnimation(anim);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideOrderLayout(View view, int duration) {
        if (view != null && view.getVisibility() != View.GONE) {
            TranslateAnimation anim = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f);
            anim.setDuration(duration);
            view.startAnimation(anim);
            view.setVisibility(View.GONE);
        }
    }
}
