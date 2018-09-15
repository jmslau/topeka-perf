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
import android.util.Log

import com.google.samples.apps.topeka.helper.ParcelableHelper

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * This abstract class provides general structure for quizzes.
 *
 * @see com.google.samples.apps.topeka.model.quiz.QuizType
 *
 * @see com.google.samples.apps.topeka.widget.quiz.AbsQuizView
 */
abstract class Quiz<A> : Parcelable {

    val question: String
    private val mQuizType: String
    var answer: A? = null
        protected set
    /**
     * Flag indicating whether this quiz has already been solved.
     * It does not give information whether the solution was correct or not.
     */
    var isSolved: Boolean = false

    /**
     * @return The [QuizType] that represents this quiz.
     */
    abstract val type: QuizType

    /**
     * Implementations need to return a human readable version of the given answer.
     */
    abstract val stringAnswer: String

    /**
     * @return The id of this quiz.
     */
    val id: Int
        get() = question.hashCode()

    protected constructor(question: String, answer: A, solved: Boolean) {
        this.question = question
        this.answer = answer
        mQuizType = type.jsonName
        isSolved = solved
    }

    protected constructor(`in`: Parcel) {
        question = `in`.readString()
        mQuizType = type.jsonName
        isSolved = ParcelableHelper.readBoolean(`in`)
    }

    open fun isAnswerCorrect(answer: A): Boolean {
        return this.answer == answer
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        ParcelableHelper.writeEnumValue(dest, type)
        dest.writeString(question)
        ParcelableHelper.writeBoolean(dest, isSolved)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Quiz<*>) {
            return false
        }

        val quiz = o as Quiz<*>?

        if (isSolved != quiz!!.isSolved) {
            return false
        }
        if (answer != quiz.answer) {
            return false
        }
        if (question != quiz.question) {
            return false
        }
        return if (mQuizType != quiz.mQuizType) {
            false
        } else true

    }

    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + answer!!.hashCode()
        result = 31 * result + mQuizType.hashCode()
        result = 31 * result + if (isSolved) 1 else 0
        return result
    }

    override fun toString(): String {
        return type.toString() + ": \"" + question + "\""
    }

    companion object {

        private val TAG = "Quiz"
        val CREATOR: Parcelable.Creator<Quiz<*>> = object : Parcelable.Creator<Quiz<*>> {
            override fun createFromParcel(`in`: Parcel): Quiz<*> {
                val ordinal = `in`.readInt()
                val type = QuizType.values()[ordinal]
                try {
                    val constructor = type.type
                            .getConstructor(Parcel::class.java)
                    return constructor.newInstance(`in`)
                } catch (e: InstantiationException) {
                    performLegacyCatch(e)
                } catch (e: IllegalAccessException) {
                    performLegacyCatch(e)
                } catch (e: InvocationTargetException) {
                    performLegacyCatch(e)
                } catch (e: NoSuchMethodException) {
                    performLegacyCatch(e)
                }

                throw UnsupportedOperationException("Could not create Quiz")
            }

            override fun newArray(size: Int): Array<Quiz<*>?> {
                return arrayOfNulls(size)
            }
        }

        private fun performLegacyCatch(e: Exception) {
            Log.e(TAG, "createFromParcel ", e)
        }
    }
}
