package com.akka.util.model

import androidx.compose.ui.graphics.Color
import com.akka.ui.theme.AngryColor
import com.akka.ui.theme.AwfulColor
import com.akka.ui.theme.BoredColor
import com.akka.ui.theme.CalmColor
import com.akka.ui.theme.DepressedColor
import com.akka.ui.theme.DisappointedColor
import com.akka.ui.theme.HappyColor
import com.akka.ui.theme.HumorousColor
import com.akka.ui.theme.LonelyColor
import com.akka.ui.theme.MysteriousColor
import com.akka.ui.theme.NeutralColor
import com.akka.ui.theme.RomanticColor
import com.akka.ui.theme.ShamefulColor
import com.akka.ui.theme.SurprisedColor
import com.akka.ui.theme.SuspiciousColor
import com.akka.ui.theme.TenseColor
import com.akka.util.R

enum class Mood(
    val icon: Int,
    val contentColor: Color,
    val containerColor: Color
) {
    Neutral(
        icon = R.drawable.neutral,
        contentColor = Color.Black,
        containerColor = NeutralColor
    ),
    Happy(
        icon = R.drawable.happy,
        contentColor = Color.Black,
        containerColor = HappyColor
    ),
    Angry(
        icon = R.drawable.angry,
        contentColor = Color.White,
        containerColor = AngryColor
    ),
    Romantic(
        icon = R.drawable.romantic,
        contentColor = Color.Black,
        containerColor = RomanticColor
    ),
    Calm(
        icon = R.drawable.calm,
        contentColor = Color.Black,
        containerColor = CalmColor
    ),
    Tense(
        icon = R.drawable.tense,
        contentColor = Color.Black,
        containerColor = TenseColor
    ),
    Lonely(
        icon = R.drawable.lonely,
        contentColor = Color.Black,
        containerColor = LonelyColor
    ),
    Mysterious(
        icon = R.drawable.mysterious,
        contentColor = Color.Black,
        containerColor = MysteriousColor
    ),
    Awful(
        icon = R.drawable.awful,
        contentColor = Color.Black,
        containerColor = AwfulColor
    ),
    Surprised(
        icon = R.drawable.surprised,
        contentColor = Color.Black,
        containerColor = SurprisedColor
    ),
    Depressed(
        icon = R.drawable.depressed,
        contentColor = Color.Black,
        containerColor = DepressedColor
    ),
    Disappointed(
        icon = R.drawable.disappointed,
        contentColor = Color.White,
        containerColor = DisappointedColor
    ),
    Shameful(
        icon = R.drawable.shameful,
        contentColor = Color.Black,
        containerColor = ShamefulColor
    ),
    Humorous(
        icon = R.drawable.humorous,
        contentColor = Color.Black,
        containerColor = HumorousColor
    ),
    Suspicious(
        icon = R.drawable.suspicious,
        contentColor = Color.Black,
        containerColor = SuspiciousColor
    ),
    Bored(
        icon = R.drawable.bored,
        contentColor = Color.Black,
        containerColor = BoredColor
    )

}