package com.example.whatwhy.Vistas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Pregunta;
import com.example.whatwhy.Modelos.Proyecto;
import com.example.whatwhy.Modelos.Respuestas;
import com.example.whatwhy.R;
import com.example.whatwhy.utils.AdapterPreguntasMake;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NuevoTestActivity extends AppCompatActivity {
    private ImageView imgPortada;
    private Spinner sTema;
    private Spinner sEstado;
    private Button bCrear;
    private TextView txtTitulo;
    private RecyclerView recyclerView;
    private AdapterPreguntasMake adapter;
    private static final int REQUEST_CODE = 1;

    private List<Pregunta> lista;
    private boolean existe;
    private String userID;
    private boolean estado;
    private int pivoteI, pivoteJ;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_test);

        imgPortada = findViewById(R.id.imgNewPortada);
        sTema = findViewById(R.id.sTema);
        sEstado = findViewById(R.id.sEstadoTest);
        bCrear = findViewById(R.id.bCreateTest);
        txtTitulo = findViewById(R.id.txtTituloNewTest);
        recyclerView = findViewById(R.id.recPregNuevas);

        // Inicializar la lista aquí
        lista = new ArrayList<>();
        lista.add(new Pregunta());

        estado = false;

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        userID = auth.getCurrentUser().getUid();

        cargarListaPreguntas();

        String[] datos = new String[] {"Default", "Ciencia", "Geografia", "Informática", "Naturaleza", "Literatura"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datos);
        sTema.setAdapter(adapter);

        String[] datos2 = new String[] {"Privado", "Público"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datos2);
        sEstado.setAdapter(adapter2);

        sTema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = sTema.getSelectedItem().toString();
                switch (seleccion) {
                    case "Default":
                        imgPortada.setImageResource(R.drawable.imgprincipal);
                        break;
                    case "Ciencia":
                        imgPortada.setImageResource(R.drawable.img_ciencia);
                        break;
                    case "Geografia":
                        imgPortada.setImageResource(R.drawable.img_geografia);
                        break;
                    case "Informática":
                        imgPortada.setImageResource(R.drawable.img_informatica);
                        break;
                    case "Naturaleza":
                        imgPortada.setImageResource(R.drawable.img_naturaleza);
                        break;
                    case "Literatura":
                        imgPortada.setImageResource(R.drawable.img_literatura);
                        break;
                    default:
                        imgPortada.setImageResource(R.drawable.imgprincipal);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(sEstado.getSelectedItem().toString().equals("Privado")){
                    estado = false;
                }else{
                    estado = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se comprueba de que no exista un projecto con ese nombre del usuario
                if(!existeNombreProyecto()){
                    if(tieneContenido()){
                        crearNuevoProyecto();
                        finish();
                    }else{
                        Toast.makeText(NuevoTestActivity.this, "No hay preguntas", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(NuevoTestActivity.this, "Ya tiene un Test creado con ese titulo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Se comprueba que el resultado sea correcto
        if (requestCode == REQUEST_CODE && data != null) {
            //Se crea la pregunta y se añade a la lista para actualizarse
            String pregunta = data.getStringExtra("pregunta");
            String r1 = data.getStringExtra("r1");
            String r2 = data.getStringExtra("r2");
            String r3 = data.getStringExtra("r3");
            String r4 = data.getStringExtra("r4");
            int correcta = data.getIntExtra("correcta", 1);
            int posicion = data.getIntExtra("posicion", -1);
            ArrayList<Respuestas> respuestas = new ArrayList<>();
            switch (correcta) {
                case 1:
                    respuestas.add(new Respuestas(r1, true));
                    if (!r4.isEmpty()) {
                        respuestas.add(new Respuestas(r1, false));
                    }
                    if (!r2.isEmpty()) {
                        respuestas.add(new Respuestas(r2, false));
                    }
                    if (!r3.isEmpty()) {
                        respuestas.add(new Respuestas(r3, false));
                    }
                    break;
                case 2:
                    if (!r1.isEmpty()) {
                        respuestas.add(new Respuestas(r1, false));
                    }
                    if (!r4.isEmpty()) {
                        respuestas.add(new Respuestas(r2, false));
                    }
                    if (!r3.isEmpty()) {
                        respuestas.add(new Respuestas(r3, false));
                    }
                    respuestas.add(new Respuestas(r2, true));
                    break;
                case 3:
                    if (!r1.isEmpty()) {
                        respuestas.add(new Respuestas(r1, false));
                    }
                    if (!r2.isEmpty()) {
                        respuestas.add(new Respuestas(r2, false));
                    }
                    respuestas.add(new Respuestas(r3, true));
                    if (!r4.isEmpty()) {
                        respuestas.add(new Respuestas(r4, false));
                    }
                    break;
                case 4:
                    if (!r1.isEmpty()) {
                        respuestas.add(new Respuestas(r1, false));
                    }
                    if (!r2.isEmpty()) {
                        respuestas.add(new Respuestas(r2, false));
                    }
                    if (!r3.isEmpty()) {
                        respuestas.add(new Respuestas(r3, false));
                    }

                    respuestas.add(new Respuestas(r4, true));
                    break;
                default:
                    break;
            }

            if (posicion  <  0) {
                lista.add(lista.size() - 1, new Pregunta(pregunta, respuestas));
                adapter.notifyDataSetChanged();
            } else {
                lista.set(posicion, new Pregunta(pregunta, respuestas));
                adapter.notifyItemChanged(posicion);
            }
        }
    }


    //Se carga la lista de preguntas en el recyclerview
    private void cargarListaPreguntas() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new AdapterPreguntasMake(NuevoTestActivity.this, lista);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setOnItemDeleteListener(new AdapterPreguntasMake.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int position) {
                lista.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, lista.size());
            }
        });
    }

    //Comprueba si ya existe un proyecto con ese nombre
    private boolean existeNombreProyecto() {
        existe = false;

        db.collection("proyectos")
                .whereEqualTo("nombre", txtTitulo.getText().toString().trim()).whereEqualTo("userID", userID)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            existe = false;
                        } else {
                            existe = true;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        return existe;
    }

    //Comprueba que los datos ingresados son correctos teniendo en cuenta:
    // - Que el titulo no este vacio
    // - Que exista mas de 1 pregunta
    private boolean tieneContenido() {
        boolean tiene = false;

        if (!txtTitulo.getText().toString().trim().isEmpty()) {
            if (lista.size() > 1) {
                lista.remove(lista.size() - 1);
                tiene = true;
            }
        }

        return tiene;
    }

    //metodo encargado de crear el proyecto
    private void crearNuevoProyecto() {
        pivoteI = 0;
        pivoteJ = 0;

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(txtTitulo.getText().toString().trim());
        proyecto.setTests((ArrayList<Pregunta>) lista);
        proyecto.setUserID(auth.getUid());

        Map<String, Object> data = new HashMap<>();
        data.put("nombre", proyecto.getNombre());
        data.put("userID", proyecto.getUserID());
        data.put("tema", sTema.getSelectedItem().toString());
        data.put("activo", estado);

        db.collection("proyectos").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference dRPro) {
                        for (pivoteI = 0; pivoteI < proyecto.getTests().size(); pivoteI++) {
                            Pregunta currentTest = proyecto.getTests().get(pivoteI);
                            Map<String, Object> dataQ = new HashMap<>();
                            dataQ.put("pregunta", currentTest.getPregunta());

                            dRPro.collection("preguntas").add(dataQ)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference dRPreg) {
                                            List<Respuestas> respuestas = currentTest.getRespuestas();
                                            if (respuestas != null && !respuestas.isEmpty()) {
                                                for (pivoteJ = 0; pivoteJ < respuestas.size(); pivoteJ++) {
                                                    Respuestas currentRespuesta = respuestas.get(pivoteJ);
                                                    Map<String, Object> dataR = new HashMap<>();
                                                    dataR.put("texto", currentRespuesta.getTexto());
                                                    dataR.put("correcta", currentRespuesta.isCorrecta());
                                                    dRPreg.collection("respuestas").add(dataR);
                                                }
                                            } else {
                                                Toast.makeText(NuevoTestActivity.this, "No hay respuestas para la pregunta " + currentTest.getPregunta(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(NuevoTestActivity.this, "Error al agregar la pregunta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        Toast.makeText(NuevoTestActivity.this, "Se ha creado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NuevoTestActivity.this, "Error al crear el proyecto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
