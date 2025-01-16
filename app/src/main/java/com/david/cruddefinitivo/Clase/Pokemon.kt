package com.david.cruddefinitivo.Clase

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
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