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

package com.ferid.app.mastermind.models

import com.ferid.app.mastermind.R

class Chance {
    val chanceColorIds = IntArray(4) {R.drawable.circle}
    val resultColorIds = IntArray(4) {R.drawable.circle}

    fun setChanceColorId(index: Int, colorId: Int) {
        chanceColorIds[index] = colorId
    }

    fun setResultColorId(index: Int, colorId: Int) {
        resultColorIds[index] = colorId
    }
}