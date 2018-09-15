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

package com.google.samples.apps.topeka.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.*
import com.google.samples.apps.topeka.widget.quiz.AbsQuizView
import com.google.samples.apps.topeka.widget.quiz.AlphaPickerQuizView
import com.google.samples.apps.topeka.widget.quiz.FillBlankQuizView
import com.google.samples.apps.topeka.widget.quiz.FillTwoBlanksQuizView
import com.google.samples.apps.topeka.widget.quiz.FourQuarterQuizView
import com.google.samples.apps.topeka.widget.quiz.MultiSelectQuizView
import com.google.samples.apps.topeka.widget.quiz.PickerQuizView
import com.google.samples.apps.topeka.widget.quiz.SelectItemQuizView
import com.google.samples.apps.topeka.widget.quiz.ToggleTranslateQuizView
import com.google.samples.apps.topeka.widget.quiz.TrueFalseQuizView

import java.util.ArrayList
import java.util.HashSet

/**
 * Adapter to display quizzes.
 */
class QuizAdapter(private val mContext: Context, private val mCategory: Category) : BaseAdapter() {
    private val mQuizzes: List<Quiz<*>>?
    private val mViewTypeCount: Int
    private var mQuizTypes: List<String>? = null

    init {
        mQuizzes = mCategory.quizzes
        mViewTypeCount = calculateViewTypeCount()

    }

    private fun calculateViewTypeCount(): Int {
        val tmpTypes = HashSet<String>()
        for (i in mQuizzes!!.indices) {
            tmpTypes.add(mQuizzes!![i].type.jsonName)
        }
        mQuizTypes = ArrayList(tmpTypes)
        return mQuizTypes!!.size
    }

    override fun getCount(): Int {
        return mQuizzes!!.size
    }

    override fun getItem(position: Int): Quiz<*> {
        return mQuizzes!![position]
    }

    override fun getItemId(position: Int): Long {
        return mQuizzes!![position].id.toLong()
    }

    override fun getViewTypeCount(): Int {
        return mViewTypeCount
    }

    override fun getItemViewType(position: Int): Int {
        return mQuizTypes!!.indexOf(getItem(position).type.jsonName)
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        val quiz = getItem(position)
        if (convertView is AbsQuizView<*>) {
            if (convertView.quiz == quiz) {
                return convertView
            }
        }
        convertView = getViewInternal(quiz)
        return convertView
    }

    private fun getViewInternal(quiz: Quiz<*>?): AbsQuizView<*> {
        if (null == quiz) {
            throw IllegalArgumentException("Quiz must not be null")
        }
        return createViewFor(quiz)
    }

    private fun createViewFor(quiz: Quiz<*>): AbsQuizView<*> {
        when (quiz.type) {
            QuizType.ALPHA_PICKER -> return AlphaPickerQuizView(mContext, mCategory, quiz as AlphaPickerQuiz)
            QuizType.FILL_BLANK -> return FillBlankQuizView(mContext, mCategory, quiz as FillBlankQuiz)
            QuizType.FILL_TWO_BLANKS -> return FillTwoBlanksQuizView(mContext, mCategory, quiz as FillTwoBlanksQuiz)
            QuizType.FOUR_QUARTER -> return FourQuarterQuizView(mContext, mCategory, quiz as FourQuarterQuiz)
            QuizType.MULTI_SELECT -> return MultiSelectQuizView(mContext, mCategory, quiz as MultiSelectQuiz)
            QuizType.PICKER -> return PickerQuizView(mContext, mCategory, quiz as PickerQuiz)
            QuizType.SINGLE_SELECT, QuizType.SINGLE_SELECT_ITEM -> return SelectItemQuizView(mContext, mCategory, quiz as SelectItemQuiz)
            QuizType.TOGGLE_TRANSLATE -> return ToggleTranslateQuizView(mContext, mCategory,
                    quiz as ToggleTranslateQuiz)
            QuizType.TRUE_FALSE -> return TrueFalseQuizView(mContext, mCategory, quiz as TrueFalseQuiz)
        }
        throw UnsupportedOperationException(
                "Quiz of type " + quiz.type + " can not be displayed.")
    }
}
