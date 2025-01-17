package com.david.cruddefinitivo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.david.cruddefinitivo.Clase.UserFb
import com.david.cruddefinitivo.Clase.UsuarioFromKey
import com.david.cruddefinitivo.ui.theme.CrudDefinitivoTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.appwrite.Client
import io.appwrite.services.Storage

var refBBDD by mutableStateOf<DatabaseReference>(FirebaseDatabase.getInstance().reference)
//para appwrite
val appwrite_project = "674f4655000119d78457"
val appwrite_bucket = "674f4717002b4ea1e2c2"

val client = Client()
    .setEndpoint("https://cloud.appwrite.io/v1")
    .setProject(appwrite_project)
val storage = Storage(client)//habilitar para subir archivos
var usuario_key by mutableStateOf("")

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.hasExtra("sesion")) {
            usuario_key = intent.getStringExtra("sesion").toString()
        }else{
            usuario_key = ""
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrudDefinitivoTheme {
                Login()
            }
        }
    }
}

@Composable
fun Login() {
    var sesionUser = UsuarioFromKey(usuario_key, refBBDD)

    //var nick by remember { mutableStateOf(sesionUser.nick) }
    var email by remember { mutableStateOf(sesionUser.email) }
    var password by remember { mutableStateOf(sesionUser.pass) }
//    var mailVacio by remember { mutableStateOf(true) }
//    var passVacio by remember { mutableStateOf(true) }
    var loginExiste by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if(usuario_key!="") {
        email = sesionUser.email
        password = sesionUser.pass
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp)
            .background(colorResource(R.color.rojo_primario))

    ) {
        val (col1, col2)=createRefs()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(col1) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(col2.top)
                }
                .fillMaxHeight(0.5f)
        ){}
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 35.dp)
                //.padding(16.dp)
                .constrainAs(col2) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(col1.bottom)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxHeight(1f),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .background(colorResource(id = R.color.transparente))
                    .fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                maxLines = 1,
                label = { Text(
                    color= colorResource(R.color.white),
                    text="Correo electrónico") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.white),
                    unfocusedBorderColor = colorResource(R.color.white),
                    cursorColor = colorResource(R.color.white),
                    focusedContainerColor= colorResource(R.color.rojo_muy_claro),
                    focusedTextColor= colorResource(R.color.white),
                    unfocusedTextColor= colorResource(R.color.white),
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                modifier = Modifier
                    .background(colorResource(id = R.color.transparente))
                    .fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                maxLines = 1,
                label = { Text(
                    color= colorResource(R.color.white),
                    text="Contraseña")},
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.white),
                    unfocusedBorderColor = colorResource(R.color.white),
                    cursorColor = colorResource(R.color.white),
                    focusedContainerColor= colorResource(R.color.rojo_muy_claro),
                    focusedTextColor= colorResource(R.color.white),
                    unfocusedTextColor= colorResource(R.color.white),
                ),

                )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.rojo_muy_claro),
                    contentColor = colorResource(R.color.white)
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    refBBDD = FirebaseDatabase.getInstance().reference
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        refBBDD.child("usuarios")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    loginExiste = false
                                    for (pojo in snapshot.children) {
                                        val checkUser = pojo.getValue(UserFb::class.java)
                                        if (email == checkUser?.email && password == checkUser?.pass) {
                                            loginExiste = true
                                            usuario_key=checkUser.key.toString()
                                            sesionUser.email=email
                                            sesionUser.pass=password
                                            //Toast.makeText(context, "Login correcto", Toast.LENGTH_SHORT).show()

                                            val intent = Intent(context, MenuActivity::class.java)
                                            intent.putExtra("sesion", checkUser.key)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)

                                            break
                                        }
                                    }
                                    if (!loginExiste) {
                                        Toast.makeText(context, "Usuario o Contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        Toast.makeText(context, "Login correcto", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Error en la conexión", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                    }
                }) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text="¿No tienes cuenta? ¡Regístrate!",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.rojo_muy_claro),
                    contentColor = colorResource(R.color.white)
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val intent = Intent(context, CreaUsuarioActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("Crear cuenta")
            }
        }
    }
}