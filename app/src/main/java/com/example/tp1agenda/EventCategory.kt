package com.example.tp1agenda

import com.example.tp1agenda.R

enum class EventCategory(val iconRes: Int, val colorRes: Int) {
    SPORT(R.drawable.ic_sport, R.color.sportColor),
    MEETING(R.drawable.ic_meeting, R.color.meetingColor),
    WORK(R.drawable.ic_work, R.color.workColor),
    LUNCH(R.drawable.ic_lunch, R.color.lunchColor);
}