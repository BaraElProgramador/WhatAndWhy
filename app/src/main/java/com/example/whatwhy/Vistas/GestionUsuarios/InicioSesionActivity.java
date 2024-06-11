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
import com.example.whatwhy.Vistas.Menus.MenuPrincipalActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class InicioSesionActivity extends AppCompatActivity {
    //Creación de variables
    private TextView txtUser;
    private TextView txtPass;
    private TextView txtOlvidadoPass;

    private Button bInicio;
    private Button bCancelar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        //Inicialización de variables
        txtUser = (TextView) findViewById(R.id.txtIniUser);
        txtPass = (TextView) findViewById(R.id.txtIniPass);
        txtOlvidadoPass = (TextView) findViewById(R.id.txtOlvidadoPass);

        bInicio = (Button) findViewById(R.id.bIni);
        bCancelar = (Button) findViewById(R.id.bCancelar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtUser.getText().toString().trim().equals("") || !txtPass.getText().toString().trim().equals("")) {
                    //Comprobar si el usuario y contraseña es correcto
                    loginUser(txtUser.getText().toString().trim(), txtPass.getText().toString().trim());
                }else{
                    Toast.makeText(InicioSesionActivity.this, "Debes escribir el usuario y la contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtOlvidadoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InicioSesionActivity.this, CambiarContrasenaActivity.class));
            }
        });
    }

    //Función para iniciar sesión
    private void loginUser (String email, String password){
        db.collection("usuarios").whereEqualTo("email", email).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.size() > 0){
                                    boolean baneado = queryDocumentSnapshots.getDocuments().get(0).get("baneado", boolean.class);
                                    if(!baneado){
                                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                //Comprueba si se ha iniciado sesión correctamente
                                                if (task.isSuccessful()) {
                                                    finish();
                                                    startActivity(new Intent(InicioSesionActivity.this, MenuPrincipalActivity.class));
                                                }//Si no se ha iniciado sesión correctamente
                                                else{
                                                    //Mensaje de error al usuario
                                                    Toast.makeText(InicioSesionActivity.this, "No se ha podido iniciar sesión", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(InicioSesionActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(InicioSesionActivity.this, "El usuario esta baneado", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(InicioSesionActivity.this, "El email no existe o no se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                                }
                                
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InicioSesionActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}