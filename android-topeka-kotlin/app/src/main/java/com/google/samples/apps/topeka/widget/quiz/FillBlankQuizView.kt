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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.FillBlankQuiz

@SuppressLint("ViewConstructor")
class FillBlankQuizView(context: Context, category: Category, quiz: FillBlankQuiz) : TextInputQuizView<FillBlankQuiz>(context, category, quiz) {

    private var mAnswerView: EditText? = null

    override var userInput: Bundle
        get() {
            val bundle = Bundle()
            bundle.putString(KEY_ANSWER, mAnswerView!!.text.toString())
            return bundle
        }
        set(savedInput) {
            if (savedInput == null) {
                return
            }
            mAnswerView!!.setText(savedInput.getString(KEY_ANSWER))
        }

    protected override val isAnswerCorrect: Boolean
        get() = quiz.isAnswerCorrect(mAnswerView!!.text.toString())

    override fun createQuizContentView(): View {
        val start = quiz.start
        val end = quiz.end
        if (null != start || null != end) {
            return getStartEndView(start, end)
        }
        if (null == mAnswerView) {
            mAnswerView = createEditText()
        }
        return mAnswerView!!
    }

    /**
     * Creates and returns views that display the start and end of a question.
     *
     * @param start The content of the start view.
     * @param end The content of the end view.
     * @return The created views within an appropriate container.
     */
    private fun getStartEndView(start: String, end: String): View {
        val container = layoutInflater.inflate(
                R.layout.quiz_fill_blank_with_surroundings, this, false) as LinearLayout
        mAnswerView = container.findViewById<View>(R.id.quiz_edit_text) as EditText
        mAnswerView!!.addTextChangedListener(this)
        mAnswerView!!.setOnEditorActionListener(this)

        val startView = container.findViewById<View>(R.id.start) as TextView
        setExistingContentOrHide(startView, start)


        val endView = container.findViewById<View>(R.id.end) as TextView
        setExistingContentOrHide(endView, end)

        return container
    }

    /**
     * Sets content to a [TextView]. If content is null, the view will not be displayed.
     *
     * @param view The view to hold the text.
     * @param content The text to display.
     */
    private fun setExistingContentOrHide(view: TextView, content: String?) {
        if (null == content) {
            view.visibility = View.GONE
        } else {
            view.text = content
        }
    }

    companion object {

        private val KEY_ANSWER = "ANSWER"
    }
}
