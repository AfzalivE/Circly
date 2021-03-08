package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountDownViewModel : ViewModel() {
    val timeLeftLiveData = MutableLiveData(CountdownState(0, 0))
    var countDownTimer: CountDownTimer? = null

    fun startCountdown(totalTime: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(totalTime, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftLiveData.value = CountdownState(millisUntilFinished, totalTime)
            }

            override fun onFinish() {
                timeLeftLiveData.value = CountdownState(0, totalTime, TimerState.FINISHED)
            }
        }.start()
    }

    fun reset() {
        countDownTimer?.cancel()
        val resetDuration = 250L
        var timeElapsed = 0L
        val delay = 10L
        viewModelScope.launch {
            while (timeElapsed < resetDuration) {
                timeElapsed += delay
                timeLeftLiveData.value = CountdownState(timeElapsed, resetDuration, TimerState.RESETTING)
                delay(delay)
            }

            timeLeftLiveData.value = CountdownState(timeElapsed, resetDuration)
        }
    }

    data class CountdownState(
        val timeLeft: Long,
        val totalTime: Long,
        val state: TimerState = TimerState.IDLE
    )

    enum class TimerState {
        IDLE, FINISHED, RESETTING
    }
}