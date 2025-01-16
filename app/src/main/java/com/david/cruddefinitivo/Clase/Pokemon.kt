package com.david.cruddefinitivo.Clase

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import java.io.Serializable
import com.david.cruddefinitivo.ui.theme.*

data class PokemonFB(
    var num:Int=0,
    var gen: String="",
    var foto:Int=0,//soporte local
    var imagenFB: String? = null,//soporte en la nube
    var name: String="",
    var desc: String="",
    var tipo: List<PokemonTipoFB> = listOf(),
    var evos: List<Int> = listOf(),
    var key: String? = null,
    var stability:Int=0
){
    constructor(
        num: Int,
        gen: String,
        foto: Int,
        name: String,
        desc: String,
        tipo: List<PokemonTipoFB>,
        evos: List<Int>,

        ) : this(num, gen, foto, null, name, desc, tipo, evos)
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