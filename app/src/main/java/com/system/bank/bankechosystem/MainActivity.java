package com.system.bank.bankechosystem;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.system.bank.bankechosystem.api.ai.ApiAiServiceProvider;

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;


public class MainActivity extends AppCompatActivity implements AIListener {
    private static final int REQ_CODE_SPEECH_INPUT = 1000;
    private GoogleTranslate translator;
    private EditText translateedittext;
    private TextView translatabletext;
    private Button mSpeak, mAPIAIBtn;
    private ApiAiServiceProvider mServiceProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServiceProvider = new ApiAiServiceProvider(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        translateedittext = (EditText) findViewById(R.id.translateedittext);
        translatabletext = (TextView) findViewById(R.id.translatabletext);
        mSpeak = (Button) findViewById(R.id.speak);
        mAPIAIBtn = (Button) findViewById(R.id.api_list);
        Button translatebutton = (Button) findViewById(R.id.translatebutton);
        translatebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new EnglishToTagalog(translateedittext.getText().toString()).execute();

            }
        });

        mSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        mAPIAIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServiceProvider.setAiListener(MainActivity.this);
                mServiceProvider.getAiService().startListening();
            }
        });
    }

    private class EnglishToTagalog extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        private String textToConvert;


        public EnglishToTagalog(String s) {
            textToConvert = s;
        }

        protected void onError(Exception ex) {

        }
        @Override
        protected Void doInBackground(Void... params) {

            try {
                translator = new GoogleTranslate("AIzaSyBJxiSh0yNpnxNpy3pTEiZMIji2pAhU0-U");

                Thread.sleep(1000);


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            //start the progress dialog
            progress = ProgressDialog.show(MainActivity.this, null, "Translating...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();

            super.onPostExecute(result);
            translatabletext.setText(translated(textToConvert));

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // use this method to disconnect from speech recognition service
        // Not destroying the SpeechRecognition object in onPause method would block other apps from using SpeechRecognition service
        if (mServiceProvider.getAiService() != null) {
            mServiceProvider.getAiService().pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // use this method to reinit connection to recognition service
        if (mServiceProvider.getAiService() != null) {
            mServiceProvider.getAiService().resume();
        }
    }

    public String translated(String input){
        String text = translator.translte(input, "hi", "en");
        return text;
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Your phone not supported speak recognization.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    new EnglishToTagalog(result.get(0)).execute();
                }
                break;
            default:

                break;
        }
    }

    @Override
    public void onResult(AIResponse result) {
        Log.e(getClass().getSimpleName(),"onResult");
    }

    @Override
    public void onError(AIError error) {
        Log.e(getClass().getSimpleName(),"onError");
    }

    @Override
    public void onAudioLevel(float level) {
        Log.e(getClass().getSimpleName(),"onAudioLevel");
    }

    @Override
    public void onListeningStarted() {
        Log.e(getClass().getSimpleName(),"onListeningStarted");
    }

    @Override
    public void onListeningCanceled() {
        Log.e(getClass().getSimpleName(),"onListeningCanceled");
    }

    @Override
    public void onListeningFinished() {
        Log.e(getClass().getSimpleName(),"onListeningFinished");
    }
}
