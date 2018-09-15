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

import android.annotation.TargetApi
import android.os.Build
import android.support.v4.app.SharedElementCallback
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView

/**
 * This callback allows a shared TextView to resize text and start padding during transition.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
open class TextSharedElementCallback(private val mInitialTextSize: Float, private val mInitialPaddingStart: Int) : SharedElementCallback() {
    private var mTargetViewTextSize: Float = 0.toFloat()
    private var mTargetViewPaddingStart: Int = 0

    override fun onSharedElementStart(sharedElementNames: List<String>?, sharedElements: List<View>?,
                                      sharedElementSnapshots: List<View>?) {
        val targetView = getTextView(sharedElements!!)
        if (targetView == null) {
            Log.w(TAG, "onSharedElementStart: No shared TextView, skipping.")
            return
        }
        mTargetViewTextSize = targetView.textSize
        mTargetViewPaddingStart = targetView.paddingStart
        // Setup the TextView's start values.
        targetView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mInitialTextSize)
        ViewUtils.setPaddingStart(targetView, mInitialPaddingStart)
    }

    override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?,
                                    sharedElementSnapshots: List<View>?) {
        val initialView = getTextView(sharedElements!!)

        if (initialView == null) {
            Log.w(TAG, "onSharedElementEnd: No shared TextView, skipping")
            return
        }

        // Setup the TextView's end values.
        initialView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTargetViewTextSize)
        ViewUtils.setPaddingStart(initialView, mTargetViewPaddingStart)

        // Re-measure the TextView (since the text size has changed).
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        initialView.measure(widthSpec, heightSpec)
        initialView.requestLayout()
    }

    private fun getTextView(sharedElements: List<View>): TextView? {
        var targetView: TextView? = null
        for (i in sharedElements.indices) {
            if (sharedElements[i] is TextView) {
                targetView = sharedElements[i] as TextView
            }
        }
        return targetView
    }

    companion object {
        private val TAG = "TextResize"
    }

}
