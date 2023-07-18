package com.example.geomain

import androidx.annotation.StringRes

data class Question(@StringRes val textResId: Int, val answer: Boolean, var isAnswered: Boolean = false)