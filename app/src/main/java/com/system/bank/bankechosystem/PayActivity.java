package com.system.bank.bankechosystem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.system.bank.bankechosystem.api.ai.ApiAiDataProvider;
import com.system.bank.bankechosystem.api.ai.ApiAiHelper;
import com.system.bank.bankechosystem.helper.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

import static com.system.bank.bankechosystem.PayActivity.ReqCodes.AMOUNT;
import static com.system.bank.bankechosystem.PayActivity.ReqCodes.RECEIVER;

/**
 * Created by B0097489 on 5/21/17.
 */

public class PayActivity extends AppCompatActivity implements View.OnClickListener ,AIListener, Result{

    private TextInputEditText mReceiverName;
    private TextInputEditText mAmount;
    private AppCompatButton mPay;
    private TextToSpeech mTextToSpeech;
    private Handler mHandler;
    private com.system.bank.bankechosystem.api.ai.ApiAiHelper mAiHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_activity);
        mHandler = new Handler();
        mAiHelper = new ApiAiHelper(this,this);
        mReceiverName = (TextInputEditText) findViewById(R.id.receiver);
        mAmount = (TextInputEditText) findViewById(R.id.amount);
        mPay = (AppCompatButton) findViewById(R.id.pay);

        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        fillForm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPay.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPay.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay:
                pay();
                break;
            default:

                break;
        }
    }

    private void pay() {
        ApiAiDataProvider.requestData("payamount",this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && data == null) {
            return;
        }
        ArrayList<String> result = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (result == null) {
            return;
        }
        switch (requestCode) {
            case RECEIVER:
                mReceiverName.setText(result.get(0));
                mTextToSpeech.speak("Please tell me amount.", TextToSpeech.QUEUE_FLUSH, null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAiHelper.promptSpeechInput(PayActivity.this, AMOUNT);
                    }
                }, 1000);
                break;
            case AMOUNT:
                mAmount.setText(result.get(0));
                pay();
                break;
            default:

                break;
        }
    }

    @Override
    public void onResult(AIResponse result) {
        if (result == null) {
            return;
        }
        if (result.getResult().getAction().equalsIgnoreCase("payed")) {
            mTextToSpeech.speak("You have successfully payed.", TextToSpeech.QUEUE_FLUSH, null);
            SessionManager.getInstance().setUserReg(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Successfully Payed.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        } else {
            mTextToSpeech.speak("You have pay again.",TextToSpeech.QUEUE_FLUSH, null);
            fillForm();
        }

    }

    private void fillForm() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextToSpeech.speak("Please tell me receiver name.",TextToSpeech.QUEUE_FLUSH, null);
            }
        },1000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAiHelper.promptSpeechInput(PayActivity.this,RECEIVER);
            }
        },2000);
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

    }


    interface ReqCodes {
        int RECEIVER = 100;
        int AMOUNT = 200;
    }
}
