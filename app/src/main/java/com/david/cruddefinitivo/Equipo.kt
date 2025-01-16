package com.david.cruddefinitivo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import kotlinx.coroutines.launch

class EquipoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                //asdf
            }
        }
    }
}

@Composable
fun MuestraEquipo_last(
    modifier: Modifier = Modifier,
    equipoState: List<PokemonFB>,
    usuario: UserFb,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val equipoPoke by equipo_lista.collectAsState(initial = equipoState) // Collect flow as state

    ConstraintLayout(
        modifier = modifier
    ) {
        val (equipo, boton1, boton2, titulo) = createRefs()
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
            items(equipoPoke, key = { it.num }) { pokemon -> // Use a key
                usuario.key?.let { usuario_key ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                scope.launch {
                                    val updatedEquipo = equipoPoke.filter { it.name != pokemon.name }
                                    equipo_lista.emit(updatedEquipo) // Emit the new list
                                    val updates = hashMapOf<String, Any>(
                                        "usuarios/$usuario_key/equipo" to updatedEquipo
                                    )
                                    refBBDD.updateChildren(updates)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Has liberado a ${pokemon.name}", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            // Handle error
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
                                    tint = blanco5
                                )
                            }
                        }
                    ) {
                        PokemonCard(
                            pokemon = pokemon,
                            usuario_key = usuario_key,
                            opc = 2,
                            onCardClick = { selectedPokemon ->
                                navController.navigate(Screen.Vista.route)
                                seleccionado = selectedPokemon
                                index = selectedPokemon.num - 1
                            }
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .constrainAs(boton1) {
                    start.linkTo(parent.start)
                    end.linkTo(boton2.start)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.bottom)
                },
            shape = RoundedCornerShape(10.dp),
            onClick = {
                //mostrar interacciones
                navController.navigate(Screen.Interacciones.route)

            }
        ) {
            Text("Interacciones")
        }
        Button(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .constrainAs(boton2) {
                    start.linkTo(boton1.end)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.bottom)
                },
            shape = RoundedCornerShape(10.dp),
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Atrás")
        }
    }
}