package com.david.cruddefinitivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.david.cruddefinitivo.Clase.ForoFB
import com.david.cruddefinitivo.Clase.Mensaje
import com.david.cruddefinitivo.Clase.MensajeFB
import com.david.cruddefinitivo.Clase.cargaForo
import com.david.cruddefinitivo.Clase.observeForo
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import kotlinx.coroutines.launch
import java.util.Date

class ForoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                Foro()
            }
        }
    }
}

@Composable
fun Foro(){
    val lifecycleScope = rememberCoroutineScope()
    var conversacion by remember { mutableStateOf<ForoFB?>(null) }
    var mensajes = remember { mutableStateListOf<MensajeFB>() }
    val foroid = conversacion?.idForo ?: "" // Get chatId from conversation or use empty string
    val chatState = remember { mutableStateListOf<MensajeFB>() }


    LaunchedEffect(Unit) {
        lifecycleScope.launch {
            val loadedConversacion = cargaForo()
            conversacion = loadedConversacion
            if (loadedConversacion != null) {
                val sortedMessages = loadedConversacion.mensajes.sortedBy { it.fecha }
                //Log.d("ChatScreen", "Initial ${sortedMessages.size} messages")
                //sortedMessages.forEach { Log.d("ChatScreen", "Initial Message: ${it.texto} from ${it.idUser}") }
                chatState.clear()
                chatState.addAll(sortedMessages)
            }
        }
    }

    LaunchedEffect(key1 = foroid) {
        if (foroid.isNotEmpty()) {
            observeForo(foroid).collect { newMessages ->
                //Log.d("ChatScreen", "Received ${newMessages.size} messages")
                //newMessages.forEach { Log.d("ChatScreen", "Message: ${it.texto} from ${it.idUser}") }
                chatState.clear()
                chatState.addAll(newMessages)
            }
        }
    }

    val context = LocalContext.current
    var listState = rememberLazyListState()

    LaunchedEffect(chatState.size) {
        if (chatState.isNotEmpty()) {
            listState.animateScrollToItem(chatState.lastIndex)
        }
    }



    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.rojo_claro))
    ) {
        val (fila_avatar,chat, escribe) = createRefs()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.08f)
                //.wrapContentHeight()
                .constrainAs(fila_avatar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    //bottom
                },
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                modifier = Modifier
                    .weight(0.2f),
                //.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.Center
            ){
                Button(
                    modifier = Modifier
                        .background(colorResource(R.color.transparente))
                        //.fillMaxHeight(),
                        .wrapContentSize(),
                    onClick = {
                        if (context is ComponentActivity) {
                            context.finish()
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.transparente),
                        contentColor = colorResource(R.color.white)
                    ),
                )
                {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Cancelar"
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Foro de Entrenadores",
                    color = colorResource(R.color.white),
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                    //.padding(vertical = 20.dp)
                )
            }
        }
        Row(//lazyrow cuando apañe FB
            modifier = Modifier
                .fillMaxHeight(0.82f)
                .fillMaxWidth()
                .constrainAs(chat) {
                    top.linkTo(fila_avatar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(escribe.top)
                }
                .background(colorResource(R.color.rojo_muy_claro)),
            verticalAlignment = Alignment.Top
        ){
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                //reverseLayout = true
            ) {
                items(chatState, key = { it }) { mensaje ->
                    //Log.d("mensaje",mensaje.texto)
                    val color = if (mensaje.idUser == usuario_key) {
                        colorResource(id=R.color.mensajeout)
                    } else {
                        colorResource(id=R.color.mensajein)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // Fill the width for better alignment
                            .wrapContentHeight()
                            .background(colorResource(R.color.transparente))
                            .padding(15.dp),
                        horizontalArrangement = if (mensaje.idUser == usuario_key) {
                            Arrangement.End // el que escribe a la derecha
                        } else {
                            Arrangement.Start // el que recibe a la izquierda
                        },
                    ) {
                        Mensaje(opc=2, mensaje = mensaje, color = color)
                    }

                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .constrainAs(escribe) {
                    top.linkTo(chat.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .background(colorResource(R.color.transparente))
        ){
            var scrollMensajeInput= rememberScrollState()
            var text by remember { mutableStateOf("escribe tu mensaje") }
            var isFocused by remember { mutableStateOf(false) }
            var mensajeAux= MensajeFB(usuario_key,"",0L, text)
            BasicTextField(
                value = text,
                modifier = Modifier
                    .background(colorResource(id = R.color.transparente))
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .scrollable(
                        state = scrollMensajeInput,
                        enabled = true,
                        orientation = androidx.compose.foundation.gestures.Orientation.Vertical
                    )
                    .padding(horizontal = 20.dp)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        if (isFocused && text == "escribe tu mensaje") {
                            text = ""
                        }
                    },
                onValueChange = { newText ->
                    text = newText
                },
                minLines = 1,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        innerTextField()
                        IconButton(
                            onClick = {
                                mensajeAux.texto=text
                                mensajeAux.idUser= usuario_key
                                mensajeAux.idReceptor=foroid
                                mensajeAux.fecha= Date().time
                                mensajes+=mensajeAux
                                if (conversacion == null) {

                                    conversacion = ForoFB(foroid, mutableListOf())
                                    refBBDD.child("foro").child(foroid).child("mensajes").push().setValue(mensajeAux)
                                } else {
                                    conversacion!!.mensajes.add(mensajeAux)
                                    refBBDD.child("foro").child(foroid).child("mensajes").push().setValue(mensajeAux)
                                }
                                text = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = colorResource(id = R.color.white)
                            )
                        }
                    }
                },
                textStyle = TextStyle(color = colorResource(id = R.color.white)),
                cursorBrush= SolidColor(colorResource(id = R.color.white)),
            )


        }

    }
}

