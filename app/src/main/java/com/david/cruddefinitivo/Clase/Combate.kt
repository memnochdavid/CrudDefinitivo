package com.david.cruddefinitivo.Clase

import java.io.Serializable

data class Combate(
    var id: String="",
    var usuario1: String="",
    var usuario2: String="",
    var vencedor: String="",
    var fecha: Long=0L
) : Serializable {
    constructor(
        usuario1: String,
        usuario2: String,
        vencedor: String,
    ) : this(usuario1=usuario1, usuario2=usuario2, vencedor=vencedor, id="", fecha=0L)
    init {
        id = generateCombateId(usuario1, usuario2)
        fecha = System.currentTimeMillis()
    }
}
fun generateCombateId(user1Id: String, user2Id: String): String {
    val userIds = listOf(user1Id, user2Id).sorted()
    return userIds[0] + "@" + userIds[1]
}
