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

package com.ferid.app.mastermind;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.ferid.app.mastermind.enums.SelectedColour;
import com.ferid.app.mastermind.interfaces.ColourSelectionListener;

/**
 * Created by Ferid Cafer on 12/18/2015.
 */
public class ColourDialog extends Dialog {

    private ImageView red;
    private ImageView orange;
    private ImageView yellow;
    private ImageView green;
    private ImageView blue;
    private ImageView purple;

    private ColourSelectionListener colourSelectionListener;

    public ColourDialog(Context context__) {
        super(context__);
        setContentView(R.layout.colour_dialog);

        red = (ImageView) findViewById(R.id.red);
        orange = (ImageView) findViewById(R.id.orange);
        yellow = (ImageView) findViewById(R.id.yellow);
        green = (ImageView) findViewById(R.id.green);
        blue = (ImageView) findViewById(R.id.blue);
        purple = (ImageView) findViewById(R.id.purple);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(SelectedColour.RED);
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(SelectedColour.ORANGE);
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(SelectedColour.YELLOW);
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(SelectedColour.GREEN);
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(SelectedColour.BLUE);
            }
        });
        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(SelectedColour.PURPLE);
            }
        });
    }

    /**
     * Initialise colour selection listener
     * @param colourSelectionListener
     */
    public void setOnColourSelectionListener(ColourSelectionListener colourSelectionListener) {
        this.colourSelectionListener = colourSelectionListener;
    }

    private void setSelection(SelectedColour selectedColour) {
        if (colourSelectionListener != null) {
            colourSelectionListener.OnColourSelected(selectedColour);
        }

        dismiss();
    }
}