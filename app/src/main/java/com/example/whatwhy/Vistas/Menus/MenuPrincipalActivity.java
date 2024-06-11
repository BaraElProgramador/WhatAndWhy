package com.example.whatwhy.Vistas.Menus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatwhy.R;
import com.example.whatwhy.Vistas.Administracion.GestionReportsActivity;
import com.example.whatwhy.Vistas.Administracion.GestionUsersActivity;
import com.example.whatwhy.Vistas.Listas.ListaFavoritosActivity;
import com.example.whatwhy.Vistas.Listas.ListaMensajesActivity;
import com.example.whatwhy.Vistas.Listas.ListaMisTestActivity;
import com.example.whatwhy.Vistas.Listas.ListaTestActivity;
import com.example.whatwhy.Vistas.NuevoTestActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MenuPrincipalActivity extends AppCompatActivity {
    //Creación de variables
    private Button bMisTest;
    private Button bToFavoritos;
    private Button bNewTest;
    private Button bGlobalTest;
    private Button bListaUsuarios;
    private Button bListaResports;
    private ImageView imgMenuUser;
    private TextView notifyNumber;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        //Inicialización de variables
        bMisTest = (Button) findViewById(R.id.bMisTest);
        bToFavoritos = (Button) findViewById(R.id.bToFavoritos);
        bNewTest = (Button) findViewById(R.id.bNewTest);
        bGlobalTest = (Button) findViewById(R.id.bSearchGametest);
        bListaUsuarios = (Button) findViewById(R.id.bListaUsers);
        bListaResports = (Button) findViewById(R.id.bListaReports);
        imgMenuUser = (ImageView) findViewById(R.id.imgMenuUser);
        notifyNumber = (TextView) findViewById(R.id.notifyNumber);

        //Oculta el número de notificaciones
        notifyNumber.setVisibility(View.GONE);

        //Oculta los botones de usuarios y reportes
        bListaUsuarios.setVisibility(View.GONE);
        bListaResports.setVisibility(View.GONE);

        //Inicialización de variables
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Listener de los botones
        bMisTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abre la lista de test favoritos
                startActivity(new Intent(MenuPrincipalActivity.this, ListaMisTestActivity.class));
            }
        });

        bNewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abre el formulario para crear un nuevo test
                Intent i = new Intent(MenuPrincipalActivity.this, NuevoTestActivity.class);
                startActivity(i);
            }
        });

        bToFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abre la lista de test favoritos
                startActivity(new Intent(MenuPrincipalActivity.this, ListaFavoritosActivity.class));
            }
        });

        bGlobalTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abre la lista de test favoritos
                startActivity(new Intent(MenuPrincipalActivity.this, ListaTestActivity.class));
            }
        });

        imgMenuUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Muestra el menú de usuario
                PopupMenu popupMenu = new PopupMenu(MenuPrincipalActivity.this, imgMenuUser);
                popupMenu.getMenuInflater().inflate(R.menu.menu_user, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Ve todos los mensajes que tiene el usuario
                        if (item.getItemId() == R.id.itemVerMensaje) {
                            //Abre la lista de mensajes y marca todos los mensajes como leídos
                            startActivity(new Intent(MenuPrincipalActivity.this, ListaMensajesActivity.class));
                            db.collection("mensajes").whereEqualTo("userID", auth.getCurrentUser().getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                documentSnapshot.getReference().update("pendiente", false);
                                            }
                                        }
                                    });
                            return true;
                        }
                        //Cambia la contraseña del usuario
                        if(item.getItemId() == R.id.itemChangePass){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipalActivity.this);
                            builder.setTitle("Atención")
                                    .setMessage("¿Desea recibir un correo para cambiar la contraseña?")
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Envia un correo para cambiar la contraseña
                                            db.collection("usuarios").document(auth.getUid()).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            auth.sendPasswordResetEmail(documentSnapshot.getString("email"))
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Toast.makeText(MenuPrincipalActivity.this, "Se ha enviado un correo de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(MenuPrincipalActivity.this, "Error al enviar el correo de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.show();
                            return true;
                        }
                        //Cierra la sesión del usuario y vuelve al menu inicial
                        if (item.getItemId() == R.id.itemCerrarSesion) {
                            auth.signOut();
                            Toast.makeText(MenuPrincipalActivity.this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MenuPrincipalActivity.this, MenuInicioActivity.class));
                            finish();
                            return true;
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        //Carga los datos del usuario
        db.collection("usuarios").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //Verifica si el usuario es administrador
                        if (documentSnapshot.getLong("rol").intValue() >= 1) {
                            bListaUsuarios.setVisibility(View.VISIBLE);
                            bListaResports.setVisibility(View.VISIBLE);


                            bListaUsuarios.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(MenuPrincipalActivity.this, GestionUsersActivity.class));
                                }
                            });

                            bListaResports.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(MenuPrincipalActivity.this, GestionReportsActivity.class));
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuPrincipalActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        String userID = auth.getCurrentUser().getUid();

        //Verifica si el usuario esta baneado
        db.collection("usuarios").document(userID).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                //Si el usuario esta baneado, cierra la sesión y muestra un mensaje
                                if(documentSnapshot.getBoolean("baneado")){
                                    Toast.makeText(MenuPrincipalActivity.this, "Su cuenta ha sido baneada", Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                    startActivity(new Intent(MenuPrincipalActivity.this, MenuInicioActivity.class));
                                    finish();
                                }
                            }
                        });

        //Verifica si el usuario tiene notificaciones
        db.collection("mensajes")
                .whereEqualTo("userID", userID).whereEqualTo("pendiente", true)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int size = queryDocumentSnapshots.size();
                        if (size > 0) {
                            notifyNumber.setText(String.valueOf(size));
                            notifyNumber.setVisibility(View.VISIBLE);
                        }else{
                            notifyNumber.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}