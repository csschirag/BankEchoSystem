package com.system.bank.bankechosystem;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.system.bank.bankechosystem.api.ai.ApiAiDataProvider;
import com.system.bank.bankechosystem.api.ai.ApiAiHelper;

import java.util.ArrayList;

import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;


public class MainActivity extends AppCompatActivity implements AIListener, Result {


    private EditText translateedittext;
    private TextView translatabletext;
    private Button mSpeak, mAPIAIBtn;
    private ApiAiHelper mAiHelper;

    public static final int REQ_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAiHelper = new ApiAiHelper(this,this);
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
                 mAiHelper.initTranslatorAndConvert(translateedittext.getText().toString());

            }
        });

        mSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAiHelper.promptSpeechInput(MainActivity.this,REQ_CODE_SPEECH_INPUT);
            }
        });
        mAPIAIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAiHelper.getServiceProvider().setAiListener(MainActivity.this);
                mAiHelper.getServiceProvider().getAiService().startListening();
            }
        });
        Intent intent = new Intent(this,Registration.class);
        startActivity(intent);
    }



    @Override
    protected void onPause() {
        super.onPause();

        // use this method to disconnect from speech recognition service
        // Not destroying the SpeechRecognition object in onPause method would block other apps from using SpeechRecognition service
        if (mAiHelper.getServiceProvider().getAiService() != null) {
            mAiHelper.getServiceProvider().getAiService().pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // use this method to reinit connection to recognition service
        if (mAiHelper.getServiceProvider().getAiService() != null) {
            mAiHelper.getServiceProvider().getAiService().resume();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mAiHelper.initTranslatorAndConvert(result.get(0));
                }
                break;
            default:

                break;
        }
    }

    @Override
    public void onResult(AIResponse result) {
        Log.e(getClass().getSimpleName(),"onResult " +result.getResult().getAction());
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

    @Override
    public void onResult(String result) {
        translatabletext.setText(result);
        ApiAiDataProvider.requestData(result,this);
    }
}
