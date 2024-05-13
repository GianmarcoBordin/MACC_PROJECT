package com.mygdx.game.presentation.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LegendToggle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mygdx.game.R

@Composable
fun InfoDialog(onDismissRequest: () -> Unit) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),

            ) {
            Column(
                modifier = Modifier
                    .padding(top = 32.dp, start = 32.dp, end = 32.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                InfoDialogLine(
                    imageId = R.drawable.main_player_location,
                    contentDescription = "Your location"
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))

                InfoDialogLine(
                    imageId = R.drawable.other_player_location,
                    contentDescription = "Other player"
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))

                InfoDialogLine(imageId = R.drawable.gunner_black, contentDescription = "Objects")
                Spacer(modifier = Modifier.padding(top = 10.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    Button(
                        onClick = onDismissRequest
                    ) {
                        Text("Close")
                    }

                }

            }


        }
    }


}

@Composable
fun InfoDialogLine(imageId : Int, contentDescription: String){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp),
            painter = painterResource(id = imageId),
            contentDescription = contentDescription,
            //contentScale = ContentScale.Inside
        )
        Text(
            modifier = Modifier.padding(start = 20.dp),
            text = contentDescription,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}



/*
@Preview
@Composable
fun dialogPreview(){
    InfoDialog()
}
*/
