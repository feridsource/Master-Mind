/*
 * Copyright (C) 2016 Ferid Cafer
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

package com.ferid.app.mastermind.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ferid.app.mastermind.R
import com.ferid.app.mastermind.models.Chance
import kotlinx.android.synthetic.main.item_chance.view.*

class ChanceAdapter(private val items: ArrayList<Chance>)
    : RecyclerView.Adapter<ChanceAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(item: Chance) = with(itemView) {
            itemView.hole1.setImageDrawable(ContextCompat.getDrawable(context, item.chanceColorIds[0]))
            itemView.hole2.setImageDrawable(ContextCompat.getDrawable(context, item.chanceColorIds[1]))
            itemView.hole3.setImageDrawable(ContextCompat.getDrawable(context, item.chanceColorIds[2]))
            itemView.hole4.setImageDrawable(ContextCompat.getDrawable(context, item.chanceColorIds[3]))

            itemView.result1.setImageDrawable(ContextCompat.getDrawable(context, item.resultColorIds[0]))
            itemView.result2.setImageDrawable(ContextCompat.getDrawable(context, item.resultColorIds[1]))
            itemView.result3.setImageDrawable(ContextCompat.getDrawable(context, item.resultColorIds[2]))
            itemView.result4.setImageDrawable(ContextCompat.getDrawable(context, item.resultColorIds[3]))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chance, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}