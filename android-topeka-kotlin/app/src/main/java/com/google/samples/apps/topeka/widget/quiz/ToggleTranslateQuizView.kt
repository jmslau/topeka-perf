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
import android.util.SparseBooleanArray
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.ListView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.adapter.OptionsQuizAdapter
import com.google.samples.apps.topeka.helper.AnswerHelper
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.ToggleTranslateQuiz

@SuppressLint("ViewConstructor")
class ToggleTranslateQuizView(context: Context, category: Category, quiz: ToggleTranslateQuiz) : AbsQuizView<ToggleTranslateQuiz>(context, category, quiz) {

    private var mAnswers: BooleanArray? = null
    private var mListView: ListView? = null

    protected override val isAnswerCorrect: Boolean
        get() {
            val checkedItemPositions = mListView!!.checkedItemPositions
            val answer = quiz.answer
            return AnswerHelper.isAnswerCorrect(checkedItemPositions, answer)
        }

    override var userInput: Bundle
        get() {
            val bundle = Bundle()
            bundle.putBooleanArray(KEY_ANSWERS, mAnswers)
            return bundle
        }
        set(savedInput) {
            if (savedInput == null) {
                return
            }
            mAnswers = savedInput.getBooleanArray(KEY_ANSWERS)
            if (mAnswers == null) {
                initAnswerSpace()
                return
            }

            for (i in mAnswers!!.indices) {
                if (mAnswers!![i]) {
                    setUpUserListSelection(mListView!!, i)
                }
            }
        }

    init {
        initAnswerSpace()
    }

    private fun initAnswerSpace() {
        mAnswers = BooleanArray(quiz.options!!.size)
    }

    override fun createQuizContentView(): View {
        mListView = ListView(context)
        mListView!!.divider = null
        mListView!!.setSelector(R.drawable.selector_button)
        mListView!!.adapter = OptionsQuizAdapter(quiz.readableOptions as Array<String>?,
                R.layout.item_answer)
        mListView!!.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
        mListView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            toggleAnswerFor(position)
            if (view is CompoundButton) {
                view.isChecked = mAnswers!![position]
            }

            allowAnswer()
        }
        return mListView!!
    }

    private fun toggleAnswerFor(answerId: Int) {
        mAnswers!![answerId] = !mAnswers!![answerId]
    }

    companion object {

        private val KEY_ANSWERS = "ANSWERS"
    }
}
