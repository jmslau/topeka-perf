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
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.Quiz

/**
 * Adapter for displaying score cards.
 */
class ScoreAdapter(private val mCategory: Category) : BaseAdapter() {
    private val count: Int
    private val mQuizList: List<Quiz<*>>

    private var mSuccessIcon: Drawable? = null
    private var mFailedIcon: Drawable? = null

    init {
        mQuizList = mCategory.quizzes!!
        count = mQuizList.size
    }

    override fun getCount(): Int {
        return count
    }

    override fun getItem(position: Int): Quiz<*> {
        return mQuizList[position]
    }

    override fun getItemId(position: Int): Long {
        return if (position > count || position < 0) {
            AbsListView.INVALID_POSITION.toLong()
        } else position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (null == convertView) {
            convertView = createView(parent)
        }

        val quiz = getItem(position)
        val viewHolder = convertView.tag as ViewHolder
        viewHolder.mQuizView.text = quiz.question
        viewHolder.mAnswerView.text = quiz.stringAnswer
        setSolvedStateForQuiz(viewHolder.mSolvedState, position)
        return convertView
    }

    private fun setSolvedStateForQuiz(solvedState: ImageView, position: Int) {
        val context = solvedState.context
        val tintedImage: Drawable?
        if (mCategory.isSolvedCorrectly(getItem(position))) {
            tintedImage = getSuccessIcon(context)
        } else {
            tintedImage = getFailedIcon(context)
        }
        solvedState.setImageDrawable(tintedImage)
    }

    private fun getSuccessIcon(context: Context): Drawable? {
        if (null == mSuccessIcon) {
            mSuccessIcon = loadAndTint(context, R.drawable.ic_tick, R.color.theme_green_primary)
        }
        return mSuccessIcon
    }

    private fun getFailedIcon(context: Context): Drawable? {
        if (null == mFailedIcon) {
            mFailedIcon = loadAndTint(context, R.drawable.ic_cross, R.color.theme_red_primary)
        }
        return mFailedIcon
    }

    /**
     * Convenience method to aid tintint of vector drawables at runtime.
     *
     * @param context The [Context] for this app.
     * @param drawableId The id of the drawable to load.
     * @param tintColor The tint to apply.
     * @return The tinted drawable.
     */
    private fun loadAndTint(context: Context, @DrawableRes drawableId: Int,
                            @ColorRes tintColor: Int): Drawable {
        val imageDrawable = ContextCompat.getDrawable(context, drawableId)
                ?: throw IllegalArgumentException("The drawable with id " + drawableId
                        + " does not exist")
        DrawableCompat.setTint(DrawableCompat.wrap(imageDrawable), tintColor)
        return imageDrawable
    }

    private fun createView(parent: ViewGroup): View {
        val convertView: View
        val inflater = LayoutInflater.from(parent.context)
        val scorecardItem = inflater.inflate(
                R.layout.item_scorecard, parent, false) as ViewGroup
        convertView = scorecardItem
        val holder = ViewHolder(scorecardItem)
        convertView.setTag(holder)
        return convertView
    }

    private inner class ViewHolder(scorecardItem: ViewGroup) {

        internal val mAnswerView: TextView
        internal val mQuizView: TextView
        internal val mSolvedState: ImageView

        init {
            mQuizView = scorecardItem.findViewById<View>(R.id.quiz) as TextView
            mAnswerView = scorecardItem.findViewById<View>(R.id.answer) as TextView
            mSolvedState = scorecardItem.findViewById<View>(R.id.solved_state) as ImageView
        }

    }
}
