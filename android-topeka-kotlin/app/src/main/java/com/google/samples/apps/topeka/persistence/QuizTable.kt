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

package com.google.samples.apps.topeka.persistence

import android.provider.BaseColumns

/**
 * Structure of the quiz table.
 */
interface QuizTable {
    companion object {

        val NAME = "quiz"

        val COLUMN_ID = BaseColumns._ID
        val FK_CATEGORY = "fk_category"
        val COLUMN_TYPE = "type"
        val COLUMN_QUESTION = "question"
        val COLUMN_ANSWER = "answer"
        val COLUMN_OPTIONS = "options"
        val COLUMN_MIN = "min"
        val COLUMN_MAX = "max"
        val COLUMN_STEP = "step"
        val COLUMN_START = "start"
        val COLUMN_END = "end"
        val COLUMN_SOLVED = "solved"

        val PROJECTION = arrayOf(COLUMN_ID, FK_CATEGORY, COLUMN_TYPE, COLUMN_QUESTION, COLUMN_ANSWER, COLUMN_OPTIONS, COLUMN_MIN, COLUMN_MAX, COLUMN_STEP, COLUMN_START, COLUMN_END, COLUMN_SOLVED)

        val CREATE = ("CREATE TABLE " + NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + FK_CATEGORY + " REFERENCES "
                + CategoryTable.NAME + "(" + CategoryTable.COLUMN_ID + "), "
                + COLUMN_TYPE + " TEXT NOT NULL, "
                + COLUMN_QUESTION + " TEXT NOT NULL, "
                + COLUMN_ANSWER + " TEXT NOT NULL, "
                + COLUMN_OPTIONS + " TEXT, "
                + COLUMN_MIN + " TEXT, "
                + COLUMN_MAX + " TEXT, "
                + COLUMN_STEP + " TEXT, "
                + COLUMN_START + " TEXT, "
                + COLUMN_END + " TEXT, "
                + COLUMN_SOLVED + ");")
    }
}