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

@SuppressLint("ParcelCreator")
class SelectItemQuiz : OptionsQuiz<String> {

    override val type: QuizType
        get() = QuizType.SINGLE_SELECT

    override val stringAnswer: String
        get() = AnswerHelper.getAnswer(answer!!, options as Array<String?>)

    constructor(question: String, answer: IntArray, options: Array<String>, solved: Boolean) : super(question, answer, options, solved) {}

    constructor(`in`: Parcel) : super(`in`) {
        var options = `in`.createStringArray()
        options = options
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeStringArray(options)
    }
}
