package com.example.whatwhy.Vistas.GestionUsuarios;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatwhy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CambiarContrasenaActivity extends AppCompatActivity {
    //Creación de variables
    private TextView txtEmail;
    private Button bEnviar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);

        //Inicialización de variables
        txtEmail = (TextView) findViewById(R.id.txtEditPass);
        bEnviar = (Button) findViewById(R.id.bChangePass);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Listener del botón
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si no esta vacio llama al metodo de cambiar la contraseña
                if(!txtEmail.getText().toString().trim().equals("")){
                    cambiarContrasena();
                }else{
                    Toast.makeText(CambiarContrasenaActivity.this, "Debe escribir un correo electrónico válido", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void cambiarContrasena(){
        //Se inicia la base de datos para comprobar que exista el email descrito
        db.collection("usuarios").whereEqualTo("email", txtEmail.getText()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot userSnapshots) {
                                //Si el resultado de la busqueda es mayor a 0 entonces se envía el correo
                                if(userSnapshots.size() > 0){
                                    auth.sendPasswordResetEmail(txtEmail.getText().toString().trim())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(CambiarContrasenaActivity.this, "Se ha enviado un correo de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CambiarContrasenaActivity.this, "Error al enviar el correo de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else{
                                    Toast.makeText(CambiarContrasenaActivity.this, "El email no existe", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }
}
