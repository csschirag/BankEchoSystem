package com.system.bank.bankechosystem.api.ai;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.system.bank.bankechosystem.GoogleTranslate;
import com.system.bank.bankechosystem.Result;

import java.util.Locale;

/**
 * Created by B0097489 on 5/21/17.
 */

public class ApiAiHelper {

    private Context mContext;
    private GoogleTranslate translator;
    private ApiAiServiceProvider mServiceProvider;
    private Result mResult;

    public ApiAiHelper(Context context, Result result) {
        mContext =context;
        mResult = result;
        mServiceProvider = new ApiAiServiceProvider(mContext);
    }

    public class LanguageTranslator extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;
        private String textToConvert;

        public LanguageTranslator(String s) {
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
            progress = ProgressDialog.show(mContext, null, "Translating...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();

            super.onPostExecute(result);
            mResult.onResult(translated(textToConvert));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    public String translated(String input){
        String text = translator.translte(input, "hi", "en");
        return text;
    }

    public ApiAiServiceProvider getServiceProvider() {
        return mServiceProvider;
    }

    public GoogleTranslate getTranslator() {
        return translator;
    }

    public void initTranslatorAndConvert(String text) {
        new LanguageTranslator(text).execute();
    }

    public void promptSpeechInput(@NonNull Activity activity, int reqCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");
        try {
            activity.startActivityForResult(intent, reqCode);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(activity.getApplicationContext(),
                    "Your phone not supported speak recognization.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void promptSpeechInput(@NonNull Fragment fragment, int reqCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");
        try {
            fragment.startActivityForResult(intent, reqCode);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(fragment.getActivity().getApplicationContext(),
                    "Your phone not supported speak recognization.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
