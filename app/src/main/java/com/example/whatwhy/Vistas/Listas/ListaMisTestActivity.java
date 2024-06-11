package com.example.whatwhy.Vistas.Listas;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Proyecto;
import com.example.whatwhy.R;
import com.example.whatwhy.utils.AdapterListaTest;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ListaMisTestActivity extends AppCompatActivity {
    //Creación de variables
    private RecyclerView recyclerView;
    private ProgressBar carga;

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

        db = FirebaseFirestore.getInstance();

        fUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarRecycler();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void cargarRecycler() {
        //Creamos la consulta
        //Esta consulta busca en la tabla "proyectos" los proyectos que pertenecen al usuario actual
        Query query = db.collection("proyectos").whereEqualTo("userID", fUser.getUid());

        //Ejecutamos la consulta
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Comprobamos que no esté vacío
                if(!queryDocumentSnapshots.isEmpty()){
                    //Si no esta vacio creamos el options
                    // que sera el encargado de cargar los datos necesarios al recyclerview
                    FirestoreRecyclerOptions<Proyecto> options =
                            new FirestoreRecyclerOptions.Builder<Proyecto>()
                                    .setQuery(query, new SnapshotParser<Proyecto>() {
                                        @NonNull
                                        @Override
                                        public Proyecto parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                            //Obtenemos el proyecto
                                            Proyecto proyecto = snapshot.toObject(Proyecto.class);
                                            //Si el proyecto no es nulo le asignamos el id del proyecto
                                            // y lo asignamos al modelo ya que no carga el id al no ser un campo
                                            if (proyecto != null) {
                                                proyecto.setId(snapshot.getId());
                                            }
                                            return proyecto;
                                        }
                                    })
                                    .build();

                    //Asignamos el adapter
                    adapter = new AdapterListaTest(options);
                    //Notificamos al adapter que los datos han cambiado
                    adapter.notifyDataSetChanged();
                    //Asignamos el adapter al recyclerview
                    recyclerView.setAdapter(adapter);
                    //Iniciamos el listener
                    adapter.startListening();
                    //Mostramos el resultado del recyclerview
                    carga.setVisibility(carga.INVISIBLE);
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                }else{
                    finish();
                }
            }
        });



    }

}
