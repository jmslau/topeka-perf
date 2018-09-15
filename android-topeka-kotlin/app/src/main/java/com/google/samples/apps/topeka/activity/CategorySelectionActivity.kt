/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.google.samples.apps.topeka.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.databinding.ActivityCategorySelectionBinding
import com.google.samples.apps.topeka.fragment.CategorySelectionFragment
import com.google.samples.apps.topeka.helper.ApiLevelHelper
import com.google.samples.apps.topeka.helper.PreferencesHelper
import com.google.samples.apps.topeka.model.Player
import com.google.samples.apps.topeka.persistence.TopekaDatabaseHelper

class CategorySelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
                .setContentView<ActivityCategorySelectionBinding>(this, R.layout.activity_category_selection)
        var player: Player? = intent.getParcelableExtra(EXTRA_PLAYER)
        if (!PreferencesHelper.isSignedIn(this)) {
            if (player == null) {
                player = PreferencesHelper.getPlayer(this)
            } else {
                PreferencesHelper.writeToPreferences(this, player)
            }
        }
        binding.player = player
        setUpToolbar()
        if (savedInstanceState == null) {
            attachCategoryGridFragment()
        } else {
            setProgressBarVisibility(View.GONE)
        }
        supportPostponeEnterTransition()
    }

    override fun onResume() {
        super.onResume()
        val scoreView = findViewById<View>(R.id.score) as TextView
        val score = TopekaDatabaseHelper.getScore(this)
        scoreView.text = getString(R.string.x_points, score)
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar_player) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_category, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val fragment = supportFragmentManager.findFragmentById(R.id.category_container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NewApi")
    private fun signOut() {
        PreferencesHelper.signOut(this)
        TopekaDatabaseHelper.reset(this)
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            window.exitTransition = TransitionInflater.from(this)
                    .inflateTransition(R.transition.category_enter)
        }
        SignInActivity.start(this, false)
        finish()
    }

    private fun attachCategoryGridFragment() {
        val supportFragmentManager = supportFragmentManager
        var fragment = supportFragmentManager.findFragmentById(R.id.category_container)
        if (fragment !is CategorySelectionFragment) {
            fragment = CategorySelectionFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.category_container, fragment)
                .commit()
        setProgressBarVisibility(View.GONE)
    }

    private fun setProgressBarVisibility(visibility: Int) {
        findViewById<View>(R.id.progress).visibility = visibility
    }

    companion object {

        private val EXTRA_PLAYER = "player"

        fun start(activity: Activity, player: Player, options: ActivityOptionsCompat) {
            val starter = getStartIntent(activity, player)
            ActivityCompat.startActivity(activity, starter, options.toBundle())
        }

        fun start(context: Context, player: Player) {
            val starter = getStartIntent(context, player)
            context.startActivity(starter)
        }

        internal fun getStartIntent(context: Context, player: Player): Intent {
            val starter = Intent(context, CategorySelectionActivity::class.java)
            starter.putExtra(EXTRA_PLAYER, player)
            return starter
        }
    }
}

