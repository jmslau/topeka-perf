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
import android.os.Parcelable

import com.google.samples.apps.topeka.helper.AnswerHelper

import java.util.Arrays

class FourQuarterQuiz : OptionsQuiz<String> {

    constructor(question: String, answer: IntArray, options: Array<String>, solved: Boolean) : super(question, answer, options, solved) {}

    constructor(`in`: Parcel) : super(`in`) {
        val options = `in`.createStringArray()
        //options = options
    }

    override val type: QuizType = QuizType.FOUR_QUARTER


    override val stringAnswer: String = AnswerHelper.getAnswer(answer!!, options as Array<String?>)


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        val options = options
        dest.writeStringArray(options)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is FourQuarterQuiz) {
            return false
        }

        val quiz = o as FourQuarterQuiz?
        val answer = answer
        val question = question
        if (if (answer != null) !Arrays.equals(answer, quiz!!.answer) else quiz!!.answer != null) {
            return false
        }
        if (question != quiz.question) {
            return false
        }


        return if (!Arrays.equals(options, quiz.options)) {
            false
        } else true

    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + Arrays.hashCode(options)
        result = 31 * result + Arrays.hashCode(answer)
        return result
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<FourQuarterQuiz> = object : Parcelable.Creator<FourQuarterQuiz> {
            override fun createFromParcel(`in`: Parcel): FourQuarterQuiz {
                return FourQuarterQuiz(`in`)
            }

            override fun newArray(size: Int): Array<FourQuarterQuiz?> {
                return arrayOfNulls(size)
            }
        }
    }

}
