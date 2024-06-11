package com.example.whatwhy.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatwhy.FormularioBaneoTestActivity;
import com.example.whatwhy.Modelos.Proyecto;
import com.example.whatwhy.R;
import com.example.whatwhy.Vistas.DatosTest;
import com.example.whatwhy.Vistas.ListaInfoReports;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdapterReports extends FirestoreRecyclerAdapter<Proyecto, AdapterReports.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private FirebaseFirestore db;

    public AdapterReports(@NonNull FirestoreRecyclerOptions<Proyecto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterReports.ViewHolder holder, int position, @NonNull Proyecto model) {
        //Se inicializa la base de datos
        db = FirebaseFirestore.getInstance();

        //Coloco el titulo del test
        holder.txtTituloReport.setText(model.getNombre());
        holder.txtTituloReport.setVisibility(View.VISIBLE);
        holder.pBTituloReport.setVisibility(View.GONE);

        //Obtengo el email y el nombre de usuario y lo asigno a los textview
        db.collection("usuarios").document(model.getUserID()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        holder.txtEmail.setText(documentSnapshot.get("email").toString());
                        holder.txtName.setText(documentSnapshot.get("nombre").toString());
                        holder.txtEmail.setVisibility(View.VISIBLE);
                        holder.txtName.setVisibility(View.VISIBLE);
                        holder.pBEmailReport.setVisibility(View.GONE);
                        holder.pBNameReport.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.txtEmail.setText("Error");
                        holder.txtName.setText("Error");
                        holder.txtEmail.setVisibility(View.VISIBLE);
                        holder.txtName.setVisibility(View.VISIBLE);
                        holder.pBEmailReport.setVisibility(View.GONE);
                        holder.pBNameReport.setVisibility(View.GONE);
                    }
                });

        //Obtengo el número de reportes que tiene un test y lo asigno al textview
        db.collection("reportes").whereEqualTo("projectID", model.getId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String total = String.valueOf(queryDocumentSnapshots.size());
                        holder.txtNReportTestReport.setText(total);
                        holder.txtNReportTestReport.setVisibility(View.VISIBLE);
                        holder.pBNReport.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.txtNReportTestReport.setText("Error");
                        holder.txtNReportTestReport.setVisibility(View.VISIBLE);
                        holder.pBNReport.setVisibility(View.GONE);
                    }
                });

        //Cargo la imagen del test
        switch (model.getTema()) {
            case "Default":
                holder.imgPortadaReport.setImageResource(R.drawable.imgprincipal);
                holder.imgPortadaReport.setVisibility(View.VISIBLE);
                holder.pBImgReport.setVisibility(View.GONE);
                break;
            case "Ciencia":
                holder.imgPortadaReport.setImageResource(R.drawable.img_ciencia);
                holder.imgPortadaReport.setVisibility(View.VISIBLE);
                holder.pBImgReport.setVisibility(View.GONE);
                break;
            case "Geografia":
                holder.imgPortadaReport.setImageResource(R.drawable.img_geografia);
                holder.imgPortadaReport.setVisibility(View.VISIBLE);
                holder.pBImgReport.setVisibility(View.GONE);
                break;
            case "Informática":
                holder.imgPortadaReport.setImageResource(R.drawable.img_informatica);
                holder.imgPortadaReport.setVisibility(View.VISIBLE);
                holder.pBImgReport.setVisibility(View.GONE);
                break;
            case "Naturaleza":
                holder.imgPortadaReport.setImageResource(R.drawable.img_naturaleza);
                holder.imgPortadaReport.setVisibility(View.VISIBLE);
                holder.pBImgReport.setVisibility(View.GONE);
                break;
            case "Literatura":
                holder.imgPortadaReport.setImageResource(R.drawable.img_literatura);
                holder.imgPortadaReport.setVisibility(View.VISIBLE);
                holder.pBImgReport.setVisibility(View.GONE);
                break;
            default:
                holder.imgPortadaReport.setImageResource(R.drawable.imgprincipal);
                holder.imgPortadaReport.setVisibility(View.VISIBLE);
                holder.pBImgReport.setVisibility(View.GONE);
                break;
        }

        //Inicializo los listeners de los botones

        //Listener para ver el test
        holder.bViewReporterTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent i = new Intent(v.getContext(), DatosTest.class);
                i.putExtra("idProyecto", model.getId());
                context.startActivity(i);
            }
        });

        holder.bBanTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), FormularioBaneoTestActivity.class);
                i.putExtra("proyectoID", model.getId());
                v.getContext().startActivity(i);
            }
        });

        holder.bVerReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ListaInfoReports.class);
                i.putExtra("proyectoID", model.getId());
                v.getContext().startActivity(i);
            }
        });


    }

    @NonNull
    @Override
    public AdapterReports.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_gestor_test_report, parent, false);
        return new AdapterReports.ViewHolder(v);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTituloReport, txtEmail, txtName, txtNReportTestReport;
        private ImageView imgPortadaReport;
        private ProgressBar pBEmailReport, pBNameReport, pBNReport, pBTituloReport, pBImgReport;
        private Button bBanTest, bViewReporterTest, bVerReports;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTituloReport = (TextView) itemView.findViewById(R.id.txtTituloReport);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmailTestReport);
            txtName = (TextView) itemView.findViewById(R.id.txtNameTestReport);
            txtNReportTestReport = (TextView) itemView.findViewById(R.id.txtNReportTestReport);

            imgPortadaReport = (ImageView) itemView.findViewById(R.id.imgPortadaReport);

            pBEmailReport = (ProgressBar) itemView.findViewById(R.id.pBEmailReport);
            pBNameReport = (ProgressBar) itemView.findViewById(R.id.pBNameReport);
            pBNReport = (ProgressBar) itemView.findViewById(R.id.pBNReport);
            pBTituloReport = (ProgressBar) itemView.findViewById(R.id.pBTituloReport);
            pBImgReport = (ProgressBar) itemView.findViewById(R.id.pBImgReport);
            bBanTest = (Button) itemView.findViewById(R.id.bBanTest);
            bViewReporterTest = (Button) itemView.findViewById(R.id.bViewReporterTest);
            bVerReports = (Button) itemView.findViewById(R.id.bVerReports);


        }
    }
}
