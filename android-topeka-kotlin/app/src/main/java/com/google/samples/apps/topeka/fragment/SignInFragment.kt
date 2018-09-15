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

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.text.Editable
import android.text.TextWatcher
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.GridView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.activity.CategorySelectionActivity
import com.google.samples.apps.topeka.adapter.AvatarAdapter
import com.google.samples.apps.topeka.helper.ApiLevelHelper
import com.google.samples.apps.topeka.helper.PreferencesHelper
import com.google.samples.apps.topeka.helper.TransitionHelper
import com.google.samples.apps.topeka.model.Avatar
import com.google.samples.apps.topeka.model.Player
import com.google.samples.apps.topeka.widget.TransitionListenerAdapter

/**
 * Enable selection of an [Avatar] and user name.
 */
class SignInFragment : Fragment() {
    private var mPlayer: Player? = null
    private var mFirstName: EditText? = null
    private var mLastInitial: EditText? = null
    private var mSelectedAvatar: Avatar? = null
    private var mSelectedAvatarView: View? = null
    private var mAvatarGrid: GridView? = null
    private var mDoneFab: FloatingActionButton? = null
    private var edit: Boolean = false

    private val isAvatarSelected: Boolean
        get() = mSelectedAvatarView != null || mSelectedAvatar != null

    private val isInputDataValid: Boolean
        get() = PreferencesHelper.isInputDataValid(mFirstName!!.text, mLastInitial!!.text)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val savedAvatarIndex = savedInstanceState.getInt(KEY_SELECTED_AVATAR_INDEX)
            if (savedAvatarIndex != GridView.INVALID_POSITION) {
                mSelectedAvatar = Avatar.values()[savedAvatarIndex]
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_sign_in, container, false)
        contentView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int,
                                        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)
                setUpGridView(view!!)
            }
        })
        return contentView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mAvatarGrid != null) {
            outState.putInt(KEY_SELECTED_AVATAR_INDEX, mAvatarGrid!!.checkedItemPosition)
        } else {
            outState.putInt(KEY_SELECTED_AVATAR_INDEX, GridView.INVALID_POSITION)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        assurePlayerInit()
        checkIsInEditMode()

        if (mPlayer == null || edit) {
            view.findViewById<View>(R.id.empty).visibility = View.GONE
            view.findViewById<View>(R.id.content).visibility = View.VISIBLE
            initContentViews(view)
            initContents()
        } else {
            val activity = activity
            CategorySelectionActivity.start(activity!!, mPlayer!!)
            activity.finish()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun checkIsInEditMode() {
        val arguments = arguments

        if (arguments == null) {
            edit = false
        } else {
            edit = arguments.getBoolean(ARG_EDIT, false)
        }
    }

    private fun initContentViews(view: View) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                /* no-op */
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // hiding the floating action button if text is empty
                if (s.length == 0) {
                    mDoneFab!!.hide()
                }
            }

            override fun afterTextChanged(s: Editable) {
                // showing the floating action button if avatar is selected and input data is valid
                if (isAvatarSelected && isInputDataValid) {
                    mDoneFab!!.show()
                }
            }
        }

        mFirstName = view.findViewById<View>(R.id.first_name) as EditText
        mFirstName!!.addTextChangedListener(textWatcher)
        mLastInitial = view.findViewById<View>(R.id.last_initial) as EditText
        mLastInitial!!.addTextChangedListener(textWatcher)
        mDoneFab = view.findViewById<View>(R.id.done) as FloatingActionButton
        mDoneFab!!.setOnClickListener { v ->
            when (v.id) {
                R.id.done -> {
                    savePlayer(activity)
                    removeDoneFab(Runnable {
                        if (null == mSelectedAvatarView) {
                            performSignInWithTransition(mAvatarGrid!!.getChildAt(
                                    mSelectedAvatar!!.ordinal))
                        } else {
                            performSignInWithTransition(mSelectedAvatarView)
                        }
                    })
                }
                else -> throw UnsupportedOperationException(
                        "The onClick method has not been implemented for " + resources
                                .getResourceEntryName(v.id))
            }
        }
    }

    private fun removeDoneFab(endAction: Runnable?) {
        ViewCompat.animate(mDoneFab)
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(FastOutSlowInInterpolator())
                .withEndAction(endAction)
                .start()
    }

    private fun setUpGridView(container: View) {
        mAvatarGrid = container.findViewById<View>(R.id.avatars) as GridView
        mAvatarGrid!!.adapter = AvatarAdapter(activity!!)
        mAvatarGrid!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            mSelectedAvatarView = view
            mSelectedAvatar = Avatar.values()[position]
            // showing the floating action button if input data is valid
            if (isInputDataValid) {
                mDoneFab!!.show()
            }
        }
        mAvatarGrid!!.numColumns = calculateSpanCount()
        if (mSelectedAvatar != null) {
            mAvatarGrid!!.setItemChecked(mSelectedAvatar!!.ordinal, true)
        }
    }

    private fun performSignInWithTransition(v: View?) {
        val activity = activity
        if (v == null || ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            // Don't run a transition if the passed view is null
            CategorySelectionActivity.start(activity!!, mPlayer!!)
            activity.finish()
            return
        }

        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            activity!!.window.sharedElementExitTransition.addListener(
                    object : TransitionListenerAdapter() {
                        override fun onTransitionEnd(transition: Transition) {
                            activity.finish()
                        }
                    })

            val pairs = TransitionHelper.createSafeTransitionParticipants(activity, true,
                    Pair(v, activity.getString(R.string.transition_avatar)))
            val activityOptions = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity, *pairs)
            CategorySelectionActivity.start(activity, mPlayer!!, activityOptions)
        }
    }

    private fun initContents() {
        assurePlayerInit()
        if (mPlayer != null) {
            mFirstName!!.setText(mPlayer!!.firstName)
            mLastInitial!!.setText(mPlayer!!.lastInitial)
            mSelectedAvatar = mPlayer!!.avatar
        }
    }

    private fun assurePlayerInit() {
        if (mPlayer == null) {
            mPlayer = PreferencesHelper.getPlayer(activity!!)
        }
    }

    private fun savePlayer(activity: Activity?) {
        mPlayer = Player(mFirstName!!.text.toString(), mLastInitial!!.text.toString(),
                mSelectedAvatar!!)
        PreferencesHelper.writeToPreferences(activity!!, mPlayer!!)
    }

    /**
     * Calculates spans for avatars dynamically.
     *
     * @return The recommended amount of columns.
     */
    private fun calculateSpanCount(): Int {
        val avatarSize = resources.getDimensionPixelSize(R.dimen.size_fab)
        val avatarPadding = resources.getDimensionPixelSize(R.dimen.spacing_double)
        return mAvatarGrid!!.width / (avatarSize + avatarPadding)
    }

    companion object {

        private val ARG_EDIT = "EDIT"
        private val KEY_SELECTED_AVATAR_INDEX = "selectedAvatarIndex"

        fun newInstance(edit: Boolean): SignInFragment {
            val args = Bundle()
            args.putBoolean(ARG_EDIT, edit)
            val fragment = SignInFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
