package com.example.geomain

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders

const val EXTRA_ANSWER_SHOWN = "com.example.geomain.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.example.geomain.answer_is_true"

class CheatActivity : AppCompatActivity() {
    private lateinit var answerTextView: TextView
    private lateinit var apiLevelText: TextView
    private lateinit var apiLevelNumber: TextView
    private lateinit var showAnswerButton: Button
    private var answerIsTrue = false

    private val cheatViewModel: CheatViewModel by lazy {
        ViewModelProviders.of(this).get(CheatViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        apiLevelText = findViewById(R.id.api_level_text)
        apiLevelNumber = findViewById(R.id.api_level_number)
        apiLevelNumber.text = Build.VERSION.SDK_INT.toString()

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener {
            showAnswer()
        }

        if (cheatViewModel.isAnswerShown) showAnswer()
    }

    private fun showAnswer() {
        val answerText = when {
            answerIsTrue -> R.string.true_button
            else -> R.string.false_button
        }
        answerTextView.setText(answerText)
        setAnswerShownResult(true)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        cheatViewModel.isAnswerShown = isAnswerShown
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, cheatViewModel.isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}