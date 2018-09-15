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

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.model.quiz.Quiz

abstract class TextInputQuizView<Q : Quiz<*>>(context: Context, category: Category, quiz: Q) : AbsQuizView<Q>(context, category, quiz), TextWatcher, TextView.OnEditorActionListener {

    private val inputMethodManager: InputMethodManager
        get() = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    protected fun createEditText(): EditText {
        val editText = layoutInflater.inflate(
                R.layout.quiz_edit_text, this, false) as EditText
        editText.addTextChangedListener(this)
        editText.setOnEditorActionListener(this)
        return editText
    }

    override fun submitAnswer() {
        hideKeyboard(this)
        super.submitAnswer()
    }

    /**
     * Convenience method to hide the keyboard.
     *
     * @param view A view in the hierarchy.
     */
    protected fun hideKeyboard(view: View) {
        val inputMethodManager = inputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        if (TextUtils.isEmpty(v.text)) {
            return false
        }
        allowAnswer()
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            submitAnswer()
            hideKeyboard(v)
            return true
        }
        return false
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        /* no-op */
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        /* no-op */
    }

    override fun afterTextChanged(s: Editable) {
        allowAnswer(!TextUtils.isEmpty(s))
    }
}
