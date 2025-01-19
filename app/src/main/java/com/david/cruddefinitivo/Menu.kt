package com.david.cruddefinitivo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.getDrawable
import com.david.cruddefinitivo.Clase.UserFb
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import kotlinx.coroutines.delay
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.PI
import kotlin.math.sin
import kotlin.ranges.coerceIn

@SuppressLint("CustomSplashScreen")
class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            val usuario = UsuarioFromKey(usuario_key, refBBDD)
            LaunchedEffect(key1 = usuario.key) {
                if (usuario.key != null) {
                    equipo_lista.value = usuario.equipo
                    cargaRegistrados()
                }
            }
            CrudDefinitivoTheme(){
                Splash()
            }
        }
    }
}

@Composable
fun Splash() {
    val context = LocalContext.current
    ConstraintLayout(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.black)),
    ) {
        val (logo, botones, boton_registra,boton_equipo, boton_foro) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.pokemonlogo),
            contentDescription = "Pokemon",
            modifier = androidx.compose.ui.Modifier
                .padding(start = 15.dp,end = 15.dp)
                .fillMaxWidth()
                .constrainAs(logo) {
                    top.linkTo(parent.top)
                    bottom.linkTo(botones.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        )
        Column(
            modifier = androidx.compose.ui.Modifier
                .constrainAs(botones) {
                    top.linkTo(logo.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ){
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val intent = Intent(context, RegistraActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("PokeRegistro")
            }
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val intent = Intent(context, EquipoActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("PokeEquipo")
            }
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val intent = Intent(context, ForoActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("PokeForo")
            }
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val intent = Intent(context, ListaRegistradosActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("Registrados")
            }
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val intent = Intent(context, UsuariosActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("Retar a Usuario")
            }
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val intent = Intent(context, PalmaresActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("Palmarés")
            }

        }
    }
}
