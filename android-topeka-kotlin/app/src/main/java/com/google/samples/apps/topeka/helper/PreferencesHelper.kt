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

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

import com.google.samples.apps.topeka.model.Avatar
import com.google.samples.apps.topeka.model.Player

/**
 * Easy storage and retrieval of preferences.
 */
object PreferencesHelper {

    private val PLAYER_PREFERENCES = "playerPreferences"
    private val PREFERENCE_FIRST_NAME = "$PLAYER_PREFERENCES.firstName"
    private val PREFERENCE_LAST_INITIAL = "$PLAYER_PREFERENCES.lastInitial"
    private val PREFERENCE_AVATAR = "$PLAYER_PREFERENCES.avatar"

    /**
     * Writes a [com.google.samples.apps.topeka.model.Player] to preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @param player  The [com.google.samples.apps.topeka.model.Player] to write.
     */
    fun writeToPreferences(context: Context, player: Player) {
        val editor = getEditor(context)
        editor.putString(PREFERENCE_FIRST_NAME, player.firstName)
        editor.putString(PREFERENCE_LAST_INITIAL, player.lastInitial)
        editor.putString(PREFERENCE_AVATAR, player.avatar.name)
        editor.apply()
    }

    /**
     * Retrieves a [com.google.samples.apps.topeka.model.Player] from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return A previously saved player or `null` if none was saved previously.
     */
    fun getPlayer(context: Context): Player? {
        val preferences = getSharedPreferences(context)
        val firstName = preferences.getString(PREFERENCE_FIRST_NAME, null)
        val lastInitial = preferences.getString(PREFERENCE_LAST_INITIAL, null)
        val avatarPreference = preferences.getString(PREFERENCE_AVATAR, null)
        val avatar: Avatar?
        if (null != avatarPreference) {
            avatar = Avatar.valueOf(avatarPreference)
        } else {
            avatar = null
        }

        return if (null == firstName || null == lastInitial || null == avatar) {
            null
        } else Player(firstName, lastInitial, avatar)
    }

    /**
     * Signs out a player by removing all it's data.
     *
     * @param context The context which to obtain the SharedPreferences from.
     */
    fun signOut(context: Context) {
        val editor = getEditor(context)
        editor.remove(PREFERENCE_FIRST_NAME)
        editor.remove(PREFERENCE_LAST_INITIAL)
        editor.remove(PREFERENCE_AVATAR)
        editor.apply()
    }

    /**
     * Checks whether a player is currently signed in.
     *
     * @param context The context to check this in.
     * @return `true` if login data exists, else `false`.
     */
    fun isSignedIn(context: Context): Boolean {
        val preferences = getSharedPreferences(context)
        return preferences.contains(PREFERENCE_FIRST_NAME) &&
                preferences.contains(PREFERENCE_LAST_INITIAL) &&
                preferences.contains(PREFERENCE_AVATAR)
    }

    /**
     * Checks whether the player's input data is valid.
     *
     * @param firstName   The player's first name to be examined.
     * @param lastInitial The player's last initial to be examined.
     * @return `true` if both strings are not null nor 0-length, else `false`.
     */
    fun isInputDataValid(firstName: CharSequence, lastInitial: CharSequence): Boolean {
        return !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastInitial)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        val preferences = getSharedPreferences(context)
        return preferences.edit()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PLAYER_PREFERENCES, Context.MODE_PRIVATE)
    }
}//no instance
