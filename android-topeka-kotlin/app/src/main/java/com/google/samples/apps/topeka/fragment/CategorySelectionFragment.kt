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
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.activity.QuizActivity
import com.google.samples.apps.topeka.adapter.CategoryAdapter
import com.google.samples.apps.topeka.helper.TransitionHelper
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.JsonAttributes
import com.google.samples.apps.topeka.widget.OffsetDecoration

class CategorySelectionFragment : Fragment() {

    private var mAdapter: CategoryAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpQuizGrid(view.findViewById<View>(R.id.categories) as RecyclerView)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpQuizGrid(categoriesView: RecyclerView) {
        val spacing = context!!.resources
                .getDimensionPixelSize(R.dimen.spacing_nano)
        categoriesView.addItemDecoration(OffsetDecoration(spacing))
        mAdapter = CategoryAdapter(activity!!)
        mAdapter!!.setOnItemClickListener(
                object : CategoryAdapter.OnItemClickListener {
                    override fun onClick(v: View, position: Int) {
                        val activity = activity
                        startQuizActivityWithTransition(activity,
                                v.findViewById(R.id.category_title),
                                mAdapter!!.getItem(position))
                    }
                })
        categoriesView.adapter = mAdapter
        categoriesView.viewTreeObserver
                .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        categoriesView.viewTreeObserver.removeOnPreDrawListener(this)
                        activity!!.supportStartPostponedEnterTransition()
                        return true
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CATEGORY && resultCode == R.id.solved) {
            mAdapter!!.notifyItemChanged(data!!.getStringExtra(JsonAttributes.ID))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startQuizActivityWithTransition(activity: Activity?, toolbar: View,
                                                category: Category) {

        val pairs = TransitionHelper.createSafeTransitionParticipants(activity!!, false,
                Pair(toolbar, activity.getString(R.string.transition_toolbar)))
        val sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, *pairs)

        // Start the activity with the participants, animating from one to the other.
        val transitionBundle = sceneTransitionAnimation.toBundle()
        val startIntent = QuizActivity.getStartIntent(activity, category)
        ActivityCompat.startActivityForResult(activity,
                startIntent,
                REQUEST_CATEGORY,
                transitionBundle)
    }

    companion object {
        private val REQUEST_CATEGORY = 0x2300

        fun newInstance(): CategorySelectionFragment {
            return CategorySelectionFragment()
        }
    }

}
