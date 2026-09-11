package com.cetin072.worknotebook.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.cetin072.worknotebook.domain.SpeechTextMerger

class VoiceInputController(
    context: Context,
    private val onText: (String) -> Unit,
    private val onListeningChanged: (Boolean) -> Unit,
    private val onStatus: (String) -> Unit,
) : RecognitionListener {
    private val appContext = context.applicationContext
    private val handler = Handler(Looper.getMainLooper())
    private val recognizer: SpeechRecognizer? = if (SpeechRecognizer.isRecognitionAvailable(appContext)) {
        SpeechRecognizer.createSpeechRecognizer(appContext).also { it.setRecognitionListener(this) }
    } else {
        null
    }

    val isAvailable: Boolean
        get() = recognizer != null

    private var keepListening = false
    private var active = false
    private var destroyed = false
    private var persistentText = ""
    private var sessionBaseText = ""
    private var sessionFinalText = ""
    private var lastPartialText = ""

    private val restartRunnable = Runnable {
        if (keepListening && !active && !destroyed) startSession()
    }

    fun start(existingText: String) {
        if (destroyed) return
        if (recognizer == null) {
            onStatus("이 기기에서 음성인식을 사용할 수 없습니다. 키보드 음성입력을 사용해주세요.")
            onListeningChanged(false)
            return
        }

        persistentText = SpeechTextMerger.normalize(existingText)
        keepListening = true
        onListeningChanged(true)
        onStatus("듣는 중입니다. 잠시 쉬었다가 이어서 말씀해도 됩니다.")
        startSession()
    }

    fun stop() {
        if (destroyed) return
        keepListening = false
        handler.removeCallbacks(restartRunnable)
        onStatus("마지막 말을 정리 중입니다…")

        if (!active) {
            commitPartialIfNeeded()
            onListeningChanged(false)
            onStatus("인식 결과를 확인하고 저장하세요.")
            return
        }

        runCatching { recognizer?.stopListening() }
            .onFailure {
                commitPartialIfNeeded()
                active = false
                onListeningChanged(false)
                onStatus("인식을 중지했습니다. 결과를 확인하고 저장하세요.")
            }
    }

    fun destroy() {
        if (destroyed) return
        destroyed = true
        keepListening = false
        active = false
        handler.removeCallbacksAndMessages(null)
        runCatching { recognizer?.destroy() }
    }

    private fun startSession() {
        val speechRecognizer = recognizer ?: return
        if (!keepListening || active || destroyed) return

        sessionBaseText = persistentText
        sessionFinalText = ""
        lastPartialText = ""

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        runCatching {
            active = true
            speechRecognizer.startListening(intent)
        }.onFailure {
            active = false
            keepListening = false
            onListeningChanged(false)
            onStatus("음성인식을 시작하지 못했습니다. 다시 눌러주세요.")
        }
    }

    private fun scheduleRestart() {
        if (!keepListening || destroyed) {
            onListeningChanged(false)
            if (!destroyed) onStatus("인식 결과를 확인하고 저장하세요.")
            return
        }
        handler.removeCallbacks(restartRunnable)
        handler.postDelayed(restartRunnable, RESTART_DELAY_MS)
    }

    private fun firstTranscript(bundle: Bundle?): String {
        return bundle
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull()
            .orEmpty()
    }

    private fun commitPartialIfNeeded() {
        val candidate = SpeechTextMerger.normalize(lastPartialText)
        if (candidate.isBlank()) return
        persistentText = SpeechTextMerger.merge(sessionBaseText, candidate)
        sessionBaseText = persistentText
        lastPartialText = ""
        onText(persistentText)
    }

    override fun onReadyForSpeech(params: Bundle?) {
        onListeningChanged(true)
        onStatus("듣는 중입니다. 편하게 말씀하세요.")
    }

    override fun onBeginningOfSpeech() {
        onStatus("말씀을 듣고 있습니다…")
    }

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        onStatus("말씀을 정리 중입니다…")
    }

    override fun onError(error: Int) {
        active = false
        commitPartialIfNeeded()

        when (error) {
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                if (keepListening) {
                    onStatus("계속 듣는 중입니다. 이어서 말씀하세요.")
                    scheduleRestart()
                } else {
                    onListeningChanged(false)
                    onStatus("인식 결과를 확인하고 저장하세요.")
                }
            }

            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                if (keepListening) {
                    onStatus("음성인식을 다시 준비 중입니다…")
                    handler.postDelayed(restartRunnable, BUSY_RESTART_DELAY_MS)
                } else {
                    onListeningChanged(false)
                }
            }

            SpeechRecognizer.ERROR_CLIENT -> {
                if (keepListening) {
                    onStatus("음성인식을 다시 시작합니다…")
                    scheduleRestart()
                } else {
                    onListeningChanged(false)
                    onStatus("인식을 중지했습니다. 결과를 확인하세요.")
                }
            }

            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                keepListening = false
                onListeningChanged(false)
                onStatus("마이크 권한이 필요합니다.")
            }

            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
            SpeechRecognizer.ERROR_SERVER -> {
                keepListening = false
                onListeningChanged(false)
                onStatus("음성인식 서비스를 사용할 수 없습니다. 직접 입력은 오프라인으로 계속 사용할 수 있습니다.")
            }

            else -> {
                keepListening = false
                onListeningChanged(false)
                onStatus("음성인식 오류가 발생했습니다. 다시 눌러주세요. ($error)")
            }
        }
    }

    override fun onResults(results: Bundle?) {
        val transcript = SpeechTextMerger.normalize(firstTranscript(results))
        if (transcript.isNotBlank()) {
            sessionFinalText = SpeechTextMerger.merge(sessionFinalText, transcript)
            persistentText = SpeechTextMerger.merge(sessionBaseText, sessionFinalText)
            lastPartialText = ""
            onText(persistentText)
        } else {
            commitPartialIfNeeded()
        }

        active = false
        if (keepListening) {
            onStatus("계속 듣는 중입니다. 잠시 쉬었다가 이어서 말씀하세요.")
            scheduleRestart()
        } else {
            onListeningChanged(false)
            onStatus("인식 결과를 확인하고 저장하세요.")
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val partial = SpeechTextMerger.normalize(firstTranscript(partialResults))
        if (partial.isBlank()) return
        lastPartialText = partial
        onText(SpeechTextMerger.merge(sessionBaseText, partial))
    }

    override fun onEvent(eventType: Int, params: Bundle?) = Unit

    private companion object {
        const val RESTART_DELAY_MS = 450L
        const val BUSY_RESTART_DELAY_MS = 900L
    }
}
