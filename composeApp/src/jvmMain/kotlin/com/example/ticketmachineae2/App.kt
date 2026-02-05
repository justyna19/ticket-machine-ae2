package com.example.ticketmachineae2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.repo.AdminRepository
import data.repo.CardRepository
import data.repo.DestinationRepository
import data.repo.OfferRepository
import domain.Destination
import domain.Offer
import domain.TicketType
import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun App() {
    var screen by remember { mutableStateOf(Screen.CUSTOMER) }

    MaterialTheme {
        Column(Modifier.fillMaxSize()) {

            // --- TOP BAR ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ticket Machine", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.weight(1f))

                Text(
                    "Customer",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable { screen = Screen.CUSTOMER }
                )
                Text(
                    "Admin",
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable {
                            screen = if (screen == Screen.ADMIN_PANEL) Screen.ADMIN_PANEL else Screen.ADMIN_LOGIN
                        }
                )
            }
            Divider()

            // --- CONTENT ---
            when (screen) {
                Screen.CUSTOMER -> CustomerScreen()

                Screen.ADMIN_LOGIN -> AdminLoginScreen(
                    onLoginOk = { screen = Screen.ADMIN_PANEL }
                )

                Screen.ADMIN_PANEL -> AdminPanelScreen(
                    onLogout = { screen = Screen.ADMIN_LOGIN }
                )
            }
        }
    }
}

/* =========================================================
   CUSTOMER SCREEN
   ========================================================= */
@Composable
private fun CustomerScreen() {
    val destRepo = remember { DestinationRepository() }
    val cardRepo = remember { CardRepository() }
    val offerRepo = remember { OfferRepository() }

    var destinations by remember { mutableStateOf<List<Destination>>(emptyList()) }
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    var ticketType by remember { mutableStateOf(TicketType.Single) }

    // Card
    var cardNumber by remember { mutableStateOf("") }
    var cardCredit by remember { mutableStateOf<Double?>(null) }

    // Output
    var message by remember { mutableStateOf<String?>(null) }
    var ticketText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        destinations = destRepo.getAll()
        selectedDestination = destinations.firstOrNull()
    }

    val dest = selectedDestination
    val basePrice = dest?.getPrice(ticketType)

    // --- Special offer (best active) ---
    val nowDate = LocalDate.now()
    val nowTime = LocalTime.now()

    val discountPercent = remember(dest?.id, ticketType) {
        dest?.let { offerRepo.getBestActiveDiscountPercent(it.id, nowDate, nowTime) }
    }

    val finalPrice = if (basePrice != null && discountPercent != null) {
        basePrice * (1.0 - (discountPercent / 100.0))
    } else basePrice

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        // Ticket type chips
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Ticket type:")
            Spacer(Modifier.width(12.dp))

            FilterChip(
                selected = ticketType == TicketType.Single,
                onClick = { ticketType = TicketType.Single; ticketText = null; message = null },
                label = { Text("Single") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = ticketType == TicketType.Return,
                onClick = { ticketType = TicketType.Return; ticketText = null; message = null },
                label = { Text("Return") }
            )
        }

        Spacer(Modifier.height(16.dp))

        Text("Choose destination:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(destinations) { d ->
                val isSelected = selectedDestination?.id == d.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = {
                        selectedDestination = d
                        ticketText = null
                        message = null
                    }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(d.name, style = MaterialTheme.typography.titleMedium)
                        Text("Single: ${"%.2f".format(d.singlePrice)}")
                        Text("Return: ${"%.2f".format(d.returnPrice)}")
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Price section (with offer)
        if (finalPrice != null) {
            val typeText = if (ticketType == TicketType.Single) "Single" else "Return"
            Text("Price: ${"%.2f".format(finalPrice)} ($typeText)", style = MaterialTheme.typography.titleLarge)

            if (discountPercent != null) {
                Spacer(Modifier.height(4.dp))
                Text("Special offer active: -${"%.0f".format(discountPercent)}% (applied)")
            }
        } else {
            Text("Price: -", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(12.dp))

        // Insert card
        Text("Insert card", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it.filter { ch -> ch.isDigit() } },
            label = { Text("Card number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    val credit = cardRepo.getCredit(cardNumber)
                    cardCredit = credit
                    message = if (credit == null) "Card not found"
                    else "Card inserted. Credit: ${"%.2f".format(credit)}"
                    ticketText = null
                }
            ) { Text("Insert card") }

            Spacer(Modifier.width(12.dp))
            Text(if (cardCredit != null) "Credit: ${"%.2f".format(cardCredit)}" else "Credit: -")
        }

        if (message != null) {
            Spacer(Modifier.height(8.dp))
            Text(message!!)
        }

        Spacer(Modifier.height(12.dp))

        // Buy ticket
        Button(
            onClick = {
                val d = selectedDestination ?: return@Button
                val credit = cardCredit
                if (credit == null) {
                    message = "Insert a card first."
                    return@Button
                }

                val payPrice = finalPrice ?: d.getPrice(ticketType)

                if (credit < payPrice) {
                    message = "Transaction refused: insufficient funds."
                    return@Button
                }

                // Update card
                val newCredit = credit - payPrice
                cardRepo.updateCredit(cardNumber, newCredit)
                cardCredit = newCredit

                // Update destination stats
                destRepo.incrementSales(d.id)
                destRepo.addTakings(d.id, payPrice)

                // Refresh
                destinations = destRepo.getAll()
                selectedDestination = destinations.firstOrNull { it.id == d.id }

                // Print ticket (required format)
                val formattedPrice = "%.2f".format(payPrice)
                val typeText = if (ticketType == TicketType.Single) "Single" else "Return"

                ticketText =
                    "ORIGIN STATION\n" +
                            "to\n" +
                            "${d.name}\n" +
                            "Price: $formattedPrice [$typeText]"

                message = "Purchase successful."
            },
            enabled = selectedDestination != null,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Buy ticket") }

        Spacer(Modifier.height(12.dp))

        if (ticketText != null) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Ticket", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(ticketText!!)
                }
            }
        }
    }
}

/* =========================================================
   ADMIN LOGIN
   ========================================================= */
@Composable
private fun AdminLoginScreen(onLoginOk: () -> Unit) {
    val adminRepo = remember { AdminRepository() }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Admin login", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val ok = adminRepo.validateLogin(username, password)
                if (ok) {
                    msg = null
                    onLoginOk()
                } else {
                    msg = "Login failed"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        if (msg != null) {
            Spacer(Modifier.height(8.dp))
            Text(msg!!)
        }

        Spacer(Modifier.height(10.dp))
        Text("Test login: admin / admin123")
    }
}

/* =========================================================
   ADMIN PANEL
   ========================================================= */
@Composable
private fun AdminPanelScreen(onLogout: () -> Unit) {
    val destRepo = remember { DestinationRepository() }
    val offerRepo = remember { OfferRepository() }

    var destinations by remember { mutableStateOf<List<Destination>>(emptyList()) }
    var selected by remember { mutableStateOf<Destination?>(null) }

    // edit prices
    var newSingle by remember { mutableStateOf("") }
    var newReturn by remember { mutableStateOf("") }

    // factor
    var factorText by remember { mutableStateOf("") }

    // add destination
    var newName by remember { mutableStateOf("") }
    var addSingle by remember { mutableStateOf("") }
    var addReturn by remember { mutableStateOf("") }

    // offers
    var offers by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var offerDestinationId by remember { mutableStateOf("") }
    var offerPercent by remember { mutableStateOf("") }
    var offerStart by remember { mutableStateOf("") } // YYYY-MM-DD
    var offerEnd by remember { mutableStateOf("") }   // YYYY-MM-DD
    var offerAfter by remember { mutableStateOf("") } // HH:MM or blank

    var msg by remember { mutableStateOf<String?>(null) }

    fun refreshAll() {
        destinations = destRepo.getAll()
        if (selected == null) selected = destinations.firstOrNull()
        offers = offerRepo.getAllOffers()
    }

    LaunchedEffect(Unit) {
        refreshAll()
        selected?.let {
            newSingle = "%.2f".format(it.singlePrice)
            newReturn = "%.2f".format(it.returnPrice)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {


    Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Admin panel", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.weight(1f))
            Button(onClick = onLogout) { Text("Logout") }
        }

        Spacer(Modifier.height(12.dp))

        // Select destination list
        Text("Select destination:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp)) {
            items(destinations) { d ->
                val isSelected = selected?.id == d.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = {
                        selected = d
                        newSingle = "%.2f".format(d.singlePrice)
                        newReturn = "%.2f".format(d.returnPrice)
                        msg = null
                    }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(d.name, style = MaterialTheme.typography.titleMedium)
                        Text("Single: ${"%.2f".format(d.singlePrice)}")
                        Text("Return: ${"%.2f".format(d.returnPrice)}")
                        Text("Sales: ${d.sales}")
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Edit prices for selected
        Text("Edit prices (selected station)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = newSingle,
            onValueChange = { newSingle = it },
            label = { Text("New single price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = newReturn,
            onValueChange = { newReturn = it },
            label = { Text("New return price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                val d = selected ?: return@Button
                val s = newSingle.replace(",", ".").toDoubleOrNull()
                val r = newReturn.replace(",", ".").toDoubleOrNull()
                if (s == null || r == null) {
                    msg = "Invalid prices"
                    return@Button
                }
                if (s < 0 || r < 0) {
                    msg = "Prices cannot be negative"
                    return@Button
                }
                destRepo.updatePrices(d.id, s, r)
                msg = "Prices updated."
                refreshAll()
                selected = destinations.firstOrNull { it.id == d.id }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save prices") }

        Spacer(Modifier.height(18.dp))

        // Factor
        Text("Change ALL prices by factor", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = factorText,
            onValueChange = { factorText = it },
            label = { Text("Factor (e.g. 1.1 or 0.9)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                val f = factorText.replace(",", ".").toDoubleOrNull()
                if (f == null) {
                    msg = "Invalid factor"
                    return@Button
                }
                if (f <= 0) {
                    msg = "Factor must be > 0"
                    return@Button
                }
                destRepo.applyPriceFactor(f)
                msg = "All prices changed by factor $f"
                refreshAll()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Apply factor") }

        Spacer(Modifier.height(18.dp))
        Divider()
        Spacer(Modifier.height(18.dp))

        // Add destination
        Text("Add destination", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = addSingle,
            onValueChange = { addSingle = it },
            label = { Text("Single price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = addReturn,
            onValueChange = { addReturn = it },
            label = { Text("Return price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                val name = newName.trim()
                val s = addSingle.replace(",", ".").toDoubleOrNull()
                val r = addReturn.replace(",", ".").toDoubleOrNull()

                if (name.isBlank()) {
                    msg = "Name cannot be empty"
                    return@Button
                }
                if (s == null || r == null) {
                    msg = "Invalid prices"
                    return@Button
                }
                if (s < 0 || r < 0) {
                    msg = "Prices cannot be negative"
                    return@Button
                }

                destRepo.addDestination(name, s, r)
                msg = "Destination added: $name"
                newName = ""
                addSingle = ""
                addReturn = ""

                refreshAll()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add destination") }

        Spacer(Modifier.height(18.dp))
        Divider()
        Spacer(Modifier.height(18.dp))

        // Special offers
        Text("Special offers", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Text("Add offer", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = offerDestinationId,
            onValueChange = { offerDestinationId = it.filter { ch -> ch.isDigit() } },
            label = { Text("Destination ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = offerPercent,
            onValueChange = { offerPercent = it },
            label = { Text("Discount percent (e.g. 10)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = offerStart,
            onValueChange = { offerStart = it },
            label = { Text("Start date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = offerEnd,
            onValueChange = { offerEnd = it },
            label = { Text("End date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = offerAfter,
            onValueChange = { offerAfter = it },
            label = { Text("After time (HH:MM) optional") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                val destId = offerDestinationId.toIntOrNull()
                val pct = offerPercent.replace(",", ".").toDoubleOrNull()
                val start = offerStart.trim()
                val end = offerEnd.trim()
                val after = offerAfter.trim().ifBlank { null }

                if (destId == null) {
                    msg = "Invalid Destination ID"
                    return@Button
                }
                if (pct == null || pct <= 0 || pct > 100) {
                    msg = "Discount must be between 0 and 100"
                    return@Button
                }
                runCatching { LocalDate.parse(start) }.getOrElse {
                    msg = "Invalid start date"
                    return@Button
                }
                runCatching { LocalDate.parse(end) }.getOrElse {
                    msg = "Invalid end date"
                    return@Button
                }
                if (after != null) {
                    runCatching { LocalTime.parse(after) }.getOrElse {
                        msg = "Invalid after time"
                        return@Button
                    }
                }

                offerRepo.addOffer(destId, pct, start, end, after)
                msg = "Offer added."
                offerDestinationId = ""
                offerPercent = ""
                offerStart = ""
                offerEnd = ""
                offerAfter = ""
                refreshAll()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add offer") }

        Spacer(Modifier.height(14.dp))

        Text("Existing offers", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)) {
            items(offers) { o ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Offer #${o.id}", style = MaterialTheme.typography.titleMedium)
                        Text("Destination ID: ${o.destinationId}")
                        Text("Discount: ${o.discountPercent}%")
                        Text("From: ${o.startDate}  To: ${o.endDate}")
                        Text("After: ${o.afterTime ?: "Any time"}")
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                offerRepo.deleteOffer(o.id)
                                msg = "Offer deleted."
                                refreshAll()
                            }
                        ) { Text("Delete") }
                    }
                }
            }
        }

        if (msg != null) {
            Spacer(Modifier.height(10.dp))
            Text(msg!!)
        }
    }
}
