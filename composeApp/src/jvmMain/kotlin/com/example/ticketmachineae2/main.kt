package com.example.ticketmachineae2

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.CardsDatabase
import data.Database

fun main() = application {
    // Init databases
    CardsDatabase.init()

    // Force-create / open DB and print path in console (important!)
    Database.connect().close()

    val windowState = rememberWindowState(width = 1000.dp, height = 750.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "ticketmachineae2",
        state = windowState
    ) {
        App()
    }
}
