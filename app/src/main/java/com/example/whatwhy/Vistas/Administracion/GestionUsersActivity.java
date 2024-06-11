package com.example.whatwhy.Vistas.Administracion;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Usuario;
import com.example.whatwhy.R;
import com.example.whatwhy.utils.AdapterUser;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class GestionUsersActivity extends AppCompatActivity {
    //Creación de variables
    private RecyclerView recyclerView;

    private AdapterUser adapter;
    private Spinner sFiltroUser;

    private Query query;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_user);

        //Obtenemos el recyclerview y le asignamos el layout manager
        recyclerView = (RecyclerView) findViewById(R.id.recyclerGestionUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Obtenemos el spinner y le asignamos el adapter
        sFiltroUser = (Spinner) findViewById(R.id.sFiltroUser);

        String [] datos = new String[] {"Todos", "Usuarios activos", "Usuarios baneados"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datos);
        sFiltroUser.setAdapter(adapter);

        sFiltroUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarDatos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Inicializamos la base de datos
        db = FirebaseFirestore.getInstance();
        //Llamamos a la función encargada de cargar los datos
        cargarDatos();
    }

    //Función encargada de cargar los datos
    private void cargarDatos(){
        if(sFiltroUser.getSelectedItem().equals("Todos")){
            query = db.collection("usuarios").whereEqualTo("rol", 0);
        }else{
            if(sFiltroUser.getSelectedItem().equals("Usuarios baneados")){
                query = db.collection("usuarios").whereEqualTo("rol", 0).whereEqualTo("baneado", true);
            }
            if(sFiltroUser.getSelectedItem().equals("Usuarios activos")){
                query = db.collection("usuarios").whereEqualTo("rol", 0).whereEqualTo("baneado", false);
            }
        }

        //Obtenemos los datos de la base de datos y los asignamos al query
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                //Si no esta vacio creamos el options
                // que sera el encargado de cargar los datos necesarios al recyclerview
                FirestoreRecyclerOptions<Usuario> options =
                        new FirestoreRecyclerOptions.Builder<Usuario>()
                                .setQuery(query, new SnapshotParser<Usuario>() {
                                    @NonNull
                                    @Override
                                    public Usuario parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                        //Obtenemos el proyecto
                                        Usuario usuario = snapshot.toObject(Usuario.class);
                                        //Si el proyecto no es nulo le asignamos el id del proyecto
                                        // y lo asignamos al modelo ya que no carga el id al no ser un campo
                                        if (usuario != null) {
                                            usuario.setId(snapshot.getId());
                                        }
                                        return usuario;
                                    }
                                })
                                .build();

                //Asignamos el adapter
                adapter = new AdapterUser(options);
                //Notificamos al adapter que los datos han cambiado
                adapter.notifyDataSetChanged();
                //Asignamos el adapter al recyclerview
                recyclerView.setAdapter(adapter);
                //Iniciamos el listener
                adapter.startListening();

            }
        });
    }
}
