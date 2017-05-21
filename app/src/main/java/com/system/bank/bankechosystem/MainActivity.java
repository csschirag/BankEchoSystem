package com.system.bank.bankechosystem;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.system.bank.bankechosystem.api.ai.ApiAiDataProvider;
import com.system.bank.bankechosystem.api.ai.ApiAiHelper;
import com.system.bank.bankechosystem.helper.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;


public class MainActivity extends AppCompatActivity implements AIListener, Result {

//    private TextView translatabletext;
    private Button mSpeak, mSpendAnalyzer;
    private ApiAiHelper mAiHelper;
    private TextToSpeech mTextToSpeech;
    private Handler mHandler;

    public static final int REQ_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        mAiHelper = new ApiAiHelper(this,this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        translatabletext = (TextView) findViewById(R.id.translatabletext);
        mSpeak = (Button) findViewById(R.id.speak);
        mSpendAnalyzer = (Button) findViewById(R.id.spend_analyzer);
        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        mSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextToSpeech.speak("How may i help you.", TextToSpeech.QUEUE_FLUSH, null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAiHelper.promptSpeechInput(MainActivity.this,REQ_CODE_SPEECH_INPUT);
                    }
                },1000);
            }
        });

        mSpendAnalyzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SessionManager.getInstance().isUserReg()) {
                    Intent intent = new Intent(MainActivity.this, SpendAnalyzer.class);
                    startActivity(intent);
                } else {
                    mTextToSpeech.speak("Sorry you are not a bank customer you have to register.do you want to register.", TextToSpeech.QUEUE_FLUSH, null);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAiHelper.promptSpeechInput(MainActivity.this,REQ_CODE_SPEECH_INPUT);
                        }
                    },3000);
                }
            }
        });

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
        if (result ==  null || result.getResult() == null) {
            return;
        }
        if ("register".equalsIgnoreCase(result.getResult().getAction())) {
            Intent intent = new Intent(this,Registration.class);
            startActivity(intent);
        } else if ("pay".equalsIgnoreCase(result.getResult().getAction()) && SessionManager.getInstance().isUserReg()) {
            Intent intent = new Intent(this,PayActivity.class);
            startActivity(intent);
        } else if ("pay".equalsIgnoreCase(result.getResult().getAction()) && !SessionManager.getInstance().isUserReg()) {
            mTextToSpeech.speak("Sorry you are not a bank customer you have to register.do you want to register.", TextToSpeech.QUEUE_FLUSH, null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAiHelper.promptSpeechInput(MainActivity.this,REQ_CODE_SPEECH_INPUT);
                }
            },3000);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTextToSpeech.speak("Sorry i will not be able to help you.", TextToSpeech.QUEUE_FLUSH, null);
                }
            },1000);
        }
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
        ApiAiDataProvider.requestData(result,this);
    }
}
