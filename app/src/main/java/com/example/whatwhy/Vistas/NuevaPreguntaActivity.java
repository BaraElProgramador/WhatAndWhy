package com.example.whatwhy.Vistas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatwhy.R;

public class NuevaPreguntaActivity extends AppCompatActivity {
    private EditText txtPregunta, txtR1, txtR2, txtR3, txtR4;
    private RadioGroup grupOpciones;
    private RadioButton r1, r2, r3, r4;
    private Button btnGuardar;

    private String pregunta, r1str, r2str, r3str, r4str;
    private int posicion;
    private int correcta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_pregunta);

        txtPregunta = findViewById(R.id.txtEditPregunta);
        txtR1 = findViewById(R.id.txtEditR1);
        txtR2 = findViewById(R.id.txtEditR2);
        txtR3 = findViewById(R.id.txtEditR3);
        txtR4 = findViewById(R.id.txtEditR4);

        grupOpciones = findViewById(R.id.rGrupOpciones);

        r1 = findViewById(R.id.rBEditR1);
        r2 = findViewById(R.id.rBEditR2);
        r3 = findViewById(R.id.rBEditR3);
        r4 = findViewById(R.id.rBEditR4);
        btnGuardar = findViewById(R.id.bSaveQ);

        Intent i = getIntent();
        posicion = -1;
        if(i.getIntExtra("posicion", -1) >= 0){
            cargarDatos();
        }

        //Listener para gestionar la creacion de la respuesta correcta
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controlDatos()){
                    enviarDatos();
                }else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(NuevaPreguntaActivity.this)
                            .setTitle("Atención")
                            .setMessage("La pregunta correcta no debe estar vacia y debe tener 2 o mas respuestas")
                            .setNeutralButton("Vale", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    alert.show();
                }
            }
        });



    }

    //Comprueba que los datos ingresados son correctos teniendo en cuenta:
    // - Que la respuesta correcta no este vacia
    // - Que exista mas de 1 respuesta
    private boolean controlDatos(){
        boolean correcto = false;
        int nResp = 0;

        if ((r1.isChecked() && !txtR1.getText().toString().isEmpty()) || (r2.isChecked() && !txtR2.getText().toString().isEmpty()) || (r4.isChecked() && !txtR4.getText().toString().isEmpty()) || (r3.isChecked() && !txtR3.getText().toString().isEmpty())) {
            if(!txtR1.getText().toString().isEmpty()){
                nResp ++;
            }if(!txtR2.getText().toString().isEmpty()){
                nResp ++;
            }if(!txtR3.getText().toString().isEmpty()){
                nResp ++;
            }if(!txtR4.getText().toString().isEmpty()){
                nResp ++;
            }if(nResp >= 2){
                correcto = true;
            }
        }

        return correcto;
    }

    //Envia los datos a la actividad anterior
    private void enviarDatos(){
        Intent intent = new Intent();
        intent.putExtra("pregunta", txtPregunta.getText().toString());
        intent.putExtra("r1", txtR1.getText().toString());
        intent.putExtra("r2", txtR2.getText().toString());
        intent.putExtra("r3", txtR3.getText().toString());
        intent.putExtra("r4", txtR4.getText().toString());
        intent.putExtra("posicion", posicion);

        int correcta = 0;

        if(r1.isChecked()){
            correcta = 1;
        }else if(r2.isChecked()){
            correcta = 2;
        }else if(r3.isChecked()){
            correcta = 3;
        }else if(r4.isChecked()){
            correcta = 4;
        }

        intent.putExtra("correcta", correcta);

        setResult(RESULT_OK, intent);
        finish();
    }

    //Carga los datos del intent en las vistas
    private void cargarDatos(){
        // Obtener los datos del Intent
        Intent i = getIntent();
        pregunta = i.getStringExtra("pregunta");
        r1str = i.getStringExtra("r1");
        r2str = i.getStringExtra("r2");
        r3str = i.getStringExtra("r3");
        r4str = i.getStringExtra("r4");
        correcta = i.getIntExtra("correcta", 1);
        posicion = i.getIntExtra("posicion", -1);
        // Configurar las vistas con los datos recibidos
        txtPregunta.setText(pregunta);
        txtR1.setText(r1str);
        txtR2.setText(r2str);
        txtR3.setText(r3str);
        txtR4.setText(r4str);
        if(!txtR3.getText().toString().isEmpty()){
            txtR4.setEnabled(true);
        }
        switch (correcta){
            case 1:
                r1.setChecked(true);
                break;
            case 2:
                r2.setChecked(true);
                break;
            case 3:
                r3.setChecked(true);
                break;
            case 4:
                r4.setChecked(true);
                break;
            default:
                r1.setChecked(true);
                break;
        }
    }

}
