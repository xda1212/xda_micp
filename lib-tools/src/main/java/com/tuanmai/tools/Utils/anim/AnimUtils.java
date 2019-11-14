package com.tuanmai.tools.Utils.anim;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public final class AnimUtils {

    public static Animation getViewAnimation() {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(500);
        return animation;
    }

    public static LayoutAnimationController getListAnim() {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);
        set.addAnimation(animation);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        return controller;
    }

    public static LayoutAnimationController getListAnimLossTime() {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(100);
        set.addAnimation(animation);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(200);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        return controller;
    }

    public static LayoutAnimationController getListAnimAlpha() {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(60);
        set.addAnimation(animation);
        animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(120);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.5f);
        return controller;
    }


    /**
     * 开启View闪烁效果
     */
    public static void startFlick(final View view) {
        if (null == view) return;
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(alphaAnimation);
    }

    /**
     * 取消View闪烁效果
     */
    public static void stopFlick(View view) {
        if (null != view) {
            view.clearAnimation();
        }
    }

    //=============================================================================
    public static void clickAnim(final View view, final AnimUtilsListner listner) {
        if (null != view) {
            ScaleAnimation scaleAnim;
            if (view.getWidth() > 200) {
                scaleAnim = new ScaleAnimation(0.9f, 1.02f, 0.9f, 1.1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
            } else {
                scaleAnim = new ScaleAnimation(0.9f, 1.15f, 0.9f, 1.15f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
            }
            scaleAnim.setDuration(200);
            scaleAnim.setInterpolator(new LinearInterpolator());
            scaleAnim.setFillBefore(true);
            scaleAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (listner != null) listner.animEnd(view);
                }
            });
            view.startAnimation(scaleAnim);
        } else {
            if (listner != null) listner.animEnd(view);
        }
    }


    public static void chooseAnim(final View view, final AnimUtilsListner Listener) {
        if (null != view) {
            ScaleAnimation scaleAnim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnim.setDuration(200);
            scaleAnim.setInterpolator(new LinearInterpolator());
            scaleAnim.setFillBefore(true);
            scaleAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (Listener != null) Listener.animEnd(view);
                }
            });
            view.startAnimation(scaleAnim);
        } else {
            if (Listener != null) Listener.animEnd(view);
        }
    }


    public static void showImgAnim(View view) {
        if (null != view) {
            ScaleAnimation scaleAnim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnim.setDuration(300);
            //scaleAnim.setInterpolator(new AccelerateInterpolator());
            scaleAnim.setInterpolator(new LinearInterpolator());
            scaleAnim.setFillBefore(true);
            view.startAnimation(scaleAnim);
        }
    }


    public static void clickRotateAnim(final View view, final AnimUtilsListner Listener) {
        if (null != view) {
            RotateAnimation rotateAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(200);
            rotateAnim.setInterpolator(new LinearInterpolator());
            rotateAnim.setFillBefore(true);
            rotateAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (Listener != null) Listener.animEnd(view);
                }
            });
            view.startAnimation(rotateAnim);
        } else {
            if (Listener != null) Listener.animEnd(view);
        }
    }

    public static void clickRotateAnim(final View view, int angle, int duration, final AnimUtilsListner Listener) {
        if (null != view) {
            RotateAnimation rotateAnim = new RotateAnimation(0, angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setInterpolator(new LinearInterpolator());
            rotateAnim.setFillBefore(true);
            rotateAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (Listener != null) Listener.animEnd(view);
                }
            });
            view.startAnimation(rotateAnim);
        } else {
            if (Listener != null) Listener.animEnd(view);
        }
    }

    public static void clickRotateAnim(final View view, int angle, final AnimUtilsListner Listener) {
        if (null != view) {
            RotateAnimation rotateAnim = new RotateAnimation(0, angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(400);
            rotateAnim.setInterpolator(new LinearInterpolator());
            rotateAnim.setFillBefore(true);
            rotateAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (Listener != null) Listener.animEnd(view);
                }
            });
            view.startAnimation(rotateAnim);
        } else {
            if (Listener != null) Listener.animEnd(view);
        }
    }

    public static void rotateHalfCircle(final View view, final AnimUtilsListner Listener) {
        if (null != view) {
            RotateAnimation rotateAnim = new RotateAnimation(0, 180,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(200);
            rotateAnim.setInterpolator(new LinearInterpolator());
            rotateAnim.setFillAfter(true);
            rotateAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (Listener != null) Listener.animEnd(view);
                }
            });
            view.startAnimation(rotateAnim);
        } else {
            if (Listener != null) Listener.animEnd(view);
        }
    }

    public static void deleteTranslateAnim(final View view, final AnimUtilsListner Listener,
                                           float fromY, float toY) {
        if (null != view) {
            TranslateAnimation translateAnim = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, fromY,
                    TranslateAnimation.RELATIVE_TO_SELF, toY);
            translateAnim.setDuration(300);
            translateAnim.setFillBefore(true);
            translateAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (Listener != null) Listener.animEnd(view);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(translateAnim);
        } else {
            if (Listener != null) Listener.animEnd(view);
        }
    }


    public static void setBombAnim(final View view, final AnimUtilsListner Listener, float startX, float endX) {
        if (null != view) {
            TranslateAnimation translateAnim = new TranslateAnimation(startX, endX, 0.0f, 0f);
            view.startAnimation(translateAnim);
            translateAnim.setDuration(350);
            translateAnim.setInterpolator(new OvershootInterpolator());
            translateAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (Listener != null) Listener.animEnd(view);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(translateAnim);
        } else {
            if (Listener != null) Listener.animEnd(view);
        }
    }

    public interface AnimUtilsListner {
        void animEnd(View view);
    }
}
