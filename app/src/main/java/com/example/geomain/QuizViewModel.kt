package com.example.geomain

import android.util.Log
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var isQuestionCheated = BooleanArray(questionBank.size) {false}

    var currentIndex = 0

    var numberOfCorrectAnswers = 0

    var isCurrentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].isAnswered
        set(value) {questionBank[currentIndex].isAnswered = value}

    val allQuestionsAnswered: Boolean get() = questionBank.all { it.isAnswered }
    val currentQuestionAnswer: Boolean get() = questionBank[currentIndex].answer
    val currentQuestionText: Int get() = questionBank[currentIndex].textResId

    fun resetQuestions() {
        questionBank.forEach { it.isAnswered = false }
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = if (currentIndex != 0) (currentIndex - 1) % questionBank.size
    else questionBank.size - 1
    }
}
