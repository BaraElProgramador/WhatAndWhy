package com.example.whatwhy.Vistas.GestionUsuarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatwhy.R;
import com.example.whatwhy.Vistas.Menus.MenuInicioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class NuevoUsuarioActivity extends AppCompatActivity {
    // Creación de variables
    TextView txtEmail, txtNick, txtPass;
    Button bCreate, bCancel;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);

        // Declaración de variables
        txtEmail = findViewById(R.id.txtNewUserEmail);
        txtNick = findViewById(R.id.txtNewUserNick);
        txtPass = findViewById(R.id.txtNewUserPass);
        bCreate = findViewById(R.id.bNewUser);
        bCancel = findViewById(R.id.bCancelNewUser);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Listeners de los botones
        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String nick = txtNick.getText().toString().trim();
                String pass = txtPass.getText().toString().trim();

                // Comprueba que ninguno de los 3 está vacío
                if (!email.isEmpty() && !nick.isEmpty() && !pass.isEmpty()) {
                    // Verifica si el usuario ya existe
                    verificarUsuario(nick, new VerificarUsuarioCallback() {
                        @Override
                        public void onCallback(boolean existeUsuario) {
                            if (!existeUsuario) {
                                if (pass.length() >= 6) {
                                    registrarUsuario(email, nick, pass);
                                } else {
                                    Toast.makeText(NuevoUsuarioActivity.this, "La contraseña debe tener 6 o más caracteres", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(NuevoUsuarioActivity.this, "El email o el nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Indica que una de las celdas está vacía
                    Toast.makeText(NuevoUsuarioActivity.this, "No se ha podido crear el usuario, una de las celdas está vacía.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void verificarUsuario(String nombreUsuario, VerificarUsuarioCallback callback) {
        db.collection("usuarios")
                .whereEqualTo("nombre", nombreUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean existeUsuario = !task.getResult().isEmpty();
                            if (existeUsuario) {
                                // Llama al método de devolución de llamada con el booleano
                                callback.onCallback(existeUsuario);
                            }else{
                                   db.collection("usuarios").whereEqualTo("email", txtEmail.getText().toString()).get()
                                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                   boolean existeUsuario = !task.getResult().isEmpty();
                                                   callback.onCallback(existeUsuario);
                                               }
                                           });
                            }
                        } else {
                            // Si la consulta falla, llama al método de devolución de llamada con false
                            callback.onCallback(false);
                        }
                    }
                });
    }

    private void registrarUsuario(String email, String nick, String pass) {
        db.collection("usuariosBaneados").whereEqualTo("email", email).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.size() <= 0){
                                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Si se ha creado el usuario, registra en la base de datos
                                                String id = auth.getCurrentUser().getUid();
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("email", email);
                                                user.put("nombre", nick);
                                                user.put("rol", 0);
                                                user.put("puntos", 10);
                                                user.put("baneado", false);

                                                db.collection("usuarios").document(id).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        finish();
                                                        startActivity(new Intent(NuevoUsuarioActivity.this, MenuInicioActivity.class));
                                                        Toast.makeText(NuevoUsuarioActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(NuevoUsuarioActivity.this, "No se pudo crear el usuario, inténtelo de nuevo o más tarde", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(NuevoUsuarioActivity.this, "No se pudo crear el usuario, inténtelo de nuevo o más tarde", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(NuevoUsuarioActivity.this, "El usuario esta eliminado, no puede crear una cuenta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NuevoUsuarioActivity.this, "Error al crear una cuenta", Toast.LENGTH_SHORT).show();
                    }
                });
        
    }

    // Interfaz de devolución de llamada para verificar si el usuario existe
    private interface VerificarUsuarioCallback {
        void onCallback(boolean existeUsuario);
    }
}
