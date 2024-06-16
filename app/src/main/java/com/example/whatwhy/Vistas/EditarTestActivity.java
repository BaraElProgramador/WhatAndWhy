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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarTestActivity extends AppCompatActivity {
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
    private String userID, proyectoID;
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

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent i = getIntent();
        proyectoID = i.getStringExtra("proyectoID");

        // Inicializar la lista aquí
        lista = new ArrayList<>();

        estado = false;

        cargarDatos();

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
                if(txtTitulo.getText().toString().trim().length() <= 28){
                    if(tieneContenido()){
                        editarProyecto();
                    }
                }else{
                    Toast.makeText(EditarTestActivity.this, "El título no puede tener más de 28 caracteres", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && data != null) {
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

    //metodo encargado de editar el proyecto
    private void editarProyecto() {
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

        DocumentReference projectRef = db.collection("proyectos").document(proyectoID);

        projectRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Elimino las preguntas y sus respuestas existentes antes de agregar las nuevas
                        projectRef.collection("preguntas").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //Obtengo la referencia a la pregunta
                                                DocumentReference preguntaRef = document.getReference();

                                                //Eliminar las respuestas de cada pregunta
                                                preguntaRef.collection("respuestas").get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot respuestaDoc : task.getResult()) {
                                                                        respuestaDoc.getReference().delete();
                                                                    }

                                                                    // Después de eliminar todas las respuestas, eliminar la pregunta
                                                                    preguntaRef.delete();
                                                                }
                                                            }
                                                        });
                                            }

                                            //Agregar nuevas preguntas y respuestas
                                            for (pivoteI = 0; pivoteI < proyecto.getTests().size(); pivoteI++) {
                                                Pregunta currentTest = proyecto.getTests().get(pivoteI);
                                                Map<String, Object> dataQ = new HashMap<>();
                                                dataQ.put("pregunta", currentTest.getPregunta());

                                                projectRef.collection("preguntas").add(dataQ)
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
                                                                    Toast.makeText(EditarTestActivity.this, "No hay respuestas para la pregunta " + currentTest.getPregunta(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(EditarTestActivity.this, "Error al agregar la pregunta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                            Toast.makeText(EditarTestActivity.this, "Proyecto actualizado con éxito", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(EditarTestActivity.this, "Error al eliminar las preguntas antiguas: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditarTestActivity.this, "Error al actualizar el proyecto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Elimino los report del proyecto
        db.collection("reportes").whereEqualTo("projectID", proyectoID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().delete();
                        }
                    }
                });
    }



    //Función encargada de los datos que seran modificados
    private void cargarDatos(){
        //Obtengo los datos del proyecto
        db.collection("proyectos").document(proyectoID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        txtTitulo.setText(documentSnapshot.getString("nombre"));

                        //Obtengo el tema del proyecto y lo coloco tanto en la imagen como la seleccion en el splitter
                        String tema = documentSnapshot.getString("tema");
                        switch (tema) {
                            case "Ciencia":
                                imgPortada.setImageResource(R.drawable.img_ciencia);
                                sTema.setSelection(1);
                                break;
                            case "Geografia":
                                imgPortada.setImageResource(R.drawable.img_geografia);
                                sTema.setSelection(2);
                                break;
                            case "Informática":
                                imgPortada.setImageResource(R.drawable.img_informatica);
                                sTema.setSelection(3);
                                break;
                            case "Naturaleza":
                                imgPortada.setImageResource(R.drawable.img_naturaleza);
                                sTema.setSelection(5);
                                break;
                            case "Literatura":
                                imgPortada.setImageResource(R.drawable.img_literatura);
                                sTema.setSelection(4);
                                break;
                            default:
                                imgPortada.setImageResource(R.drawable.imgprincipal);
                                sTema.setSelection(0);
                                break;
                        }

                        //Obtengo el estado del proyecto y lo coloco en el splitter
                        if (documentSnapshot.getBoolean("activo")) {
                            sEstado.setSelection(1);
                        } else {
                            sEstado.setSelection(0);
                        }

                        // Accede a la subcolección "preguntas" dentro del proyecto
                        db.collection("proyectos").document(proyectoID).collection("preguntas")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot preguntaDoc : task.getResult()) {
                                                String preguntaId = preguntaDoc.getId();
                                                ArrayList<Respuestas> listaRespuestas = new ArrayList<>();
                                                // Accede a la subcolección "respuestas" dentro de cada pregunta
                                                db.collection("proyectos").document(proyectoID).collection("preguntas").document(preguntaId).collection("respuestas")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot respuestaDoc : task.getResult()) {
                                                                        // Procesa cada documento en "respuestas"
                                                                        Respuestas respuesta = new Respuestas();
                                                                        respuesta.setTexto(respuestaDoc.getString("texto"));
                                                                        respuesta.setCorrecta(respuestaDoc.getBoolean("correcta"));
                                                                        listaRespuestas.add(respuesta);
                                                                    }
                                                                } else {

                                                                }
                                                            }
                                                        });
                                                Pregunta p = new Pregunta();
                                                p.setPregunta(preguntaDoc.getString("pregunta"));
                                                p.setRespuestas(listaRespuestas);
                                                lista.add(p);
                                            }
                                            lista.add(new Pregunta());
                                            cargarRecyclers();
                                        } else {

                                        }
                                    }
                                });
                    }
                });

    }

    //Función encargada de cargar los datos en el recycler e inicializar los adaptadores
    private void cargarRecyclers() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new AdapterPreguntasMake(EditarTestActivity.this, lista);
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
}
