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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferid.app.mastermind.action_managers.DeviceActionManager;
import com.ferid.app.mastermind.action_managers.PlayerActionManager;
import com.ferid.app.mastermind.adapters.ChanceAdapter;
import com.ferid.app.mastermind.enums.SelectedColour;
import com.ferid.app.mastermind.interfaces.ColourSelectionListener;
import com.ferid.app.mastermind.learn_playing.LearnPlayingActivity;
import com.ferid.app.mastermind.models.Chance;

import java.util.ArrayList;

/**
 * Created by Ferid Cafer on 12/17/2015.
 */
public class MainActivity extends AppCompatActivity {
    private Context context;

    private RecyclerView recyclerView;
    private ArrayList<Chance> chanceList = new ArrayList<>();
    private ChanceAdapter adapter;

    private TextView chanceText;
    private ImageView hole1;
    private ImageView hole2;
    private ImageView hole3;
    private ImageView hole4;

    private Chance chance;
    private int stepNumber = 1;
    private boolean isCombinationFound = false; //is the game won

    //game chance decision
    private DeviceActionManager deviceActionManager;
    private PlayerActionManager playerActionManager;
    private int numberOfBlack = 0; //concretely found ones
    private int numberOfGrey = 0;  //found only colour but not position


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialiseActionManagers();

        initialiseRecyclerView();

        initialiseCurrentChanceItem();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
    }

    /**
     * Initialise action managers to start the game
     */
    private void initialiseActionManagers() {
        chance = new Chance();
        deviceActionManager = new DeviceActionManager();
        playerActionManager = new PlayerActionManager();
    }

    private void initialiseCurrentChanceItem(){
        chanceText = (TextView) findViewById(R.id.chanceText);
        hole1 = (ImageView) findViewById(R.id.hole1);
        hole2 = (ImageView) findViewById(R.id.hole2);
        hole3 = (ImageView) findViewById(R.id.hole3);
        hole4 = (ImageView) findViewById(R.id.hole4);

        chanceText.setText(getString(R.string.find_combination));
        hole1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForColour(hole1, 0);
            }
        });
        hole2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForColour(hole2, 1);
            }
        });
        hole3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForColour(hole3, 2);
            }
        });
        hole4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForColour(hole4, 3);
            }
        });
    }

    /**
     * Paint the hole with the selected colour
     * @param hole
     */
    private void askForColour(final ImageView hole, final int holeNumber) {
        if (!isGameOver()) {
            ColourDialog colourDialog = new ColourDialog(context);
            colourDialog.setOnColourSelectionListener(new ColourSelectionListener() {
                @Override
                public void OnColourSelected(SelectedColour selectedColour) {
                    playerActionManager.setHole(holeNumber, selectedColour);

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
     * @param hole
     * @param holeNumber
     */
    private void setUnselectColour(ImageView hole, int holeNumber) {
        playerActionManager.setHole(holeNumber, null);

        hole.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        chance.setChanceColourId(holeNumber, R.drawable.circle);
    }

    /**
     * Paint colours of selection panel
     * @param selectedColourValue
     * @param hole
     * @param holeNumber Send -1 if you do not want to change the list
     */
    private void setSelectedColour(int selectedColourValue, ImageView hole, int holeNumber) {
        switch (selectedColourValue) {
            case 0:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_red));
                if (holeNumber >= 0) {
                    chance.setChanceColourId(holeNumber, R.drawable.circle_red);
                }
                break;
            case 1:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_orange));
                if (holeNumber >= 0) {
                    chance.setChanceColourId(holeNumber, R.drawable.circle_orange);
                }
                break;
            case 2:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_yellow));
                if (holeNumber >= 0) {
                    chance.setChanceColourId(holeNumber, R.drawable.circle_yellow);
                }
                break;
            case 3:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_green));
                if (holeNumber >= 0) {
                    chance.setChanceColourId(holeNumber, R.drawable.circle_green);
                }
                break;
            case 4:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_blue));
                if (holeNumber >= 0) {
                    chance.setChanceColourId(holeNumber, R.drawable.circle_blue);
                }
                break;
            case 5:
                hole.setImageDrawable(getResources().getDrawable(R.drawable.circle_purple));
                if (holeNumber >= 0) {
                    chance.setChanceColourId(holeNumber, R.drawable.circle_purple);
                }
                break;
        }
    }

    private void initialiseRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ChanceAdapter(context, chanceList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Is all the holes selected by the player
     * @return
     */
    private boolean isAllSelected() {
        for (int i = 0; i < chance.getChanceColourIds().length; i++) {
            int selectedColourId = chance.getChanceColourIds()[i];
            if (selectedColourId == R.drawable.circle) {
                return false;
            }
        }

        return true;
    }

    /**
     * Decide if the game is over
     * @return
     */
    private boolean isGameOver() {
        if (chanceList.size() > 9 || isCombinationFound) {
            return true;
        } else {
            return false;
        }
    }

    private void showMessage(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //message
        builder.setMessage(message);
        //create and show
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Compare and decide on the player's current game chance
     */
    private void makeDecision() {
        ArrayList<Integer> blackIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            SelectedColour playerSelectedColour = playerActionManager.getHoles()[i];
            SelectedColour deviceSelectedColour = deviceActionManager.getHoles()[i];

            if (playerSelectedColour == deviceSelectedColour) {
                blackIndices.add(i);

                numberOfBlack++;
            }
        }

        ArrayList<Integer> greyIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (!blackIndices.contains(i)) {
                SelectedColour playerSelectedColour = playerActionManager.getHoles()[i];

                for (int j = 0; j < 4; j++) {
                    if (!blackIndices.contains(j) && !greyIndices.contains(j)) {
                        SelectedColour deviceSelectedColour = deviceActionManager.getHoles()[j];

                        if (i != j && playerSelectedColour == deviceSelectedColour) {
                            greyIndices.add(j);

                            numberOfGrey++;

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
        int resultForBlacks = numberOfBlack;
        int resultForGreys = numberOfGrey;

        for (int i = 0; i < 4; i++) {
            if (resultForBlacks > 0) {
                chance.setResultColourId(i, R.drawable.circle_black);

                resultForBlacks--;
            } else {
                if (resultForGreys > 0) {
                    chance.setResultColourId(i, R.drawable.circle_grey);

                    resultForGreys--;
                } else {
                    chance.setResultColourId(i, R.drawable.circle);
                }
            }
        }

        chanceList.add(0, chance);
        adapter.notifyDataSetChanged();

        if (numberOfBlack == 4) {
            showMessage(getString(R.string.you_won));

            chanceText.setText(getString(R.string.you_found_in)
                    + stepNumber + getString(R.string.steps));

            isCombinationFound = true;
        } else if (isGameOver()) {
            showMessage(getString(R.string.game_over));

            showActualColours();
        } else {
            if (stepNumber <= 10) {
                stepNumber++;
            }

            clearCurrentChance();
        }
    }

    /**
     * Clear the colours of the player's current game chance
     */
    private void clearCurrentChance() {
        chance = new Chance();
        numberOfBlack = 0;
        numberOfGrey = 0;

        hole1.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        hole2.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        hole3.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        hole4.setImageDrawable(getResources().getDrawable(R.drawable.circle));

        if (stepNumber == 1) {
            chanceText.setText(getString(R.string.find_combination));
        } else if (stepNumber > 1 && stepNumber <= 10) {
            chanceText.setText(String.valueOf(stepNumber));
        }
    }

    /**
     * Start a new game
     */
    private void setNewGame() {
        initialiseActionManagers();

        isCombinationFound = false;
        stepNumber = 1;

        clearCurrentChance();

        chanceList.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * When the game is lost, show the machine's combination
     */
    private void showActualColours() {
        SelectedColour[] selectedColours = deviceActionManager.getHoles();
        setSelectedColour(selectedColours[0].getValue(), hole1, -1);
        setSelectedColour(selectedColours[1].getValue(), hole2, -1);
        setSelectedColour(selectedColours[2].getValue(), hole3, -1);
        setSelectedColour(selectedColours[3].getValue(), hole4, -1);

        chanceText.setText(getString(R.string.machine_combination));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_game) {
            setNewGame();
            return true;
        } else if (id == R.id.action_learn_playing) {
            Intent intent = new Intent(context, LearnPlayingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.move_in_from_bottom, R.anim.stand_still);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}