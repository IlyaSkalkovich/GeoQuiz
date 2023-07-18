package com.example.geomain

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_IS_ANSWERED = "isAnswered"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                    quizViewModel.isQuestionCheated[quizViewModel.currentIndex] =
                        it.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            }
        }

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }
    

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG,"onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val isCurrentQuestionAnswered = savedInstanceState?.getBoolean(KEY_IS_ANSWERED) ?: false

        quizViewModel.currentIndex = currentIndex
        quizViewModel.isCurrentQuestionAnswered = isCurrentQuestionAnswered

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)

        trueButton.setOnClickListener {
            checkAnswer(true)
            disableAnswerButtons()
            nextQuestion()
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            disableAnswerButtons()
            nextQuestion()
        }

        nextButton.setOnClickListener {
            nextQuestion()
        }

        previousButton.setOnClickListener{
            previousQuestion()
        }

        questionTextView.setOnClickListener{
            nextQuestion()
        }

        cheatButton.setOnClickListener{
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options =
                    ActivityOptionsCompat.makeClipRevealAnimation(it, 0, 0, it.width, it.height)

                activityLauncher.launch(intent, options)
            } else activityLauncher.launch(intent)
            
        }
        Activity.RESULT_OK
        updateQuestion()
        quizViewModel.numberOfCorrectAnswers = 0
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText

        questionTextView.setText(questionTextResId)

        if (quizViewModel.allQuestionsAnswered) {
            enableAnswerButtons()
            quizViewModel.resetQuestions()
            showScore()
        }
        if (quizViewModel.isCurrentQuestionAnswered && trueButton.isEnabled) disableAnswerButtons()
        if (!quizViewModel.isCurrentQuestionAnswered) enableAnswerButtons()
    }

    private fun showScore() {
        Toast.makeText(this, "Your score: ${quizViewModel.numberOfCorrectAnswers}", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "You cheated ${quizViewModel.isQuestionCheated.count{it}} times", Toast.LENGTH_SHORT).show()

        quizViewModel.numberOfCorrectAnswers = 0
        quizViewModel.isQuestionCheated.fill(false)
    }

    private fun enableAnswerButtons() {
        trueButton.isEnabled = true
        falseButton.isEnabled = true
    }

    private fun disableAnswerButtons() {
        trueButton.isEnabled = false
        falseButton.isEnabled = false
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when (userAnswer) {
            correctAnswer -> {
                quizViewModel.numberOfCorrectAnswers++
                if (!quizViewModel.isQuestionCheated[quizViewModel.currentIndex]) R.string.correct_toast
                else R.string.judgment_toast
            }
            else -> R.string.incorrect_toast
        }

        quizViewModel.isCurrentQuestionAnswered = true
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun previousQuestion() {
        quizViewModel.moveToPrevious()
        updateQuestion()
    }

    private fun nextQuestion() {
        quizViewModel.moveToNext()
        updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")

        with(savedInstanceState) {
            putInt(KEY_INDEX, quizViewModel.currentIndex)
            putBoolean(KEY_IS_ANSWERED, quizViewModel.isCurrentQuestionAnswered)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}
