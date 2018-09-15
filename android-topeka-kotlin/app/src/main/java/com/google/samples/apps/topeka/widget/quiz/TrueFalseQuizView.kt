/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.topeka.widget.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.TrueFalseQuiz

@SuppressLint("ViewConstructor")
class TrueFalseQuizView(context: Context, category: Category, quiz: TrueFalseQuiz) : AbsQuizView<TrueFalseQuiz>(context, category, quiz) {

    private var mAnswer: Boolean = false
    private var mAnswerTrue: View? = null
    private var mAnswerFalse: View? = null

    protected override val isAnswerCorrect: Boolean
        get() = quiz.isAnswerCorrect(mAnswer)

    override var userInput: Bundle
        get() {
            val bundle = Bundle()
            bundle.putBoolean(KEY_SELECTION, mAnswer)
            return bundle
        }
        set(savedInput) {
            if (savedInput == null) {
                return
            }
            val tmpAnswer = savedInput.getBoolean(KEY_SELECTION)
            performSelection(if (tmpAnswer) mAnswerTrue!! else mAnswerFalse!!)
        }

    override fun createQuizContentView(): View {
        val container = layoutInflater.inflate(
                R.layout.quiz_radio_group_true_false, this, false) as ViewGroup

        val clickListener = OnClickListener { v ->
            when (v.id) {
                R.id.answer_true -> mAnswer = true
                R.id.answer_false -> mAnswer = false
            }
            allowAnswer()
        }

        mAnswerTrue = container.findViewById(R.id.answer_true)
        mAnswerTrue!!.setOnClickListener(clickListener)
        mAnswerFalse = container.findViewById(R.id.answer_false)
        mAnswerFalse!!.setOnClickListener(clickListener)
        return container
    }

    private fun performSelection(selection: View) {
        selection.performClick()
        selection.isSelected = true
    }

    companion object {

        private val KEY_SELECTION = "SELECTION"
        private val LAYOUT_PARAMS = LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1f)

        init {
            LAYOUT_PARAMS.gravity = Gravity.CENTER
        }
    }
}
