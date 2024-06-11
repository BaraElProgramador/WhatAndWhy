package com.example.whatwhy.Vistas.Listas;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Mensaje;
import com.example.whatwhy.R;
import com.example.whatwhy.utils.AdapterMensajes;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ListaMensajesActivity extends AppCompatActivity {
    //Creación de variables
    private RecyclerView recyclerView;
    private ProgressBar carga;

    private AdapterMensajes adapter;
    private FirebaseFirestore db;
    private FirebaseAuth fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tests);

        recyclerView = findViewById(R.id.recyclerActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carga = findViewById(R.id.pbListaPro);

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = db.collection("mensajes").whereEqualTo("userID", fUser.getUid());

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Comprobamos que no esté vacío
                if(!queryDocumentSnapshots.isEmpty()){
                    //Si no esta vacio creamos el options
                    // que sera el encargado de cargar los datos necesarios al recyclerview
                    FirestoreRecyclerOptions<Mensaje> options =
                            new FirestoreRecyclerOptions.Builder<Mensaje>()
                                    .setQuery(query, new SnapshotParser<Mensaje>() {
                                        @NonNull
                                        @Override
                                        public Mensaje parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                            //Obtenemos el proyecto
                                            Mensaje mensaje = snapshot.toObject(Mensaje.class);
                                            //Si el proyecto no es nulo le asignamos el id del proyecto
                                            // y lo asignamos al modelo ya que no carga el id al no ser un campo
                                            if (mensaje != null) {
                                                mensaje.setId(snapshot.getId());
                                                mensaje.setTitulo(snapshot.get("titulo", String.class));
                                                mensaje.setMensaje(snapshot.get("mensaje", String.class));
                                            }
                                            return mensaje;
                                        }
                                    })
                                    .build();

                    //Asignamos el adapter
                    adapter = new AdapterMensajes(options);
                    //Notificamos al adapter que los datos han cambiado
                    adapter.notifyDataSetChanged();
                    //Asignamos el adapter al recyclerview
                    recyclerView.setAdapter(adapter);
                    //Iniciamos el listener
                    adapter.startListening();
                    carga.setVisibility(carga.GONE);
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                }else{
                    finish();
                }
            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
    }
}
