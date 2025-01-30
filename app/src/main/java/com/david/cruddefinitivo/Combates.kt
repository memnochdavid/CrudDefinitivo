package com.david.cruddefinitivo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.david.cruddefinitivo.Clase.UserFb
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.david.cruddefinitivo.Clase.Combate
import com.david.cruddefinitivo.Clase.PokeCard
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.Clase.fetchAllUsers
import com.david.cruddefinitivo.ui.theme.Purple40
import com.david.cruddefinitivo.ui.theme.color_planta
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

var id_receptor by mutableStateOf("")

class UsuariosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                MuestraUsuarios(
                    modifier = Modifier
                        //.background(colorResource(R.color.lista_con_foco))
                        .fillMaxSize()
                        .background(Purple40)
                        .padding(vertical = 50.dp)
                ) { selectedUser ->
                    id_receptor = selectedUser.key.toString()
                }
            }
        }
    }
}
class CombatesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                RegistraCombate()
            }
        }
    }
}
class PalmaresActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                MuestraPalmares()
            }
        }
    }
}



//LISTA USUARIOS
@Composable
fun MuestraUsuarios(
    modifier: Modifier = Modifier,
    onUserSelected: (UserFb) -> Unit
) {
    var usuarios by remember { mutableStateOf<List<UserFb>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = refBBDD) {
        scope.launch {
            usuarios = fetchAllUsers(refBBDD)
        }
    }
    Box(
        modifier = modifier
    ){
        ConstraintLayout(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            val (listaUsuarios, boton1, boton2, titulo) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxHeight(0.1f)
                    .constrainAs(titulo) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(listaUsuarios.top)
                    },
                //color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                text = "Retar a:"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .constrainAs(listaUsuarios) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(titulo.bottom)
                        bottom.linkTo(boton1.top)
                    }
            ) {
                items(usuarios) { usuario ->
                    if (usuario.key != usuario_key) {
                        UserCard(usuario, 1,onUserClick = { selectedUser ->
                            onUserSelected(selectedUser)
                        })
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.1f)
                    .constrainAs(boton1) {
                        //top.linkTo(listaUsuarios.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ){
                BotonAtras()
            }
        }
    }
}
@Composable
fun UserCard(usuario: UserFb, opc:Int,onUserClick: (UserFb) -> Unit) {
    //val refBBDD = FirebaseDatabase.getInstance().reference
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Moderate bouncing
            stiffness = Spring.StiffnessMedium // Moderate stiffness
        )
    )
    val context= LocalContext.current
    ConstraintLayout() {
        val(avatar,nick)=createRefs()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .scale(scale.value)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,

                    onClick = {
                        onUserClick(usuario)

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
                                isPressed = false
                            }
                            if (opc == 1) {
                                id_receptor = usuario.key.toString()
                                val intent = Intent(context, CombatesActivity::class.java)
                                context.startActivity(intent)
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
                    model = usuario.avatar,
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxSize()
                        .constrainAs(avatar) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
                Text(
                    text = usuario.nick,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .constrainAs(nick) {
                            start.linkTo(avatar.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
                )
            }
        }

    }

}


//REGISTRO DE COMBATES
@Composable
fun RegistraCombate(){
    val combatiente1 = UsuarioFromKey(usuario_key, refBBDD)
    val combatiente2 = UsuarioFromKey(id_receptor, refBBDD)
    var click_dummy by remember { mutableStateOf("") }
    var ganador by remember { mutableStateOf(UserFb()) }
    val context= LocalContext.current
    val shape = RoundedCornerShape(10.dp)
    val colores_boton = ButtonDefaults.buttonColors(
        containerColor = color_planta,
        contentColor = Color.White
    )

    Column(
        modifier = Modifier
            .background(Purple40)
            .padding(top = 16.dp)
            .fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserCard(combatiente1, 2,onUserClick = { selectedUser ->
            click_dummy = selectedUser.nick
        })
        Image(
            painter = painterResource(id = R.drawable.versus),
            contentDescription = "versus",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(150.dp)
                .align(androidx.compose.ui.Alignment.CenterHorizontally)
        )
        UserCard(combatiente2, 2,onUserClick = { selectedUser ->
            click_dummy = selectedUser.nick
        })
        Text(text = "Vencedor")
        SeleccionaWinner( modifier = Modifier.align(Alignment.CenterHorizontally), lista_usuarios = listOf(combatiente1,combatiente2)) {
            ganador = it
        }
        Button(
            modifier = Modifier
                .padding(vertical = 8.dp),
            shape = shape,
            colors = colores_boton,
            onClick = {
                var combate = Combate(combatiente1.key!!, combatiente2.key!!, ganador.key!!)
                //Log.d("combate", combate.toString())
                try {
                    refBBDD.child("combates").child(combate.id).child(combate.fecha.toString()).setValue(combate)
                }
                catch (e:Exception){
                    Toast.makeText(context, "Error al guardar combate", Toast.LENGTH_SHORT).show()
                }
                finally {
                    Toast.makeText(context, "Combate guardado", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Guardar Combate")
        }
        BotonAtras()


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionaWinner(
    modifier: Modifier = Modifier,
    lista_usuarios: List<UserFb>,
    onUsuarioChange: (UserFb) -> Unit,
) {
    var selectedUsuario by remember { mutableStateOf(lista_usuarios[0]) }
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedUsuario.nick,
            onValueChange = {
                selectedUsuario.nick = it
            },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .wrapContentWidth(),

            )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            lista_usuarios.forEach { usuario ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = usuario.nick,
                            color = colorResource(R.color.black)
                        )
                    },
                    onClick = {
                        selectedUsuario = usuario
                        expanded = false
                        onUsuarioChange(usuario)
                    },
                    modifier = Modifier
                        .padding(vertical = 0.dp)
                )
            }
        }
    }
}

//MUESTRA PALMARÉS
@Composable
fun MuestraPalmares() {
    var combates by remember { mutableStateOf<List<Combate>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val context= LocalContext.current

    LaunchedEffect(combates) {
        scope.launch {
            combates = cargaPalmares(refBBDD, usuario_key)
        }
    }
    Box(
        modifier = Modifier
            .background(Purple40)
            .fillMaxSize()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            val (listaCombates, boton1, titulo) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxHeight(0.1f)
                    .padding(top = 20.dp)
                    .constrainAs(titulo) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(listaCombates.top)
                    },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                text = "Mi Palmarés"
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .constrainAs(listaCombates) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(titulo.bottom)
                        bottom.linkTo(boton1.top)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    items = combates,
                    key = { combate -> combate.fecha }
                ) { combate ->
                    combate.id.let { id ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    scope.launch {
                                        val combateRef = refBBDD.child("combates").child(id).child(combate.fecha.toString())
                                        //se actualiza combates
                                        combates = cargaPalmares(refBBDD, usuario_key)
                                        combateRef.removeValue()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Has borrado el combate", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {}
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
                                        tint = Color.Black
                                    )
                                }
                            }
                        ) {
                            CombateCard(combate)
                        }
                    }
                }
            }
            //////////////////////////
            /*
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    //.fillMaxHeight(0.9f)
                    .constrainAs(listaCombates) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(titulo.bottom)
                        bottom.linkTo(boton1.top)
                    },
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                items(combates) { combate ->
                    CombateCard(combate)
                }
            }
            */
            ///
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.1f)
                    .constrainAs(boton1) {
                        top.linkTo(listaCombates.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ){
                BotonAtras()
            }
        }

    }

}

@Composable
fun CombateCard(combate: Combate){
    val messageDate = Date(combate.fecha)

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val todayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val today = todayFormat.format(Date())
    val messageDay = todayFormat.format(messageDate)

    val fecha_combate = if (today == messageDay) {
        timeFormat.format(messageDate)
    } else {
        fullDateFormat.format(messageDate)
    }

    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Moderate bouncing
            stiffness = Spring.StiffnessMedium // Moderate stiffness
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight()
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




                    }
                )
            }
            .padding(vertical = 16.dp, horizontal = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = UsuarioFromKey(combate.usuario1, refBBDD).nick)
            Text(text = " VS ")
            Text(text = UsuarioFromKey(combate.usuario2, refBBDD).nick)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = fecha_combate,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Vencedor")
            Text(text = UsuarioFromKey(combate.vencedor, refBBDD).nick)
        }
    }

}




suspend fun cargaPalmares(refBBDD: DatabaseReference, idUser: String): List<Combate> {
    return suspendCancellableCoroutine { continuation ->
        refBBDD.child("combates").get().addOnSuccessListener { snapshot ->
            val combates = mutableListOf<Combate>()
            if (snapshot.exists()) {

                snapshot.children.forEach { combateEntry ->

                    combateEntry.children.forEach { battleSnapshot ->
                        val combate = battleSnapshot.getValue(Combate::class.java)
                        if (combate != null) {

                            if (combate.usuario1 == idUser || combate.usuario2 == idUser) {
                                combates.add(combate)
                            }
                        }
                    }
                }
            }
            continuation.resume(combates)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}