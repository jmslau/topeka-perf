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

package com.google.samples.apps.topeka.helper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewCompat
import android.transition.ChangeBounds
import android.util.Property
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

object ViewUtils {

    val FOREGROUND_COLOR: Property<FrameLayout, Int> = object : IntProperty<FrameLayout>("foregroundColor") {

        override fun setValue(layout: FrameLayout, value: Int) {
            if (layout.foreground is ColorDrawable) {
                (layout.foreground.mutate() as ColorDrawable).color = value
            } else {
                layout.foreground = ColorDrawable(value)
            }
        }

        override fun get(layout: FrameLayout): Int? {
            return if (layout.foreground is ColorDrawable) {
                (layout.foreground as ColorDrawable).color
            } else {
                Color.TRANSPARENT
            }
        }
    }

    val BACKGROUND_COLOR: Property<View, Int> = object : IntProperty<View>("backgroundColor") {

        override fun setValue(view: View, value: Int) {
            view.setBackgroundColor(value)
        }

        override fun get(view: View): Int? {
            val d = view.background
            return (d as? ColorDrawable)?.color ?: Color.TRANSPARENT
        }
    }

    /**
     * Allows changes to the text size in transitions and animations.
     * Using this with something else than [ChangeBounds]
     * can result in a severe performance penalty due to layout passes.
     */
    val PROPERTY_TEXT_SIZE: Property<TextView, Float> = object : FloatProperty<TextView>("textSize") {
        override fun get(view: TextView): Float {
            return view.textSize
        }

        override fun setValue(view: TextView, textSize: Float) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
    }

    /**
     * Allows making changes to the start padding of a view.
     * Using this with something else than [ChangeBounds]
     * can result in a severe performance penalty due to layout passes.
     */
    val PROPERTY_TEXT_PADDING_START: Property<TextView, Int> = object : IntProperty<TextView>("paddingStart") {
        override fun get(view: TextView): Int {
            return ViewCompat.getPaddingStart(view)
        }

        override fun setValue(view: TextView, paddingStart: Int) {
            ViewCompat.setPaddingRelative(view, paddingStart, view.paddingTop,
                    ViewCompat.getPaddingEnd(view), view.paddingBottom)
        }
    }

    abstract class IntProperty<T>(name: String) : Property<T, Int>(Int::class.java, name) {

        /**
         * A type-specific override of the [.set] that is faster when
         * dealing
         * with fields of type `int`.
         */
        abstract fun setValue(`object`: T, value: Int)

        override fun set(`object`: T, value: Int) {

            setValue(`object`, value)
        }
    }

    abstract class FloatProperty<T>(name: String) : Property<T, Float>(Float::class.java, name) {

        /**
         * A type-specific override of the [.set] that is faster when dealing
         * with fields of type `int`.
         */
        abstract fun setValue(`object`: T, value: Float)

        override fun set(`object`: T, value: Float) {

            setValue(`object`, value)
        }
    }

    fun setPaddingStart(target: TextView, paddingStart: Int) {
        ViewCompat.setPaddingRelative(target, paddingStart, target.paddingTop,
                ViewCompat.getPaddingEnd(target), target.paddingBottom)
    }

}//no instance
