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
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.FillTwoBlanksQuiz

@SuppressLint("ViewConstructor")
class FillTwoBlanksQuizView(context: Context, category: Category, quiz: FillTwoBlanksQuiz) : TextInputQuizView<FillTwoBlanksQuiz>(context, category, quiz) {
    private var mAnswerOne: EditText? = null
    private var mAnswerTwo: EditText? = null

    override var userInput: Bundle
        get() {
            val bundle = Bundle()
            bundle.putString(KEY_ANSWER_ONE, mAnswerOne!!.text.toString())
            bundle.putString(KEY_ANSWER_TWO, mAnswerTwo!!.text.toString())
            return bundle
        }
        set(savedInput) {
            if (savedInput == null) {
                return
            }
            mAnswerOne!!.setText(savedInput.getString(KEY_ANSWER_ONE))
            mAnswerTwo!!.setText(savedInput.getString(KEY_ANSWER_TWO))
        }

    protected override val isAnswerCorrect: Boolean
        get() {
            val partOne = getAnswerFrom(mAnswerOne!!)
            val partTwo = getAnswerFrom(mAnswerTwo!!)
            return quiz.isAnswerCorrect(arrayOf(partOne, partTwo))
        }

    override fun createQuizContentView(): View {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        mAnswerOne = createEditText()
        mAnswerOne!!.imeOptions = EditorInfo.IME_ACTION_NEXT
        mAnswerTwo = createEditText()
        mAnswerTwo!!.id = R.id.quiz_edit_text_two
        addEditText(layout, mAnswerOne as EditText)
        addEditText(layout, mAnswerTwo as EditText)
        return layout
    }

    private fun addEditText(layout: LinearLayout, editText: EditText) {
        layout.addView(editText, CHILD_LAYOUT_PARAMS)
    }

    private fun getAnswerFrom(view: EditText): String {
        return view.text.toString()
    }

    companion object {

        private val KEY_ANSWER_ONE = "ANSWER_ONE"
        private val KEY_ANSWER_TWO = "ANSWER_TWO"
        private val CHILD_LAYOUT_PARAMS = LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0, 1f)
    }
}
