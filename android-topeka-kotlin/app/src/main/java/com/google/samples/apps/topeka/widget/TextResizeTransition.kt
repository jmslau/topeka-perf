/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.google.samples.apps.topeka.widget

import com.google.samples.apps.topeka.helper.ViewUtils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v4.view.ViewCompat
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView

/**
 * A transition that resizes text of a TextView.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class TextResizeTransition(context: Context, attrs: AttributeSet) : Transition(context, attrs) {

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view !is TextView) {
            throw UnsupportedOperationException("Doesn't work on " + transitionValues.view.javaClass.name)
        }
        val view = transitionValues.view as TextView
        transitionValues.values[PROPERTY_NAME_TEXT_RESIZE] = view.textSize
        transitionValues.values[PROPERTY_NAME_PADDING_RESIZE] = ViewCompat.getPaddingStart(view)
    }

    override fun getTransitionProperties(): Array<String> {
        return TRANSITION_PROPERTIES
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?,
                                endValues: TransitionValues?): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }

        val initialTextSize = startValues.values[PROPERTY_NAME_TEXT_RESIZE] as Float
        val targetTextSize = endValues.values[PROPERTY_NAME_TEXT_RESIZE] as Float
        val targetView = endValues.view as TextView
        targetView.setTextSize(TypedValue.COMPLEX_UNIT_PX, initialTextSize)

        val initialPaddingStart = startValues.values[PROPERTY_NAME_PADDING_RESIZE] as Int
        val targetPaddingStart = endValues.values[PROPERTY_NAME_PADDING_RESIZE] as Int

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(targetView,
                        ViewUtils.PROPERTY_TEXT_SIZE,
                        initialTextSize,
                        targetTextSize),
                ObjectAnimator.ofInt(targetView,
                        ViewUtils.PROPERTY_TEXT_PADDING_START,
                        initialPaddingStart,
                        targetPaddingStart))
        return animatorSet
    }

    companion object {

        private val PROPERTY_NAME_TEXT_RESIZE = "com.google.samples.apps.topeka.widget:TextResizeTransition:textSize"
        private val PROPERTY_NAME_PADDING_RESIZE = "com.google.samples.apps.topeka.widget:TextResizeTransition:paddingStart"

        private val TRANSITION_PROPERTIES = arrayOf(PROPERTY_NAME_TEXT_RESIZE, PROPERTY_NAME_PADDING_RESIZE)
    }
}
