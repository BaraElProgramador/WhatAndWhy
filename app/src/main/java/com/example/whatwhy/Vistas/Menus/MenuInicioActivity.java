package com.example.whatwhy.Vistas.Menus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whatwhy.R;
import com.example.whatwhy.Vistas.GestionUsuarios.InicioSesionActivity;
import com.example.whatwhy.Vistas.GestionUsuarios.NuevoUsuarioActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuInicioActivity extends AppCompatActivity {
    //Creación de variables
    Button bInic;
    Button bRegistrar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bInic = (Button) findViewById(R.id.button);
        bRegistrar = (Button) findViewById(R.id.bToCrearUser);

        auth = FirebaseAuth.getInstance();

//        if(isLogin()){
//            Intent i = new Intent(MainActivity.this, MenuPrinc.class);
//            startActivity(i);
//        }else{
//            Intent intent = new Intent(this, InicSession.class);
//            startActivity(intent);
//        }

        bInic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuInicioActivity.this, InicioSesionActivity.class);
                startActivity(i);
            }
        });

        bRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuInicioActivity.this, NuevoUsuarioActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();

        //Si el usuario no es nulo, se muestra el menú principal
        if(user != null){
            startActivity(new Intent(MenuInicioActivity.this, MenuPrincipalActivity.class));
            finish();
        }
    }
}