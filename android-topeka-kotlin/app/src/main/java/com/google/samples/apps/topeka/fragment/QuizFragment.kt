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

package com.google.samples.apps.topeka.fragment

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterViewAnimator
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.adapter.QuizAdapter
import com.google.samples.apps.topeka.adapter.ScoreAdapter
import com.google.samples.apps.topeka.helper.ApiLevelHelper
import com.google.samples.apps.topeka.helper.PreferencesHelper
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.Player
import com.google.samples.apps.topeka.model.Theme
import com.google.samples.apps.topeka.model.quiz.Quiz
import com.google.samples.apps.topeka.persistence.TopekaDatabaseHelper
import com.google.samples.apps.topeka.widget.AvatarView
import com.google.samples.apps.topeka.widget.quiz.AbsQuizView

/**
 * Encapsulates Quiz solving and displays it to the user.
 */
class QuizFragment : android.support.v4.app.Fragment() {
    private var mProgressText: TextView? = null
    private var mQuizSize: Int = 0
    private var mProgressBar: ProgressBar? = null
    private var mCategory: Category? = null
    private var mQuizView: AdapterViewAnimator? = null
    private var mScoreAdapter: ScoreAdapter? = null
    private var mQuizAdapter: QuizAdapter? = null
    private var mSolvedStateListener: SolvedStateListener? = null

    private val quizAdapter: QuizAdapter
        get() {
            if (null == mQuizAdapter) {
                mQuizAdapter = QuizAdapter(activity!!, mCategory!!)
            }
            return mQuizAdapter!!
        }

    private val scoreAdapter: ScoreAdapter
        get() {
            if (null == mScoreAdapter) {
                mScoreAdapter = ScoreAdapter(mCategory!!)
            }
            return mScoreAdapter!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val categoryId = arguments!!.getString(Category.TAG)
        mCategory = TopekaDatabaseHelper.getCategoryWith(activity!!, categoryId!!)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Create a themed Context and custom LayoutInflater
        // to get nicely themed views in this Fragment.
        val theme = mCategory!!.theme
        val context = ContextThemeWrapper(activity,
                theme.styleId)
        val themedInflater = LayoutInflater.from(context)
        return themedInflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mQuizView = view.findViewById<View>(R.id.quiz_view) as AdapterViewAnimator
        decideOnViewToDisplay()
        setQuizViewAnimations()
        val avatar = view.findViewById<View>(R.id.avatar) as AvatarView
        setAvatarDrawable(avatar)
        initProgressToolbar(view)
        super.onViewCreated(view, savedInstanceState)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setQuizViewAnimations() {
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            return
        }
        mQuizView!!.setInAnimation(activity, R.animator.slide_in_bottom)
        mQuizView!!.setOutAnimation(activity, R.animator.slide_out_top)
    }

    private fun initProgressToolbar(view: View) {
        val firstUnsolvedQuizPosition = mCategory!!.firstUnsolvedQuizPosition
        val quizzes = mCategory!!.quizzes
        mQuizSize = quizzes!!.size
        mProgressText = view.findViewById<View>(R.id.progress_text) as TextView
        mProgressBar = view.findViewById<View>(R.id.progress) as ProgressBar
        mProgressBar!!.max = mQuizSize

        setProgress(firstUnsolvedQuizPosition)
    }

    private fun setProgress(currentQuizPosition: Int) {
        if (!isAdded) {
            return
        }
        mProgressText!!.text = getString(R.string.quiz_of_quizzes, currentQuizPosition, mQuizSize)
        mProgressBar!!.progress = currentQuizPosition
    }

    private fun setAvatarDrawable(avatarView: AvatarView) {
        val player = PreferencesHelper.getPlayer(activity!!)
        avatarView.setAvatar(player!!.avatar.drawableId)
        ViewCompat.animate(avatarView)
                .setInterpolator(FastOutLinearInInterpolator())
                .setStartDelay(500)
                .scaleX(1f)
                .scaleY(1f)
                .start()
    }

    private fun decideOnViewToDisplay() {
        val isSolved = mCategory!!.isSolved
        if (isSolved) {
            showSummary()
            if (null != mSolvedStateListener) {
                mSolvedStateListener!!.onCategorySolved()
            }
        } else {
            mQuizView!!.adapter = quizAdapter
            mQuizView!!.setSelection(mCategory!!.firstUnsolvedQuizPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val childView = mQuizView!!.currentView
        if (childView is ViewGroup) {
            val currentView = childView.getChildAt(0)
            if (currentView is AbsQuizView<*>) {
                outState.putBundle(KEY_USER_INPUT, currentView.userInput)
            }
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        restoreQuizState(savedInstanceState)
        super.onViewStateRestored(savedInstanceState)
    }

    private fun restoreQuizState(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            return
        }
        mQuizView!!.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int,
                                        oldLeft: Int,
                                        oldTop: Int, oldRight: Int, oldBottom: Int) {
                mQuizView!!.removeOnLayoutChangeListener(this)
                val currentChild = mQuizView!!.getChildAt(0)
                if (currentChild is ViewGroup) {
                    val potentialQuizView = currentChild.getChildAt(0)
                    if (potentialQuizView is AbsQuizView<*>) {
                        potentialQuizView.userInput = savedInstanceState.getBundle(KEY_USER_INPUT)
                    }
                }
            }
        })

    }

    /**
     * Displays the next page.
     *
     * @return `true` if there's another quiz to solve, else `false`.
     */
    fun showNextPage(): Boolean {
        if (null == mQuizView) {
            return false
        }
        val nextItem = mQuizView!!.displayedChild + 1
        setProgress(nextItem)
        val count = mQuizView!!.adapter.count
        if (nextItem < count) {
            mQuizView!!.showNext()
            TopekaDatabaseHelper.updateCategory(activity!!, mCategory!!)
            return true
        }
        markCategorySolved()
        return false
    }

    private fun markCategorySolved() {
        mCategory!!.isSolved = true
        TopekaDatabaseHelper.updateCategory(activity!!, mCategory!!)
    }

    fun showSummary() {
        val scorecardView = view!!.findViewById<View>(R.id.scorecard) as ListView
        mScoreAdapter = scoreAdapter
        scorecardView.adapter = mScoreAdapter
        scorecardView.visibility = View.VISIBLE
        mQuizView!!.visibility = View.GONE
    }

    fun hasSolvedStateListener(): Boolean {
        return mSolvedStateListener != null
    }

    fun setSolvedStateListener(solvedStateListener: SolvedStateListener) {
        mSolvedStateListener = solvedStateListener
        if (mCategory!!.isSolved && null != mSolvedStateListener) {
            mSolvedStateListener!!.onCategorySolved()
        }
    }

    /**
     * Interface definition for a callback to be invoked when the quiz is started.
     */
    interface SolvedStateListener {

        /**
         * This method will be invoked when the category has been solved.
         */
        fun onCategorySolved()
    }

    companion object {

        private val KEY_USER_INPUT = "USER_INPUT"

        fun newInstance(categoryId: String?,
                        solvedStateListener: SolvedStateListener?): QuizFragment {
            if (categoryId == null) {
                throw IllegalArgumentException("The category can not be null")
            }
            val args = Bundle()
            args.putString(Category.TAG, categoryId)
            val fragment = QuizFragment()
            if (solvedStateListener != null) {
                fragment.mSolvedStateListener = solvedStateListener
            }
            fragment.arguments = args
            return fragment
        }
    }
}