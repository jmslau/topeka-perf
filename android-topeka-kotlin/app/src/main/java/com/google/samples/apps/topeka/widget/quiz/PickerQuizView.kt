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
import com.google.samples.apps.topeka.model.quiz.PickerQuiz

@SuppressLint("ViewConstructor")
class PickerQuizView(context: Context, category: Category, quiz: PickerQuiz) : AbsQuizView<PickerQuiz>(context, category, quiz) {

    private var mCurrentSelection: TextView? = null
    private var mSeekBar: SeekBar? = null
    private var mStep: Int = 0
    private var mMin: Int = 0
    private var mProgress: Int = 0

    protected override val isAnswerCorrect: Boolean
        get() = quiz.isAnswerCorrect(mProgress)

    override var userInput: Bundle
        get() {
            val bundle = Bundle()
            bundle.putInt(KEY_ANSWER, mProgress)
            return bundle
        }
        set(savedInput) {
            if (null == savedInput) {
                return
            }
            mSeekBar!!.progress = savedInput.getInt(KEY_ANSWER) - mMin
        }

    /**
     * Calculates the actual max value of the SeekBar
     */
    private val seekBarMax: Int
        get() {
            val absMin = Math.abs(quiz.min)
            val absMax = Math.abs(quiz.max)
            val realMin = Math.min(absMin, absMax)
            val realMax = Math.max(absMin, absMax)
            return realMax - realMin
        }

    override fun createQuizContentView(): View {
        initStep()
        mMin = quiz.min
        val layout = layoutInflater.inflate(
                R.layout.quiz_layout_picker, this, false) as ScrollView
        mCurrentSelection = layout.findViewById<View>(R.id.seekbar_progress) as TextView
        mCurrentSelection!!.text = mMin.toString()
        mSeekBar = layout.findViewById<View>(R.id.seekbar) as SeekBar
        mSeekBar!!.max = seekBarMax
        mSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                setCurrentSelectionText(mMin + progress)
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

    private fun setCurrentSelectionText(progress: Int) {
        mProgress = progress / mStep * mStep
        mCurrentSelection!!.text = mProgress.toString()
    }

    private fun initStep() {
        val tmpStep = quiz.step
        //make sure steps are never 0
        if (0 == tmpStep) {
            mStep = 1
        } else {
            mStep = tmpStep
        }
    }

    companion object {

        private val KEY_ANSWER = "ANSWER"
    }
}
