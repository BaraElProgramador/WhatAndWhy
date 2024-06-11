package com.example.whatwhy.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Pregunta;
import com.example.whatwhy.R;
import com.example.whatwhy.Vistas.NuevaPreguntaActivity;

import java.util.List;

public class AdapterPreguntasMake extends RecyclerView.Adapter<AdapterPreguntasMake.ViewHolder> {
    List<Pregunta> preguntas;
    private Activity activity;
    private static final int REQUEST_CODE = 1;
    private OnItemDeleteListener onItemDeleteListener;

    public AdapterPreguntasMake(Activity activity, List<Pregunta> preguntasList) {
        this.activity = activity;
        preguntas = preguntasList;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    @NonNull
    @Override
    public AdapterPreguntasMake.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_modify_preguntas, parent, false);
        return new AdapterPreguntasMake.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPreguntasMake.ViewHolder holder, int position) {
        Pregunta pregunta = preguntas.get(position);

        if (pregunta.getPregunta().isEmpty()) {
            holder.txtNew.setVisibility(View.VISIBLE);
            holder.imgNew.setVisibility(View.VISIBLE);
            holder.txtPregunta.setVisibility(View.GONE);
            holder.bEditPregunta.setVisibility(View.GONE);
            holder.imgDeletePregunta.setVisibility(View.GONE);

            listenerForEmpty(holder);
        } else {
            holder.txtNew.setVisibility(View.GONE);
            holder.imgNew.setVisibility(View.GONE);
            holder.txtPregunta.setVisibility(View.VISIBLE);
            holder.bEditPregunta.setVisibility(View.VISIBLE);
            holder.imgDeletePregunta.setVisibility(View.VISIBLE);

            holder.txtPregunta.setText(pregunta.getPregunta());

            listenerForPregunta(holder, position);
        }

        holder.bEditPregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NuevaPreguntaActivity.class);
                intent.putExtra("pregunta", pregunta.getPregunta());
                intent.putExtra("posicion", position);
                intent.putExtra("r1", pregunta.getRespuestas().get(0).getTexto());
                intent.putExtra("r2", pregunta.getRespuestas().get(1).getTexto());
                if(pregunta.getRespuestas().size() > 2){
                    intent.putExtra("r3", pregunta.getRespuestas().get(2).getTexto());
                    if(pregunta.getRespuestas().size() > 3){
                        intent.putExtra("r4", pregunta.getRespuestas().get(3).getTexto());
                    }
                }

                if(pregunta.getRespuestas().get(0).isCorrecta()){
                    intent.putExtra("correcta", 1);
                }else{
                    if(pregunta.getRespuestas().get(1).isCorrecta()){
                        intent.putExtra("correcta", 2);
                    }else{
                        if(pregunta.getRespuestas().get(2).isCorrecta()){
                            intent.putExtra("correcta", 3);
                        }else{
                            intent.putExtra("correcta", 4);
                        }
                    }
                }

                activity.startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return preguntas.size();
    }

    private void listenerForPregunta(@NonNull AdapterPreguntasMake.ViewHolder holder, int position) {
        holder.imgDeletePregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemDeleteListener != null) {
                    onItemDeleteListener.onItemDelete(holder.getAdapterPosition());
                }
            }
        });
    }

    private void listenerForEmpty(@NonNull AdapterPreguntasMake.ViewHolder holder) {
        holder.imgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NuevaPreguntaActivity.class);
                activity.startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNew, txtPregunta;
        Button bEditPregunta;
        ImageView imgNew, imgDeletePregunta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPregunta = itemView.findViewById(R.id.txtPregunta);
            txtNew = itemView.findViewById(R.id.textView12);
            bEditPregunta = itemView.findViewById(R.id.bEditPregunta);
            imgNew = itemView.findViewById(R.id.imgNewPregunta);
            imgDeletePregunta = itemView.findViewById(R.id.imgDeletePregunta);

            imgNew.setImageResource(R.drawable.img_add);
        }
    }
}
