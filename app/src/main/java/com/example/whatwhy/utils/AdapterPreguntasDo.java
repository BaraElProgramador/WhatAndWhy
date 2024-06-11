package com.example.whatwhy.utils;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Vistas.Listas.ListaPreguntas;
import com.example.whatwhy.Modelos.Respuestas;
import com.example.whatwhy.Modelos.Pregunta;
import com.example.whatwhy.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdapterPreguntasDo extends FirestoreRecyclerAdapter<Pregunta, AdapterPreguntasDo.ViewHolder> {
    private String idProyect;
    private ListaPreguntas clasePreguntas;

    public AdapterPreguntasDo(@NonNull FirestoreRecyclerOptions<Pregunta> options, String idProyect, ListaPreguntas clasePreguntas) {
        super(options);
        this.idProyect = idProyect;
        this.clasePreguntas = clasePreguntas;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterPreguntasDo.ViewHolder holder, int position, @NonNull Pregunta model) {
        holder.pregunta.setText(model.getPregunta());

        // Obtener respuestas
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        String preguntaId = snapshot.getId();
        Query query = db.collection("proyectos").document(idProyect)
                .collection("preguntas").document(preguntaId)
                .collection("respuestas");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Respuestas> respuestas = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Respuestas respuesta = document.toObject(Respuestas.class);
                    respuestas.add(respuesta);
                }

                // Actualiza el modelo con las respuestas obtenidas
                model.setRespuestas((ArrayList<Respuestas>) respuestas);

                // Esconde los RadioButton sobrantes
                esconderRespuestas(holder, model.getRespuestas().size());
                // Carga el texto de las respuestas
                cargarRespuestas(holder, model);
                // Indica cuál es la respuesta correcta
                colocarRespuestaCorrecta(holder, model);

                // Listener de cuando se pincha sobre una respuesta
                holder.grupoRespuestas.setOnCheckedChangeListener((group, checkedId) -> {
                    if (holder.correct.isChecked()) {
                        holder.correct.setTextColor(Color.GREEN);
                        clasePreguntas.incrementCorrectQuestions();
                    } else {
                        holder.r1.setTextColor(Color.RED);
                        holder.r2.setTextColor(Color.RED);
                        holder.r3.setTextColor(Color.RED);
                        holder.r4.setTextColor(Color.RED);
                        holder.correct.setTextColor(Color.GREEN);
                    }
                    holder.r1.setClickable(false);
                    holder.r2.setClickable(false);
                    holder.r3.setClickable(false);
                    holder.r4.setClickable(false);
                });

                holder.pregunta.setVisibility(View.VISIBLE);
                holder.grupoRespuestas.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);

            } else {
                // Manejar errores
                holder.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void esconderRespuestas(@NonNull AdapterPreguntasDo.ViewHolder holder, int total) {
        switch (total) {
            case 2:
                holder.r4.setVisibility(View.GONE);
                holder.r3.setVisibility(View.GONE);
                break;
            case 3:
                holder.r4.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void cargarRespuestas(@NonNull AdapterPreguntasDo.ViewHolder holder, @NonNull Pregunta model) {
        for (int i = 0; i < model.getRespuestas().size(); i++) {
            switch (i) {
                case 0:
                    holder.r1.setText(model.getRespuestas().get(i).getTexto());
                    break;
                case 1:
                    holder.r2.setText(model.getRespuestas().get(i).getTexto());
                    break;
                case 2:
                    holder.r3.setText(model.getRespuestas().get(i).getTexto());
                    break;
                case 3:
                    holder.r4.setText(model.getRespuestas().get(i).getTexto());
                    break;
                default:
                    break;
            }
        }
    }

    private void colocarRespuestaCorrecta(@NonNull AdapterPreguntasDo.ViewHolder holder, @NonNull Pregunta model) {
        boolean localizado = false;
        for (int i = 0; i < model.getRespuestas().size() && !localizado; i++) {
            if (model.getRespuestas().get(i).isCorrecta()) {
                switch (i) {
                    case 0:
                        holder.correct = holder.r1;
                        break;
                    case 1:
                        holder.correct = holder.r2;
                        break;
                    case 2:
                        holder.correct = holder.r3;
                        break;
                    case 3:
                        holder.correct = holder.r4;
                        break;
                    default:
                        break;
                }
                localizado = true;
            }
        }
    }

    @NonNull
    @Override
    public AdapterPreguntasDo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter_pregunta, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pregunta;
        private RadioGroup grupoRespuestas;
        private RadioButton r1, r2, r3, r4, correct;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pregunta = itemView.findViewById(R.id.txtTitleQ);
            grupoRespuestas = itemView.findViewById(R.id.grupoRespuestas);
            r1 = itemView.findViewById(R.id.resp1);
            r2 = itemView.findViewById(R.id.resp2);
            r3 = itemView.findViewById(R.id.resp3);
            r4 = itemView.findViewById(R.id.resp4);
            progressBar = itemView.findViewById(R.id.cargaPreguntas);

            pregunta.setVisibility(View.GONE);
            grupoRespuestas.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
