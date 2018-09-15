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
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.adapter.OptionsQuizAdapter
import com.google.samples.apps.topeka.helper.ApiLevelHelper
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.FourQuarterQuiz

@SuppressLint("ViewConstructor")
class FourQuarterQuizView(context: Context, category: Category, quiz: FourQuarterQuiz) : AbsQuizView<FourQuarterQuiz>(context, category, quiz) {
    private var mAnswered = -1
    private var mAnswerView: GridView? = null

    override var userInput: Bundle
        get() {
            val bundle = Bundle()
            bundle.putInt(KEY_ANSWER, mAnswered)
            return bundle
        }
        @SuppressLint("NewApi")
        set(savedInput) {
            if (savedInput == null) {
                return
            }
            mAnswered = savedInput.getInt(KEY_ANSWER)
            if (mAnswered != -1) {
                if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.KITKAT) && isLaidOut) {
                    setUpUserListSelection(mAnswerView!!, mAnswered)
                } else {
                    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                        override fun onLayoutChange(v: View, left: Int, top: Int,
                                                    right: Int, bottom: Int,
                                                    oldLeft: Int, oldTop: Int,
                                                    oldRight: Int, oldBottom: Int) {
                            v.removeOnLayoutChangeListener(this)
                            setUpUserListSelection(mAnswerView!!, mAnswered)
                        }
                    })
                }
            }
        }

    protected override val isAnswerCorrect: Boolean
        get() = quiz.isAnswerCorrect(intArrayOf(mAnswered))

    override fun createQuizContentView(): View {
        mAnswerView = GridView(context)
        mAnswerView!!.setSelector(R.drawable.selector_button)
        mAnswerView!!.numColumns = 2
        mAnswerView!!.adapter = OptionsQuizAdapter(quiz.options!!,
                R.layout.item_answer)
        mAnswerView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            allowAnswer()
            mAnswered = position
        }
        return mAnswerView!!
    }

    companion object {

        private val KEY_ANSWER = "ANSWER"
    }
}
