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

import com.google.samples.apps.topeka.model.JsonAttributes

/**
 * Available types of quizzes.
 * Maps [JsonAttributes.QuizType] to subclasses of [Quiz].
 */
enum class QuizType private constructor(val jsonName: String, val type: Class<out Quiz<*>>) {
    ALPHA_PICKER(JsonAttributes.QuizType.ALPHA_PICKER, AlphaPickerQuiz::class.java),
    FILL_BLANK(JsonAttributes.QuizType.FILL_BLANK, FillBlankQuiz::class.java),
    FILL_TWO_BLANKS(JsonAttributes.QuizType.FILL_TWO_BLANKS, FillTwoBlanksQuiz::class.java),
    FOUR_QUARTER(JsonAttributes.QuizType.FOUR_QUARTER, FourQuarterQuiz::class.java),
    MULTI_SELECT(JsonAttributes.QuizType.MULTI_SELECT, MultiSelectQuiz::class.java),
    PICKER(JsonAttributes.QuizType.PICKER, PickerQuiz::class.java),
    SINGLE_SELECT(JsonAttributes.QuizType.SINGLE_SELECT, SelectItemQuiz::class.java),
    SINGLE_SELECT_ITEM(JsonAttributes.QuizType.SINGLE_SELECT_ITEM, SelectItemQuiz::class.java),
    TOGGLE_TRANSLATE(JsonAttributes.QuizType.TOGGLE_TRANSLATE, ToggleTranslateQuiz::class.java),
    TRUE_FALSE(JsonAttributes.QuizType.TRUE_FALSE, TrueFalseQuiz::class.java)
}
