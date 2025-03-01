package com.david.cruddefinitivo

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import coil.compose.AsyncImage
import com.david.cruddefinitivo.Clase.PokemonFB
import com.david.cruddefinitivo.Clase.PokemonTipoFB
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.Clase.enumTipoToColorTipo
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import com.david.cruddefinitivo.ui.theme.*
import com.david.cruddefinitivo.ui.theme.Purple80
import io.appwrite.models.InputFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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
//
@Composable
fun Registra() {
    var newPokemon= PokemonFB()
    var nombre by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var tipo1 by remember { mutableStateOf(PokemonTipoFB.NULL) }
    var tipo2 by remember { mutableStateOf(PokemonTipoFB.NULL) }
    var link_foto by remember { mutableStateOf("") }
    var puntuacion by remember { mutableIntStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )
    var scopeUser = rememberCoroutineScope()
    val context = LocalContext.current
    val tipoList = mutableListOf<PokemonTipoFB>()
    val shape = RoundedCornerShape(10.dp)
    val colores_boton = ButtonDefaults.buttonColors(
        containerColor = color_planta,
        contentColor = Color.White
    )

    LaunchedEffect(key1 = tipoList) {
        tipoList
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize(),
    ){
        val (foto, text_inputs, spinner_tipos,estrellas,botones)=createRefs()
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
                val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.pokeball, null)
                val bitmap = (drawable as BitmapDrawable).bitmap
                val file = File(context.cacheDir, "pokeball.png")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                selectedImageUri = Uri.fromFile(file)
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
                .padding(horizontal = 10.dp, vertical = 15.dp)
                .constrainAs(text_inputs) {
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
                .fillMaxWidth()
                .constrainAs(spinner_tipos) {
                    top.linkTo(text_inputs.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(estrellas.top)
                },
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            SeleccionaTipo(modifier = Modifier.fillMaxWidth(0.4f),tipo1) { tipo1 = it }
            SeleccionaTipo(modifier = Modifier.fillMaxWidth(0.6f),tipo2) { tipo2 = it }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .constrainAs(estrellas) {
                    top.linkTo(spinner_tipos.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(botones.top)
                },
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Estrellas(modifier = Modifier,puntuacion) { puntuacion = it }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .constrainAs(botones) {
                    top.linkTo(estrellas.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                shape = shape,
                colors = colores_boton,
                onClick = {
                    val identificador_poke = refBBDD.child("registrados").push().key

                    var identificadorAppWrite = refBBDD.child("registrados").push().key!!.substring(1, 20) ?: ""
                    //necesario para crear un archivo temporal con la imagen

                    val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                    scopeUser.launch {//scope para las funciones de appwrite, pero ya aprovechamos y metemos el código de firebase
                        try{
                            val file = inputStream.use { input ->
                                val tempFile = kotlin.io.path.createTempFile(identificadorAppWrite).toFile()
                                if (input != null) {
                                    tempFile.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                InputFile.fromFile(tempFile) // tenemos un archivo temporal con la imagen
                            }

                            withContext(Dispatchers.IO) {
                                //se sube la imagen a appwrite
                                storage.createFile(
                                    bucketId = appwrite_bucket,
                                    fileId = identificadorAppWrite,
                                    file = file
                                )
                            }
                            link_foto = "https://cloud.appwrite.io/v1/storage/buckets/$appwrite_bucket/files/$identificadorAppWrite/preview?project=$appwrite_project&output=png"


                            tipoList.add(tipo1)
                            if (tipo2 != PokemonTipoFB.NULL) {
                                tipoList.add(tipo2)
                            }

                            newPokemon = PokemonFB(
                                id = identificador_poke,
                                imagenFB = link_foto,
                                id_imagen = identificadorAppWrite,
                                name = nombre,
                                tipo = tipoList,
                                num = numero.toInt(),
                                entrenador = usuario_key,
                                puntuacion = puntuacion.toFloat()
                            )
                            refBBDD.child("registrados").child(identificador_poke!!).setValue(newPokemon)




                        }catch (e: Exception){
                            Log.e("UploadError", "Error al subir la imagen: ${e.message}")
                        }finally {
                            Toast.makeText(context, "${newPokemon.name} registrado correctamente", Toast.LENGTH_SHORT).show()
                            if (context is ComponentActivity) {
                                context.finish()
                            }
                        }
                    }


                }
            ) {
                Text("Registrar")
            }
            BotonAtras()
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionaTipo(
    modifier: Modifier = Modifier,
    tipo1: PokemonTipoFB,
    onTipoChange: (PokemonTipoFB) -> Unit,
) {

    var selectedTipo by remember { mutableStateOf(tipo1) }
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
                .wrapContentWidth(),
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
                        onTipoChange(tipo)
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

