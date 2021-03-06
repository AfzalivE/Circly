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

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.channels.ticker
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

class CountDownViewModel : ViewModel() {
    val timeLeftLiveData = MutableLiveData(Long.MAX_VALUE)
    var countDownTimer: CountDownTimer? = null

    fun startCountdown(totalTime: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(totalTime, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftLiveData.value = millisUntilFinished
            }

            override fun onFinish() {
                timeLeftLiveData.value = 0
            }
        }.start()
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val timerViewModel: CountDownViewModel = viewModel()
    val timeLeft by timerViewModel.timeLeftLiveData.observeAsState()

    Surface(color = MaterialTheme.colors.background) {
        val totalTime = 10000L
        TickMarks(timeLeft = timeLeft!!, totalTime = totalTime) {
            timerViewModel.startCountdown(totalTime)
        }
    }
}

@Composable
fun TickMarks(timeLeft: Long, totalTime: Long, onCountdownStart: () -> Unit) {
    val numTicks = 60
    val timePerTick = totalTime / numTicks
    val timeElapsed = totalTime - timeLeft

    Box(modifier = Modifier.fillMaxSize()) {
        for (i in 0 until numTicks) {
            val tickStart = timePerTick * i
            val tickEnd = timePerTick * (i + 1)
            val lineAngle by
                animateFloatAsState(if (timeElapsed < tickStart) {
                    0f
                } else {
                    ((timeElapsed.toFloat() / tickEnd) * 90f).coerceIn(0f, 90f)
                }, animationSpec = tween(easing = LinearEasing))

            Log.d("MainActivity", "lineAngle: $lineAngle")

            // val lineAngle by animateFloatAsState(if (timeLeft / 1000 <= i) 90f else 0f)

            TickMark(
                angle = i * -6,
                lineAngle = lineAngle
            )
        }
    }

    Button(onClick = {
        onCountdownStart()
    }) {
        Text("Toggle")
    }
}

@Composable
fun TickMark(
    angle: Int,
    lineAngle: Float
) {
    val theta = angle * PI.toFloat() / 180

    Box(
        Modifier
            .rotate(-90f)
            .fillMaxSize()
            .drawBehind {
                val startRadius = size.width / 2 * 0.7f
                val endRadius = size.width / 2 * 0.8f
                val startPos = Offset(
                    cos(theta) * startRadius,
                    sin(theta) * startRadius
                )

                val endPos = Offset(
                    cos(theta) * endRadius,
                    sin(theta) * endRadius
                )

                val midPos = Offset(
                    (endPos.x + startPos.x) / 2,
                    (endPos.y + startPos.y) / 2
                )

                rotate(lineAngle, center + midPos) {
                    drawLine(
                        Color.Green,
                        center + startPos,
                        center + endPos,
                        10f
                    )
                }
            }
    )
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
