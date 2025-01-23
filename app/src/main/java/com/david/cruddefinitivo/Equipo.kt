package com.david.cruddefinitivo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.david.cruddefinitivo.Clase.PokeCard
import com.david.cruddefinitivo.Clase.PokemonFB
import com.david.cruddefinitivo.Clase.PokemonTipoFB
import com.david.cruddefinitivo.Clase.UserFb
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.Clase.enumTipoToColorTipo
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import com.david.cruddefinitivo.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

var equipo_lista: MutableStateFlow<List<PokemonFB>> = MutableStateFlow(emptyList())
var registrados_lista: MutableStateFlow<List<PokemonFB>> = MutableStateFlow(emptyList())
var campoBusqueda by mutableStateOf(false)
var confirmaBusqueda by mutableStateOf(true)

class EquipoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //enableEdgeToEdge()
        setContent {
            val usuario = UsuarioFromKey(usuario_key, refBBDD)
            LaunchedEffect(key1 = usuario.key) {
                if (usuario.key != null) {
                    equipo_lista.value = usuario.equipo
                    cargaRegistrados()
                }
            }
            val equipoState by equipo_lista.collectAsState()
            CrudDefinitivoTheme {
                MuestraEquipo(
                    modifier = Modifier
                        .background(Purple40)
                        .fillMaxSize(),
                    usuario = usuario,
                    equipoState = equipoState
                )
            }
        }
    }
}
//
@Composable
fun MuestraEquipo(
    modifier: Modifier = Modifier,
    equipoState: List<PokemonFB>,
    usuario: UserFb,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val equipoPoke by equipo_lista.collectAsState(initial = equipoState)

    var textobusqueda by remember { mutableStateOf("") }
    var tipoBuscado1 by remember { mutableStateOf("") }
    var tipoBuscado2 by remember { mutableStateOf("") }
    var byFecha by remember { mutableStateOf(false) }
    var listaFiltrada by remember { mutableStateOf(equipoPoke) }


    LaunchedEffect(key1=confirmaBusqueda) {
        if (confirmaBusqueda) {
            if(tipoBuscado1.contains("Sin tipo")) tipoBuscado1=""
            if(tipoBuscado2.contains("Sin tipo")) tipoBuscado2=""

            listaFiltrada = equipoPoke.filter { pokemon ->
                (textobusqueda.isEmpty() || pokemon.name.contains(textobusqueda, ignoreCase = true)) &&
                        (tipoBuscado1.isEmpty() || pokemon.tipo.any { it.tag.contains(tipoBuscado1, ignoreCase = true) }) &&
                        (tipoBuscado2.isEmpty() || pokemon.tipo.any { it.tag.contains(tipoBuscado2, ignoreCase = true) })
            }
            confirmaBusqueda = false
        }
    }

    LaunchedEffect(key1 = equipoPoke) {
        listaFiltrada = equipoPoke
    }

    val alturaCampoBusqueda by animateFloatAsState(
        targetValue = if (campoBusqueda) 200f else 0f,
        animationSpec = tween(durationMillis = 300) // duración
    )

    ConstraintLayout(
        modifier = modifier
    ) {
        val (equipo, boton1, titulo,boton_busqueda,layoutBusqueda) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(titulo) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(equipo.top)
                },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            text = "EQUIPO"
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .constrainAs(equipo) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(titulo.bottom)
                    bottom.linkTo(boton1.top)
                }
        ) {
            items(
                items = listaFiltrada, // Use listaFiltrada here
                key = { pokemon -> pokemon.name } // Use a unique and stable key
            ) { pokemon ->
                usuario.key?.let { usuario_key ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                scope.launch {
                                    val updatedEquipo = equipoPoke.filter { it.name != pokemon.name }
                                    //Log.d("MuestraEquipo", "Updated equipoPoke: ${updatedEquipo.map { it.name }}")
                                    equipo_lista.emit(updatedEquipo) // Emit the new list
                                    listaFiltrada = updatedEquipo
                                    val updates = hashMapOf<String, Any>(
                                        "usuarios/$usuario_key/equipo" to updatedEquipo
                                    )
                                    refBBDD.updateChildren(updates)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "${pokemon.name} ya no está en tu Equipo", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            // Handle error
                                        }
                                    //se borra la foto de appwrite
                                    val id_imagen = pokemon.id_imagen
                                    if (id_imagen != null) {
                                        try {
                                            withContext(Dispatchers.IO) {
                                                storage.deleteFile(
                                                    bucketId = appwrite_bucket,
                                                    fileId = id_imagen
                                                )
                                            }
                                        } catch (e: Exception) {
                                            Log.e("DeleteError", "Error deleting Appwrite file: ${e.message}")
                                        }
                                    }
                                }
                                true
                            } else {
                                false
                            }
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromEndToStart = true,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Borrar",
                                    tint = Color.Black
                                )
                            }
                        }
                    ) {
                        PokeCard(pokemon)
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .constrainAs(boton1)
                {
                    top.linkTo(equipo.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        ){
            BotonAtras()
        }
    }
}






@Composable
fun BotonAtras() {
    val context = LocalContext.current
    Button(onClick = {
        if (context is ComponentActivity) {
            context.finish()
        }
    }) {
        Text("Atras")
    }
}