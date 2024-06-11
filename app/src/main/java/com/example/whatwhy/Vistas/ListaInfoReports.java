package com.example.whatwhy.Vistas;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Reporte;
import com.example.whatwhy.R;
import com.example.whatwhy.utils.AdapterListaInfoReports;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ListaInfoReports extends AppCompatActivity {
    private RecyclerView recyclerView;

    private AdapterListaInfoReports adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_info_reports);

        recyclerView = findViewById(R.id.recyclerInfoReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String idProyecto = getIntent().getStringExtra("proyectoID");

        db.collection("reportes").whereEqualTo("estado", "pendiente").whereEqualTo("projectID", idProyecto).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Query query = queryDocumentSnapshots.getQuery();

                        FirestoreRecyclerOptions<Reporte> options =
                                new FirestoreRecyclerOptions.Builder<Reporte>()
                                        .setQuery(query, new SnapshotParser<Reporte>() {
                                            @NonNull
                                            @Override
                                            public Reporte parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                                Reporte reporte = snapshot.toObject(Reporte.class);
                                                reporte.setMotivo(snapshot.get("tipo").toString());
                                                return reporte;
                                            }
                                        })
                                        .build();

                        adapter = new AdapterListaInfoReports(options);
                        recyclerView.setAdapter(adapter);
                        adapter.startListening();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListaInfoReports.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
