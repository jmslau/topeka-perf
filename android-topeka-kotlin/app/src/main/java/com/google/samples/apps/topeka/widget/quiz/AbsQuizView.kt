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

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v4.view.MarginLayoutParamsCompat
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.util.Property
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.activity.QuizActivity
import com.google.samples.apps.topeka.helper.ApiLevelHelper
import com.google.samples.apps.topeka.helper.ViewUtils
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.Quiz
import com.google.samples.apps.topeka.widget.fab.CheckableFab

/**
 * This is the base class for displaying a [com.google.samples.apps.topeka.model.quiz.Quiz].
 *
 *
 * Subclasses need to implement [AbsQuizView.createQuizContentView]
 * in order to allow solution of a quiz.
 *
 *
 *
 * Also [AbsQuizView.allowAnswer] needs to be called with
 * `true` in order to mark the quiz solved.
 *
 *
 * @param <Q> The type of [com.google.samples.apps.topeka.model.quiz.Quiz] you want to
 * display.
</Q> */
abstract class AbsQuizView<Q : Quiz<*>>
/**
 * Enables creation of views for quizzes.
 *
 * @param context The context for this view.
 * @param category The [Category] this view is running in.
 * @param quiz The actual [Quiz] that is going to be displayed.
 */
(context: Context, private val mCategory: Category, val quiz: Q) : FrameLayout(context) {
    private val mSpacingDouble: Int
    protected val layoutInflater: LayoutInflater
    private val mLinearOutSlowInInterpolator: Interpolator
    private val mHandler: Handler
    private val mInputMethodManager: InputMethodManager
    protected var isAnswered: Boolean = false
        private set
    private var mQuestionView: TextView? = null
    private var mSubmitAnswer: CheckableFab? = null
    private var mHideFabRunnable: Runnable? = null
    private var mMoveOffScreenRunnable: Runnable? = null

    private val initializedContentView: View
        get() {
            val quizContentView = createQuizContentView()
            quizContentView.id = R.id.quiz_content
            quizContentView.isSaveEnabled = true
            setDefaultPadding(quizContentView)
            if (quizContentView is ViewGroup) {
                quizContentView.clipToPadding = false
            }
            setMinHeightInternal(quizContentView)
            return quizContentView
        }

    private val submitButton: CheckableFab
        get() {
            if (null == mSubmitAnswer) {
                mSubmitAnswer = layoutInflater
                        .inflate(R.layout.answer_submit, this, false) as CheckableFab
                mSubmitAnswer!!.hide()
                mSubmitAnswer!!.setOnClickListener { v ->
                    submitAnswer(v)
                    if (mInputMethodManager.isAcceptingText) {
                        mInputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                    mSubmitAnswer!!.isEnabled = false
                }
            }
            return mSubmitAnswer as CheckableFab
        }

    /**
     * Implementations must make sure that the answer provided is evaluated and correctly rated.
     *
     * @return `true` if the question has been correctly answered, else
     * `false`.
     */
    protected abstract val isAnswerCorrect: Boolean

    /**
     * Save the user input to a bundle for orientation changes.
     *
     * @return The bundle containing the user's input.
     */
    /**
     * Restore the user's input.
     *
     * @param savedInput The input that the user made in a prior instance of this view.
     */
    abstract var userInput: Bundle

    init {
        mSpacingDouble = resources.getDimensionPixelSize(R.dimen.spacing_double)
        layoutInflater = LayoutInflater.from(context)
        mSubmitAnswer = submitButton
        mLinearOutSlowInInterpolator = LinearOutSlowInInterpolator()
        mHandler = Handler()
        mInputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        id = quiz.id
        setUpQuestionView()
        val container = createContainerLayout(context)
        val quizContentView = initializedContentView
        addContentView(container, quizContentView)
        addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int,
                                        oldLeft: Int,
                                        oldTop: Int, oldRight: Int, oldBottom: Int) {
                removeOnLayoutChangeListener(this)
                addFloatingActionButton()
            }
        })
    }

    /**
     * Sets the behaviour for all question views.
     */
    private fun setUpQuestionView() {
        mQuestionView = layoutInflater.inflate(R.layout.question, this, false) as TextView
        mQuestionView!!.setBackgroundColor(ContextCompat.getColor(context,
                mCategory.theme.primaryColor))
        mQuestionView!!.text = quiz.question
    }

    private fun createContainerLayout(context: Context): LinearLayout {
        val container = LinearLayout(context)
        container.id = R.id.absQuizViewContainer
        container.orientation = LinearLayout.VERTICAL
        return container
    }

    private fun addContentView(container: LinearLayout, quizContentView: View) {
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
        container.addView(mQuestionView, layoutParams)
        container.addView(quizContentView, layoutParams)
        addView(container, layoutParams)
    }

    private fun addFloatingActionButton() {
        val fabSize = resources.getDimensionPixelSize(R.dimen.size_fab)
        val bottomOfQuestionView = findViewById<View>(R.id.question_view).bottom
        val fabLayoutParams = FrameLayout.LayoutParams(fabSize, fabSize,
                Gravity.END or Gravity.TOP)
        val halfAFab = fabSize / 2
        fabLayoutParams.setMargins(0, // left
                bottomOfQuestionView - halfAFab, //top
                0, // right
                mSpacingDouble) // bottom
        MarginLayoutParamsCompat.setMarginEnd(fabLayoutParams, mSpacingDouble)
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            // Account for the fab's emulated shadow.
            fabLayoutParams.topMargin -= mSubmitAnswer!!.paddingTop / 2
        }
        addView(mSubmitAnswer, fabLayoutParams)
    }

    private fun setDefaultPadding(view: View) {
        view.setPadding(mSpacingDouble, mSpacingDouble, mSpacingDouble, mSpacingDouble)
    }

    /**
     * Implementations should create the content view for the type of
     * [com.google.samples.apps.topeka.model.quiz.Quiz] they want to display.
     *
     * @return the created view to solve the quiz.
     */
    protected abstract fun createQuizContentView(): View

    /**
     * Sets the quiz to answered or unanswered.
     *
     * @param answered `true` if an answer was selected, else `false`.
     */
    protected fun allowAnswer(answered: Boolean) {
        if (null != mSubmitAnswer) {
            if (answered) {
                mSubmitAnswer!!.show()
            } else {
                mSubmitAnswer!!.hide()
            }
            isAnswered = answered
        }
    }

    /**
     * Sets the quiz to answered if it not already has been answered.
     * Otherwise does nothing.
     */
    protected fun allowAnswer() {
        if (!isAnswered) {
            allowAnswer(true)
        }
    }

    /**
     * Allows children to submit an answer via code.
     */
    protected open fun submitAnswer() {
        submitAnswer(findViewById(R.id.submitAnswer))
    }

    private fun submitAnswer(v: View) {
        val answerCorrect = isAnswerCorrect
        quiz.isSolved = true
        performScoreAnimation(answerCorrect)
    }

    /**
     * Animates the view nicely when the answer has been submitted.
     *
     * @param answerCorrect `true` if the answer was correct, else `false`.
     */
    private fun performScoreAnimation(answerCorrect: Boolean) {
        (context as QuizActivity).lockIdlingResource()
        // Decide which background color to use.
        val backgroundColor = ContextCompat.getColor(context,
                if (answerCorrect) R.color.green else R.color.red)
        adjustFab(answerCorrect, backgroundColor)
        resizeView()
        moveViewOffScreen(answerCorrect)
        // Animate the foreground color to match the background color.
        // This overlays all content within the current view.
        animateForegroundColor(backgroundColor)
    }

    @SuppressLint("NewApi")
    private fun adjustFab(answerCorrect: Boolean, backgroundColor: Int) {
        mSubmitAnswer!!.isChecked = answerCorrect
        mSubmitAnswer!!.backgroundTintList = ColorStateList.valueOf(backgroundColor)
        mHideFabRunnable = Runnable { mSubmitAnswer!!.hide() }
        mHandler.postDelayed(mHideFabRunnable, ANSWER_HIDE_DELAY.toLong())
    }

    private fun resizeView() {
        val widthHeightRatio = height.toFloat() / width.toFloat()
        // Animate X and Y scaling separately to allow different start delays.
        // object animators for x and y with different durations and then run them independently
        resizeViewProperty(View.SCALE_X, .5f, 200)
        resizeViewProperty(View.SCALE_Y, .5f / widthHeightRatio, 300)
    }

    private fun resizeViewProperty(property: Property<View, Float>,
                                   targetScale: Float, durationOffset: Int) {
        val animator = ObjectAnimator.ofFloat(this, property,
                1f, targetScale)
        animator.interpolator = mLinearOutSlowInInterpolator
        animator.startDelay = (FOREGROUND_COLOR_CHANGE_DELAY + durationOffset).toLong()
        animator.start()
    }

    override fun onDetachedFromWindow() {
        if (mHideFabRunnable != null) {
            mHandler.removeCallbacks(mHideFabRunnable)
        }
        if (mMoveOffScreenRunnable != null) {
            mHandler.removeCallbacks(mMoveOffScreenRunnable)
        }
        super.onDetachedFromWindow()
    }

    private fun animateForegroundColor(@ColorInt targetColor: Int) {
        val animator = ObjectAnimator.ofInt(this, ViewUtils.FOREGROUND_COLOR,
                Color.TRANSPARENT, targetColor)
        animator.setEvaluator(ArgbEvaluator())
        animator.startDelay = FOREGROUND_COLOR_CHANGE_DELAY.toLong()
        animator.start()
    }

    private fun moveViewOffScreen(answerCorrect: Boolean) {
        // Move the current view off the screen.
        mMoveOffScreenRunnable = Runnable {
            mCategory.setScore(quiz, answerCorrect)
            if (context is QuizActivity) {
                (context as QuizActivity).proceed()
            }
        }
        mHandler.postDelayed(mMoveOffScreenRunnable,
                (FOREGROUND_COLOR_CHANGE_DELAY * 2).toLong())
    }

    private fun setMinHeightInternal(view: View) {
        view.minimumHeight = resources.getDimensionPixelSize(R.dimen.min_height_question)
    }

    protected fun setUpUserListSelection(listView: AbsListView, index: Int) {
        listView.post {
            listView.requestFocusFromTouch()
            listView.performItemClick(listView.getChildAt(index), index,
                    listView.adapter.getItemId(index))
            listView.setSelection(index)
        }
    }

    companion object {

        private val ANSWER_HIDE_DELAY = 500
        private val FOREGROUND_COLOR_CHANGE_DELAY = 750
    }
}
