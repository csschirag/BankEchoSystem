package com.system.bank.bankechosystem.api.ai;

import android.content.Context;
import android.support.annotation.NonNull;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;

import static com.system.bank.bankechosystem.helper.Constants.API_AI_KEY;

/**
 * Created by B0097489 on 5/20/17.
 */

public class ApiAiServiceProvider {

    final AIConfiguration config = new AIConfiguration(API_AI_KEY,
            AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System);

    private AIService aiService;

    public ApiAiServiceProvider(@NonNull Context context) {
        aiService = AIService.getService(context, config);
    }

    public void setAiListener(AIListener aiListener) {
        if (aiService == null) {
            return;
        }
        aiService.setListener(aiListener);
    }

    public AIService getAiService() {
        return aiService;
    }
}

