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

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.support.v4.util.Pair
import android.view.View

import java.util.ArrayList
import java.util.Arrays

/**
 * Helper class for creating content transitions used with [android.app.ActivityOptions].
 */
object TransitionHelper {
    /**
     * Create the transition participants required during a activity transition while
     * avoiding glitches with the system UI.
     *
     * @param activity The activity used as start for the transition.
     * @param includeStatusBar If false, the status bar will not be added as the transition
     * participant.
     * @return All transition participants.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun createSafeTransitionParticipants(activity: Activity,
                                         includeStatusBar: Boolean,
                                         vararg otherParticipants: Pair<View, String>): Array<Pair<View, String>> {
        // Avoid system UI glitches as described here:
        // https://plus.google.com/+AlexLockwood/posts/RPtwZ5nNebb
        val decor = activity.window.decorView
        var statusBar: View? = null
        if (includeStatusBar) {
            statusBar = decor.findViewById(android.R.id.statusBarBackground)
        }
        val navBar = decor.findViewById<View>(android.R.id.navigationBarBackground)

        // Create pair of transition participants.
        val participants = ArrayList<Pair<View, String>>(3)
        addNonNullViewToTransitionParticipants(statusBar, participants as MutableList<Pair<*, *>>)
        addNonNullViewToTransitionParticipants(navBar, participants as MutableList<Pair<*, *>>)
        // only add transition participants if there's at least one none-null element
        if (otherParticipants != null && !(otherParticipants.size == 1 && otherParticipants[0] == null)) {
            participants.addAll(Arrays.asList<Pair<View, String>>(*otherParticipants))
        }

        return participants.toTypedArray<Pair<View, String>>()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addNonNullViewToTransitionParticipants(view: View?, participants: MutableList<Pair<*, *>>) {
        if (view == null) {
            return
        }
        participants.add(Pair(view, view.transitionName))
    }

}//no instance
