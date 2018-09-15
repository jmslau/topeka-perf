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
import android.widget.ListView

import com.google.samples.apps.topeka.adapter.OptionsQuizAdapter
import com.google.samples.apps.topeka.helper.AnswerHelper
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.MultiSelectQuiz

@SuppressLint("ViewConstructor")
class MultiSelectQuizView(context: Context, category: Category, quiz: MultiSelectQuiz) : AbsQuizView<MultiSelectQuiz>(context, category, quiz) {

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
            val bundleableAnswer = bundleableAnswer
            bundle.putBooleanArray(KEY_ANSWER, bundleableAnswer)
            return bundle
        }
        set(savedInput) {
            if (savedInput == null) {
                return
            }
            val answers = savedInput.getBooleanArray(KEY_ANSWER) ?: return

            for (i in answers.indices) {
                if (answers[i]) {
                    mListView!!.performItemClick(mListView!!.getChildAt(i), i, mListView!!.adapter.getItemId(i))
                }
            }
        }

    private val bundleableAnswer: BooleanArray?
        get() {
            val checkedItemPositions = mListView!!.checkedItemPositions
            val answerSize = checkedItemPositions.size()
            if (0 == answerSize) {
                return null
            }
            val optionsSize = quiz.options!!.size
            val bundleableAnswer = BooleanArray(optionsSize)
            var key: Int
            for (i in 0 until answerSize) {
                key = checkedItemPositions.keyAt(i)
                bundleableAnswer[key] = checkedItemPositions.valueAt(i)
            }
            return bundleableAnswer
        }

    override fun createQuizContentView(): View {
        mListView = ListView(context)
        mListView!!.adapter = OptionsQuizAdapter(quiz.options!!,
                android.R.layout.simple_list_item_multiple_choice)
        mListView!!.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
        mListView!!.itemsCanFocus = false
        mListView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> allowAnswer() }
        return mListView!!
    }

    companion object {

        private val KEY_ANSWER = "ANSWER"
    }
}
