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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.system.bank.bankechosystem.api.ai.ApiAiDataProvider;
import com.system.bank.bankechosystem.api.ai.ApiAiHelper;
import com.system.bank.bankechosystem.helper.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

import static com.system.bank.bankechosystem.RegistrationFragment.ReqCodes.ADDRESS;
import static com.system.bank.bankechosystem.RegistrationFragment.ReqCodes.ADHAR;
import static com.system.bank.bankechosystem.RegistrationFragment.ReqCodes.NAME;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener, Result, AIListener {

    private TextInputEditText mFullName;
    private TextInputEditText mAdharNumber;
    private TextInputEditText mAddress;
    private AppCompatButton mSubmitButton;
    private ApiAiHelper mAiHelper;
    private TextToSpeech mTextToSpeech;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAiHelper = new ApiAiHelper(getContext(),this);
        mFullName = (TextInputEditText) view.findViewById(R.id.fullName);
        mAdharNumber = (TextInputEditText) view.findViewById(R.id.adhar_no);
        mAddress = (TextInputEditText) view.findViewById(R.id.full_address);
        mSubmitButton = (AppCompatButton) view.findViewById(R.id.submit);
        mSubmitButton.setOnClickListener(this);
        mTextToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        mHandler = new Handler();
        mAiHelper.getServiceProvider().setAiListener(this);
        fillForm();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                submitForm();
                break;
            default:

                break;
        }
    }

    private void fillForm() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextToSpeech.speak("What is your name.",TextToSpeech.QUEUE_FLUSH, null);
            }
        },1000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAiHelper.promptSpeechInput(RegistrationFragment.this,NAME);
            }
        },2000);
    }

    private void submitForm() {
        ApiAiDataProvider.requestData("submit",this);
    }


    @Override
    public void onResult(String result) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && data == null) {
            return;
        }
        ArrayList<String> result = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (result == null) {
            return;
        }
        switch (requestCode) {
            case NAME:
                mFullName.setText(result.get(0));
                mTextToSpeech.speak("What is your adhar number.",TextToSpeech.QUEUE_FLUSH, null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAiHelper.promptSpeechInput(RegistrationFragment.this,ADHAR);
                    }
                },1000);
                break;
            case ADDRESS:
                mAddress.setText(result.get(0));
                submitForm();
                break;
            case ADHAR:
                mAdharNumber.setText(result.get(0));
                mTextToSpeech.speak("What is your address.",TextToSpeech.QUEUE_FLUSH, null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAiHelper.promptSpeechInput(RegistrationFragment.this,ADDRESS);
                    }
                },1000);
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
        if (result.getResult().getAction().equalsIgnoreCase("submit")) {
            mTextToSpeech.speak("You have registered successfully.",TextToSpeech.QUEUE_FLUSH, null);
            SessionManager.getInstance().setUserReg(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Successfully Registered");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            builder.create().show();
        } else {
            mTextToSpeech.speak("You have registered again.",TextToSpeech.QUEUE_FLUSH, null);
            fillForm();
        }

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


    interface ReqCodes {
        int NAME = 100;
        int ADHAR = 200;
        int ADDRESS = 300;
    }
}
