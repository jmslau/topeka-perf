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

package com.google.samples.apps.topeka.model

interface JsonAttributes {

    interface QuizType {
        companion object {

            const val ALPHA_PICKER = "alpha-picker"
            const val FILL_BLANK = "fill-blank"
            const val FILL_TWO_BLANKS = "fill-two-blanks"
            const val FOUR_QUARTER = "four-quarter"
            const val MULTI_SELECT = "multi-select"
            const val PICKER = "picker"
            const val SINGLE_SELECT = "single-select"
            const val SINGLE_SELECT_ITEM = "single-select-item"
            const val TOGGLE_TRANSLATE = "toggle-translate"
            const val TRUE_FALSE = "true-false"
        }
    }

    companion object {

        val ANSWER = "answer"
        val END = "end"
        val ID = "id"
        val MAX = "max"
        val MIN = "min"
        val NAME = "name"
        val OPTIONS = "options"
        val QUESTION = "question"
        val QUIZZES = "quizzes"
        val START = "start"
        val STEP = "step"
        val THEME = "theme"
        val TYPE = "type"
        val SCORES = "scores"
        val SOLVED = "solved"
    }
}
