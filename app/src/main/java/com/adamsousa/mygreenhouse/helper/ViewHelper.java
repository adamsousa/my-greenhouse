package com.adamsousa.mygreenhouse.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adamsousa.mygreenhouse.R;

public final class ViewHelper {

    public static final int VALUE_OVER_MAX = 1;
    public static final int VALUE_UNDER_MIN = 2;

    public static void hideKeyboard(Activity activity) {
        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.findViewById(android.R.id.content).getRootView().getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void showProgress(Context context, View progressView, final boolean show) {
        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public static FragmentTransaction addFragmentInFragment(Fragment currentFragment, Fragment newFragment, int containerId) {
        FragmentTransaction transaction = null;
        if (currentFragment.getFragmentManager() != null) {
            transaction = currentFragment.getFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);
            transaction.replace(containerId, newFragment);
            transaction.addToBackStack(null);
            return transaction;
        }
        return null;
    }

    public static int calculateShowWarning(double value, int maxValue, int minValue) {
        if (value < minValue) {
            return 2;
        } else if (value > maxValue) {
            return 1;
        }
        return 0;
    }
}
