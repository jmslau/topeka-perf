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

import android.util.Log
import android.util.SparseBooleanArray

/**
 * Collection of methods to convert answers to human readable forms.
 */
object AnswerHelper {

    internal val SEPARATOR = System.getProperty("line.separator")
    private val TAG = "AnswerHelper"

    /**
     * Converts an array of answers to a readable answer.
     *
     * @param answers The answers to display.
     * @return The readable answer.
     */
    fun getAnswer(answers: Array<String?>): String {
        val readableAnswer = StringBuilder()
        //Iterate over all answers
        for (i in answers.indices) {
            val answer = answers[i]
            readableAnswer.append(answer)
            //Don't add a separator for the last answer
            if (i < answers.size - 1) {
                readableAnswer.append(SEPARATOR)
            }
        }
        return readableAnswer.toString()
    }

    /**
     * Converts an array of answers with options to a readable answer.
     *
     * @param answers The actual answers
     * @param options The options to display.
     * @return The readable answer.
     */
    fun getAnswer(answers: IntArray, options: Array<String?>): String {
        val readableAnswers = arrayOfNulls<String>(answers.size)
        for (i in answers.indices) {
            val humanReadableAnswer = options[answers[i]]
            readableAnswers[i] = humanReadableAnswer
        }
        return getAnswer(readableAnswers)
    }

    /**
     * Checks whether a provided answer is correct.
     *
     * @param checkedItems The items that were selected.
     * @param answerIds The actual correct answer ids.
     * @return `true` if correct else `false`.
     */
    fun isAnswerCorrect(checkedItems: SparseBooleanArray?, answerIds: IntArray?): Boolean {
        if (null == checkedItems || null == answerIds) {
            Log.i(TAG, "isAnswerCorrect got a null parameter input.")
            return false
        }
        for (answer in answerIds) {
            if (0 > checkedItems.indexOfKey(answer)) {
                return false
            }
        }
        return checkedItems.size() == answerIds.size
    }

}//no instance
