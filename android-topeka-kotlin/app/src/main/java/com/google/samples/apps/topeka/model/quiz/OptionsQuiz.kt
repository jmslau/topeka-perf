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

import android.os.Parcel

import java.util.Arrays

/**
 * Base class holding details for quizzes with several potential answers.
 *
 * @param <T> The options that can result in an answer.
</T> */
abstract class OptionsQuiz<T> : Quiz<IntArray> {

    var options: Array<T>? = null
        protected set

    constructor(question: String, answer: IntArray, options: Array<T>?, solved: Boolean) : super(question, answer, solved) {
        this.options = options
    }

    constructor(`in`: Parcel) : super(`in`) {
        var answer = `in`.createIntArray()
        answer = answer
    }

    override fun isAnswerCorrect(answer: IntArray): Boolean {
        return Arrays.equals(answer, answer)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeIntArray(answer)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is OptionsQuiz<*>) {
            return false
        }

        val that = o as OptionsQuiz<*>?

        if (!Arrays.equals(answer, that!!.answer as IntArray)) {
            return false
        }
        return if (!Arrays.equals(options, that.options)) {
            false
        } else true

    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + Arrays.hashCode(options)
        return result
    }
}
