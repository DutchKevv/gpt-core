package io.dutchapps.tellgpt;

import android.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.community.speechrecognition.SpeechRecognition;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
// import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class FloatingViewService extends Service implements View.OnClickListener {

  static View collapsedView;

  private WindowManager mWindowManager;
  private View mFloatingView;
  private View expandedView;

  public FloatingViewService() {
  }

  public static void show() {
    if (collapsedView != null) {
      collapsedView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    // getting the widget layout from xml using layout inflater
    mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

    // setting the layout parameters
    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);

    // getting windows services and adding the floating view to it
    mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    mWindowManager.addView(mFloatingView, params);

    // getting the collapsed and expanded view from the floating view
    collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
    expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

    // adding click listener to close button and expanded view
    mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
    expandedView.setOnClickListener(this);

    // adding an touchlistener to make drag movement of the floating widget
    mFloatingView.findViewById(R.id.buttonClose).setOnTouchListener(new View.OnTouchListener() {
      private int initialX;
      private int initialY;
      private float initialTouchX;
      private float initialTouchY;

      @Override
      public boolean onTouch(View v, MotionEvent event) {


        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            initialX = params.x;
            initialY = params.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            return true;

          case MotionEvent.ACTION_UP:
            // collapsedView.setVisibility(View.GONE);
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // when the drag is ended switching the state of the widget

            // expandedView.setVisibility(View.VISIBLE);
            return true;

          case MotionEvent.ACTION_MOVE:
            // this code is helping the widget to move around the screen with fingers
            params.x = initialX + (int) (event.getRawX() - initialTouchX);
            params.y = initialY + (int) (event.getRawY() - initialTouchY);
            mWindowManager.updateViewLayout(mFloatingView, params);
            return true;
        }
        return false;
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mFloatingView != null)
      mWindowManager.removeView(mFloatingView);
  }

  /**
   * @param v
   */
  @Override
  public void onClick(View v) {

    System.out.println("22222222222222222");
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);

    collapsedView.setVisibility(View.GONE);

    // TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    // stackBuilder.addNextIntentWithParentStack(intent);
    // // Get the PendingIntent containing the entire back stack
    // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
    //     PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

    // switch (v.getId()) {
    // case R.id.layoutExpanded:
    // //switching views
    // collapsedView.setVisibility(View.VISIBLE);
    // expandedView.setVisibility(View.GONE);
    // break;

    // // case R.id.buttonClose:
    // // //closing the widget
    // // stopSelf();
    // // break;
    // }
  }

  private boolean applicationInForeground() {
    ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
    boolean isActivityFound = false;

    if (services.get(0).processName
        .equalsIgnoreCase(getPackageName())
        && services.get(0).importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
      isActivityFound = true;
    }

    return isActivityFound;
  }
}
