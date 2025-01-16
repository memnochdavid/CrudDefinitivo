package com.david.cruddefinitivo


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.david.cruddefinitivo.Clase.UserFb
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var identificador: String

class CreaUsuarioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FormNewUser()
        }
    }
}


@Composable
fun FormNewUser() {
    val scopeUser = rememberCoroutineScope()
    val storage = Storage(client)
    lateinit var newUser : UserFb
    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.fuego)),
    ) {
        var objetoCreado by remember { mutableStateOf(false) }
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var newUserAvatar by remember { mutableStateOf("") }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                selectedImageUri = uri
            }
        )

        val (col1)=createRefs()
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.padding(16.dp)
                .padding(vertical = 16.dp, horizontal = 35.dp)
                .constrainAs(col1){
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(col1.bottom)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxHeight(1f),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            OutlinedTextField(
                modifier = Modifier
                    .background(colorResource(id = R.color.transparente))
                    .fillMaxWidth(),
                value = username,
                onValueChange = { username = it },
                label = { Text(
                    color= colorResource(R.color.white),
                    text="nick")},
                maxLines = 1,
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
                value = email,
                onValueChange = { email = it },
                maxLines = 1,
                label = { Text(
                    color= colorResource(R.color.white),
                    text="email")},
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
                value = password,
                modifier = Modifier
                    .background(colorResource(id = R.color.transparente))
                    .fillMaxWidth(),
                onValueChange = { password = it },
                maxLines = 1,
                label = { Text(
                    color= colorResource(R.color.white),
                    text="contraseña")},
                visualTransformation = PasswordVisualTransformation(),
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


            Button(modifier = Modifier
                .padding(vertical = 8.dp),
                onClick = { launcher.launch("image/*") },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.rojo_muy_claro),
                    contentColor = colorResource(R.color.white)
                ),
                shape = RoundedCornerShape(10.dp),

                ) {
                Text("Seleccionar foto de perfil")
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier.size(100.dp)
                )
            }
            Button(modifier = Modifier
                .padding(vertical = 8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.rojo_muy_claro),
                    contentColor = colorResource(R.color.white)
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    if (username.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty() || selectedImageUri != null) {
                        refBBDD = FirebaseDatabase.getInstance().reference
                        identificador = refBBDD.child("usuarios").push().key!!
                        val identificadorAppWrite = identificador.substring(1, 20) ?: "" // coge el identificador
                        val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                        if (inputStream != null) {

                            scopeUser.launch {
                                try{
                                    val file = inputStream.use { input ->
                                        val tempFile = kotlin.io.path.createTempFile().toFile()
                                        tempFile.outputStream().use { output ->
                                            input.copyTo(output)
                                        }
                                        InputFile.fromFile(tempFile) // Use fromFile method
                                    }
                                    withContext(Dispatchers.IO) {
                                        storage.createFile(
                                            bucketId = appwrite_bucket,
                                            fileId = identificadorAppWrite,
                                            file = file
                                        )
                                    }
                                    newUserAvatar = "https://cloud.appwrite.io/v1/storage/buckets/$appwrite_bucket/files/$identificadorAppWrite/preview?project=$appwrite_project"
                                    newUser = UserFb(username,email,password,newUserAvatar,identificador)
                                    refBBDD.child("usuarios").child(identificador).setValue(newUser)

                                }catch (e: Exception){
                                    Log.e("UploadError", "Failed to upload image: ${e.message}")
                                }
                                finally {
                                    Toast.makeText(context, "Usuario $username creado con éxito", Toast.LENGTH_SHORT).show()
                                }
                                withContext(Dispatchers.Main) { // Update on main thread
                                    objetoCreado = true
                                }
                            }
                        }
                    }else{
                        Toast.makeText(context, "Rellena todos los campos y elige una imagen", Toast.LENGTH_SHORT).show()
                    }
                }) {
                Text("Crear usuario")
            }

            if (objetoCreado) {

                val intent = Intent(context, LoginActivity::class.java)
                intent.putExtra("sesion", newUser.key)
                context.startActivity(intent)
            }
        }
    }
}