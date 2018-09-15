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

package com.google.samples.apps.topeka.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.model.Avatar
import com.google.samples.apps.topeka.widget.AvatarView

/**
 * Adapter to display [Avatar] icons.
 */
class AvatarAdapter(context: Context) : BaseAdapter() {

    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_avatar, parent, false)
        }
        setAvatar((convertView as AvatarView?)!!, mAvatars[position])
        return convertView
    }

    private fun setAvatar(mIcon: AvatarView, avatar: Avatar) {
        mIcon.setAvatar(avatar.drawableId)
        mIcon.contentDescription = avatar.nameForAccessibility
    }

    override fun getCount(): Int {
        return mAvatars.size
    }

    override fun getItem(position: Int): Any {
        return mAvatars[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {

        private val mAvatars = Avatar.values()
    }
}
