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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                Menu()
            }
        }
    }
}
//
@Composable
fun Menu() {
    val context = LocalContext.current
    val shape = RoundedCornerShape(10.dp)
    val modifier = Modifier
        .fillMaxWidth(0.5f)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.black)),
    ) {
        val (logo, botones, texto) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.pokemonlogo),
            contentDescription = "Pokemon",
            modifier = Modifier
                .padding(start = 15.dp,end = 15.dp)
                .fillMaxWidth()
                .constrainAs(logo) {
                    top.linkTo(parent.top)
                    bottom.linkTo(texto.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        )
        Text(
            text = "CRUD definitivo",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(texto) {
                    top.linkTo(logo.bottom)
                    bottom.linkTo(botones.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .constrainAs(botones) {
                    top.linkTo(texto.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ){
            Button(
                shape = shape,
                onClick = {
                    val intent = Intent(context, RegistraActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = modifier
            ){
                Text("PokeRegistro")
            }
            Button(
                shape = shape,
                onClick = {
                    val intent = Intent(context, EquipoActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = modifier
            ) {
                Text("PokeEquipo")
            }
            Button(
                shape = shape,
                onClick = {
                    val intent = Intent(context, ForoActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = modifier
            ) {
                Text("PokeForo")
            }
            Button(
                shape = shape,
                onClick = {
                    val intent = Intent(context, ListaRegistradosActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = modifier
            ) {
                Text("Pokes Registrados")
            }
            Button(
                shape = shape,
                onClick = {
                    val intent = Intent(context, UsuariosActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = modifier
            ) {
                Text("Retar a Usuario")
            }
            Button(
                shape = shape,
                onClick = {
                    val intent = Intent(context, PalmaresActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = modifier
            ) {
                Text("Mi Palmarés")
            }

        }
    }
}
