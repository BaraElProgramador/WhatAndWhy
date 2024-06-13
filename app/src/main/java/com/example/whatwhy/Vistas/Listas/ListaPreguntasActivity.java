package com.example.whatwhy.Vistas.Listas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.whatwhy.Modelos.Pregunta;
import com.example.whatwhy.R;
import com.example.whatwhy.Vistas.ResultadosTestActivity;
import com.example.whatwhy.utils.AdapterPreguntasDo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ListaPreguntasActivity extends AppCompatActivity {
    //Variables de la parte grafica
    private ViewPager2 viewPager;
    private Button endButton;
    private TextView txtPage, txtCorrectQuestions;
    private FirestoreRecyclerAdapter adapter;
    private ImageView imgLsygnal, imgRsygnal;

    //Otras variables
    private String idProyecto;
    private int totalPreguntas, preguntasCorrectas;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_preguntas);

        viewPager = findViewById(R.id.viewPager);
        endButton = findViewById(R.id.nextPageQ);
        txtPage = findViewById(R.id.txtPagQ);
        txtCorrectQuestions = findViewById(R.id.txtCorrectA);
        imgLsygnal = findViewById(R.id.imgLsygnal);
        imgRsygnal = findViewById(R.id.imgRsygnal);

        preguntasCorrectas = 0;


        idProyecto = getIntent().getStringExtra("idProyecto");
        db = FirebaseFirestore.getInstance();

        cargarDatosProyecto();

        //Controla la posición de las preguntas
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //Se actualiza el numero de la pagina
                txtPage.setText(String.valueOf(position + 1) + "/" + String.valueOf(totalPreguntas));
                //Muestra o esconde las imagenes de siguiente o anterior
                if (position == 0) {
                    imgLsygnal.setVisibility(View.GONE);
                    imgRsygnal.setVisibility(View.VISIBLE);
                } else if (position == totalPreguntas - 1) {
                    imgLsygnal.setVisibility(View.VISIBLE);
                    imgRsygnal.setVisibility(View.GONE);
                } else {
                    imgLsygnal.setVisibility(View.VISIBLE);
                    imgRsygnal.setVisibility(View.VISIBLE);
                }
            }
        });

        //Listener del boton que gestiona la parte en la que el usuario termina el test
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ListaPreguntasActivity.this)
                        .setTitle("Atención")
                        .setMessage("¿Desea terminar el juego e ir a la página de resultados?")
                        .setPositiveButton("Si, deseo terminar el juego", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Al aceptar creamos el Intent que contendra todos los datos que pasaremos y lo iniciamos
                                Intent i = new Intent(getApplicationContext(), ResultadosTestActivity.class);
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

        //Listeners para ir a la siguiente o a la anterior pregunta
        imgLsygnal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtengo la pagina en la que esta el viewPage2
                int actual = viewPager.getCurrentItem();
                //A pesar de que escondo los botones para asegurarme de que no existan errores compruebo si se le puede pulsar
                if(actual > 0){
                    viewPager.setCurrentItem(actual -1);
                }
            }
        });

        imgRsygnal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtengo la pagina en la que esta el viewPage2
                int actual = viewPager.getCurrentItem();
                //A pesar de que escondo los botones para asegurarme de que no existan errores compruebo si se le puede pulsar
                if(actual < totalPreguntas){
                    viewPager.setCurrentItem(actual +1);
                }

            }
        });

    }



    //Carga los datos de las preguntas
    private void cargarDatosProyecto(){
        //Inicializo un query con la consulta que necesito
        Query query = db.collection("proyectos").document(idProyecto).collection("preguntas");

        //Lo ejecuto
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Obtiene el total de preguntas
                totalPreguntas = queryDocumentSnapshots.size();
                txtPage.setText(String.valueOf(1) + "/" + String.valueOf(totalPreguntas));
            }
        });

        //Inicializo el adaptador
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

