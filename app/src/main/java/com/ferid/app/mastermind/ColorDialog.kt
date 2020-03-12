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

package com.ferid.app.mastermind

import android.app.Dialog
import android.content.Context
import com.ferid.app.mastermind.enums.SelectedColor
import com.ferid.app.mastermind.interfaces.ColorSelectionListener
import kotlinx.android.synthetic.main.dialog_color.*


class ColorDialog(context: Context, colorSelectionListener: ColorSelectionListener) : Dialog(context) {
    private var colorSelectionListener: ColorSelectionListener? = null

    init {
        setContentView(R.layout.dialog_color)

        this.colorSelectionListener = colorSelectionListener

        red.setOnClickListener{setSelection(SelectedColor.RED)}
        orange.setOnClickListener{setSelection(SelectedColor.ORANGE)}
        yellow.setOnClickListener{setSelection(SelectedColor.YELLOW)}
        green.setOnClickListener{setSelection(SelectedColor.GREEN)}
        blue.setOnClickListener{setSelection(SelectedColor.BLUE)}
        purple.setOnClickListener{setSelection(SelectedColor.PURPLE)}
    }

    /**
     * Set color selection
     * @param selectedColor
     */
    private fun setSelection(selectedColor: SelectedColor) {
        if (colorSelectionListener != null) {
            colorSelectionListener!!.onColorSelected(selectedColor)
        }

        dismiss()
    }

}