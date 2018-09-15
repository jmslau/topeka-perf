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

package com.google.samples.apps.topeka.widget.fab

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable

import com.google.samples.apps.topeka.R

/**
 * A [FloatingActionButton] that implements [Checkable] to allow display of different
 * icons in it's states.
 */
class CheckableFab @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FloatingActionButton(context, attrs, defStyle), Checkable {

    private var mIsChecked = true

    init {
        setImageResource(R.drawable.answer_quiz_fab)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        var extraSpace = extraSpace
        val drawableState = super.onCreateDrawableState(++extraSpace)
        if (mIsChecked) {
            View.mergeDrawableStates(drawableState, CHECKED)
        }
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (mIsChecked == checked) {
            return
        }
        mIsChecked = checked
        refreshDrawableState()
    }

    override fun isChecked(): Boolean {
        return mIsChecked
    }

    override fun toggle() {
        isChecked = !mIsChecked
    }

    companion object {

        private val CHECKED = intArrayOf(android.R.attr.state_checked)
    }
}
