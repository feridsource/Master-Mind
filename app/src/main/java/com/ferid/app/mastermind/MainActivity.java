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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ferid.app.mastermind.action_managers.DeviceActionManager;
import com.ferid.app.mastermind.action_managers.PlayerActionManager;
import com.ferid.app.mastermind.adapters.ChanceAdapter;
import com.ferid.app.mastermind.enums.SelectedColour;
import com.ferid.app.mastermind.interfaces.ColourSelectionListener;
import com.ferid.app.mastermind.learn_playing.LearnPlayingActivity;
import com.ferid.app.mastermind.models.Chance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Ferid Cafer on 12/17/2015.
 */
public class MainActivity extends AppCompatActivity {
    private Context mContext;

    private ArrayList<Chance> mChanceList = new ArrayList<>();
    private ChanceAdapter mAdapter;

    private TextView mChanceText;
    private ImageView mHole1;
    private ImageView mHole2;
    private ImageView mHole3;
    private ImageView mHole4;

    private Chance mChance;
    private int mStepNumber = 1;
    private boolean mIsCombinationFound = false; //is the game won

    //game mChance decision
    private DeviceActionManager mDeviceActionManager;
    private PlayerActionManager mPlayerActionManager;
    private int mNumberOfBlack = 0; //concretely found ones
    private int mNumberOfGrey = 0;  //found only colour but not position

    //stop-watch
    private Chronometer mChronometer;
    private long mTimeWhenStopped = 0;

    //empty text
    private TextView mEmptyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);


        mContext = this;

        initialiseActionManagers();

        initialiseRecyclerView();

        initialiseRemainingChanceItem();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllSelected()) {
                    Snackbar.make(view, getString(R.string.fill_all), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (isGameOver()) {
                    Snackbar.make(view, getString(R.string.game_over), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //otherwise make decision
                makeDecision();
            }
        });

        Button buttonHowToPlay = findViewById(R.id.buttonHowToPlay);
        buttonHowToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LearnPlayingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_in_from_bottom, R.anim.stand_still);
            }
        });

        Button buttonNewGame = findViewById(R.id.buttonNewGame);
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewGame();
            }
        });
    }

    /**
     * Initialise action managers to start the game
     */
    private void initialiseActionManagers() {
        mChance = new Chance();
        mDeviceActionManager = new DeviceActionManager();
        mPlayerActionManager = new PlayerActionManager();
    }

    private void initialiseRemainingChanceItem(){
        mChanceText = findViewById(R.id.chanceText);
        mHole1 = findViewById(R.id.hole1);
        mHole2 = findViewById(R.id.hole2);
        mHole3 = findViewById(R.id.hole3);
        mHole4 = findViewById(R.id.hole4);
        mEmptyText = findViewById(R.id.emptyText);

        setRemainingChanceText();
        setEmptyText();

        mHole1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForColour(mHole1, 0);
            }
        });
        mHole2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForColour(mHole2, 1);
            }
        });
        mHole3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForColour(mHole3, 2);
            }
        });
        mHole4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForColour(mHole4, 3);
            }
        });

        mChronometer = findViewById(R.id.chronometer);
        if (mChronometer != null) mChronometer.start();
    }

    /**
     * Display the remaining mChance
     */
    private void setRemainingChanceText() {
        mChanceText.setText(String.format(Locale.getDefault(), "%s%d/%d", getString(R.string.step),
                mStepNumber, getResources().getInteger(R.integer.max_step)));
    }

    /**
     * Set empty list text
     */
    private void setEmptyText() {
        if (mEmptyText != null) {
            if (mChanceList.isEmpty()) {
                mEmptyText.setVisibility(View.VISIBLE);
            } else {
                mEmptyText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Paint the hole with the selected colour
     * @param hole ImageView
     * @param holeNumber int
     */
    private void askForColour(final ImageView hole, final int holeNumber) {
        if (!isGameOver()) {
            ColourDialog colourDialog = new ColourDialog(mContext);
            colourDialog.setOnColourSelectionListener(new ColourSelectionListener() {
                @Override
                public void OnColourSelected(SelectedColour selectedColour) {
                    mPlayerActionManager.setHole(holeNumber, selectedColour);

                    setSelectedColour(selectedColour.getValue(), hole, holeNumber);
                }
            });
            colourDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //unselect the colour
                    setUnselectColour(hole, holeNumber);
                }
            });
            colourDialog.show();
        }
    }

    /**
     * Empty the selected hole
     * @param hole ImageView
     * @param holeNumber int
     */
    private void setUnselectColour(ImageView hole, int holeNumber) {
        mPlayerActionManager.setHole(holeNumber, null);

        hole.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        mChance.setChanceColourId(holeNumber, R.drawable.circle);
    }

    /**
     * Paint colours of selection panel
     * @param selectedColourValue int
     * @param hole ImageView
     * @param holeNumber int - send -1 if you do not want to change the list
     */
    private void setSelectedColour(int selectedColourValue, ImageView hole, int holeNumber) {
        switch (selectedColourValue) {
            case 0:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_red));
                if (holeNumber >= 0) {
                    mChance.setChanceColourId(holeNumber, R.drawable.circle_red);
                }
                break;
            case 1:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_orange));
                if (holeNumber >= 0) {
                    mChance.setChanceColourId(holeNumber, R.drawable.circle_orange);
                }
                break;
            case 2:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_yellow));
                if (holeNumber >= 0) {
                    mChance.setChanceColourId(holeNumber, R.drawable.circle_yellow);
                }
                break;
            case 3:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_green));
                if (holeNumber >= 0) {
                    mChance.setChanceColourId(holeNumber, R.drawable.circle_green);
                }
                break;
            case 4:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_blue));
                if (holeNumber >= 0) {
                    mChance.setChanceColourId(holeNumber, R.drawable.circle_blue);
                }
                break;
            case 5:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_purple));
                if (holeNumber >= 0) {
                    mChance.setChanceColourId(holeNumber, R.drawable.circle_purple);
                }
                break;
        }
    }

    /**
     * Initialise recycler view
     */
    private void initialiseRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ChanceAdapter(mContext, mChanceList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Is all the holes selected by the player
     * @return boolean
     */
    private boolean isAllSelected() {
        for (int i = 0; i < mChance.getChanceColourIds().length; i++) {
            int selectedColourId = mChance.getChanceColourIds()[i];
            if (selectedColourId == R.drawable.circle) {
                return false;
            }
        }

        return true;
    }

    /**
     * Decide if the game is over
     * @return boolean
     */
    private boolean isGameOver() {
        return mChanceList.size() >= getResources().getInteger(R.integer.max_step) || mIsCombinationFound;
    }

    private void showMessage(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //message
        builder.setMessage(message);
        //create and show
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Compare and decide on the player's remaining game mChance
     */
    private void makeDecision() {
        ArrayList<Integer> blackIndices = new ArrayList<>();
        for (int i = 0; i < getResources().getInteger(R.integer.holes_number); i++) {
            SelectedColour playerSelectedColour = mPlayerActionManager.getHoles()[i];
            SelectedColour deviceSelectedColour = mDeviceActionManager.getHoles()[i];

            if (playerSelectedColour == deviceSelectedColour) {
                blackIndices.add(i);

                mNumberOfBlack++;
            }
        }

        ArrayList<Integer> greyIndices = new ArrayList<>();
        for (int i = 0; i < getResources().getInteger(R.integer.holes_number); i++) {
            if (!blackIndices.contains(i)) {
                SelectedColour playerSelectedColour = mPlayerActionManager.getHoles()[i];

                for (int j = 0; j < getResources().getInteger(R.integer.holes_number); j++) {
                    if (!blackIndices.contains(j) && !greyIndices.contains(j)) {
                        SelectedColour deviceSelectedColour = mDeviceActionManager.getHoles()[j];

                        if (i != j && playerSelectedColour == deviceSelectedColour) {
                            greyIndices.add(j);

                            mNumberOfGrey++;

                            break;
                        }
                    }
                }
            }
        }

        setResultColours();
    }

    /**
     * After making decision
     */
    private void setResultColours() {
        int resultForBlacks = mNumberOfBlack;
        int resultForGreys = mNumberOfGrey;

        for (int i = 0; i < getResources().getInteger(R.integer.holes_number); i++) {
            if (resultForBlacks > 0) {
                mChance.setResultColourId(i, R.drawable.circle_black);

                resultForBlacks--;
            } else {
                if (resultForGreys > 0) {
                    mChance.setResultColourId(i, R.drawable.circle_grey);

                    resultForGreys--;
                } else {
                    mChance.setResultColourId(i, R.drawable.circle);
                }
            }
        }

        mChanceList.add(0, mChance);
        mAdapter.notifyDataSetChanged();

        if (mNumberOfBlack == getResources().getInteger(R.integer.holes_number)) {
            showMessage(getString(R.string.you_won));

            mChanceText.setText(String.format(Locale.getDefault(), "%s%d%s",
                    getString(R.string.you_found_in), mStepNumber, getString(R.string.steps)));

            mIsCombinationFound = true;

            mChronometer.stop();
        } else if (isGameOver()) {
            showMessage(getString(R.string.game_over));

            showActualColours();

            mChronometer.stop();
        } else {
            if (mStepNumber <= getResources().getInteger(R.integer.max_step)) {
                mStepNumber++;
            }

            clearRemainingChance();
        }
    }

    /**
     * Clear the colours of the player's remaining game mChance
     */
    private void clearRemainingChance() {
        mChance = new Chance();
        mNumberOfBlack = 0;
        mNumberOfGrey = 0;

        mHole1.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        mHole2.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        mHole3.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        mHole4.setImageDrawable(getResources().getDrawable(R.drawable.circle));

        setRemainingChanceText();
        setEmptyText();
    }

    /**
     * Start a new game
     */
    private void setNewGame() {
        initialiseActionManagers();

        mIsCombinationFound = false;
        mStepNumber = 1;

        mChanceList.clear();
        mAdapter.notifyDataSetChanged();

        clearRemainingChance();

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    /**
     * When the game is lost, show the machine's combination
     */
    private void showActualColours() {
        SelectedColour[] selectedColours = mDeviceActionManager.getHoles();
        setSelectedColour(selectedColours[0].getValue(), mHole1, -1);
        setSelectedColour(selectedColours[1].getValue(), mHole2, -1);
        setSelectedColour(selectedColours[2].getValue(), mHole3, -1);
        setSelectedColour(selectedColours[3].getValue(), mHole4, -1);

        mChanceText.setText(getString(R.string.machine_combination));
    }

    @Override
    protected void onResume() {
        super.onResume();

        //if game is not over, continue to count
        if (mChronometer != null && !isGameOver()) {
            mChronometer.setBase(SystemClock.elapsedRealtime() + mTimeWhenStopped);

            mChronometer.start();
        }
    }

    @Override
    protected void onPause() {
        if (mChronometer != null) {
            mTimeWhenStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();

            mChronometer.stop();
        }

        super.onPause();
    }

}