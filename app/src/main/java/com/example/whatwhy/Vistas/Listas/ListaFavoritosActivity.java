package com.example.whatwhy.Vistas.Listas;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Proyecto;
import com.example.whatwhy.R;
import com.example.whatwhy.utils.AdapterListaTest;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

//Clase dedicada a mostrar los GameTest favoritos del usuario
public class ListaFavoritosActivity extends AppCompatActivity {
    //Creación de variables
    private RecyclerView recyclerView;
    private ProgressBar carga;
    private Spinner sFiltros;

    private ArrayList <String> proyectosFavoritos;
    private Query proyectosQuery;

    private AdapterListaTest adapter;
    private FirebaseFirestore db;
    private FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tests);

        recyclerView = findViewById(R.id.recyclerActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carga = findViewById(R.id.pbListaPro);
        sFiltros = findViewById(R.id.sFiltros);

        //Al tener problemas con referencias esta variable se coloca aqui para evitarlos
        Query proyectosQuery = null;

        String[] datos = new String[] {"Todos", "Ciencia", "Geografia", "Informática", "Naturaleza", "Literatura"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datos);
        sFiltros.setAdapter(adapter);

        sFiltros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                obtenerListaProyectosFavoritos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        proyectosFavoritos = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Obtiene la lista de proyectos favoritos del usuario cada vez que se inicie la actividad
        obtenerListaProyectosFavoritos();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void obtenerListaProyectosFavoritos() {
        //Obtengo los proyectos favoritos del usuario y lo cargo en el Recycler
        db.collection("favoritos").whereEqualTo("userId", fUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                proyectosFavoritos.add(document.getString("projectId"));
                            }

                            if(sFiltros.getSelectedItem().toString().equals("Todos")){
                                proyectosQuery = db.collection("proyectos").whereEqualTo("activo", true);
                            }else{
                                String tema = sFiltros.getSelectedItem().toString();
                                proyectosQuery = db.collection("proyectos").whereEqualTo("activo", true).whereEqualTo("tema", tema);
                            }

                            proyectosQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                                    // Configurar opciones para el adaptador FirestoreRecycler
                                    FirestoreRecyclerOptions<Proyecto> options = new FirestoreRecyclerOptions.Builder<Proyecto>()
                                            .setQuery(proyectosQuery, new SnapshotParser<Proyecto>() {
                                                @NonNull
                                                @Override
                                                public Proyecto parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                                    Proyecto proyecto = snapshot.toObject(Proyecto.class);
                                                    if (proyecto != null) {
                                                        proyecto.setId(snapshot.getId()); // Establecer el ID del documento en el modelo
                                                    }
                                                    return proyecto;
                                                }
                                            })
                                            .build();

                                    // Crear el adaptador
                                    adapter = new AdapterListaTest(options);

                                    // Configurar el RecyclerView con el adaptador
                                    recyclerView.setAdapter(adapter);

                                    // Iniciar la escucha del adaptador
                                    adapter.startListening();

                                    // Hacer visible el RecyclerView y ocultar el indicador de carga
                                    carga.setVisibility(View.INVISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Manejar errores al obtener los proyectos favoritos
                                    Toast.makeText(ListaFavoritosActivity.this, "Error al obtener proyectos favoritos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Manejar el caso en que no hay favoritos
                            Toast.makeText(ListaFavoritosActivity.this, "No hay favoritos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores al obtener los documentos de favoritos
                        Toast.makeText(ListaFavoritosActivity.this, "Error al obtener favoritos", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
