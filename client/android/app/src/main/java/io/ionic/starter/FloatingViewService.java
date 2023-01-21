package io.dutchapps.tellgpt;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

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
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
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

public class FloatingViewService extends Service
    implements View.OnClickListener, android.speech.tts.TextToSpeech.OnInitListener {

  static FloatingViewService instance;
  public static final Integer RecordAudioRequestCode = 1;

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
  private int initialX;
  private int initialY;
  private float initialTouchX;
  private float initialTouchY;
  private android.speech.tts.TextToSpeech tts = null;
  private Context context;
  private int initializationStatus;

  public FloatingViewService() {
    System.out.println("INIT INIT INIT INIT #$3$###$#");
    instance = this;
  }

  @Override
  public void onInit(int status) {
    this.initializationStatus = status;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    System.out.println("onCreateonCreateonCreateonCreateonCreat");

    tts = new android.speech.tts.TextToSpeech(this.getApplicationContext(), this);

    // getting the widget layout from xml using layout inflater
    mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

    // setting the layout parameters
    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
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
    // mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
    mFloatingView.setOnClickListener(this);
    expandedView.setOnClickListener(this);

    ///////// TEMP TEMP TEMP
    // editText = mFloatingView.findViewById(R.id.text);
    // micButton = mFloatingView.findViewById(R.id.button);

    if (SpeechRecognizer.isRecognitionAvailable(this)) {
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    } else {
      Toast.makeText(this, "SpeechToText is unavailable.", Toast.LENGTH_LONG);
      // return;
    }

    Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
    }
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);


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

    speechRecognizer.startListening(recognizerIntent);

    ///////// TEMP TEMP TEMP

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
      public void run() {
        Intent intent = new Intent(mFloatingView.getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

      }
    };

    // adding an touchlistener to make drag movement of the floating widget
    mFloatingView.setOnTouchListener(new View.OnTouchListener() {
      private int initialX;
      private int initialY;
      private float initialTouchX;
      private float initialTouchY;

      @Override
      public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN)
          handler.postDelayed(mLongPressed, 300);
        if ((event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_UP))
          handler.removeCallbacks(mLongPressed);

        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            initialX = params.x;
            initialY = params.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            return true;

          case MotionEvent.ACTION_UP:
            handler.removeCallbacks(mLongPressed);
            System.out.println("layoutExpanded UP UP UP");
            toggleView("recording");

            // speechRecognizer.startListening(speechRecognizerIntent);
            return true;

          case MotionEvent.ACTION_MOVE:
            System.out.println("layoutExpanded UP UP UP");
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

  @Override
  public void onClick(View v) {
    // Intent intent = new Intent(this, MainActivity.class);
    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // startActivity(intent);

    // collapsedView.setVisibility(View.INVISIBLE);

    // handler.removeCallbacks(mLongPressed);

    tts.stop();

    System.out.println("onClickonClickonClickonClickonClickonClick");

    // if (SpeechRecognizer.isRecognitionAvailable(this)) {
    //   speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    // } else {
    //   Toast.makeText(this, "SpeechToText is unavailable.", Toast.LENGTH_LONG);
    //   return;
    // }

    // toggleView("recording");

    // speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
    // speechRecognizer.setRecognitionListener(this);

    Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
        .getPackage().getName());
    voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    voice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

    speechRecognizer.startListening(voice);
  }

  private void speak(String text) {
    tts.setOnUtteranceProgressListener(
        new UtteranceProgressListener() {
          @Override
          public void onStart(String utteranceId) {
          }

          @Override
          public void onDone(String utteranceId) {
            System.out.println("onDoneonDoneonDoneonDoneonDoneonDone");
            toggleView("collapsed");
          }

          @Override
          public void onError(String utteranceId) {
            System.out.println("onDoneonDoneonDoneonDoneonDoneonDone");
            toggleView("collapsed");
          }
        });

    Locale locale = Locale.forLanguageTag("en");

    // if (Build.VERSION.SDK_INT >= 21) {
    HashMap<String, String> ttsParams = new HashMap<>();
    ttsParams.put(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "1");
    // ttsParams.put(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME,
    // Float.toString(volume));

    tts.setLanguage(locale);
    // tts.setSpeechRate(rate);
    // tts.setPitch(pitch);
    tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, ttsParams);
    // }
  }

  private void makeCall(String text) {
    this.toggleView("loading");

    setLoading();

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

        loadingView.setVisibility(View.INVISIBLE);
        recordingView.setVisibility(View.INVISIBLE);
        toggleView("expanded");

        // expandedText.setText(responseJSON.getString("response"));
        speak(responseJSON.getString("response"));

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
    loadingView.setVisibility(View.INVISIBLE);
    // collapsedView.setVisibility(View.GONE);
  }

  public void setLoading() {
    toggleView("");

    loadingView.setVisibility(View.VISIBLE);
    collapsedView.setVisibility(View.INVISIBLE);
    expandedView.setVisibility(View.INVISIBLE);
  }

  public void toggleView(String viewName) {
    loadingView = mFloatingView.findViewById(R.id.layoutLoading);
    recordingView = mFloatingView.findViewById(R.id.layoutRecording);
    collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
    expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

    if (loadingView != null) {
      loadingView.setVisibility(View.INVISIBLE);
      // loadingView.setVisibility(View.GONE);
    }

    if (collapsedView != null) {
      collapsedView.setVisibility(View.INVISIBLE);
      // collapsedView.setVisibility(View.GONE);
    }

    if (expandedView != null) {
      expandedView.setVisibility(View.INVISIBLE);
      // expandedView.setVisibility(View.GONE);
    }

    if (recordingView != null) {
      recordingView.setVisibility(View.INVISIBLE);
      // recordingView.setVisibility(View.GONE);
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
