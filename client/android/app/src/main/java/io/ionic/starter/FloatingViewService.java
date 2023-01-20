package io.dutchapps.tellgpt;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.speech.tts.TextToSpeech;
import android.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.community.speechrecognition.SpeechRecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FloatingViewService extends Service implements View.OnClickListener {

  static FloatingViewService instance;
  public static final Integer RecordAudioRequestCode = 1;

  TextToSpeech textToSpeech;

  private View loadingView;
  private View recordingView;
  private View collapsedView;
  private WindowManager mWindowManager;
  private View mFloatingView;
  private View expandedView;
  private TextView expandedText;
  private SpeechRecognizer speechRecognizer;
  private EditText editText;
  private ImageView micButton;

  public FloatingViewService() {
    System.out.println("INIT INIT INIT INIT #$3$###$#");
    instance = this;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();

      System.out.println("onCreateonCreateonCreateonCreateonCreat");

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
    loadingView = mFloatingView.findViewById(R.id.layoutLoading);
    recordingView = mFloatingView.findViewById(R.id.layoutRecording);
    collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
    expandedView = mFloatingView.findViewById(R.id.layoutExpanded);
    // expandedText = (TextView) mFloatingView.findViewById(R.id.expandedText);

    // adding click listener to close button and expanded view
    mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
    expandedView.setOnClickListener(this);

    ///////// TEMP TEMP TEMP

    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
      @Override
      public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
          textToSpeech.setLanguage(Locale.UK);
        }
      }
    });

    // if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)
    ///////// != PackageManager.PERMISSION_GRANTED) {
    // // requestRecordAudioPermission();
    // }

    // editText = mFloatingView.findViewById(R.id.text);
    // micButton = mFloatingView.findViewById(R.id.button);


    if(SpeechRecognizer.isRecognitionAvailable(this)) {
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    } else {
      // SOME SORT OF ERROR
    }

    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());




    speechRecognizer.setRecognitionListener(new RecognitionListener() {
      @Override
      public void onReadyForSpeech(Bundle bundle) {

      }

      @Override
      public void onBeginningOfSpeech() {
        // editText.setText("");
        // editText.setHint("Listening...");
      }

      @Override
      public void onRmsChanged(float v) {

      }

      @Override
      public void onBufferReceived(byte[] bytes) {

      }

      @Override
      public void onEndOfSpeech() {

      }

      @Override
      public void onError(int i) {

      }

      @Override
      public void onResults(Bundle bundle) {
        // micButton.setImageResource(R.drawable.splash);
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        makeCall(data.get(0));
        // editText.setText(data.get(0));
      }

      @Override
      public void onPartialResults(Bundle bundle) {

      }

      @Override
      public void onEvent(int i, Bundle bundle) {

      }
    });

    // micButton.setOnTouchListener(new View.OnTouchListener() {
    // @Override
    // public boolean onTouch(View view, MotionEvent motionEvent) {
    // if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
    // speechRecognizer.stopListening();
    // }
    // if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
    // micButton.setImageResource(R.drawable.splash);
    // speechRecognizer.startListening(speechRecognizerIntent);
    // }
    // return false;
    // }
    // });

    ///////// TEMP TEMP TEMP

    // adding an touchlistener to make drag movement of the floating widget
    mFloatingView.findViewById(R.id.layoutExpanded).setOnTouchListener(new View.OnTouchListener() {
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
            toggleView("collapsed");
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

    // adding an touchlistener to make drag movement of the floating widget
    mFloatingView.findViewById(R.id.buttonClose).setOnTouchListener(new View.OnTouchListener() {
      private int initialX;
      private int initialY;
      private float initialTouchX;
      private float initialTouchY;
      private View view;

      // final Handler handler = new Handler();
      // Runnable mLongPressed = new Runnable() {
      //   public void run() {
      //     System.out.println("LOOOOOOOOOOOOOOOOOOOOOOOOONG");
      //     Intent intent = new Intent(view.getContext(), MainActivity.class);
      //     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //     startActivity(intent);
      //   }
      // };


      @Override
      public boolean onTouch(View v, MotionEvent event) {
        // view = v;
        // if (event.getAction() == MotionEvent.ACTION_DOWN)
        //   handler.postDelayed(mLongPressed, 300);
        // if ((event.getAction() == MotionEvent.ACTION_UP))
        //   handler.removeCallbacks(mLongPressed);

        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            initialX = params.x;
            initialY = params.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            return true;

          case MotionEvent.ACTION_UP:
            // handler.removeCallbacks(mLongPressed);
            toggleView("recording");

            speechRecognizer.startListening(speechRecognizerIntent);
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
    speechRecognizer.destroy();

    if (mFloatingView != null)
      mWindowManager.removeView(mFloatingView);
  }

  /**
   * @param v
   */
  @Override
  public void onClick(View v) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);

    collapsedView.setVisibility(View.GONE);
  }

  // @Override
  // public void onRequestPermissionsResult(int requestCode, @NonNull String[]
  // permissions, @NonNull int[] grantResults) {
  // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  // if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
  // if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
  // Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
  // }
  // }

  private void makeCall(String text) {
    this.toggleView("loading");

    try {
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httppost = new HttpPost("https://real-estate.templatejump.com/api/speak");

      try {
        // Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("prompt", text));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJSON = new JSONObject(responseBody);

        this.toggleView("expanded");

        // expandedText.setText(responseJSON.getString("response"));
        textToSpeech.speak(responseJSON.getString("response"), TextToSpeech.QUEUE_FLUSH, null);

      } catch (ClientProtocolException e) {
        // TODO Auto-generated catch block
      } catch (IOException e) {
        // TODO Auto-generated catch block
      }
    } catch (JSONException e) {
      // some exception handler code.
    }

  }

  public void show() {
    toggleView("");

    collapsedView.setVisibility(View.VISIBLE);
    // collapsedView.setVisibility(View.GONE);
  }


  public void hide() {
    toggleView("");

    collapsedView.setVisibility(View.INVISIBLE);
    // collapsedView.setVisibility(View.GONE);
  }

  public void toggleView(String viewName) {
    if (textToSpeech != null) {
      textToSpeech.stop();
    }

    if (loadingView != null) {
      loadingView.setVisibility(View.INVISIBLE);
      loadingView.setVisibility(View.GONE);
    }

    if (collapsedView != null) {
      collapsedView.setVisibility(View.INVISIBLE);
      collapsedView.setVisibility(View.GONE);
    }

    if (expandedView != null) {
      expandedView.setVisibility(View.INVISIBLE);
      expandedView.setVisibility(View.GONE);
    }

    if (recordingView != null) {
      recordingView.setVisibility(View.INVISIBLE);
      recordingView.setVisibility(View.GONE);
    }


    if (viewName == "loading") {
      loadingView.setVisibility(View.VISIBLE);
    }

    if (viewName == "collapsed") {
      collapsedView.setVisibility(View.VISIBLE);
    }

    if (viewName == "expanded") {
      expandedView.setVisibility(View.VISIBLE);
    }

    if (viewName == "recording") {
      recordingView.setVisibility(View.VISIBLE);
    }
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
