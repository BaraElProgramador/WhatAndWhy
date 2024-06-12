package com.example.whatwhy.Vistas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatwhy.Modelos.Resultado;
import com.example.whatwhy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ResultadosTestActivity extends AppCompatActivity {
        //Creación de variables
        private TextView txtResult, txtPorcentaje;
        private Button bVolver;

        private FirebaseFirestore db;
        private FirebaseAuth auth;

        private String idProject, idUser;
        private int result, total;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_resultados);

            //Asignación de variables
            txtResult = findViewById(R.id.txtResultTest);
            txtPorcentaje = findViewById(R.id.txtResultPorc);
            bVolver = findViewById(R.id.bResultVolver);

            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

            idUser = auth.getCurrentUser().getUid();

            idProject = getIntent().getStringExtra("idProyecto");
            result = getIntent().getIntExtra("preguntasCorrectas", 0);
            total = getIntent().getIntExtra("totalPreguntas", 0);

            obtenerPorcentaje();

            txtResult.setText(result + "/" + total);

            guardarResultado();

            bVolver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }

        //Obtengo el porcentaje de preguntas correctas
        private void obtenerPorcentaje(){
            int porcentaje = (result * 100) / total;
            txtPorcentaje.setText(porcentaje + "%");
            if(porcentaje >= 70){
                txtPorcentaje.setTextColor(Color.GREEN);
            }else{
                if(porcentaje < 50){
                    txtPorcentaje.setTextColor(Color.RED);
                }else{
                    txtPorcentaje.setTextColor(Color.YELLOW);
                }
            }
        }

        private void guardarResultado(){
            //Primero debo comparar si hay resultados
            db.collection("resultados")
                    .whereEqualTo("userID", idUser)
                    .whereEqualTo("projectID", idProject)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.isEmpty()){
                                //Escribo un nuevo resultado
                                //Guardo el nuevo resultado
                                db.collection("resultados")
                                        .document()
                                        .set(new Resultado(idUser, idProject, result));
                            }else{
                                //Comparo cual es el mayor resultado
                                Resultado lastResult;
                                lastResult = queryDocumentSnapshots.getDocuments().get(0).toObject(Resultado.class);
                                if(result > lastResult.getScore()){
                                    //Edito el nuevo resultado
                                    db.collection("resultados")
                                            .document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                            .update("score", result);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }
}
