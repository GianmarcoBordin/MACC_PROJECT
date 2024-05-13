package com.mygdx.game.presentation.map.components



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.gson.JsonObject
import com.mygdx.game.R

@Composable
fun ObjectDialog(imageId: Int, distanceFromMe: String, enabled: Boolean, onCatchObject: () -> Unit ,onDismissRequest: () -> Unit) {

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

                // TODO structure dialog content
                //Text(text = dialogContent)
                ObjectDialogLine(imageId = imageId, distance = distanceFromMe)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    Button(
                        modifier = Modifier.padding(end = 5.dp),
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Close")
                    }

                    Button(
                        modifier = Modifier.padding(start = 5.dp),
                        onClick = onCatchObject,
                    ) {
                        Text("Catch")
                    }
                }

            }



        }
    }


}

@Composable
fun ObjectDialogLine(imageId : Int, distance: String){
    Image(
        painter = painterResource(id = imageId),
        contentDescription = "",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .width(70.dp)
            .height(70.dp)
    )


    Text(
        text = "The object is far $distance from you! Catch it",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(16.dp),

    )

}


/*
@Preview
@Composable
fun DialogPreview(){
    ObjectDialog(imageId = R.drawable.gunner_yellow, distanceFromMe = "19", onCatchObject = { /*TODO*/ }) {

    }

}
*/


