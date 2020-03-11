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

package com.ferid.app.mastermind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.ferid.app.mastermind.R;
import com.ferid.app.mastermind.models.Chance;

import java.util.ArrayList;

/**
 * Created by Ferid Cafer on 12/17/2015.
 */
public class ChanceAdapter extends RecyclerView.Adapter<ChanceAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Chance> chanceList;

    public ChanceAdapter(Context context, ArrayList<Chance> chanceList) {
        this.context = context;
        this.chanceList = chanceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chance_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Chance item = chanceList.get(position);

        viewHolder.hole1.setImageDrawable(context.getResources().getDrawable(item.getChanceColourIds()[0]));
        viewHolder.hole2.setImageDrawable(context.getResources().getDrawable(item.getChanceColourIds()[1]));
        viewHolder.hole3.setImageDrawable(context.getResources().getDrawable(item.getChanceColourIds()[2]));
        viewHolder.hole4.setImageDrawable(context.getResources().getDrawable(item.getChanceColourIds()[3]));

        viewHolder.result1.setImageDrawable(context.getResources().getDrawable(item.getResultColourIds()[0]));
        viewHolder.result2.setImageDrawable(context.getResources().getDrawable(item.getResultColourIds()[1]));
        viewHolder.result3.setImageDrawable(context.getResources().getDrawable(item.getResultColourIds()[2]));
        viewHolder.result4.setImageDrawable(context.getResources().getDrawable(item.getResultColourIds()[3]));
    }

    @Override
    public int getItemCount() {
        return chanceList == null ? 0 : chanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView hole1;
        ImageView hole2;
        ImageView hole3;
        ImageView hole4;

        ImageView result1;
        ImageView result2;
        ImageView result3;
        ImageView result4;

        public ViewHolder(View itemView) {
            super(itemView);
            hole1 = itemView.findViewById(R.id.hole1);
            hole2 = itemView.findViewById(R.id.hole2);
            hole3 = itemView.findViewById(R.id.hole3);
            hole4 = itemView.findViewById(R.id.hole4);

            result1 = itemView.findViewById(R.id.result1);
            result2 = itemView.findViewById(R.id.result2);
            result3 = itemView.findViewById(R.id.result3);
            result4 = itemView.findViewById(R.id.result4);
        }
    }
}