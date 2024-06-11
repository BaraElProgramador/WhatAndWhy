package com.example.whatwhy.Vistas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatwhy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RecyPregunta extends AppCompatActivity {
    private TextView titulo;
    private RadioGroup rGroup;
    private RadioButton r1, r2, r3, r4;

    private FirebaseFirestore db;

    private RadioButton correct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter_pregunta);

        titulo = (TextView) findViewById(R.id.txtTitleQ);
        rGroup = (RadioGroup) findViewById(R.id.grupoRespuestas);
        r1 = (RadioButton) findViewById(R.id.resp1);
        r2 = (RadioButton) findViewById(R.id.resp2);
        r3 = (RadioButton) findViewById(R.id.resp3);
        r4 = (RadioButton) findViewById(R.id.resp4);

        db = FirebaseFirestore.getInstance();


        respuestaCorrecta(2);

        cargarDatos();

        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                comprobarRespuesta();
            }
        });

    }

    //Se indica e inicializa la respuesta correcta
    private void respuestaCorrecta(int nCorrectQ){
        switch(nCorrectQ){
            case 1:
                correct = r1;
                break;
            case 2:
                correct = r2;
                break;
            case 3:
                correct = r3;
                break;
            case 4:
                correct = r4;
                break;
            default:
                //alerta
                finish();
                break;
        }
    }

    private void ocultarButtons(int totalQ){
        switch(totalQ){
            case 2:
                r4.setVisibility(View.GONE);
                r3.setVisibility(View.GONE);
                break;
            case 3:
                r4.setVisibility(View.GONE);
            case 4:
                break;
            default:
                //alerta
                finish();
                break;
        }
    }

    private void comprobarRespuesta(){
        if(correct.isChecked()){
            correct.setTextColor(Color.GREEN);
            Toast.makeText(RecyPregunta.this, "Correcto", Toast.LENGTH_SHORT).show();
        }else{
            r1.setTextColor(Color.RED);
            r2.setTextColor(Color.RED);
            r3.setTextColor(Color.RED);
            r4.setTextColor(Color.RED);
            correct.setTextColor(Color.GREEN);
            Toast.makeText(RecyPregunta.this, "Incorrecto", Toast.LENGTH_SHORT).show();
        }
        r1.setClickable(false);
        r2.setClickable(false);
        r3.setClickable(false);
        r4.setClickable(false);
    }

    private void cargarDatos(){
        String projectID = getIntent().getStringExtra("idProyecto");

        db.collection("proyectos").document(projectID)
                .collection("preguntas").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Obtengo el texto de la pregunta
                        String pregunta = queryDocumentSnapshots.getDocuments().get(0).getString("pregunta");
                        titulo.setText(pregunta);

                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        //Obtengo las respuestas
                        document.getReference().collection("respuestas").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot responseSnapshots) {
                                        for (DocumentSnapshot responseDoc : responseSnapshots.getDocuments()) {
                                            // Aquí puedes obtener los datos de cada respuesta
                                            String respuesta = responseDoc.getString("respuesta");

//                                            int cA = 1 + (int)(Math.random() * ((2 - 1) + 1));
//                                            respuestaCorrecta(cA);
                                            ocultarButtons(responseSnapshots.size());

                                            int aux = 0;

                                            for(DocumentSnapshot document : responseSnapshots.getDocuments()){
                                                aux ++;
                                                if(document.get("correcta", Boolean.class)){
                                                    respuestaCorrecta(aux);
                                                }
                                                switch (aux){
                                                    case 1:
                                                        r1.setText(document.getString("texto"));
                                                        break;
                                                    case 2:
                                                        r2.setText(document.getString("texto"));
                                                        break;
                                                    case 3:
                                                        r3.setText(document.getString("texto"));
                                                        break;
                                                    case 4:
                                                        r4.setText(document.getString("texto"));
                                                        break;
                                                    default:
                                                        Toast.makeText(RecyPregunta.this, "Error", Toast.LENGTH_SHORT).show();
                                                        break;
                                                }
                                            }

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Manejar el error al obtener las respuestas
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
