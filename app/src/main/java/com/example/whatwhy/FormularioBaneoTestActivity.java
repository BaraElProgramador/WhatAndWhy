package com.example.whatwhy;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FormularioBaneoTestActivity extends AppCompatActivity {
    //Creación de variables
    private TextView txtTituloSendReport;
    private Spinner sSelectionData;
    private TextView txtMoreInfo;
    private Button bSendSomething;
    private ProgressBar pBSendBan;

    private FirebaseFirestore db;

    private String userID;
    private String proyectoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        //Inicializo los elementos de la vista
        txtTituloSendReport = (TextView) findViewById(R.id.txtTituloSendReport);
        sSelectionData = (Spinner) findViewById(R.id.sSelectionData);
        txtMoreInfo = (TextView) findViewById(R.id.txtMoreInfo);
        bSendSomething = (Button) findViewById(R.id.bSendSomething);
        pBSendBan = (ProgressBar) findViewById(R.id.pBSendBan);

        bSendSomething.setVisibility(View.INVISIBLE);
        pBSendBan.setVisibility(View.VISIBLE);

        //Remplazo el texto del titulo
        txtTituloSendReport.setText("Formulario de baneo de Test");

        //Inicializo la base de datos
        db = FirebaseFirestore.getInstance();

        //Obtengo el id del proyecto
        proyectoID = getIntent().getStringExtra("proyectoID");

        //Obtengo el id del usuario
        db.collection("proyectos").document(proyectoID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userID = documentSnapshot.get("userID", String.class);
                        if (userID != null) {
                            bSendSomething.setVisibility(View.VISIBLE);
                            pBSendBan.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(FormularioBaneoTestActivity.this, "El documento no contiene un userID", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FormularioBaneoTestActivity.this, "Error en la carga de datos, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        String[] datos = new String[] {"Eliminar reportes", "Leve", "Grave", "Muy grave"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datos);
        sSelectionData.setAdapter(adapter);

        sSelectionData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(sSelectionData.getSelectedItem().toString()){
                    case "Eliminar reportes":
                        txtMoreInfo.setText("");
                        break;
                    case "Leve":
                        txtMoreInfo.setText("Se le ha puesto una amonestación leve en uno de sus test y se le a puesto en privado su proyecto.");
                        break;
                    case "Grave":
                        txtMoreInfo.setText("Se le ha puesto una amonestación grave en uno de sus test y se le ha borrado su proyecto.");
                        break;
                    case "Muy grave":
                        txtMoreInfo.setText("Se le ha puesto una amonestación muy grave en uno de sus test.");
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Creo los listeners necesarios
        bSendSomething.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(sSelectionData.getSelectedItem().toString()){
                    case "Eliminar reportes":
                        enviarReport(0);
                        break;
                    case "Leve":
                        enviarReport(1);
                        break;
                    case "Grave":
                        enviarReport(5);
                        break;
                    case "Muy grave":
                        enviarReport(10);
                        break;
                    default:
                }
                finish();
            }
        });


    }

    //Envia la sentencia del report
    private void enviarReport(int castigo){
        db.collection("usuarios").document(userID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Long puntosLong = documentSnapshot.getLong("puntos");
                        int puntos = puntosLong.intValue();
                        int resultado = puntos - castigo;

                        if(castigo > 0){
                            enviarMensaje();
                        }

                        //Dependiendo del castigo se le restan puntos al usuario, se le borra el proyecto o se le pone en privado
                        if(resultado <= 0){
                            banearUsuario();
                            limpiarReports();
                        }else{
                            if(castigo == 1){
                                documentSnapshot.getReference().update("puntos", documentSnapshot.getLong("puntos") - castigo);
                                limpiarReports();
                                db.collection("proyectos").document(proyectoID).update("activo", false);
                            }else{
                                documentSnapshot.getReference().update("puntos", documentSnapshot.getLong("puntos") - castigo);
                                limpiarReports();
                                eliminarProyecto();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FormularioBaneoTestActivity.this, "Error al castigar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Envia un mensaje al creador del proyecto
    private void enviarMensaje(){
        db.collection("proyectos").document(proyectoID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("userID", documentSnapshot.get("userID"));
                        data.put("mensaje", txtMoreInfo.getText().toString());
                        data.put("titulo", "Se ha baneado un test suyo");
                        data.put("pendiente", true);

                        db.collection("mensajes").add(data);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FormularioBaneoTestActivity.this, "Error al eliminar el proyecto", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    //Limpia los reportes del proyecto ya que se ha analizado los reportes de los usuarios
    private void limpiarReports(){
        db.collection("reportes").whereEqualTo("projectID", proyectoID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("estado", "realizado");
                        for (QueryDocumentSnapshot documentReference : queryDocumentSnapshots) {
                            documentReference.getReference().update(data);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void banearUsuario(){
        db.collection("favoritos").whereEqualTo("userId", userID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentReference : queryDocumentSnapshots) {
                            documentReference.getReference().delete();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FormularioBaneoTestActivity.this, "Error al borrar de favoritos", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("respuestas").whereEqualTo("userID", userID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentReference : queryDocumentSnapshots) {
                            documentReference.getReference().delete();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FormularioBaneoTestActivity.this, "Error al borrar de resultados", Toast.LENGTH_SHORT).show();
                    }
                });

        eliminarProyecto();

        db.collection("usuarios").document(userID).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                documentSnapshot.getReference().update("baneado", true);
                            }
                        });

    }

    //Elimina el proyecto y sus preguntas y respuestas
    private void eliminarProyecto() {
        DocumentReference projectRef = db.collection("proyectos").document(proyectoID);

        // Eliminar preguntas y respuestas asociadas
        projectRef.collection("preguntas").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference questionRef = document.getReference();

                                //Eliminar respuestas asociadas
                                questionRef.collection("respuestas").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot responseDoc : task.getResult()) {
                                                        responseDoc.getReference().delete();
                                                    }
                                                }
                                            }
                                        });

                                //Eliminar la pregunta
                                questionRef.delete();
                            }

                            //Eliminar el proyecto después de eliminar todas las preguntas y respuestas
                            projectRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(FormularioBaneoTestActivity.this, "Proyecto eliminado con éxito", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(FormularioBaneoTestActivity.this, "Error al eliminar el proyecto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(FormularioBaneoTestActivity.this, "Error al obtener las preguntas: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
