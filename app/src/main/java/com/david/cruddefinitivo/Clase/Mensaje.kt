package com.david.cruddefinitivo.Clase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.david.cruddefinitivo.R
import com.david.cruddefinitivo.refBBDD
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class MensajeFB(
    var idUser: String,
    var idReceptor: String,
    var fecha: Long, // Change to Long
    var texto: String
    //var leido:Boolean
) : Serializable {
    constructor() : this("", "", 0L, "")
}


data class ForoFB(
    var idForo: String,
    var mensajes: MutableList<MensajeFB>
) : Serializable {
    constructor(idForo: String) : this(idForo, mutableListOf<MensajeFB>())
    constructor() : this("", mutableListOf<MensajeFB>())
}
@Composable
fun Mensaje(mensaje: MensajeFB, color: Color, opc:Int) {
    // Convert Long timestamp to Date
    val messageDate = Date(mensaje.fecha)

    // Format the date
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val todayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val today = todayFormat.format(Date())
    val messageDay = todayFormat.format(messageDate)

    // Determine if the message is from today
    val fecha_mensaje_muestra = if (today == messageDay) {
        timeFormat.format(messageDate)
    } else {
        fullDateFormat.format(messageDate)
    }
    when (opc) {
        1 -> {//chat privado
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                ) {
                    Text(text = mensaje.texto, color = Color.White)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = fecha_mensaje_muestra,
                        color = Color.White,
                        fontSize = 8.sp
                    )
                }
            }
        }
        2 -> {//foro de usuarios
            val modifier = Modifier
                .height(30.dp)
                .wrapContentWidth()
                .background(colorResource(R.color.transparente))
            val usarAux = UsuarioFromKey(mensaje.idUser, refBBDD)
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column(
                        modifier = Modifier
                            .size(30.dp)
                    ){
                        UserButton(opc=2, sesion_id = mensaje.idUser, modifier = modifier)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                    ){
                        Text(text = usarAux.nick+" dijo:", color = Color.White)
                    }
                }
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(text = mensaje.texto, color = Color.White)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .wrapContentSize(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = fecha_mensaje_muestra,
                        color = Color.White,
                        fontSize = 8.sp,
                    )
                }
            }
        }
    }


}




suspend fun cargaForo(): ForoFB? {
    val idForo = "-OGQX_cyLTmCACBNRLtZ"
    return suspendCoroutine { continuation ->
        refBBDD.child("foro").child(idForo).get().addOnSuccessListener { snapshot ->
            val conversacionFB = if (snapshot.exists()) {
                val idForo_existente = snapshot.child("foro").getValue(String::class.java) ?: idForo
                val mensajes = mutableListOf<MensajeFB>()
                snapshot.child("mensajes").children.forEach { messageSnapshot ->
                    val mensaje = messageSnapshot.getValue(MensajeFB::class.java)
                    mensaje?.let { mensajes.add(it) }
                }
                ForoFB(idForo_existente, mensajes)
            } else {
                ForoFB(idForo, mutableListOf())
            }
            continuation.resume(conversacionFB)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}
fun observeForo(foroID: String): Flow<List<MensajeFB>> {
    return callbackFlow {
        val listener = refBBDD.child("foro").child(foroID).child("mensajes")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mensajes = mutableListOf<MensajeFB>()
                    for (childSnapshot in snapshot.children) {
                        val mensaje = childSnapshot.getValue(MensajeFB::class.java)
                        if (mensaje != null) {
                            mensajes.add(mensaje)
                        }
                    }
                    trySend(mensajes.sortedBy { it.fecha })
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { refBBDD.removeEventListener(listener) }
    }
}