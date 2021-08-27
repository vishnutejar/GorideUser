package com.base;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.models.DeviceScreenModel;
import com.quickzetuser.R;
import com.quickzetuser.ui.MyApplication;
import com.utilities.Utils;


/**
 * Created by bitu on 15/8/17.
 */

public abstract class
BaseActivity extends AppCompatActivity {
    private FragmentManager fm;
    private Bundle savedInstanceState;
    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        fm = getSupportFragmentManager();
        beforeSetContentView();
        setContentView(getLayoutResourceId());
        initializeComponent();
    }


    public FragmentManager getFm() {
        return fm;
    }

    public FragmentTransaction getNewFragmentTransaction() {
        return getFm().beginTransaction();
    }

    public void clearFragmentBackStack() {
        clearFragmentBackStack(0);
    }

    public int getFragmentCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    public Fragment getFragmentByTag(String tag) {
        return getFm().findFragmentByTag(tag);
    }

    public void clearFragmentBackStack(int pos) {
        if (fm.getBackStackEntryCount() > pos) {
            try {
                fm.popBackStack(fm.getBackStackEntryAt(pos).getId(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (IllegalStateException e) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment baseFragment = getFm().findFragmentById(getFragmentContainerResourceId());
        if (baseFragment != null && baseFragment instanceof BaseFragment) {
            if (((BaseFragment) baseFragment).handleOnBackPress()) {
                return;
            }
        }
        super.onBackPressed();
    }

    public BaseFragment getLatestFragment() {

        Fragment fragment = fm.findFragmentById(getFragmentContainerResourceId());
        if (fragment != null && fragment instanceof BaseFragment) {
            return ((BaseFragment) fragment);
        }

        return null;
    }

    public void changeFragment(Fragment f, boolean addBackStack,
                               boolean clearAll, int pos, int enterAnim, int exitAnim,
                               int enterAnimBackStack, int exitAnimBackStack, boolean isReplace) {
        if (getFragmentContainerResourceId() == -1) return;
        if (clearAll) {
            clearFragmentBackStack(pos);
        }
        if (f != null) {
            try {
                FragmentTransaction ft = getNewFragmentTransaction();
                ft.setCustomAnimations(enterAnim, exitAnim, enterAnimBackStack, exitAnimBackStack);
                if (isReplace) {
                    ft.replace(getFragmentContainerResourceId(), f, f.getClass().getSimpleName());
                } else {
                    ft.add(getFragmentContainerResourceId(), f, f.getClass().getSimpleName());
                }
                if (addBackStack) {
                    ft.addToBackStack(f.getClass().getSimpleName());
                }
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changeFragment(Fragment f, boolean addBackStack,
                               boolean clearAll, int pos, boolean isReplace) {
        if (getFragmentContainerResourceId() == -1) return;
        if (clearAll) {
            clearFragmentBackStack(pos);
        }
        if (f != null) {
            try {
                FragmentTransaction ft = getNewFragmentTransaction();
                if (isReplace) {
                    ft.replace(getFragmentContainerResourceId(), f, f.getClass().getSimpleName());
                } else {
                    ft.add(getFragmentContainerResourceId(), f, f.getClass().getSimpleName());
                }
                if (addBackStack) {
                    ft.addToBackStack(f.getClass().getSimpleName());
                }
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void beforeSetContentView() {

    }

    public abstract int getLayoutResourceId();

    public int getFragmentContainerResourceId() {
        return -1;
    }

    public abstract void initializeComponent();

    public void onCreateViewFragment(BaseFragment baseFragment) {
    }


    public void onDestroyViewFragment(BaseFragment baseFragment) {
    }


    public boolean isValidString(String data) {
        return data != null && !data.trim().isEmpty();
    }

    public void printLog(String msg) {
        if (Utils.isDebugBuild(this) && msg == null) return;
        Log.e(getClass().getSimpleName(), msg);
    }

    public void showToast(String message) {
        if (message == null || message.trim().isEmpty()) return;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public synchronized void hideKeyboard() {
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        hideKeyboard(view);
    }

    public synchronized void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean isValidObject(Object object) {
        return object != null;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }


    public void shareApp() {
        final String appName = getString(R.string.app_name);
        String shareBodyText = "Hey! Download the " + appName +
                " App, and sell and purchase item," +
                " https://play.google.com/store/apps/details?id="
                + getPackageName();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
        try {
            startActivity(Intent.createChooser(shareIntent, "Share"));
        } catch (ActivityNotFoundException e) {
            showToast("No Apps found please install app from PlayStore.");
        }
    }

    public void showCustomToast(String message) {
        if (!isFinishing()) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_layout, null);
            TextView toastTextView = layout.findViewById(R.id.tv_toast);
            toastTextView.setText(message);
//        toastImageView.setImageResource(R.drawable.ic_launcher);
            if (toast != null)
                toast.cancel();
            toast = new Toast(MyApplication.getInstance());
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0,
                    DeviceScreenModel.getInstance().convertDpToPixel(70));
            /**
             * | Gravity.FILL_HORIZONTAL
             * if you want width full
             */
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }

}
