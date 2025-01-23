package com.david.cruddefinitivo

import android.content.Context
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.david.cruddefinitivo.Clase.ForoFB
import com.david.cruddefinitivo.Clase.MensajeFB
import com.david.cruddefinitivo.Clase.PokeCard
import com.david.cruddefinitivo.Clase.PokemonFB
import com.david.cruddefinitivo.Clase.PokemonTipoFB
import com.david.cruddefinitivo.Clase.UserFb
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.Clase.enumTipoToColorTipo
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import com.david.cruddefinitivo.ui.theme.Pink40
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
            cargaRegistrados()
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
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val RegistroPoke by registrados_lista.collectAsState(initial = registradosState)

    var textobusqueda by remember { mutableStateOf("") }
    var tipoBuscado1 by remember { mutableStateOf("") }
    var tipoBuscado2 by remember { mutableStateOf("") }
    var byFecha by remember { mutableStateOf(false) }
    var byPuntuacion by remember { mutableStateOf(false) }
    var listaFiltrada by remember { mutableStateOf(RegistroPoke) }


    LaunchedEffect(key1 = confirmaBusqueda) {
        if (confirmaBusqueda) {
            if (tipoBuscado1.contains("Sin tipo")) tipoBuscado1 = ""
            if (tipoBuscado2.contains("Sin tipo")) tipoBuscado2 = ""

            listaFiltrada = RegistroPoke.filter { pokemon ->
                (textobusqueda.isEmpty() || pokemon.name.contains(textobusqueda, ignoreCase = true)) &&
                        (tipoBuscado1.isEmpty() || pokemon.tipo.any { it.tag.contains(tipoBuscado1, ignoreCase = true) }) &&
                        (tipoBuscado2.isEmpty() || pokemon.tipo.any { it.tag.contains(tipoBuscado2, ignoreCase = true) })
            }
            if(byFecha){
                listaFiltrada = listaFiltrada.sortedByDescending { it.fecha_captura }
            }
            if(byPuntuacion){
                listaFiltrada = listaFiltrada.sortedByDescending { it.puntuacion }
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
        val equipo_usuario = UsuarioFromKey(usuario_key, refBBDD).equipo
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
                                    if (pokemon in equipo_usuario) {
                                        Toast.makeText(context, "No puedes liberar a ${pokemon.name} mientras esté en tu equipo", Toast.LENGTH_SHORT).show()
                                    }
                                    else{
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
                                }
                                true
                            } else {
                                false
                            }
                        }
                    )
                    if (pokemon !in equipo_usuario) {
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
                    else{
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
                    .background(Pink40)
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
                    byPuntuacion,
                    { textobusqueda = it },
                    { tipoBuscado1 = it },
                    { tipoBuscado2 = it },
                    { byFecha = it },
                    { byPuntuacion = it }
                )
            }
        }
    }
}

fun cargaRegistrados(){
    refBBDD.child("registrados").get().addOnSuccessListener { dataSnapshot ->
        val pokemonList = mutableListOf<PokemonFB>()
        for (childSnapshot in dataSnapshot.children) {
            val pokemon = childSnapshot.getValue(PokemonFB::class.java)
            if (pokemon != null) {
                if(pokemon.entrenador.equals(usuario_key)){
                    pokemon.let { pokemonList.add(it) }
                }
            }
        }
        registrados_lista.value = pokemonList
    }.addOnFailureListener { exception ->
        // Handle error (e.g., show Toast)

    }
}

@Composable
fun ContenidoMenuBusqueda(
    textobusqueda: String,
    tipoBuscado1: String,
    tipoBuscado2: String,
    byFecha: Boolean,
    byPuntuacion: Boolean,
    textobusquedaOnChange: (String) -> Unit,
    onTipoBuscado1Change: (String) -> Unit,
    onTipoBuscado2Change: (String) -> Unit,
    onByFechaChange: (Boolean) -> Unit,
    onByPuntuacionChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f), // weight vertical
        ) {
            Column(
                // por nombre
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.75f), // weight horizontal
            ) {
                BusquedaNombre(textobusqueda) { newValue -> textobusquedaOnChange(newValue) }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f), // weight horizontal
            ) {
                OrdenaByFecha(byFecha) { newValue -> onByFechaChange(newValue) } // Call onByFechaChange
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f), // weight horizontal
            ) {
                OrdenaByPuntuacion(byPuntuacion) { newValue -> onByPuntuacionChange(newValue) } // Call onByFechaChange
            }
        }
        // tipos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f), // weight vertical
        ) {
            Column(
                modifier = Modifier
                    .weight(0.4f), // weight horizontal
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BusquedaTipo(tipoBuscado1, tipoBuscado2, 1) { newValue -> onTipoBuscado1Change(newValue) } // tipo1
            }
            Spacer(modifier = Modifier.weight(0.2f))
            Column(
                modifier = Modifier
                    .weight(0.4f), // weight horizontal
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BusquedaTipo(tipoBuscado1, tipoBuscado2, 2) { newValue -> onTipoBuscado2Change(newValue) } // tipo2
            }
        }
    }
}
@Composable
fun BusquedaNombre(
    textobusqueda: String,
    textobusquedaOnChange: (String) -> Unit,
){
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    val dark = sharedPreferences.getBoolean("dark_mode", false)

    OutlinedTextField(
        value = textobusqueda,
        onValueChange = { newValue -> textobusquedaOnChange(newValue) },
        label = { Text(
            color = Color.Black,
            text = "Buscar",
        ) },
        maxLines = 1,
        modifier = Modifier
            .padding(top = 15.dp, end = 35.dp)
            //.weight(0.3f)//weight vertical
            //.background(if(!dark){blanco60}else{blanco40})
            .clip(RoundedCornerShape(10.dp)),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                tint = colorResource(id = R.color.black),
                contentDescription = "Buscar"
            )
        },
        placeholder = { Text("nombre del Pokémon",style = TextStyle(color = colorResource(id = R.color.black)))},
        //shape = RoundedCornerShape(16.dp),


    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaTipo(
    tipoBuscado1: String,
    tipoBuscado2: String,
    opc: Int,
    onTipoBuscadoChange: (String) -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    val dark = sharedPreferences.getBoolean("dark_mode", false)

    var selectedTipo by remember {
        mutableStateOf(
            when {
                tipoBuscado1.isNotEmpty() -> {
                    try {
                        PokemonTipoFB.valueOf(tipoBuscado1)
                    } catch (e: IllegalArgumentException) {
                        PokemonTipoFB.NULL // Default if mapping fails
                    }
                }
                tipoBuscado2.isNotEmpty() -> {
                    // Logic to map tipoBuscado2 to a PokemonTipoFB value
                    try {
                        PokemonTipoFB.valueOf(tipoBuscado2)
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
        modifier = Modifier
            .wrapContentWidth()
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
                            color = Color.Black
                        )
                    },
                    onClick = {
                        selectedTipo = tipo
                        expanded = false
                        when (opc) {
                            1 -> onTipoBuscadoChange(tipo.tag)
                            2 -> onTipoBuscadoChange(tipo.tag)
                        }
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
fun OrdenaByFecha(
    byFecha: Boolean,
    onByFechaChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Fecha",
            color = Color.Black
        )
        Checkbox(
            checked = byFecha,
            onCheckedChange = { newValue -> onByFechaChange(newValue) }, // Call onByFechaChange
            colors = CheckboxDefaults.colors(
                checkedColor = colorResource(R.color.white),
                uncheckedColor = colorResource(R.color.white),
                checkmarkColor = Color.Green
            )
        )
    }
}
@Composable
fun OrdenaByPuntuacion(
    byPuntuacion: Boolean,
    onByPuntuacionChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Punt.",
            color = Color.Black
        )
        Checkbox(
            checked = byPuntuacion,
            onCheckedChange = { newValue -> onByPuntuacionChange(newValue) }, // Call onByFechaChange
            colors = CheckboxDefaults.colors(
                checkedColor = colorResource(R.color.white),
                uncheckedColor = colorResource(R.color.white),
                checkmarkColor = Color.Green
            )
        )
    }
}