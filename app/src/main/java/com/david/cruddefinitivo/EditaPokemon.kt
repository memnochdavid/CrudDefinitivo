package com.david.cruddefinitivo

import android.content.Intent
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.david.cruddefinitivo.Clase.EstrellasVisualizacion
import com.david.cruddefinitivo.Clase.PokemonFB
import com.david.cruddefinitivo.Clase.PokemonTipoFB
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import io.appwrite.models.InputFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class EditaPokemonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pokemon = intent.getSerializableExtra("pokemon") as PokemonFB
        //enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                PokemonEdita(pokemon)
            }
        }
    }
}

@Composable
fun PokemonEdita(pokemon: PokemonFB) {
    var newPokemon= pokemon
    var nombre by remember { mutableStateOf(pokemon.name) }
    var numero by remember { mutableStateOf(pokemon.num.toString()) }
    var tipo1 by remember { mutableStateOf(pokemon.tipo[0]) }
    var tipo2 by remember { mutableStateOf(PokemonTipoFB.NULL) }
    if (pokemon.tipo.size > 1) {
        tipo2 = pokemon.tipo[1]
    }
    var link_foto by remember { mutableStateOf(pokemon.imagenFB) }
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
    LaunchedEffect(key1 = tipoList) {
        tipoList
    }
    LaunchedEffect(Unit) {
        nombre = newPokemon.name
        numero = newPokemon.num.toString()
        tipo1 = newPokemon.tipo[0]
        if (newPokemon.tipo.size > 1) {
            tipo2 = newPokemon.tipo[1]
        }
        link_foto = newPokemon.imagenFB
        puntuacion = newPokemon.puntuacion.toInt()

        cargaRegistrados()
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
                AsyncImage(
                    model = pokemon.imagenFB,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .size(300.dp)
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
                    text=nombre) },
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
                    .fillMaxWidth(0.5f),
                value = numero,
                onValueChange = { numero = it },
                maxLines = 1,
                label = { Text(
                    color= colorResource(R.color.black),
                    text=numero) },
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
                .constrainAs(spinner_tipos){
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
                .constrainAs(estrellas){
                    top.linkTo(spinner_tipos.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(botones.top)
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Estrellas(modifier = Modifier,puntuacion) { puntuacion = it }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .constrainAs(botones){
                    top.linkTo(estrellas.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val identificador_poke = pokemon.id

                    //subimos la imagen a appwrite storage y los datos a firebase
                    //var identificadorAppWrite = identificador_poke?.substring(1, 20) ?: "" // coge el identificador y lo adapta a appwrite

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
                Text("Guardar")
            }
            BotonAtras()
        }


    }
}