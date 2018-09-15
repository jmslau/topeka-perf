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
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.google.samples.apps.topeka.R

/**
 * A simple adapter to display a options of a quiz.
 */
class OptionsQuizAdapter : BaseAdapter {

    private val mOptions: Array<String>?
    private val mLayoutId: Int
    private val mAlphabet: Array<String>?

    /**
     * Creates an [OptionsQuizAdapter].
     *
     * @param options The options to add to the adapter.
     * @param layoutId Must consist of a single [TextView].
     */
    constructor(options: Array<String>?, @LayoutRes layoutId: Int) {
        mOptions = options
        mLayoutId = layoutId
        mAlphabet = null
    }

    /**
     * Creates an [OptionsQuizAdapter].
     *
     * @param options The options to add to the adapter.
     * @param layoutId Must consist of a single [TextView].
     * @param context The context for the adapter.
     * @param withPrefix True if a prefix should be given to all items.
     */
    constructor(options: Array<String>?, @LayoutRes layoutId: Int,
                context: Context, withPrefix: Boolean) {
        mOptions = options
        mLayoutId = layoutId
        if (withPrefix) {
            mAlphabet = context.resources.getStringArray(R.array.alphabet)
        } else {
            mAlphabet = null
        }
    }

    override fun getCount(): Int {
        return mOptions!!.size
    }

    override fun getItem(position: Int): String {
        return mOptions!![position]!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        /* Important to return tru ein order to get checked items from this adapter correctly */
        return true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            convertView = inflater.inflate(mLayoutId, parent, false)
        }
        val text = getText(position)
        (convertView as TextView).text = text
        return convertView
    }

    private fun getText(position: Int): String {
        val text: String
        if (mAlphabet == null) {
            text = getItem(position)
        } else {
            text = getPrefix(position) + getItem(position)
        }
        return text
    }

    private fun getPrefix(position: Int): String {
        val length = mAlphabet!!.size
        if (position >= length || 0 > position) {
            throw IllegalArgumentException(
                    "Only positions between 0 and $length are supported")
        }
        val prefix: StringBuilder
        if (position < length) {
            prefix = StringBuilder(mAlphabet[position])
        } else {
            val tmpPosition = position % length
            prefix = StringBuilder(tmpPosition)
            prefix.append(getPrefix(position - tmpPosition))
        }
        prefix.append(". ")
        return prefix.toString()
    }
}
