package com.david.cruddefinitivo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.david.cruddefinitivo.Clase.ForoFB
import com.david.cruddefinitivo.Clase.MensajeFB
import com.david.cruddefinitivo.Clase.PokeCard
import com.david.cruddefinitivo.Clase.PokemonFB
import com.david.cruddefinitivo.Clase.UserFb
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import com.david.cruddefinitivo.ui.theme.Purple40
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ListaRegistradosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                cargaRegistrados()
            }
            val registradosState by registrados_lista.collectAsState()
            CrudDefinitivoTheme {
                ListaRegistrados(
                    modifier = Modifier
                        .background(Purple40)
                        .fillMaxSize(),
                    registradosState = registradosState)
            }
        }
    }
}
//
@Composable
fun ListaRegistrados(
    modifier: Modifier = Modifier,
    registradosState: List<PokemonFB>,
    //usuario: UserFb,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val RegistroPoke by registrados_lista.collectAsState(initial = registradosState)

    var textobusqueda by remember { mutableStateOf("") }
    var tipoBuscado1 by remember { mutableStateOf("") }
    var tipoBuscado2 by remember { mutableStateOf("") }
    var byFecha by remember { mutableStateOf(false) }
    var listaFiltrada by remember { mutableStateOf(RegistroPoke) }


    LaunchedEffect(key1=confirmaBusqueda) {
        if (confirmaBusqueda) {
            if(tipoBuscado1.contains("Sin tipo")) tipoBuscado1=""
            if(tipoBuscado2.contains("Sin tipo")) tipoBuscado2=""

            listaFiltrada = RegistroPoke.filter { pokemon ->
                (textobusqueda.isEmpty() || pokemon.name.contains(textobusqueda, ignoreCase = true)) &&
                        (tipoBuscado1.isEmpty() || pokemon.tipo.any { it.tag.contains(tipoBuscado1, ignoreCase = true) }) &&
                        (tipoBuscado2.isEmpty() || pokemon.tipo.any { it.tag.contains(tipoBuscado2, ignoreCase = true) })
            }
            confirmaBusqueda = false
        }
    }

    LaunchedEffect(key1 = RegistroPoke) {
        listaFiltrada = RegistroPoke
    }

    val alturaCampoBusqueda by animateFloatAsState(
        targetValue = if (campoBusqueda) 200f else 0f,
        animationSpec = tween(durationMillis = 300) // duración
    )

    ConstraintLayout(
        modifier = modifier
    ) {
        val (lista, boton1, titulo,boton_busqueda,layoutBusqueda) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(titulo) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(lista.top)
                },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            text = "REGISTRADOS"
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .constrainAs(lista) {
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
                pokemon.id?.let { pokemon_id ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { it ->
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                scope.launch {
                                    val updatedRegistrados = RegistroPoke.filter { it.id != pokemon.id }
                                    registrados_lista.emit(updatedRegistrados) // Emit the new list
                                    listaFiltrada = updatedRegistrados
                                    refBBDD.child("registrados").child(pokemon_id).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Has liberado a ${pokemon.name}", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            // Handle error
                                        }
                                    // Delete from Appwrite
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
                    top.linkTo(lista.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(boton_busqueda.start)
                    bottom.linkTo(parent.bottom)
                }
        ){
            BotonAtras()
        }
        Button(
            onClick = {
                if(!campoBusqueda){
                    textobusqueda = ""
                    tipoBuscado1 = ""
                    tipoBuscado2 = ""
                }
                campoBusqueda = !campoBusqueda
                confirmaBusqueda = true
            },
            modifier = Modifier
                .constrainAs(boton_busqueda) {
                    //end.linkTo(parent.end)
                    start.linkTo(boton1.end)
                    end.linkTo(parent.end)
                    if (campoBusqueda) bottom.linkTo(layoutBusqueda.top)
                    else bottom.linkTo(boton1.bottom)
                },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp
            ),
        ) {
            Text(text = "Filtrar")
        }
        //BUSQUEDA
        if (campoBusqueda || alturaCampoBusqueda > 0f) {
            Row (
                modifier = Modifier
                    .background(colorResource(R.color.rojo_primario))
                    .constrainAs(layoutBusqueda) {
                        //top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        linkTo(layoutBusqueda.bottom, parent.bottom, bias = 1f)
                    }
                    .height(alturaCampoBusqueda.dp)
                    .padding(horizontal = 10.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {},
            ){
                ContenidoMenuBusqueda(
                    textobusqueda,
                    tipoBuscado1,
                    tipoBuscado2,
                    byFecha,
                    { textobusqueda = it },
                    { tipoBuscado1 = it },
                    { tipoBuscado2 = it },
                    { byFecha = it }
                )
            }
        }
    }
}

fun observaRegistrados(): Flow<List<PokemonFB>> {
    return callbackFlow {
        val listener = refBBDD.child("registrados")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val registrados = mutableListOf<PokemonFB>()
                    for (childSnapshot in snapshot.children) {
                        val pokemon = childSnapshot.getValue(PokemonFB::class.java)
                        if (pokemon != null) {
                            registrados.add(pokemon)
                        }
                    }
                    trySend(registrados)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { refBBDD.removeEventListener(listener) }
    }
}

fun cargaRegistrados(){
    refBBDD.child("registrados").get().addOnSuccessListener { dataSnapshot ->
        val pokemonList = mutableListOf<PokemonFB>()
        for (childSnapshot in dataSnapshot.children) {
            val pokemon = childSnapshot.getValue(PokemonFB::class.java)
            pokemon?.let { pokemonList.add(it) }
        }
        registrados_lista.value = pokemonList
    }.addOnFailureListener { exception ->
        // Handle error (e.g., show Toast)

    }
}