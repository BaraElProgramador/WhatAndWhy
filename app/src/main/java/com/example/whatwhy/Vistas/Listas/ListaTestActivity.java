package com.example.whatwhy.Vistas.Listas;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Proyecto;
import com.example.whatwhy.R;
import com.example.whatwhy.utils.AdapterListaTest;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ListaTestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar carga;
    private Spinner sFiltros;

    private AdapterListaTest adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tests);

        recyclerView = findViewById(R.id.recyclerActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carga = findViewById(R.id.pbListaPro);
        sFiltros = findViewById(R.id.sFiltros);

        String[] datos = new String[] {"Todos", "Ciencia", "Geografia", "Informática", "Naturaleza", "Literatura"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datos);
        sFiltros.setAdapter(adapter);

        sFiltros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarListaTest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        db = FirebaseFirestore.getInstance();


    }

    //Método para cargar la lista de test
    private void cargarListaTest() {
        Query query = null;
        if(sFiltros.getSelectedItem().toString().equals("Todos")){
            query = db.collection("proyectos").whereEqualTo("activo", true);
        }else{
            String tema = sFiltros.getSelectedItem().toString();
            query = db.collection("proyectos").whereEqualTo("activo", true).whereEqualTo("tema", tema);
        }


        FirestoreRecyclerOptions<Proyecto> options =
                new FirestoreRecyclerOptions.Builder<Proyecto>()
                        .setQuery(query, new SnapshotParser<Proyecto>() {
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
        adapter = new AdapterListaTest(options);

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarListaTest();
        carga.setVisibility(carga.INVISIBLE);
        recyclerView.setVisibility(RecyclerView.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
