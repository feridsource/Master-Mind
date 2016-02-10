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

package com.ferid.app.mastermind.models;

import com.ferid.app.mastermind.R;

/**
 * Created by Ferid Cafer on 12/17/2015.
 */
public class Chance {
    private int[] chanceColourIds = new int[4];

    private int[] resultColourIds = new int[4];

    public Chance() {
        chanceColourIds[0] = R.drawable.circle;
        chanceColourIds[1] = R.drawable.circle;
        chanceColourIds[2] = R.drawable.circle;
        chanceColourIds[3] = R.drawable.circle;

        resultColourIds[0] = R.drawable.circle;
        resultColourIds[1] = R.drawable.circle;
        resultColourIds[2] = R.drawable.circle;
        resultColourIds[3] = R.drawable.circle;
    }

    public int[] getChanceColourIds() {
        return chanceColourIds;
    }

    public void setChanceColourId(int index, int colourId) {
        this.chanceColourIds[index] = colourId;
    }

    public int[] getResultColourIds() {
        return resultColourIds;
    }

    public void setResultColourId(int index, int colourId) {
        this.resultColourIds[index] = colourId;
    }
}