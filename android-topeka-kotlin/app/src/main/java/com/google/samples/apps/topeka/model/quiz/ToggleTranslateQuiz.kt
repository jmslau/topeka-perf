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

package com.google.samples.apps.topeka.model.quiz

import android.annotation.SuppressLint
import android.os.Parcel

import com.google.samples.apps.topeka.helper.AnswerHelper

import java.util.Arrays

@SuppressLint("ParcelCreator")
class ToggleTranslateQuiz : OptionsQuiz<Array<String>> {

    private var mReadableOptions: Array<String?>? = null

    override val type: QuizType
        get() = QuizType.TOGGLE_TRANSLATE

    override val stringAnswer: String
        get() = AnswerHelper.getAnswer(answer!!, readableOptions!!)

    //lazily initialize
    //iterate over the options and create readable pairs
    val readableOptions: Array<String?>?
        get() {
            if (null == mReadableOptions) {
                val options = options
                mReadableOptions = null //arrayOfNulls(options!!.size)
                for (i in options!!.indices) {
                    mReadableOptions!![i] = createReadablePair(options!![i])
                }
            }
            return mReadableOptions
        }

    constructor(question: String, answer: IntArray, options: Array<Array<String>>?, solved: Boolean) : super(question, answer, options!!, solved) {}

    constructor(`in`: Parcel) : super(`in`) {
        answer = `in`.createIntArray()
        options = `in`.readSerializable() as Array<Array<String>>
    }

    private fun createReadablePair(option: Array<String>): String {
        // results in "Part one <> Part two"
        return option[0] + " <> " + option[1]
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeIntArray(answer)
        dest.writeSerializable(options)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is ToggleTranslateQuiz) {
            return false
        }

        val that = o as ToggleTranslateQuiz?

        if (!Arrays.equals(answer, that!!.answer)) {
            return false
        }

        return if (!Arrays.deepEquals(options, that.options)) {
            false
        } else true

    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + Arrays.hashCode(options)
        return result
    }
}
