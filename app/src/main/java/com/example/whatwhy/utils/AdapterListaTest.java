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

import com.example.whatwhy.Vistas.DatosTest;
import com.example.whatwhy.Modelos.Proyecto;
import com.example.whatwhy.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdapterListaTest extends FirestoreRecyclerAdapter<Proyecto, AdapterListaTest.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterListaTest(@NonNull FirestoreRecyclerOptions<Proyecto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Proyecto model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRefUser = db.collection("usuarios").document(model.getUserID());

        holder.txtTitle.setText(model.getNombre());
        holder.pBTituloTest.setVisibility(View.GONE);
        holder.txtTitle.setVisibility(View.VISIBLE);


        switch (model.getTema()) {
            case "Default":
                holder.imgTest.setImageResource(R.drawable.imgprincipal);
                holder.pBImgTest.setVisibility(View.GONE);
                holder.imgTest.setVisibility(View.VISIBLE);
                break;
            case "Ciencia":
                holder.imgTest.setImageResource(R.drawable.img_ciencia);
                holder.pBImgTest.setVisibility(View.GONE);
                holder.imgTest.setVisibility(View.VISIBLE);
                break;
            case "Geografia":
                holder.imgTest.setImageResource(R.drawable.img_geografia);
                holder.pBImgTest.setVisibility(View.GONE);
                holder.imgTest.setVisibility(View.VISIBLE);
                break;
            case "Informática":
                holder.imgTest.setImageResource(R.drawable.img_informatica);
                holder.pBImgTest.setVisibility(View.GONE);
                holder.imgTest.setVisibility(View.VISIBLE);
                break;
            case "Naturaleza":
                holder.imgTest.setImageResource(R.drawable.img_naturaleza);
                holder.pBImgTest.setVisibility(View.GONE);
                holder.imgTest.setVisibility(View.VISIBLE);
                break;
            case "Literatura":
                holder.imgTest.setImageResource(R.drawable.img_literatura);
                holder.pBImgTest.setVisibility(View.GONE);
                holder.imgTest.setVisibility(View.VISIBLE);
                break;
            default:
                holder.imgTest.setImageResource(R.drawable.imgprincipal);
                holder.pBImgTest.setVisibility(View.GONE);
                holder.imgTest.setVisibility(View.VISIBLE);
                break;
        }

        docRefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.txtAutor.setText(documentSnapshot.getString("nombre"));
                holder.pBNameAutor.setVisibility(View.GONE);
                holder.txtAutor.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override   
            public void onFailure(@NonNull Exception e) {
                holder.txtAutor.setText("Error");
                holder.pBNameAutor.setVisibility(View.GONE);
                holder.txtAutor.setVisibility(View.VISIBLE);
            }
        });

        holder.bView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent i = new Intent(v.getContext(), DatosTest.class);
                i.putExtra("idProyecto", model.getId());
                context.startActivity(i);
            }
        });

        db.collection("resultados").whereEqualTo("projectID", model.getId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int nResult = queryDocumentSnapshots.size();
                        holder.txtNumPreguntas.setText(String.valueOf(nResult));
                        holder.pBNumRealizadas.setVisibility(View.GONE);
                        holder.txtNumPreguntas.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.txtNumPreguntas.setText("Error");
                        holder.pBNumRealizadas.setVisibility(View.GONE);
                        holder.txtNumPreguntas.setVisibility(View.VISIBLE);
                    }
                });

        db.collection("favoritos").whereEqualTo("projectId", model.getId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int nResult = queryDocumentSnapshots.size();
                        holder.txtNumFav.setText(String.valueOf(nResult));
                        holder.pBNumFav.setVisibility(View.GONE);
                        holder.txtNumFav.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.txtNumFav.setText("0");
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_test, parent, false);
        return new ViewHolder(v);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtAutor, txtNumPreguntas, txtNumFav;
        private ImageView imgTest;
        private Button bView;
        private ProgressBar pBTituloTest, pBNameAutor, pBNumRealizadas, pBNumFav, pBImgTest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTituloTest);
            txtAutor = (TextView) itemView.findViewById(R.id.txtNameAutor);
            txtNumPreguntas = (TextView) itemView.findViewById(R.id.txtNumRealizadas);
            txtNumFav = (TextView) itemView.findViewById(R.id.txtNumFav);
            imgTest = (ImageView) itemView.findViewById(R.id.imgTest);
            bView = (Button) itemView.findViewById(R.id.bViewTest);
            pBTituloTest = (ProgressBar) itemView.findViewById(R.id.pBTituloTest);
            pBNameAutor = (ProgressBar) itemView.findViewById(R.id.pBNameAutor);
            pBImgTest = (ProgressBar) itemView.findViewById(R.id.pBImgTest);
            pBNumRealizadas = (ProgressBar) itemView.findViewById(R.id.pBNumRealizadas);
            pBNumFav = (ProgressBar) itemView.findViewById(R.id.pBNumFav);

            imgTest.setImageResource(R.drawable.imgprincipal);
        }
    }
}