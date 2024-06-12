package com.example.whatwhy.Vistas.Administracion;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Proyecto;
import com.example.whatwhy.R;
import com.example.whatwhy.utils.AdapterReports;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GestionReportsActivity extends AppCompatActivity {
    //Creación de variables
    private RecyclerView recyclerView;

    private AdapterReports adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_report);

        //Obtenemos el RecyclerView y le asignamos el LayoutManager
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewReport);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Inicializamos la base de datos de Firestore
        db = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Obtenemos los datos de la colección "reportes" donde el estado es "pendiente"
        db.collection("reportes").whereEqualTo("estado", "pendiente").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Creamos la lista de ids de los proyectos
                        List<String> ids = new ArrayList<>();

                        //Recorremos los documentos obtenidos
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String idProyecto = document.getString("projectID");
                            //Añadimos el id del proyecto a la lista si no es nulo
                            if (idProyecto != null) {
                                ids.add(idProyecto);
                            }
                        }

                        //Verificamos si la lista de ids está vacía
                        if (ids.size() <= 0) {
                            Toast.makeText(GestionReportsActivity.this, "No hay proyectos pendientes.", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        //Creamos la query para obtener los datos de los proyectos con los ids obtenidos
                        Query query = db.collection("proyectos").whereIn(FieldPath.documentId(), ids);

                        //Obtenemos los datos de la base de datos y configuramos las opciones del FirestoreRecyclerAdapter
                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                FirestoreRecyclerOptions<Proyecto> options =
                                        new FirestoreRecyclerOptions.Builder<Proyecto>()
                                                .setQuery(query, new SnapshotParser<Proyecto>() {
                                                    @NonNull
                                                    @Override
                                                    public Proyecto parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                                        //Obtenemos el proyecto y le asignamos el id del documento
                                                        Proyecto proyecto = snapshot.toObject(Proyecto.class);
                                                        if (proyecto != null) {
                                                            proyecto.setId(snapshot.getId());
                                                        }
                                                        return proyecto;
                                                    }
                                                })
                                                .build();

                                //Asignamos el adapter al RecyclerView
                                adapter = new AdapterReports(options);
                                recyclerView.setAdapter(adapter);
                                //Iniciamos el listener para que el adapter comience a escuchar los cambios en los datos
                                adapter.startListening();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Manejamos el error en caso de fallo al obtener los datos
                        Toast.makeText(GestionReportsActivity.this, "Error al iniciar la lista", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
