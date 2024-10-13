package com.lingyunchi.fartlek.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.SUCCESS
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TTSException(message: String) : Exception(message)

class TTS(context: Context, locale: Locale) {
    private lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context) { status ->
            if (status == SUCCESS) {
                if (tts.engines.size == 0) {
                    throw TTSException("Please install the voice engine")
                }

                val result: Int = tts.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    throw TTSException("Chinese is not supported")
                }
            } else {
                throw TTSException("Failed to initialize TTS")
            }
        }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.i("TTS", "onStart: $utteranceId")
            }

            override fun onDone(utteranceId: String?) {
                Log.i("TTS", "onDone: $utteranceId")
            }

            override fun onError(utteranceId: String?) {
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e("TTS", "onError: $utteranceId errorCode: $errorCode")
            }
        })
    }

    fun speak(text: String) {
        Log.i("TTS", "speak: $text")
        val utteranceId = text
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun destroy() {
        tts.stop()
        tts.shutdown()
    }
}