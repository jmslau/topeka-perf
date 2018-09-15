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

import org.json.JSONArray
import org.json.JSONException

/**
 * Helper class to make unsafe types safe to use in the java world.
 */
object JsonHelper {

    private val TAG = "JsonHelper"

    /**
     * Creates a String array out of a json array.
     *
     * @param json The String containing the json array.
     * @return An array with the extracted strings or an
     * empty String array if an exception occurred.
     */
    fun jsonArrayToStringArray(json: String): Array<String?> {
        try {
            val jsonArray = JSONArray(json)
            val stringArray = arrayOfNulls<String>(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                stringArray[i] = jsonArray.getString(i)
            }
            return stringArray
        } catch (e: JSONException) {
            Log.e(TAG, "Error during Json processing: ", e)
        }

        return arrayOfNulls(0)
    }

    /**
     * Creates an int array out of a json array.
     *
     * @param json The String containing the json array.
     * @return An array with the extracted integers or an
     * empty int array if an exception occurred.
     */
    fun jsonArrayToIntArray(json: String): IntArray {
        try {
            val jsonArray = JSONArray(json)
            val intArray = IntArray(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                intArray[i] = jsonArray.getInt(i)
            }
            return intArray
        } catch (e: JSONException) {
            Log.e(TAG, "Error during Json processing: ", e)
        }

        return IntArray(0)
    }
}//no instance
