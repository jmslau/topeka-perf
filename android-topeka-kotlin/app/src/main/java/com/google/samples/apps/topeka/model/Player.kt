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

package com.google.samples.apps.topeka.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Stores values to identify the subject that is currently attempting to solve quizzes.
 */
class Player : Parcelable {
    val firstName: String
    val lastInitial: String
    val avatar: Avatar

    constructor(firstName: String, lastInitial: String, avatar: Avatar) {
        this.firstName = firstName
        this.lastInitial = lastInitial
        this.avatar = avatar
    }

    constructor(`in`: Parcel) {
        firstName = `in`.readString()
        lastInitial = `in`.readString()
        avatar = Avatar.values()[`in`.readInt()]
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(firstName)
        dest.writeString(lastInitial)
        dest.writeInt(avatar.ordinal)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val player = o as Player?

        if (avatar != player!!.avatar) {
            return false
        }
        if (firstName != player.firstName) {
            return false
        }
        return if (lastInitial != player.lastInitial) {
            false
        } else true

    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + lastInitial.hashCode()
        result = 31 * result + avatar.hashCode()
        return result
    }

    companion object {



        val CREATOR: Parcelable.Creator<Player> = object : Parcelable.Creator<Player> {
            override fun createFromParcel(`in`: Parcel): Player {
                return Player(`in`)
            }

            override fun newArray(size: Int): Array<Player?> {
                return arrayOfNulls(size)
            }
        }
    }
}
