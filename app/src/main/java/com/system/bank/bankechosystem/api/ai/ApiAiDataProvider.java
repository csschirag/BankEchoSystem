package com.system.bank.bankechosystem.api.ai;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

import static com.system.bank.bankechosystem.helper.Constants.API_AI_KEY;

/**
 * Created by B0097489 on 5/20/17.
 */

public class ApiAiDataProvider {

    static final AIConfiguration config = new AIConfiguration(API_AI_KEY,
            AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System);

    static final AIDataService aiDataService = new AIDataService(config);

    private static AIRequest aiRequest;

    public static void requestData(String text,@NonNull final AIListener aiListener) {
        if (text == null) {
            return;
        }
        aiRequest = new AIRequest();
        aiRequest.setQuery(text);

        new AsyncTask<Void, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(Void... voids) {
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null && !aiResponse.isError()) {
                    aiListener.onResult(aiResponse);
                } else {
                    aiListener.onError(null);
                }
            }
        }.execute();
    }
}
