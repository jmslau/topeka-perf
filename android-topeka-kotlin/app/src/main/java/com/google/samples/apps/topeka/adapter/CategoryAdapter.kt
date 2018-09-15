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

import android.app.Activity
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.google.samples.apps.topeka.R
import com.google.samples.apps.topeka.databinding.ItemCategoryBinding
import com.google.samples.apps.topeka.helper.ApiLevelHelper
import com.google.samples.apps.topeka.model.Category
import com.google.samples.apps.topeka.persistence.TopekaDatabaseHelper

class CategoryAdapter(private val mActivity: Activity) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private val mResources: Resources
    private val mPackageName: String
    private val mLayoutInflater: LayoutInflater
    private var mCategories: List<Category>? = null

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    init {
        mResources = mActivity.resources
        mPackageName = mActivity.packageName
        mLayoutInflater = LayoutInflater.from(mActivity.applicationContext)
        updateCategories(mActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil
                .inflate<ViewDataBinding>(mLayoutInflater, R.layout.item_category, parent, false) as ItemCategoryBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val category = mCategories!![position]
        binding.category = category
        binding.executePendingBindings()
        setCategoryIcon(category, binding.categoryIcon)
        holder.itemView.setBackgroundColor(getColor(category.theme.windowBackgroundColor))
        binding.categoryTitle.setTextColor(getColor(category.theme.textPrimaryColor))
        binding.categoryTitle.setBackgroundColor(getColor(category.theme.primaryColor))
        holder.itemView.setOnClickListener { v -> mOnItemClickListener!!.onClick(v, holder.adapterPosition) }
    }

    override fun getItemId(position: Int): Long {
        return mCategories!![position].id.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return mCategories!!.size
    }

    fun getItem(position: Int): Category {
        return mCategories!![position]
    }

    /**
     * @see android.support.v7.widget.RecyclerView.Adapter.notifyItemChanged
     * @param id Id of changed category.
     */
    fun notifyItemChanged(id: String) {
        updateCategories(mActivity)
        notifyItemChanged(getItemPositionById(id))
    }

    private fun getItemPositionById(id: String): Int {
        for (i in mCategories!!.indices) {
            if (mCategories!![i].id == id) {
                return i
            }

        }
        return -1
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    private fun setCategoryIcon(category: Category, icon: ImageView) {
        val categoryImageResource = mResources.getIdentifier(
                ICON_CATEGORY + category.id, DRAWABLE, mPackageName)
        val solved = category.isSolved
        if (solved) {
            val solvedIcon = loadSolvedIcon(category, categoryImageResource)
            icon.setImageDrawable(solvedIcon)
        } else {
            icon.setImageResource(categoryImageResource)
        }
    }

    private fun updateCategories(activity: Activity) {
        mCategories = TopekaDatabaseHelper.getCategories(activity, true)
    }

    /**
     * Loads an icon that indicates that a category has already been solved.
     *
     * @param category The solved category to display.
     * @param categoryImageResource The category's identifying image.
     * @return The icon indicating that the category has been solved.
     */
    private fun loadSolvedIcon(category: Category, categoryImageResource: Int): Drawable {
        return if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            loadSolvedIconLollipop(category, categoryImageResource)
        } else loadSolvedIconPreLollipop(category, categoryImageResource)
    }

    private fun loadSolvedIconLollipop(category: Category, categoryImageResource: Int): LayerDrawable {
        val done = loadTintedDoneDrawable()
        val categoryIcon = loadTintedCategoryDrawable(category, categoryImageResource)
        val layers = arrayOf(categoryIcon, done) // ordering is back to front
        return LayerDrawable(layers)
    }

    private fun loadSolvedIconPreLollipop(category: Category, categoryImageResource: Int): Drawable {
        return loadTintedCategoryDrawable(category, categoryImageResource)
    }

    /**
     * Loads and tints a drawable.
     *
     * @param category The category providing the tint color
     * @param categoryImageResource The image resource to tint
     * @return The tinted resource
     */
    private fun loadTintedCategoryDrawable(category: Category, categoryImageResource: Int): Drawable {
        val categoryIcon = ContextCompat
                .getDrawable(mActivity, categoryImageResource)!!.mutate()
        return wrapAndTint(categoryIcon, category.theme.primaryColor)
    }

    /**
     * Loads and tints a check mark.
     *
     * @return The tinted check mark
     */
    private fun loadTintedDoneDrawable(): Drawable {
        val done = ContextCompat.getDrawable(mActivity, R.drawable.ic_tick)
        return wrapAndTint(done, android.R.color.white)
    }

    private fun wrapAndTint(done: Drawable?, @ColorRes color: Int): Drawable {
        val compatDrawable = DrawableCompat.wrap(done!!)
        DrawableCompat.setTint(compatDrawable, getColor(color))
        return compatDrawable
    }

    /**
     * Convenience method for color loading.
     *
     * @param colorRes The resource id of the color to load.
     * @return The loaded color.
     */
    private fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(mActivity, colorRes)
    }

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {

        val DRAWABLE = "drawable"
        private val ICON_CATEGORY = "icon_category_"
    }
}
