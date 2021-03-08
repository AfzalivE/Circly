/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountDownViewModel : ViewModel() {
    val timeLeftLiveData: MutableLiveData<CountdownState> = MutableLiveData()
    var countDownTimer: CountDownTimer? = null

    fun startCountdown(totalTime: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(totalTime, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftLiveData.value = CountdownState(millisUntilFinished, totalTime, TimerState.STARTED)
            }

            override fun onFinish() {
                timeLeftLiveData.value = CountdownState(0, totalTime, TimerState.FINISHED)
            }
        }.start()
    }

    fun reset() {
        countDownTimer?.cancel()
        val resetDuration = 250L
        var timeElapsed = timeLeftLiveData.value?.let {
            resetDuration * it.timeLeft / it.totalTime
        } ?: 0L

        val delay = 10L
        viewModelScope.launch {
            while (timeElapsed < resetDuration) {
                timeElapsed += delay
                timeLeftLiveData.value =
                    CountdownState(timeElapsed, resetDuration, TimerState.RESETTING)
                delay(delay)
            }

            timeLeftLiveData.value = CountdownState(timeElapsed, resetDuration)
        }
    }

    data class CountdownState(
        val timeLeft: Long,
        val totalTime: Long,
        val state: TimerState = TimerState.STOPPED
    )

    enum class TimerState {
        STOPPED, STARTED, FINISHED, RESETTING
    }
}
