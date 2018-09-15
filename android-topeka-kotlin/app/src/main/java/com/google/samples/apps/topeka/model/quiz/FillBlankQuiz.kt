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
class FillBlankQuiz : Quiz<String> {

    val start: String
    val end: String

    constructor(question: String, answer: String, start: String, end: String, solved: Boolean) : super(question, answer, solved) {
        this.start = start
        this.end = end
    }

    constructor(`in`: Parcel) : super(`in`) {
        answer = `in`.readString()
        start = `in`.readString()
        end = `in`.readString()
    }

    override val stringAnswer: String = answer!!

    override val type: QuizType =  QuizType.FILL_BLANK


    override fun isAnswerCorrect(answer: String): Boolean {
        return answer.equals(answer, ignoreCase = true)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(answer)
        dest.writeString(start)
        dest.writeString(end)
    }
}
