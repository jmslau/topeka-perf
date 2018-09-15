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

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import android.util.Log

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.helper.JsonHelper
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.JsonAttributes
import com.google.samples.apps.topeka.model.Theme
import com.google.samples.apps.topeka.model.quiz.AlphaPickerQuiz
import com.google.samples.apps.topeka.model.quiz.FillBlankQuiz
import com.google.samples.apps.topeka.model.quiz.FillTwoBlanksQuiz
import com.google.samples.apps.topeka.model.quiz.FourQuarterQuiz
import com.google.samples.apps.topeka.model.quiz.MultiSelectQuiz
import com.google.samples.apps.topeka.model.quiz.PickerQuiz
import com.google.samples.apps.topeka.model.quiz.Quiz
import com.google.samples.apps.topeka.model.quiz.SelectItemQuiz
import com.google.samples.apps.topeka.model.quiz.ToggleTranslateQuiz
import com.google.samples.apps.topeka.model.quiz.TrueFalseQuiz

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.Arrays

/**
 * Database for storing and retrieving info for categories and quizzes
 */
class TopekaDatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DB_NAME + DB_SUFFIX, null, DB_VERSION) {
    private val mResources: Resources

    init {
        mResources = context.resources
    }//prevents external instance creation

    override fun onCreate(db: SQLiteDatabase) {
        /*
         * create the category table first, as quiz table has a foreign key
         * constraint on category id
         */
        db.execSQL(CategoryTable.CREATE)
        db.execSQL(QuizTable.CREATE)
        preFillDatabase(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /* no-op */
    }

    private fun preFillDatabase(db: SQLiteDatabase) {
        try {
            db.beginTransaction()
            try {
                fillCategoriesAndQuizzes(db)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } catch (e: IOException) {
            Log.e(TAG, "preFillDatabase", e)
        } catch (e: JSONException) {
            Log.e(TAG, "preFillDatabase", e)
        }

    }

    @Throws(JSONException::class, IOException::class)
    private fun fillCategoriesAndQuizzes(db: SQLiteDatabase) {
        val values = ContentValues() // reduce, reuse
        val jsonArray = JSONArray(readCategoriesFromResources())
        var category: JSONObject
        for (i in 0 until jsonArray.length()) {
            category = jsonArray.getJSONObject(i)
            val categoryId = category.getString(JsonAttributes.ID)
            fillCategory(db, values, category, categoryId)
            val quizzes = category.getJSONArray(JsonAttributes.QUIZZES)
            fillQuizzesForCategory(db, values, quizzes, categoryId)
        }
    }

    @Throws(IOException::class)
    private fun readCategoriesFromResources(): String {
        val categoriesJson = StringBuilder()
        val rawCategories = mResources.openRawResource(R.raw.categories)
        val reader = BufferedReader(InputStreamReader(rawCategories))
        var line: String? = null

        while (true) {
            line = reader.readLine()
            line?.let { categoriesJson.append(it)  }
            if (line == null) {
                break
            }
        }

        return categoriesJson.toString()
    }

    @Throws(JSONException::class)
    private fun fillCategory(db: SQLiteDatabase, values: ContentValues, category: JSONObject,
                             categoryId: String) {
        values.clear()
        values.put(CategoryTable.COLUMN_ID, categoryId)
        values.put(CategoryTable.COLUMN_NAME, category.getString(JsonAttributes.NAME))
        values.put(CategoryTable.COLUMN_THEME, category.getString(JsonAttributes.THEME))
        values.put(CategoryTable.COLUMN_SOLVED, category.getString(JsonAttributes.SOLVED))
        values.put(CategoryTable.COLUMN_SCORES, category.getString(JsonAttributes.SCORES))
        db.insert(CategoryTable.NAME, null, values)
    }

    @Throws(JSONException::class)
    private fun fillQuizzesForCategory(db: SQLiteDatabase, values: ContentValues, quizzes: JSONArray,
                                       categoryId: String) {
        var quiz: JSONObject
        for (i in 0 until quizzes.length()) {
            quiz = quizzes.getJSONObject(i)
            values.clear()
            values.put(QuizTable.FK_CATEGORY, categoryId)
            values.put(QuizTable.COLUMN_TYPE, quiz.getString(JsonAttributes.TYPE))
            values.put(QuizTable.COLUMN_QUESTION, quiz.getString(JsonAttributes.QUESTION))
            values.put(QuizTable.COLUMN_ANSWER, quiz.getString(JsonAttributes.ANSWER))
            putNonEmptyString(values, quiz, JsonAttributes.OPTIONS, QuizTable.COLUMN_OPTIONS)
            putNonEmptyString(values, quiz, JsonAttributes.MIN, QuizTable.COLUMN_MIN)
            putNonEmptyString(values, quiz, JsonAttributes.MAX, QuizTable.COLUMN_MAX)
            putNonEmptyString(values, quiz, JsonAttributes.START, QuizTable.COLUMN_START)
            putNonEmptyString(values, quiz, JsonAttributes.END, QuizTable.COLUMN_END)
            putNonEmptyString(values, quiz, JsonAttributes.STEP, QuizTable.COLUMN_STEP)
            db.insert(QuizTable.NAME, null, values)
        }
    }

    /**
     * Puts a non-empty string to ContentValues provided.
     *
     * @param values The place where the data should be put.
     * @param quiz The quiz potentially containing the data.
     * @param jsonKey The key to look for.
     * @param contentKey The key use for placing the data in the database.
     */
    private fun putNonEmptyString(values: ContentValues, quiz: JSONObject, jsonKey: String,
                                  contentKey: String) {
        val stringToPut = quiz.optString(jsonKey, null)
        if (!TextUtils.isEmpty(stringToPut)) {
            values.put(contentKey, stringToPut)
        }
    }

    companion object {

        private val TAG = "TopekaDatabaseHelper"
        private val DB_NAME = "topeka"
        private val DB_SUFFIX = ".db"
        private val DB_VERSION = 1
        private var mCategories: MutableList<Category>? = null
        private var mInstance: TopekaDatabaseHelper? = null

        private fun getInstance(context: Context): TopekaDatabaseHelper {
            if (mInstance == null) {
                mInstance = TopekaDatabaseHelper(context.applicationContext)
            }
            return mInstance as TopekaDatabaseHelper
        }

        /**
         * Gets all categories with their quizzes.
         *
         * @param context The context this is running in.
         * @param fromDatabase `true` if a data refresh is needed, else `false`.
         * @return All categories stored in the database.
         */
        fun getCategories(context: Context, fromDatabase: Boolean): List<Category>? {
            if (mCategories == null || fromDatabase) {
                mCategories = loadCategories(context)
            }
            return mCategories
        }

        private fun loadCategories(context: Context): MutableList<Category> {
            val data = TopekaDatabaseHelper.getCategoryCursor(context)
            val tmpCategories = ArrayList<Category>(data.count)
            val readableDatabase = TopekaDatabaseHelper.getReadableDatabase(context)
            do {
                val category = getCategory(data, readableDatabase)
                tmpCategories.add(category)
            } while (data.moveToNext())
            return tmpCategories
        }


        /**
         * Gets all categories wrapped in a [Cursor] positioned at it's first element.
         *
         * There are **no quizzes** within the categories obtained from this cursor
         *
         * @param context The context this is running in.
         * @return All categories stored in the database.
         */
        private fun getCategoryCursor(context: Context): Cursor {
            val readableDatabase = getReadableDatabase(context)
            val data = readableDatabase
                    .query(CategoryTable.NAME, CategoryTable.PROJECTION, null, null, null, null, null)
            data.moveToFirst()
            return data
        }

        /**
         * Gets a category from the given position of the cursor provided.
         *
         * @param cursor The Cursor containing the data.
         * @param readableDatabase The database that contains the quizzes.
         * @return The found category.
         */
        private fun getCategory(cursor: Cursor, readableDatabase: SQLiteDatabase): Category {
            // "magic numbers" based on CategoryTable#PROJECTION
            val id = cursor.getString(0)
            val name = cursor.getString(1)
            val themeName = cursor.getString(2)
            val theme = Theme.valueOf(themeName)
            val isSolved = cursor.getString(3)
            val solved = getBooleanFromDatabase(isSolved)
            val scores = JsonHelper.jsonArrayToIntArray(cursor.getString(4))

            val quizzes = getQuizzes(id, readableDatabase)
            return Category(name, id, theme, quizzes, scores, solved)
        }

        private fun getBooleanFromDatabase(isSolved: String?): Boolean {
            // json stores booleans as true/false strings, whereas SQLite stores them as 0/1 values
            return null != isSolved && isSolved.length == 1 && Integer.valueOf(isSolved) == 1
        }

        /**
         * Looks for a category with a given id.
         *
         * @param context The context this is running in.
         * @param categoryId Id of the category to look for.
         * @return The found category.
         */
        fun getCategoryWith(context: Context, categoryId: String): Category {
            val readableDatabase = getReadableDatabase(context)
            val selectionArgs = arrayOf(categoryId)
            val data = readableDatabase
                    .query(CategoryTable.NAME, CategoryTable.PROJECTION, CategoryTable.COLUMN_ID + "=?",
                            selectionArgs, null, null, null)
            data.moveToFirst()
            return getCategory(data, readableDatabase)
        }

        /**
         * Scooooooooooore!
         *
         * @param context The context this is running in.
         * @return The score over all Categories.
         */
        fun getScore(context: Context): Int {
            val categories = getCategories(context, false)
            var score = 0
            for (cat in categories!!) {
                score += cat.score
            }
            return score
        }

        /**
         * Updates values for a category.
         *
         * @param context The context this is running in.
         * @param category The category to update.
         */
        fun updateCategory(context: Context, category: Category) {
            if (mCategories != null && mCategories!!.contains(category)) {
                val location = mCategories!!.indexOf(category)
                mCategories!!.removeAt(location)
                mCategories!!.add(location, category)
            }
            val writableDatabase = getWritableDatabase(context)
            val categoryValues = createContentValuesFor(category)
            writableDatabase.update(CategoryTable.NAME, categoryValues, CategoryTable.COLUMN_ID + "=?",
                    arrayOf(category.id))
            val quizzes = category.quizzes
            updateQuizzes(writableDatabase, quizzes!!)
        }

        /**
         * Updates a list of given quizzes.
         *
         * @param writableDatabase The database to write the quizzes to.
         * @param quizzes The quizzes to write.
         */
        private fun updateQuizzes(writableDatabase: SQLiteDatabase, quizzes: List<Quiz<*>>) {
            var quiz: Quiz<*>
            val quizValues = ContentValues()
            val quizArgs = arrayOfNulls<String>(1)
            for (i in quizzes.indices) {
                quiz = quizzes[i]
                quizValues.clear()
                quizValues.put(QuizTable.COLUMN_SOLVED, quiz.isSolved)

                quizArgs[0] = quiz.question
                writableDatabase.update(QuizTable.NAME, quizValues, QuizTable.COLUMN_QUESTION + "=?",
                        quizArgs)
            }
        }

        /**
         * Resets the contents of Topeka's database to it's initial state.
         *
         * @param context The context this is running in.
         */
        fun reset(context: Context) {
            val writableDatabase = getWritableDatabase(context)
            writableDatabase.delete(CategoryTable.NAME, null, null)
            writableDatabase.delete(QuizTable.NAME, null, null)
            getInstance(context).preFillDatabase(writableDatabase)
        }

        /**
         * Creates objects for quizzes according to a category id.
         *
         * @param categoryId The category to create quizzes for.
         * @param database The database containing the quizzes.
         * @return The found quizzes or an empty list if none were available.
         */
        private fun getQuizzes(categoryId: String, database: SQLiteDatabase): List<Quiz<*>> {
            val quizzes = ArrayList<Quiz<*>>()
            val cursor = database.query(QuizTable.NAME, QuizTable.PROJECTION,
                    QuizTable.FK_CATEGORY + " LIKE ?", arrayOf(categoryId), null, null, null)
            cursor.moveToFirst()
            do {
                quizzes.add(createQuizDueToType(cursor))
            } while (cursor.moveToNext())
            cursor.close()
            return quizzes
        }

        /**
         * Creates a quiz corresponding to the projection provided from a cursor row.
         * Currently only [QuizTable.PROJECTION] is supported.
         *
         * @param cursor The Cursor containing the data.
         * @return The created quiz.
         */
        private fun createQuizDueToType(cursor: Cursor): Quiz<*> {
            // "magic numbers" based on QuizTable#PROJECTION
            val type = cursor.getString(2)
            val question = cursor.getString(3)
            val answer = cursor.getString(4)
            val options = cursor.getString(5)
            val min = cursor.getInt(6)
            val max = cursor.getInt(7)
            val step = cursor.getInt(8)
            val solved = getBooleanFromDatabase(cursor.getString(11))

            when (type) {

                JsonAttributes.QuizType.ALPHA_PICKER -> {
                    return AlphaPickerQuiz(question, answer, solved)
                }
                JsonAttributes.QuizType.FILL_BLANK -> {
                    return createFillBlankQuiz(cursor, question, answer, solved)
                }
                JsonAttributes.QuizType.FILL_TWO_BLANKS -> {
                    return createFillTwoBlanksQuiz(question, answer, solved)
                }
                JsonAttributes.QuizType.FOUR_QUARTER -> {
                    return createFourQuarterQuiz(question, answer, options, solved)
                }
                JsonAttributes.QuizType.MULTI_SELECT -> {
                    return createMultiSelectQuiz(question, answer, options, solved)
                }
                JsonAttributes.QuizType.PICKER -> {
                    return PickerQuiz(question, Integer.valueOf(answer), min, max, step, solved)
                }
                JsonAttributes.QuizType.SINGLE_SELECT,
                    //fall-through intended
                JsonAttributes.QuizType.SINGLE_SELECT_ITEM -> {
                    return createSelectItemQuiz(question, answer, options, solved)
                }
                JsonAttributes.QuizType.TOGGLE_TRANSLATE -> {
                    return createToggleTranslateQuiz(question, answer, options, solved)
                }
                JsonAttributes.QuizType.TRUE_FALSE -> {
                    return createTrueFalseQuiz(question, answer, solved)

                }
                else -> {
                    throw IllegalArgumentException("Quiz type $type is not supported")
                }
            }
        }

        private fun createFillBlankQuiz(cursor: Cursor, question: String,
                                        answer: String, solved: Boolean): Quiz<*> {
            val start = cursor.getString(9)
            val end = cursor.getString(10)
            return FillBlankQuiz(question, answer, start, end, solved)
        }

        private fun createFillTwoBlanksQuiz(question: String, answer: String, solved: Boolean): Quiz<*> {
            val answerArray = JsonHelper.jsonArrayToStringArray(answer)
            return FillTwoBlanksQuiz(question, answerArray as Array<String>, solved)
        }

        private fun createFourQuarterQuiz(question: String, answer: String,
                                          options: String, solved: Boolean): Quiz<*> {
            val answerArray = JsonHelper.jsonArrayToIntArray(answer)
            val optionsArray = JsonHelper.jsonArrayToStringArray(options)
            return FourQuarterQuiz(question, answerArray, optionsArray as Array<String>, solved)
        }

        private fun createMultiSelectQuiz(question: String, answer: String,
                                          options: String, solved: Boolean): Quiz<*> {
            val answerArray = JsonHelper.jsonArrayToIntArray(answer)
            val optionsArray = JsonHelper.jsonArrayToStringArray(options)
            return MultiSelectQuiz(question, answerArray, optionsArray as Array<String>, solved)
        }

        private fun createSelectItemQuiz(question: String, answer: String,
                                         options: String, solved: Boolean): Quiz<*> {
            val answerArray = JsonHelper.jsonArrayToIntArray(answer)
            val optionsArray = JsonHelper.jsonArrayToStringArray(options)
            return SelectItemQuiz(question, answerArray, optionsArray as Array<String>, solved)
        }

        private fun createToggleTranslateQuiz(question: String, answer: String,
                                              options: String, solved: Boolean): Quiz<*> {
            val answerArray = JsonHelper.jsonArrayToIntArray(answer)
            val optionsArrays = extractOptionsArrays(options)
            return ToggleTranslateQuiz(question, answerArray, optionsArrays, solved)
        }

        private fun createTrueFalseQuiz(question: String, answer: String, solved: Boolean): Quiz<*> {
            /*
     * parsing json with the potential values "true" and "false"
     * see res/raw/categories.json for reference
     */
            val answerValue = "true" == answer
            return TrueFalseQuiz(question, answerValue, solved)
        }

        private fun extractOptionsArrays(options: String): Array<Array<String>>? {
            val optionsLvlOne = JsonHelper.jsonArrayToStringArray(options)
            val optionsArray = arrayOfNulls<Array<String>>(optionsLvlOne.size)
            for (i in optionsLvlOne.indices) {
                optionsArray[i] = JsonHelper.jsonArrayToStringArray(optionsLvlOne[i]!!) as Array<String>?
            }
            return optionsArray as Array<Array<String>>?
        }

        /**
         * Creates the content values to update a category in the database.
         *
         * @param category The category to update.
         * @return ContentValues containing updatable data.
         */
        private fun createContentValuesFor(category: Category): ContentValues {
            val contentValues = ContentValues()
            contentValues.put(CategoryTable.COLUMN_SOLVED, category.isSolved)
            contentValues.put(CategoryTable.COLUMN_SCORES, Arrays.toString(category.scores))
            return contentValues
        }

        private fun getReadableDatabase(context: Context): SQLiteDatabase {
            return getInstance(context).readableDatabase
        }

        private fun getWritableDatabase(context: Context): SQLiteDatabase {
            return getInstance(context).writableDatabase
        }
    }

}
