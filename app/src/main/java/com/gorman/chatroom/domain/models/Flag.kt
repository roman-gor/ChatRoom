package com.gorman.chatroom.domain.models

import com.gorman.chatroom.R

data class Flag(
    val flagImage: Int,
    val flagCountryName: Int,
    val phoneCode: String
)

val flagsList = listOf(
    Flag(R.drawable.belarus, R.string.belarus, "+375"),
    Flag(R.drawable.united_states_of_america, R.string.usa, "+1")
)