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
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.CountDownViewModel.*
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.limeGreen500
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

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

// Start building your app here!
@Composable
fun MyApp() {
    val timerViewModel: CountDownViewModel = viewModel()
    val countdownState by timerViewModel.timeLeftLiveData.observeAsState(
        CountdownState(
            0,
            0
        )
    )

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        val totalTime = 5000L

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClockUi(countdownState, totalTime)

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (countdownState.state === TimerState.STOPPED) {
                    Button(
                        onClick = { timerViewModel.startCountdown(totalTime) }
                    ) {
                        Text(stringResource(R.string.start))
                    }
                } else {
                    Button(
                        onClick = { timerViewModel.reset() }
                    ) {
                        Text(stringResource(R.string.stop))
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = countdownState) {
        if (countdownState.state === TimerState.FINISHED) {
            delay(200)
            timerViewModel.reset()
        }
    }
}

@Composable
fun ClockUi(countdownState: CountdownState, totalTime: Long) {
    val numTicks = 60
    val timePerTick = countdownState.totalTime / numTicks
    val timeElapsed = countdownState.totalTime - countdownState.timeLeft

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        for (i in 0 until numTicks) {
            val tickStart = timePerTick * i
            val tickEnd = timePerTick * (i + 1)
            val lineAngle by animateFloatAsState(
                if (timeElapsed > tickStart) 90f else 0f,
                animationSpec = tween(
                    easing = LinearEasing,
                    durationMillis = (tickEnd - tickStart).toInt()
                )
            )

            TickMark(
                angle = (i + 1) * -6,
                lineAngle = lineAngle
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TimerNumbers(countdownState, totalTime)
            val annotatedLabel = buildAnnotatedString {
                pushStyle(style = MaterialTheme.typography.h6.toSpanStyle())
                append("SEC.")
                pop()
                pushStyle(
                    style = MaterialTheme.typography.h6.toSpanStyle().copy(color = Color.Gray)
                )
                append(" REMAINING")
                pop()
            }
            Text(text = annotatedLabel)
        }
    }
}

@Composable
private fun TimerNumbers(countdownState: CountdownState, totalTime: Long) {
    val state = when (countdownState.state) {
        TimerState.STARTED -> Time.fromMillis(countdownState.timeLeft)
        TimerState.RESETTING,
        TimerState.FINISHED -> Time.fromMillis(0)
        else -> Time.fromMillis(totalTime)
    }

    Row {
        NumberColumn(state.seconds / 10)
        NumberColumn(state.seconds % 10)
        Text(
            text = ":",
            style = MaterialTheme.typography.h2,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        NumberColumn(state.centiseconds / 10)
        NumberColumn(state.centiseconds % 10)
    }
}

@Composable
private fun NumberColumn(number: Int) {
    Text(
        text = number.toString(),
        style = MaterialTheme.typography.h2,
        modifier = Modifier.width(48.dp),
        textAlign = TextAlign.Center
    )
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
                val startRadius = size.width / 2 * 0.72f
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
                        limeGreen500,
                        center + startPos,
                        center + endPos,
                        12f
                    )
                }
            }
    )
}

data class Time(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val centiseconds: Int
) {
    companion object {
        @OptIn(ExperimentalTime::class)
        fun fromMillis(millis: Long): Time {
            return millis.milliseconds.toComponents { hours, minutes, seconds, nanoseconds ->
                Time(
                    hours,
                    minutes,
                    seconds,
                    nanoseconds / 1e7.toInt()
                )
            }
        }
    }
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
