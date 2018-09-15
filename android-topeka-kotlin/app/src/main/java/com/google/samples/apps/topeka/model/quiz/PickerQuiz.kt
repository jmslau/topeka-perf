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

@SuppressLint("ParcelCreator")
class PickerQuiz : Quiz<Int> {

    val min: Int
    val max: Int
    val step: Int

    override val type: QuizType
        get() = QuizType.PICKER

    override val stringAnswer: String
        get() = answer!!.toString()

    constructor(question: String, answer: Int?, min: Int, max: Int, step: Int, solved: Boolean) : super(question, answer!!, solved) {
        this.min = min
        this.max = max
        this.step = step
    }

    constructor(`in`: Parcel) : super(`in`) {
        answer = `in`.readInt()
        min = `in`.readInt()
        max = `in`.readInt()
        step = `in`.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(answer!!)
        dest.writeInt(min)
        dest.writeInt(max)
        dest.writeInt(step)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is PickerQuiz) {
            return false
        }

        if (!super.equals(o)) {
            return false
        }

        val that = o as PickerQuiz?

        if (min != that!!.min) {
            return false
        }

        return if (max != that.max) {
            false
        } else step == that.step

    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + min
        result = 31 * result + max
        result = 31 * result + step
        return result
    }
}
