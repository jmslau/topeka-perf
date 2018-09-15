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
class FillTwoBlanksQuiz : Quiz<Array<String>> {

    constructor(question: String, answer: Array<String>, solved: Boolean) : super(question, answer, solved) {}

    constructor(`in`: Parcel) : super(`in`) {
        val answer = `in`.createStringArray()
        //answer = answer
    }

//    override fun getType(): QuizType {
//        return QuizType.FILL_TWO_BLANKS
//    }

    override val type: QuizType = QuizType.FILL_TWO_BLANKS

    override val stringAnswer: String = AnswerHelper.getAnswer(answer as Array<String?>)


    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeStringArray(answer)
    }

    fun isAnswerCorrect(answer: Array<String>?): Boolean {
        val correctAnswers = answer
        if (answer == null || correctAnswers == null) {
            return false
        }
        for (i in answer.indices) {
            if (!answer[i].equals(correctAnswers[i], ignoreCase = true)) {
                return false
            }
        }
        return answer.size == correctAnswers.size
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is FillTwoBlanksQuiz) {
            return false
        }

        val quiz = o as FillTwoBlanksQuiz?
        val answer = answer
        val question = question
        if (if (answer != null) !Arrays.equals(answer, quiz!!.answer) else quiz!!.answer != null) {
            return false
        }

        return if (question != quiz.question) {
            false
        } else true

    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + Arrays.hashCode(answer)
        return result
    }

}
