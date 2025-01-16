package com.david.cruddefinitivo.Clase

import android.os.Parcelable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Text
import coil.compose.AsyncImage
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
    var puntuacion:Float=0f,
    var fecha_captura:String="",
    var stability:Int=0
): Serializable{
    constructor(
        name: String,
        tipo: List<PokemonTipoFB>,
        num:Int,
        puntuacion:Float,
        fecha_captura:String,
    ) : this(null,null, null, name, tipo,num,puntuacion,fecha_captura)
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
fun PokeCard(poke: PokemonFB){
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Moderate bouncing
            stiffness = Spring.StiffnessMedium // Moderate stiffness
        )
    )
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val(foto,forma)=createRefs()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .scale(scale.value)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Remove default ripple effect

                    onClick = {

                    }
                )
                .indication(
                    interactionSource = interactionSource,
                    indication = null
                )
                .pointerInput(Unit) {//lo que hace al pulsar en el Card()
                    detectTapGestures(
                        onPress = {


                            isPressed = true
                            try {
                                awaitRelease()
                            } finally {
                                isPressed = false // Reset isPressed in finally block
                            }

                        }
                    )
                }
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    //.background(colorResource(R.color.objeto_lista))
                    .padding(end = 30.dp)
            ) {
                //imagen remota
                AsyncImage(
                    model = poke.imagenFB,
                    contentDescription = "Pokemon Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxSize()
                        .constrainAs(foto) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
                Text(
                    text = poke.name,
                    //color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .constrainAs(forma) {
                            start.linkTo(foto.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
                )
            }
        }
    }
}