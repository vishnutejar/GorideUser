package com.rider.ui.utilities;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.rider.R;
import com.rider.database.tables.BookingTable;
import com.rider.ui.MyApplication;

/**
 * Created by Sunil kumar yadav on 24/4/18.
 */

public class RunningBookingHead extends Service {

    static RelativeLayout.LayoutParams params;
    TextView tv_count;
    View v;
    MyApplication myApplication;
    ViewGroup rootViewGroup;
    private WindowManager windowManager;

    public static boolean isRunning (Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if ((context.getPackageName().equals(service.service.getPackageName())) &&
                    (RunningBookingHead.class.getName()).equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    public void setRootViewGroup (ViewGroup rootViewGroup) {
        this.rootViewGroup = rootViewGroup;
    }

    @Override
    public void onCreate () {
        super.onCreate();
        myApplication = (MyApplication) getApplication();
        int count = BookingTable.getInstance().getRunningBookingCount();
        if (count > 0) {

            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            final int width = windowManager.getDefaultDisplay().getWidth();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.bookinghead_layout, null);
            if (myApplication.getRunningBookingHeadListener() != null) {
                myApplication.getRunningBookingHeadListener().RunningBookingStart(this, v);
            }
            tv_count = v.findViewById(R.id.tv_count);
            tv_count.setText(String.valueOf(count));
            v.setOnTouchListener(new View.OnTouchListener() {
                boolean clickHandle = false;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private PointF downpoint = new PointF();

                @Override
                public boolean onTouch (View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.leftMargin;
                            initialY = params.topMargin;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            downpoint.set(event.getRawX(), event.getRawY());
                            clickHandle = true;
                            return true;
                        case MotionEvent.ACTION_UP:
                            if (clickHandle) {
                                if (myApplication.getRunningBookingHeadListener() != null) {
                                    myApplication.getRunningBookingHeadListener().RunningBookingClick();
                                }
                            }
                            clickHandle = false;
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            PointF newPoint = new PointF(event.getRawX(), event.getRawY());
                            double diff = calculateDistance(downpoint, newPoint);
                            if (diff > 10) {
                                clickHandle = false;
                            }
                            int leftMargin = initialX + (int) (event.getRawX() - initialTouchX);
                            int topmargin = initialY + (int) (event.getRawY() - initialTouchY);
                            if (leftMargin > rootViewGroup.getWidth() - v.getWidth()) {
                                leftMargin = rootViewGroup.getWidth() - v.getWidth();
                            } else if (leftMargin < 0) {
                                leftMargin = 0;
                            }
                            if (topmargin > rootViewGroup.getHeight() - v.getHeight()) {
                                topmargin = rootViewGroup.getHeight() - v.getHeight();
                            } else if (topmargin < 0) {
                                topmargin = 0;
                            }
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            params.setMargins(leftMargin, topmargin
                                    , 0, 0);
                            rootViewGroup.updateViewLayout(v, params);
                            return true;
                    }
                    return false;
                }
            });
            if (params == null) {
                params = new RelativeLayout.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.leftMargin = width - dpToPx(70);
                params.topMargin = dpToPx(200);
            }
            if (rootViewGroup != null) {
                rootViewGroup.addView(v, params);
            }
//            windowManager.addView(v, params);

        } else {
            stopSelf();
        }
    }

    public double calculateDistance (PointF p1, PointF p2) {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        return Math.abs(Math.sqrt((dx * dx) + (dy * dy)));
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        if (v != null && rootViewGroup != null) rootViewGroup.removeView(v);
    }

    private int dpToPx (int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public interface RunningBookingHeadListener {
        void RunningBookingStart (RunningBookingHead runningBookingHead, View view);

        void RunningBookingClick();
    }
}
