package com.example.whatwhy.Vistas.Listas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.whatwhy.Modelos.Pregunta;
import com.example.whatwhy.R;
import com.example.whatwhy.Vistas.ResultadosTest;
import com.example.whatwhy.utils.AdapterPreguntasDo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ListaPreguntas extends AppCompatActivity {
    //Variables de la parte grafica
    private ViewPager2 viewPager;
    private Button nextButton;
    private TextView txtPage, txtCorrectQuestions;
    private FirestoreRecyclerAdapter adapter;

    //Otras variables
    private String idProyecto;
    private int totalPreguntas, preguntasCorrectas;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_preguntas);

        viewPager = findViewById(R.id.viewPager);
        nextButton = findViewById(R.id.nextPageQ);
        txtPage = findViewById(R.id.txtPagQ);
        txtCorrectQuestions = findViewById(R.id.txtCorrectA);

        preguntasCorrectas = 0;

        //Inicializacion de variables
//        viewPager = findViewById(R.id.viewPager);
//        nextButton = findViewById(R.id.nextButton);

        idProyecto = getIntent().getStringExtra("idProyecto");
        db = FirebaseFirestore.getInstance();

        cargarDatosProyecto();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //Se actualiza el numero de la pagina
                txtPage.setText(String.valueOf(position + 1) + "/" + String.valueOf(totalPreguntas));
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ListaPreguntas.this)
                        .setTitle("Atención")
                        .setMessage("¿Desea terminar el juego e ir a la página de resultados?")
                        .setPositiveButton("Si, deseo terminar el juego", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Al aceptar creamos el Intent que contendra todos los datos que pasaremos y lo iniciamos
                                Intent i = new Intent(getApplicationContext(), ResultadosTest.class);
                                i.putExtra("idProyecto", idProyecto);
                                i.putExtra("preguntasCorrectas", preguntasCorrectas);
                                i.putExtra("totalPreguntas", totalPreguntas);
                                startActivity(i);
                                finish();
                            }
                        }).setNegativeButton("No, quiero seguir el juego", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                alert.show();
            }
        });

    }



    private void cargarDatosProyecto(){
        Query query = db.collection("proyectos").document(idProyecto).collection("preguntas");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Obtiene el total de preguntas
                totalPreguntas = queryDocumentSnapshots.size();
                txtPage.setText(String.valueOf(1) + "/" + String.valueOf(totalPreguntas));
            }
        });

        FirestoreRecyclerOptions<Pregunta> options = new FirestoreRecyclerOptions.Builder<Pregunta>()
                .setQuery(query, Pregunta.class)
                .build();

        adapter = new AdapterPreguntasDo(options, idProyecto, this);
        viewPager.setAdapter(adapter);

    }

    //Metodo para incrementar las preguntas correctas y actualizar el contador
    public void incrementCorrectQuestions(){
        preguntasCorrectas++;
        txtCorrectQuestions.setText(String.valueOf(preguntasCorrectas));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}

