package com.david.cruddefinitivo.Clase

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.david.cruddefinitivo.R
import com.david.cruddefinitivo.refBBDD
import com.david.cruddefinitivo.usuario_key
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.Dispatchers
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class UserFb(
    var nick: String="",
    var email: String="",
    var pass: String="",
    var avatar: String? = null,
    var key: String? = null,
    var equipo: MutableList<PokemonFB> =mutableListOf<PokemonFB>(),
    var admin: Boolean=false,
    var denunciado: Boolean=false,
    var detallesDenuncia: String? = null,
): Serializable {//), Parcelable{

    override fun toString(): String {
        return "UserFb(nick='$nick'\n, email='$email'\n, pass='$pass'\n, avatar='$avatar'\n,equipo='$equipo'\n, admin=$admin\n, denunciado=$denunciado\n, detallesDenuncia='$detallesDenuncia'\n)"
    }
}

@Composable
fun UserButton(
    modifier: Modifier = Modifier,
    opc:Int,
    navController: NavHostController = NavHostController(LocalContext.current),
    sesion_id:String = usuario_key,
){

    val context = LocalContext.current
    val sesion= UsuarioFromKey(sesion_id, refBBDD)
    val placeholder = R.drawable.pokeball
    val imageUrl = sesion.avatar

    var avatarBig by remember { mutableStateOf(false) }

    val sizeAvatar by animateFloatAsState(
        targetValue = if (!avatarBig) 100f else 300f,
        animationSpec = tween(durationMillis = 300) // duración
    )
    /*
        val listener = object : ImageRequest.Listener {
            override fun onError(request: ImageRequest, result: ErrorResult) {
                super.onError(request, result)
            }

            override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                super.onSuccess(request, result)
            }
        }*/
    val imageRequest = ImageRequest.Builder(context)
        .data(imageUrl)
        //.listener(listener)//comentar esto evita que parpadee el avatar, pero requiere reiniciar la app para cambiar la imagen
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(imageUrl)
        .diskCacheKey(imageUrl)
        .placeholder(placeholder)
        .error(placeholder)
        .fallback(placeholder)
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .build()


    IconButton(
        onClick = {
            when(opc){
                1->{
                    //ver el avatar en grande TODO()
                    avatarBig=!avatarBig

                }
                2->{
                    /*
                    mostrar=""
                    navController.popBackStack()
                    */
                }
            }
        },
        modifier = Modifier
            .size(sizeAvatar.dp)
            //.padding(10.dp)
            .background(colorResource(R.color.transparente))
    ) {

        Surface(modifier = modifier) {
            // Load and display the image with AsyncImage
            AsyncImage(
                model = imageRequest,
                contentDescription = "Image Description",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(colorResource(R.color.transparente))
            )
            if(avatarBig){
                IconButton(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .wrapContentHeight()
                        .wrapContentWidth(),
                    onClick = {
                        //click
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar"
                    )
                }
            }
        }
    }
}




@Composable
fun UsuarioFromKey(usuario_key: String, refBBDD: DatabaseReference): UserFb {
    val usuarioState = remember { mutableStateOf<UserFb?>(null) }

    LaunchedEffect(usuario_key) {
        usuarioState.value = fetchUserData(usuario_key, refBBDD)
    }

    return usuarioState.value ?: UserFb() // Return default UserFb if null
}
suspend fun fetchUserData(usuario_key: String, refBBDD: DatabaseReference): UserFb {
    return suspendCoroutine { continuation ->
        refBBDD.child("usuarios").child(usuario_key).get().addOnSuccessListener { snapshot ->
            val equipo = snapshot.child("equipo").getValue(object : GenericTypeIndicator<MutableList<PokemonFB>>() {})
                ?: mutableListOf()
            val user = UserFb(
                nick = snapshot.child("nick").value.toString(),
                email = snapshot.child("email").value.toString(),
                pass = snapshot.child("pass").value.toString(),
                avatar = snapshot.child("avatar").value.toString(),
                key = snapshot.child("key").value.toString(),
                equipo = equipo
            )
            continuation.resume(user)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}

