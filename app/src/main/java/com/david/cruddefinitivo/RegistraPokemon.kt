package com.david.cruddefinitivo

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.david.cruddefinitivo.Clase.PokemonFB
import com.david.cruddefinitivo.Clase.PokemonTipoFB
import com.david.cruddefinitivo.Clase.enumTipoToColorTipo
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme

class RegistraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                Registra()
            }
        }
    }
}

@Composable
fun Registra() {
    var newPokemon= PokemonFB()
    var nombre by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var tipo1 by remember { mutableStateOf("") }
    var tipo2 by remember { mutableStateOf("") }
    var puntuacion by remember { mutableIntStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ){
        val (foto, text_inputs, spinner_tipos,estrellas)=createRefs()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.rojo_primario))
                .constrainAs(foto) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(text_inputs.top)
                },
            horizontalArrangement = Arrangement.Center
        ){
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .size(300.dp)
                        .clickable { launcher.launch("image/*") }
                )
            }
            else{
                Image(
                    painter = painterResource(R.drawable.pokeball),
                    contentDescription = "Pokemon",
                    modifier = Modifier
                        .size(300.dp)
                        .fillMaxSize()
                        .clickable { launcher.launch("image/*") }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp,vertical = 15.dp)
                .constrainAs(text_inputs){
                    top.linkTo(foto.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    //bottom.linkTo(parent.bottom)
                },
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            OutlinedTextField(
                modifier = Modifier
                    .background(colorResource(id = R.color.transparente))
                    .fillMaxWidth(0.5f),
                value = nombre,
                onValueChange = { nombre = it },
                maxLines = 1,
                label = { Text(
                    color= colorResource(R.color.black),
                    text="nombre") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.rojo_oscuro),
                    unfocusedBorderColor = colorResource(R.color.rojo_primario),
                    cursorColor = colorResource(R.color.black),
                    focusedContainerColor= colorResource(R.color.white),
                    focusedTextColor= colorResource(R.color.black),
                    unfocusedTextColor= colorResource(R.color.black),
                )
            )
            OutlinedTextField(
                modifier = Modifier
                    .background(colorResource(id = R.color.transparente))
                    .fillMaxWidth(0.4f),
                value = numero,
                onValueChange = { numero = it },
                maxLines = 1,
                label = { Text(
                    color= colorResource(R.color.black),
                    text="núm") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.rojo_oscuro),
                    unfocusedBorderColor = colorResource(R.color.rojo_primario),
                    cursorColor = colorResource(R.color.black),
                    focusedContainerColor= colorResource(R.color.white),
                    focusedTextColor= colorResource(R.color.black),
                    unfocusedTextColor= colorResource(R.color.black),
                )
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .constrainAs(spinner_tipos){
                    top.linkTo(text_inputs.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(estrellas.top)
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            SeleccionaTipo(modifier = Modifier.fillMaxWidth(0.4f),tipo1) { tipo1 = it }
            SeleccionaTipo(modifier = Modifier.fillMaxWidth(0.4f),tipo2) { tipo2 = it }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .constrainAs(estrellas){
                    top.linkTo(spinner_tipos.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Estrellas(modifier = Modifier,puntuacion) { puntuacion = it }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionaTipo(
    modifier: Modifier = Modifier,
    tipo1: String,
    onTipoChange: (String) -> Unit,
) {

    var selectedTipo by remember {
        mutableStateOf(
            when {
                tipo1.isNotEmpty() -> {
                    try {
                        PokemonTipoFB.valueOf(tipo1)
                    } catch (e: IllegalArgumentException) {
                        PokemonTipoFB.NULL // Default if mapping fails
                    }
                }
                else -> {
                    // Default value if both are empty
                    PokemonTipoFB.NULL
                }
            }
        )
    }

    var expanded by remember { mutableStateOf(false) }

    val coloresSpinner: TextFieldColors = ExposedDropdownMenuDefaults.textFieldColors(
        focusedTextColor = colorResource(R.color.black),
        unfocusedTextColor = colorResource(R.color.black),
        focusedContainerColor = enumTipoToColorTipo(selectedTipo),
        unfocusedContainerColor = enumTipoToColorTipo(selectedTipo),
        focusedIndicatorColor = colorResource(R.color.lista_sin_foco),
        unfocusedIndicatorColor = colorResource(R.color.lista_sin_foco),
        focusedTrailingIconColor = colorResource(R.color.black),
        unfocusedTrailingIconColor = colorResource(R.color.black),
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = if (selectedTipo != PokemonTipoFB.NULL) selectedTipo.name else "Sin tipo",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    modifier = Modifier.graphicsLayer { compositingStrategy =
                        CompositingStrategy.Offscreen }
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = coloresSpinner,

            )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(enumTipoToColorTipo(selectedTipo)),
        ) {
            PokemonTipoFB.entries.forEach { tipo ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (tipo != PokemonTipoFB.NULL) tipo.name else "Sin tipo",
                            color = colorResource(R.color.black)
                        )
                    },
                    onClick = {
                        selectedTipo = tipo
                        expanded = false
                        onTipoChange(tipo.tag)
                    },
                    modifier = Modifier
                        .padding(vertical = 0.dp)
                        .background(enumTipoToColorTipo(tipo))
                )
            }
        }
    }
}
@Composable
fun Estrellas(
    modifier: Modifier = Modifier,
    puntuacion: Int,
    onPuntuacionChange: (Int) -> Unit,
) {
    var puntuacionState by remember { mutableIntStateOf(puntuacion) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..5) {
            val starIcon = if (i <= puntuacionState) {
                R.drawable.star_full
            } else {
                R.drawable.star_empty
            }
            Image(
                painter = painterResource(starIcon),
                contentDescription = "Star $i",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        puntuacionState = i
                        onPuntuacionChange(i)
                    }
            )
        }
    }
}