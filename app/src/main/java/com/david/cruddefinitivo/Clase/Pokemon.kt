package com.david.cruddefinitivo.Clase

import android.content.Intent
import android.os.Parcelable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Text
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.david.cruddefinitivo.FichaPokemonActivity
import com.david.cruddefinitivo.R
import com.david.cruddefinitivo.campoBusqueda
import java.io.Serializable
import com.david.cruddefinitivo.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PokemonFB(
    var id: String? = null,
    var imagenFB: String? = null,
    var id_imagen: String? = null,
    var name: String="",
    var tipo: List<PokemonTipoFB> = mutableListOf(),
    var num:Int=0,
    var entrenador:String="",
    var puntuacion:Float=0f,
    var fecha_captura:String="",
    var stability:Int=0
): Serializable{
    constructor(
        name: String,
        tipo: List<PokemonTipoFB>,
        num:Int,
        entrenador:String,
        puntuacion:Float,
        fecha_captura:String,
    ) : this(null,null, null, name, tipo,num, entrenador,puntuacion,fecha_captura)
    init{
        fecha_captura=getCurrentDate()
    }

}


enum class PokemonTipoFB(val tag: String) {
    PLANTA("planta"),
    AGUA("agua"),
    FUEGO("fuego"),
    LUCHA("lucha"),
    VENENO("veneno"),
    ACERO("acero"),
    BICHO("bicho"),
    DRAGON("dragon"),
    ELECTRICO("electrico"),
    HADA("hada"),
    HIELO("hielo"),
    PSIQUICO("psiquico"),
    ROCA("roca"),
    TIERRA("tierra"),
    SINIESTRO("siniestro"),
    NORMAL("normal"),
    VOLADOR("volador"),
    FANTASMA("fantasma"),
    NULL("null");
}

fun enumTipoToColorTipo(tipo:PokemonTipoFB): Color {
    when (tipo) {
        PokemonTipoFB.PLANTA -> return color_planta
        PokemonTipoFB.AGUA -> return color_agua
        PokemonTipoFB.FUEGO -> return color_fuego
        PokemonTipoFB.LUCHA -> return color_lucha
        PokemonTipoFB.VENENO -> return color_veneno
        PokemonTipoFB.ACERO -> return color_acero
        PokemonTipoFB.BICHO -> return color_bicho
        PokemonTipoFB.DRAGON -> return color_dragon
        PokemonTipoFB.ELECTRICO -> return color_electrico
        PokemonTipoFB.HADA -> return color_hada
        PokemonTipoFB.HIELO -> return color_hielo
        PokemonTipoFB.PSIQUICO -> return color_psiquico
        PokemonTipoFB.ROCA -> return color_roca
        PokemonTipoFB.TIERRA -> return color_tierra
        PokemonTipoFB.SINIESTRO -> return color_siniestro
        PokemonTipoFB.NORMAL -> return color_normal
        PokemonTipoFB.VOLADOR -> return color_volador
        PokemonTipoFB.FANTASMA -> return color_fantasma
        else -> return color_normal
    }
}
fun getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentDate.format(formatter)
}

@Composable
fun PokeCard(poke: PokemonFB) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Moderate bouncing
            stiffness = Spring.StiffnessMedium // Moderate stiffness
        )
    )
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (pokeball, pokemonImage, numero, pokemonName, tipo1, tipo2, estrellas) = createRefs()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .scale(scale.value)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {

                    }
                )
                .indication(
                    interactionSource = interactionSource,
                    indication = null
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            try {
                                awaitRelease()
                            } finally {
                                isPressed = false
                            }

                            val intent = Intent(context, FichaPokemonActivity::class.java)
                            intent.putExtra("pokemon", poke as Serializable)
                            context.startActivity(intent)


                        }
                    )
                }
        ) {
            var num = "${(poke.num)}"
            if (num.length == 1) num = "00${(poke.num)}"
            else if (num.length == 2) num = "0${(poke.num)}"

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    //.background(colorResource(R.color.objeto_lista))
                    .padding(end = 30.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.pokeball_icon),
                    contentDescription = "Pokeball",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(5.dp)
                        .constrainAs(pokeball) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        }
                )

                val painter = rememberAsyncImagePainter(
                    model = poke.imagenFB,
                    contentScale = ContentScale.Crop,
                )

                Image(
                    painter = painter,
                    contentDescription = "Pokemon Image",
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxSize()
                        .constrainAs(pokemonImage) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
                androidx.compose.material3.Text(
                    text = "#$num",
                    //color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .constrainAs(numero) {
                            start.linkTo(pokemonImage.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(tipo1.top)
                            end.linkTo(pokemonName.start)
                        }
                )
                Text(
                    text = poke.name,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .constrainAs(pokemonName) {
                            start.linkTo(numero.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(tipo1.top)
                            end.linkTo(parent.end)
                        }
                )

                EstrellasVisualizacion(
                    modifier = Modifier
                        .constrainAs(estrellas) {
                            start.linkTo(numero.start)
                            end.linkTo(parent.end)
                            top.linkTo(pokemonName.bottom)
                            bottom.linkTo(tipo1.top)
                        },
                    puntuacion = poke.puntuacion
                )

                if (poke.tipo.size == 1) {
                    Image(
                        painter = painterResource(id = enumToDrawableFB(poke.tipo[0])),
                        contentDescription = "Tipo 1",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(60.dp)
                            .height(25.dp)
                            .constrainAs(tipo1) {
                                start.linkTo(numero.start)
                                bottom.linkTo(parent.bottom)
                                top.linkTo(pokemonName.bottom)
                                end.linkTo(pokemonName.end)
                            }
                    )
                } else {
                    Image(
                        painter = painterResource(id = enumToDrawableFB(poke.tipo[0])),
                        contentDescription = "Tipo 1",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(60.dp)
                            .height(25.dp)
                            .constrainAs(tipo1) {
                                start.linkTo(numero.start)
                                bottom.linkTo(parent.bottom)
                                top.linkTo(estrellas.bottom)
                                end.linkTo(tipo2.start)
                            }
                    )
                    Image(
                        painter = painterResource(id = enumToDrawableFB(poke.tipo[1])),
                        contentDescription = "Tipo 2",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(60.dp)
                            .height(25.dp)
                            .constrainAs(tipo2) {
                                start.linkTo(tipo1.end)
                                bottom.linkTo(parent.bottom)
                                top.linkTo(estrellas.bottom)
                                end.linkTo(pokemonName.end)
                            }
                    )
                }
            }
        }
    }
}
@Composable
fun EstrellasVisualizacion(
    modifier: Modifier = Modifier,
    puntuacion: Float,
) {
    val puntuacionEntera = puntuacion.toInt()
    val decimal = puntuacion - puntuacionEntera

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..5) {
            val starIcon = when {
                i <= puntuacionEntera -> R.drawable.star_full
                i == puntuacionEntera + 1 && decimal >= 0.5f -> R.drawable.star_half
                else -> R.drawable.star_empty
            }
            Image(
                painter = painterResource(starIcon),
                contentDescription = "Star $i",
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}



fun enumToDrawableFB(tipo:PokemonTipoFB):Int{
    return when(tipo){
        PokemonTipoFB.PLANTA -> R.drawable.planta
        PokemonTipoFB.AGUA -> R.drawable.agua
        PokemonTipoFB.FUEGO -> R.drawable.fuego
        PokemonTipoFB.LUCHA -> R.drawable.lucha
        PokemonTipoFB.VENENO -> R.drawable.veneno
        PokemonTipoFB.ACERO -> R.drawable.acero
        PokemonTipoFB.BICHO -> R.drawable.bicho
        PokemonTipoFB.DRAGON -> R.drawable.dragon
        PokemonTipoFB.ELECTRICO -> R.drawable.electrico
        PokemonTipoFB.HADA -> R.drawable.hada
        PokemonTipoFB.HIELO -> R.drawable.hielo
        PokemonTipoFB.PSIQUICO -> R.drawable.psiquico
        PokemonTipoFB.ROCA -> R.drawable.roca
        PokemonTipoFB.TIERRA -> R.drawable.tierra
        PokemonTipoFB.SINIESTRO -> R.drawable.siniestro
        PokemonTipoFB.NORMAL -> R.drawable.normal
        PokemonTipoFB.VOLADOR -> R.drawable.volador
        PokemonTipoFB.FANTASMA -> R.drawable.fantasma
        else -> { R.drawable.fantasma}
    }
}

@Preview(showBackground = true,widthDp = 350, heightDp = 600)
@Composable
fun GreetingPreview9() {
    val poke = PokemonFB(
        imagenFB = "https://cloud.appwrite.io/v1/storage/buckets/674f4717002b4ea1e2c2/files/OGnfM2kVtUcjHujqVAU/preview?project=674f4655000119d78457",
        name="Pikachu",
        tipo=listOf(PokemonTipoFB.ELECTRICO),
        num=1,
        puntuacion =3f
    )
    PokeCard(poke)
}

