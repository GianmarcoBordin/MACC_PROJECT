package com.mygdx.game.presentation.inventory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mygdx.game.data.dao.GameItem

@Composable
fun InventoryScreen() {
    /*
    Scaffold(
        topBar = { TopAppBar(title = { Text("Inventory") }) },
        content = {
            InventoryPage(items)
        }
    )
     */
}

@Composable
fun InventoryItem(item: GameItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Item HP: ${item.hp}")
            Text(text = "Item Damage: ${item.damage}")
        }
    }
}

@Composable
fun InventoryPage(items: List<GameItem>) {
    LazyColumn {
        items(items.size) { index ->
            InventoryItem(items[index])
        }
    }
}