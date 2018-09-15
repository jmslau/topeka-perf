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

package com.google.samples.apps.topeka.widget.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.AlphaPickerQuiz

import java.util.Arrays

@SuppressLint("ViewConstructor")
class AlphaPickerQuizView(context: Context, category: Category, quiz: AlphaPickerQuiz) : AbsQuizView<AlphaPickerQuiz>(context, category, quiz) {

    private var mCurrentSelection: TextView? = null
    private var mSeekBar: SeekBar? = null
    private var mAlphabet: List<String>? = null

    protected override val isAnswerCorrect: Boolean
        get() = quiz.isAnswerCorrect(mCurrentSelection!!.text.toString())

    override var userInput: Bundle
        get() {
            val bundle = Bundle()
            bundle.putString(KEY_SELECTION, mCurrentSelection!!.text.toString())
            return bundle
        }
        set(savedInput) {
            if (savedInput == null) {
                return
            }
            val userInput = savedInput.getString(KEY_SELECTION, alphabet[0])
            mSeekBar!!.progress = alphabet.indexOf(userInput)
        }


    private val alphabet: List<String>
        get() {
            if (null == mAlphabet) {
                mAlphabet = Arrays.asList(*resources.getStringArray(R.array.alphabet))
            }
            return mAlphabet!!
        }

    override fun createQuizContentView(): View {
        val layout = layoutInflater.inflate(
                R.layout.quiz_layout_picker, this, false) as ScrollView
        mCurrentSelection = layout.findViewById<View>(R.id.seekbar_progress) as TextView
        mCurrentSelection!!.text = alphabet[0]
        mSeekBar = layout.findViewById<View>(R.id.seekbar) as SeekBar
        mSeekBar!!.max = alphabet.size - 1
        mSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mCurrentSelection!!.text = alphabet[progress]
                allowAnswer()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                /* no-op */
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                /* no-op */
            }
        })
        return layout
    }

    companion object {

        private val KEY_SELECTION = "SELECTION"
    }
}
