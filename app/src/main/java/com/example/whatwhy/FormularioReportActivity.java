package com.example.whatwhy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FormularioReportActivity extends AppCompatActivity {
    //Creación de variables
    private TextView txtTituloSendReport;
    private Spinner sSelectionData;
    private TextView txtMoreInfo;
    private Button bSendSomething;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

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

        //Inicializo la base de datos
        db = FirebaseFirestore.getInstance();

        //Inicializo el auth
        auth = FirebaseAuth.getInstance();

        //Obtengo el id del proyecto
        proyectoID = getIntent().getStringExtra("idProyecto");

        //Obtengo el id del usuario
        userID = auth.getCurrentUser().getUid();

        String[] datos = new String[] {"Violencia", "Contenido explicito", "Apologia al odio", "Otros"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datos);
        sSelectionData.setAdapter(adapter);

        //Creo los listeners necesarios
        bSendSomething.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("reportes").whereEqualTo("userID", userID).whereEqualTo("projectID", proyectoID).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.size() == 0){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(FormularioReportActivity.this)
                                            .setTitle("Atención")
                                            .setMessage("¿Desea reportar el test?")
                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Se crea un Map con los datos del reporte para enviarlo a administración
                                                    Map<String, Object> report = new HashMap<>();
                                                    report.put("projectID", proyectoID);
                                                    report.put("userID", userID);
                                                    report.put("tipo", sSelectionData.getSelectedItem().toString());
                                                    if(!txtMoreInfo.getText().toString().trim().isEmpty()){
                                                        report.put("moreInfo", txtMoreInfo.getText().toString());
                                                    }
                                                    report.put("estado", "pendiente");

                                                    db.collection("reportes").add(report)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Toast.makeText(FormularioReportActivity.this, "Reporte enviado", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(FormularioReportActivity.this, "Error al enviar el reporte", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });

                                    alert.show();

                                }else{
                                    Toast.makeText(FormularioReportActivity.this, "Ya has enviado un reporte para este proyecto", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FormularioReportActivity.this, "Error al enviar el reporte", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });



    }
}
