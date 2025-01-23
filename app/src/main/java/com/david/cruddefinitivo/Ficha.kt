package com.david.cruddefinitivo

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import coil.compose.rememberAsyncImagePainter
import com.david.cruddefinitivo.Clase.EstrellasVisualizacion
import com.david.cruddefinitivo.Clase.PokemonFB
import com.david.cruddefinitivo.Clase.UserFb
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.Clase.enumToDrawableFB
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FichaPokemonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pokemon = intent.getSerializableExtra("pokemon") as PokemonFB
        //enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                PokemonFicha(pokemon)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val pokemon = intent.getSerializableExtra("pokemon") as PokemonFB
        //enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                PokemonFicha(pokemon)
            }
        }
    }
}

@Composable
fun PokemonFicha(pokemon: PokemonFB) {
    var context = LocalContext.current
    val num=pokemon.num
    var numero = "${(num)}"
    if(numero.length == 1) numero = "00${(num)}"
    else if(numero.length == 2) numero = "0${(num)}"

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp)
    ) {
        val (datos_inner,foto,datos,estrellas,botones) = createRefs()
        val painter = rememberAsyncImagePainter(
            model = pokemon.imagenFB,
            contentScale = ContentScale.Crop,
        )
        Image(
            painter = painter,
            contentDescription = "avatar de usuario",
            modifier = Modifier
                .size(350.dp)
                .fillMaxSize()
                .constrainAs(foto) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(datos.top)
                }
        )
        LazyColumn(
            modifier = Modifier
                .constrainAs(datos) {
                    top.linkTo(foto.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
                .fillMaxHeight(),
            //reverseLayout = true,
        ){
            item{
                ConstraintLayout{
                    Row (
                        modifier = Modifier
                            .constrainAs(datos_inner) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                //bottom.linkTo(estrellas.top)
                            }
                    ){
                        MuestraDatos(pokemon, context)
                    }
                    Row (
                        modifier = Modifier
                            .constrainAs(estrellas) {
                                top.linkTo(datos_inner.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                //bottom.linkTo(parent.bottom)
                            },
                        horizontalArrangement =  Arrangement.Center
                    ){
                        EstrellasVisualizacion(
                            modifier = Modifier
                                .wrapContentWidth(),
                            puntuacion = pokemon.puntuacion
                        )
                    }
                    Row(
                        modifier = Modifier
                            .constrainAs(botones) {
                                top.linkTo(estrellas.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            },
                        horizontalArrangement =  Arrangement.Center
                    ){
                        Button(
                            onClick = {
                                if(pokemon.entrenador.equals(usuario_key)){
                                    val intent = Intent(context, EditaPokemonActivity::class.java)
                                    intent.putExtra("pokemon", pokemon)
                                    context.startActivity(intent)
                                }
                                else Toast.makeText(context, "No eres el entrenador de este pokemon", Toast.LENGTH_SHORT).show()
                            },
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 10.dp
                            ),
                        ) {
                            Text(text = "Editar")
                        }
                        BotonAtras()
                    }
                }
            }
        }
    }
}

@Composable
fun MuestraDatos(pokemon: PokemonFB, context: Context){
    val num=pokemon.num
    var numero = "${(num)}"
    if(numero.length == 1) numero = "00${(num)}"
    else if(numero.length == 2) numero = "0${(num)}"
    val usuario= UsuarioFromKey(usuario_key, refBBDD)

    var updatedEquipo by remember { mutableStateOf(usuario.equipo) }


    ConstraintLayout{
        val (number,desc, nombre, tipo1, tipo2,add) = createRefs()

        Text(modifier = Modifier
            .constrainAs(number) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                //bottom.linkTo(nombre.top)////////????????????
            }
            .padding(bottom = 10.dp)
            .fillMaxHeight(),//////////
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            text = "#$numero",
            fontSize = 35.sp)

        Text(modifier = Modifier
            .constrainAs(nombre) {
                top.linkTo(number.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                //bottom.linkTo(tipo1.top)
            }
            .padding(bottom = 15.dp),
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            text = pokemon.name,
            fontSize = 32.sp)

        IconButton(
            onClick = {
                if (usuario.equipo.size < 6) {
                    updatedEquipo = (usuario.equipo + pokemon).toMutableList()
                    val updates = hashMapOf<String, Any>(
                        "usuarios/${usuario.key}/equipo" to updatedEquipo
                    )

                    refBBDD.updateChildren(updates)
                        .addOnSuccessListener {
                            Toast.makeText(context, "${pokemon.name} se ha añadido a tu equipo", Toast.LENGTH_SHORT).show()
                            Log.v("AÑADEEEEEEEEE", pokemon.name)
                            // No need to finish the activity here
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure, but do not finish the activity
                            // You might want to display an error message to the user
                        }
                } else {
                    Toast.makeText(context, "Ya hay 6 Pokemon en tu equipo", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .constrainAs(add) {
                    top.linkTo(number.bottom)
                    start.linkTo(nombre.end)
                    end.linkTo(parent.end)
                    bottom.linkTo(tipo1.top)
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Descripción del icono", // Agrega una descripción de contenido
                tint = Color.Black
            )
        }

        if (pokemon.tipo.size == 1) {

            Image(
                painter = painterResource(id = enumToDrawableFB(pokemon.tipo[0])),
                contentDescription = "Tipo 1",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(80.dp)
                    .padding(top = 5.dp, bottom = 10.dp)
                    .constrainAs(tipo1) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(nombre.bottom)
                        bottom.linkTo(desc.top)
                    }
            )
        } else {
            Image(
                painter = painterResource(id = enumToDrawableFB(pokemon.tipo[0])),
                contentDescription = "Tipo 1",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(80.dp)
                    .padding(top = 5.dp, bottom = 10.dp)
                    .constrainAs(tipo1) {
                        start.linkTo(parent.start)
                        end.linkTo(tipo2.start)
                        top.linkTo(nombre.bottom)
                        bottom.linkTo(desc.top)
                    },
            )
            Image(
                painter = painterResource(id = enumToDrawableFB(pokemon.tipo[1])),
                contentDescription = "Tipo 2",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(80.dp)
                    .padding(top = 5.dp, bottom = 10.dp)
                    .constrainAs(tipo2) {
                        start.linkTo(tipo1.end)
                        end.linkTo(parent.end)
                        top.linkTo(nombre.bottom)
                        bottom.linkTo(desc.top)
                    }
            )
        }
    }
}
