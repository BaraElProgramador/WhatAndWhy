package com.example.whatwhy.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Mensaje;
import com.example.whatwhy.Modelos.Usuario;
import com.example.whatwhy.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.color.ColorRoles;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdapterMensajes extends FirestoreRecyclerAdapter<Mensaje, AdapterMensajes.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private FirebaseFirestore db;

    public AdapterMensajes(@NonNull FirestoreRecyclerOptions<Mensaje> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterMensajes.ViewHolder holder, int position, @NonNull Mensaje model) {
        db = FirebaseFirestore.getInstance();

        holder.txtTitulo.setText(model.getTitulo());
        holder.txtTitulo.setVisibility(View.VISIBLE);
        holder.pBTitulo.setVisibility(View.GONE);

        holder.txtInfo.setText(model.getMensaje());
        holder.txtInfo.setVisibility(View.VISIBLE);
        holder.pBInfo.setVisibility(View.GONE);

        holder.imgDelMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("mensajes").document(model.getId()).delete();
            }
        });
    }

    @NonNull
    @Override
    public AdapterMensajes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_mensaje, parent, false);
        return new AdapterMensajes.ViewHolder(v);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitulo, txtInfo;
        private ProgressBar pBInfo, pBTitulo;
        private ImageView imgDelMen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitulo = (TextView) itemView.findViewById(R.id.txtTituloMensaje);
            txtInfo = (TextView) itemView.findViewById(R.id.txtInfoMensaje);
            pBInfo = (ProgressBar) itemView.findViewById(R.id.pBInfoMensaje);
            pBTitulo = (ProgressBar) itemView.findViewById(R.id.pBTituloMensaje);
            imgDelMen = (ImageView) itemView.findViewById(R.id.imgDelMen);
        }
    }
}
