package io.dutchapps.tellgpt;

import java.util.ArrayList;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.annotation.TargetApi;
import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.community.speechrecognition.SpeechRecognition;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
// import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BridgeActivity implements View.OnClickListener {

  private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

  // private static int count = 0;

  @Override
  public void onPause() {
    super.onPause();

    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (FloatingViewService.class.getName().equals(service.service.getClassName())) {
      }
    }

    this.startBubbleService();
    FloatingViewService.show();
    // ((MyApplication) this.getApplication()).startActivityTransitionTimer();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_main);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
      askPermission();
    }

    // if (MainActivity.count == 0) {
    // this.startBubbleService();
    // }

    // MainActivity.count = MainActivity.count + 1;
  }

  public void startBubbleService() {
    if (isMyServiceRunning(FloatingViewService.class) == false) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        startService(new Intent(MainActivity.this, FloatingViewService.class));
        finish();
      } else if (Settings.canDrawOverlays(this)) {
        startService(new Intent(MainActivity.this, FloatingViewService.class));
        finish();
      } else {
        askPermission();
        Toast.makeText(this, "You need System Alert Window Permission to do this",
            Toast.LENGTH_SHORT).show();
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.M)
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    System.out.println(requestCode);
    if (requestCode == SYSTEM_ALERT_WINDOW_PERMISSION) {


      if (!Settings.canDrawOverlays(this)) {
        // You don't have permission
        // checkPermission( );
        System.out.println("222");
      } else {
        System.out.println("3333");
        this.startBubbleService();
        // Do as per your logic
      }

    }

  }

  private void askPermission() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
  }

  @Override
  public void onClick(View v) {
    this.startBubbleService();
  }

  private boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }
}
