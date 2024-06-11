package com.example.whatwhy.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.Modelos.Reporte;
import com.example.whatwhy.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdapterListaInfoReports extends FirestoreRecyclerAdapter<Reporte, AdapterListaInfoReports.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private FirebaseFirestore db;

    public AdapterListaInfoReports(@NonNull FirestoreRecyclerOptions<Reporte> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterListaInfoReports.ViewHolder holder, int position, @NonNull Reporte model) {
        //Se inicializa la base de datos
        db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(model.getUserID()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String email = documentSnapshot.get("email").toString();
                        String nombre = documentSnapshot.get("nombre").toString();
                        holder.txtInfoEmailReport.setText(email);
                        holder.txtInfoUserReport.setText(nombre);
                        holder.txtInfoEmailReport.setVisibility(View.VISIBLE);
                        holder.txtInfoUserReport.setVisibility(View.VISIBLE);
                        holder.pBInfoEmailReport.setVisibility(View.GONE);
                        holder.pBInfoUserReport.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.txtInfoEmailReport.setText("Error");
                        holder.txtInfoUserReport.setText("Error");
                        holder.txtInfoEmailReport.setVisibility(View.VISIBLE);
                        holder.txtInfoUserReport.setVisibility(View.VISIBLE);
                        holder.pBInfoEmailReport.setVisibility(View.GONE);
                        holder.pBInfoUserReport.setVisibility(View.GONE);
                    }
                });

        holder.txtInfoMotivoReport.setText(model.getMotivo());
        holder.txtMoreInfoReport.setText(model.getMoreInfo());
        holder.txtInfoMotivoReport.setVisibility(View.VISIBLE);
        holder.txtMoreInfoReport.setVisibility(View.VISIBLE);
        holder.pBInfoMotivoReport.setVisibility(View.GONE);
        holder.pBMoreInfoReport.setVisibility(View.GONE);

    }

    @NonNull
    @Override
    public AdapterListaInfoReports.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_report, parent, false);
        return new AdapterListaInfoReports.ViewHolder(v);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtInfoEmailReport, txtInfoUserReport, txtInfoMotivoReport, txtMoreInfoReport;
        private ProgressBar pBInfoEmailReport, pBInfoUserReport, pBInfoMotivoReport, pBMoreInfoReport;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtInfoEmailReport = (TextView) itemView.findViewById(R.id.txtInfoEmailReport);
            txtInfoUserReport = (TextView) itemView.findViewById(R.id.txtInfoUserReport);
            txtInfoMotivoReport = (TextView) itemView.findViewById(R.id.txtInfoMotivoReport);
            txtMoreInfoReport = (TextView) itemView.findViewById(R.id.txtMoreInfoReport);

            pBInfoEmailReport = (ProgressBar) itemView.findViewById(R.id.pBInfoEmailReport);
            pBInfoUserReport = (ProgressBar) itemView.findViewById(R.id.pBInfoUserReport);
            pBInfoMotivoReport = (ProgressBar) itemView.findViewById(R.id.pBInfoMotivoReport);
            pBMoreInfoReport = (ProgressBar) itemView.findViewById(R.id.pBMoreInfoReport);
        }
    }
}
