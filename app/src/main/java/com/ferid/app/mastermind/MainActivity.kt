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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferid.app.mastermind.action_managers.DeviceActionManager
import com.ferid.app.mastermind.action_managers.PlayerActionManager
import com.ferid.app.mastermind.adapters.ChanceAdapter
import com.ferid.app.mastermind.enums.SelectedColor
import com.ferid.app.mastermind.interfaces.ColorSelectionListener
import com.ferid.app.mastermind.models.Chance
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.current_chance_item.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var mContext: Context? = null

    private val mChanceList: ArrayList<Chance>? = ArrayList()
    private var mAdapter: ChanceAdapter? = null

    private var mChance: Chance? = null
    private var mStepNumber: Int = 1
    private var mIsCombinationFound = false //is the game won

    //game mChance decision
    private var mDeviceActionManager: DeviceActionManager? = null
    private var mPlayerActionManager: PlayerActionManager? = null
    private var mNumberOfBlack: Int = 0 //concretely found ones
    private var mNumberOfGrey: Int = 0  //found only colour but not position

    private var mTimeWhenStopped: Long? = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this

        initActionManagers()
        initRecyclerView()
        initRemainingChanceItem()

        fab.setOnClickListener{
            if (!isAllSelected()) {
                showMessage(getString(R.string.fill_all))
            } else if (isGameOver()) {
                showMessage(getString(R.string.game_over))
            } else { //otherwise make decision
                makeDecision()
            }
        }
        buttonHowToPlay.setOnClickListener{
            openTutorial()
        }
        buttonNewGame.setOnClickListener{setNewGame()}
    }

    /**
     * Initialise action managers to start the game
     */
    private fun initActionManagers() {
        mChance = Chance()
        mDeviceActionManager = DeviceActionManager()
        mPlayerActionManager = PlayerActionManager()
    }

    /**
     * Initialise recycler view
     */
    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = ChanceAdapter(mChanceList!!)
        recyclerView.adapter = mAdapter
    }

    /**
     * Initialise remaining chance item
     */
    private fun initRemainingChanceItem() {
        setRemainingChanceText()
        setEmptyText()

        hole1.setOnClickListener{askForColor(hole1, 0)}
        hole2.setOnClickListener{askForColor(hole2, 1)}
        hole3.setOnClickListener{askForColor(hole3, 2)}
        hole4.setOnClickListener{askForColor(hole4, 3)}

        chronometer.start()
    }

    /**
     * Display the remaining mChance
     */
    private fun setRemainingChanceText() {
        chanceText.text = String.format(Locale.getDefault(), "%s%d/%d", getString(R.string.step),
                mStepNumber, resources.getInteger(R.integer.max_step))
    }

    /**
     * Set empty list text
     */
    private fun setEmptyText() {
        if (emptyText != null) {
            if (mChanceList!!.isEmpty()) {
                emptyText.visibility = View.VISIBLE
            } else {
                emptyText.visibility = View.GONE
            }
        }
    }

    /**
     * Paint the hole with the selected colour
     * @param hole ImageView
     * @param holeNumber int
     */
    private fun askForColor(hole: ImageView, holeNumber: Int) {
        if (!isGameOver()) {
            val colorDialog = ColorDialog(mContext!!, object: ColorSelectionListener {
                override fun onColorSelected(selectedColor: SelectedColor) {
                    mPlayerActionManager!!.setHole(holeNumber, selectedColor)
                    selectColor(selectedColor.color, hole, holeNumber)
                }
            })
            colorDialog.setOnCancelListener { //deselect the colour
                deselectColor(hole, holeNumber)
            }
            colorDialog.show()
        }
    }

    /**
     * Empty the selected hole
     * @param hole ImageView
     * @param holeNumber int
     */
    private fun deselectColor(hole: ImageView, holeNumber: Int) {
        mPlayerActionManager!!.setHole(holeNumber, null)

        hole.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle))
        mChance!!.setChanceColorId(holeNumber, R.drawable.circle)
    }

    /**
     * Paint colours of selection panel
     * @param selectedColorValue int
     * @param hole ImageView
     * @param holeNumber int - send -1 if you do not want to change the list
     */
    private fun selectColor(selectedColorValue: Int, hole: ImageView, holeNumber: Int) {
        when(selectedColorValue) {
            0 -> {
                hole.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle_red))
                if (holeNumber >= 0) {
                    mChance!!.setChanceColorId(holeNumber, R.drawable.circle_red)
                }
            }
            1 -> {
                hole.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle_orange))
                if (holeNumber >= 0) {
                    mChance!!.setChanceColorId(holeNumber, R.drawable.circle_orange)
                }
            }
            2 -> {
                hole.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle_yellow))
                if (holeNumber >= 0) {
                    mChance!!.setChanceColorId(holeNumber, R.drawable.circle_yellow)
                }
            }
            3 -> {
                hole.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle_green))
                if (holeNumber >= 0) {
                    mChance!!.setChanceColorId(holeNumber, R.drawable.circle_green)
                }
            }
            4 -> {
                hole.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle_blue))
                if (holeNumber >= 0) {
                    mChance!!.setChanceColorId(holeNumber, R.drawable.circle_blue)
                }
            }
            5 -> {
                hole.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle_purple))
                if (holeNumber >= 0) {
                    mChance!!.setChanceColorId(holeNumber, R.drawable.circle_purple)
                }
            }
        }
    }

    /**
     * Is all the holes selected by the player
     * @return Boolean
     */
    private fun isAllSelected(): Boolean {
        for (selectedColorId in mChance!!.chanceColorIds) {
            if (selectedColorId == R.drawable.circle) {
                return false
            }
        }

        return true
    }

    /**
     * Decide if the game is over
     * @return Boolean
     */
    private fun isGameOver(): Boolean {
        return mChanceList!!.size >= resources.getInteger(R.integer.max_step) || mIsCombinationFound
    }

    /**
     * Clear the colours of the player's remaining game mChance
     */
    private fun clearRemainingChance() {
        mChance = Chance()
        mNumberOfBlack = 0
        mNumberOfGrey = 0

        hole1.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle))
        hole2.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle))
        hole3.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle))
        hole4.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.circle))

        setRemainingChanceText()
        setEmptyText()
    }

    /**
     * Start a new game
     */
    private fun setNewGame() {
        initActionManagers()

        mIsCombinationFound = false
        mStepNumber = 1

        mChanceList!!.clear()
        mAdapter!!.notifyDataSetChanged()

        clearRemainingChance()

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    /**
     * Compare and decide on the player's remaining game mChance
     */
    private fun makeDecision() {
        val blackIndices: ArrayList<Int> = ArrayList()

        for (i in 0 until resources.getInteger(R.integer.holes_number)) {
            val playerSelectedColor = mPlayerActionManager!!.getHoles()!![i]
            val deviceSelectedColor = mDeviceActionManager!!.getHoles()!![i]

            if (playerSelectedColor == deviceSelectedColor) {
                blackIndices.add(i)

                mNumberOfBlack++
            }
        }

        val greyIndices: ArrayList<Int> = ArrayList()

        for (i in 0 until resources.getInteger(R.integer.holes_number)) {
            if (!blackIndices.contains(i)) {
                val playerSelectedColor = mPlayerActionManager!!.getHoles()!![i]

                for (j in 0 until resources.getInteger(R.integer.holes_number)) {
                    if (!blackIndices.contains(j) && !greyIndices.contains(j)) {
                        val deviceSelectedColor = mDeviceActionManager!!.getHoles()!![j]

                        if (i != j && playerSelectedColor == deviceSelectedColor) {
                            greyIndices.add(j)

                            mNumberOfGrey++

                            break
                        }
                    }
                }
            }
        }

        setResultColors()
    }

    /**
     * After making decision
     */
    private fun setResultColors() {
        var resultForBlacks = mNumberOfBlack
        var resultForGreys = mNumberOfGrey

        for (i in 0 until resources.getInteger(R.integer.holes_number)) {
            if (resultForBlacks > 0) {
                mChance!!.setResultColorId(i, R.drawable.circle_black)

                resultForBlacks--
            } else {
                if (resultForGreys > 0) {
                    mChance!!.setResultColorId(i, R.drawable.circle_grey)

                    resultForGreys--
                } else {
                    mChance!!.setResultColorId(i, R.drawable.circle)
                }
            }
        }

        mChanceList!!.add(0, mChance!!)
        mAdapter!!.notifyDataSetChanged()

        if (mNumberOfBlack == resources.getInteger(R.integer.holes_number)) {
            showMessage(getString(R.string.you_won))

            chanceText.text = String.format(Locale.getDefault(), "%s%d%s",
                    getString(R.string.you_found_in), mStepNumber, getString(R.string.steps))

            mIsCombinationFound = true

            chronometer.stop()
        } else if (isGameOver()) {
            showMessage(getString(R.string.game_over))

            showActualColors()

            chronometer.stop()
        } else {
            if (mStepNumber <= resources.getInteger(R.integer.max_step)) {
                mStepNumber++
            }

            clearRemainingChance()
        }
    }

    /**
     * When the game is lost, show the machine's combination
     */
    private fun showActualColors() {
        val selectedColors = mDeviceActionManager!!.getHoles()
        selectColor(selectedColors!![0]!!.color, hole1, -1)
        selectColor(selectedColors[1]!!.color, hole2, -1)
        selectColor(selectedColors[2]!!.color, hole3, -1)
        selectColor(selectedColors[3]!!.color, hole4, -1)

        chanceText.text = resources.getString(R.string.machine_combination)
    }

    /**
     * Open how to play tutorial in chrome tab
     */
    private fun openTutorial() {
        val url = "http://www.industrious.com/mastermind/gamerules.html"

        try { //open custom tab
            val builder = CustomTabsIntent.Builder()
            builder.addDefaultShareMenuItem() //add share action to menu list
            builder.setShowTitle(true)
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            //set animations
            builder.setStartAnimations(this, R.anim.move_in_from_bottom, R.anim.stand_still)
            builder.setExitAnimations(this, R.anim.stand_still, R.anim.move_out_to_bottom)
            //launch chrome tab
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } catch (e: Exception) { //open browser if tab is not supported
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    /**
     * Show messages with alert dialog
     */
    private fun showMessage(message: String) {
        val builder = AlertDialog.Builder(mContext!!)
        builder.setMessage(message) //set message
        builder.create().show() //create and show
    }

    override fun onResume() {
        super.onResume()

        //if game is not over, continue to count
        if (isGameOver()) {
            chronometer.base = SystemClock.elapsedRealtime() + mTimeWhenStopped!!

            chronometer.start()
        }
    }

    override fun onPause() {
        mTimeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()

        chronometer.stop()

        super.onPause()
    }
}