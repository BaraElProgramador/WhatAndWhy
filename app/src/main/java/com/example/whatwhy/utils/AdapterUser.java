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

public class AdapterUser extends FirestoreRecyclerAdapter<Usuario, AdapterUser.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private FirebaseFirestore db;

    public AdapterUser(@NonNull FirestoreRecyclerOptions<Usuario> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterUser.ViewHolder holder, int position, @NonNull Usuario model) {
        db = FirebaseFirestore.getInstance();

        holder.txtEmail.setText(model.getEmail());
        holder.txtName.setText(model.getNombre());

        db.collection("usuarios").document(model.getId()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                holder.txtNAdvert.setText(String.valueOf(documentSnapshot.getLong("puntos")));
                                holder.pBAdvert.setVisibility(View.GONE);
                                holder.txtNAdvert.setVisibility(View.VISIBLE);

                                if (documentSnapshot.get("baneado", boolean.class)) {
                                    holder.txtTBaned.setText("Baneado");
                                    holder.txtTBaned.setTextColor(Color.RED);
                                    holder.pBBanned.setVisibility(View.GONE);
                                    holder.txtTBaned.setVisibility(View.VISIBLE);
                                    holder.bBanUnbanUser.setText("Desbanear");
                                    desban(holder, model.getId());
                                }else{
                                    holder.txtTBaned.setText("Activo");
                                    holder.txtTBaned.setTextColor(Color.GREEN);
                                    holder.pBBanned.setVisibility(View.GONE);
                                    holder.txtTBaned.setVisibility(View.VISIBLE);
                                    holder.bBanUnbanUser.setText("Banear");
                                    ban(holder, model.getId());
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.txtNAdvert.setText("Error");
                        holder.pBAdvert.setVisibility(View.GONE);
                        holder.txtNAdvert.setVisibility(View.VISIBLE);
                    }
                });

        db.collection("proyectos").whereEqualTo("userID", model.getId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        holder.txtTCreates.setText(String.valueOf(queryDocumentSnapshots.size()));
                        holder.pBTCreates.setVisibility(View.GONE);
                        holder.txtTCreates.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.txtTCreates.setText("Error");
                        holder.pBTCreates.setVisibility(View.GONE);
                        holder.txtTCreates.setVisibility(View.VISIBLE);
                    }
                });
    }

    @NonNull
    @Override
    public AdapterUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_users, parent, false);
        return new AdapterUser.ViewHolder(v);
    }

    private void desban(@NonNull AdapterUser.ViewHolder holder, String userID){
        holder.bBanUnbanUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Desbanear usuario")
                        .setPositiveButton("Desbaneo total (10 puntos)", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map <String, Object> data = new HashMap<>();
                                data.put("baneado", false);
                                data.put("puntos", 10);

                                db.collection("usuarios").document(userID).update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(v.getContext(), "Usuario desbaneado", Toast.LENGTH_SHORT).show();
                                                holder.bBanUnbanUser.setText("Banear");
                                                holder.txtNAdvert.setText("10");
                                                holder.bBanUnbanUser.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ban(holder, userID);
                                                    }

                                                });

                                                db.collection("proyectos").whereEqualTo("userID", userID).get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                if(queryDocumentSnapshots.size() > 0){
                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("activo", true);
                                                                    for (DocumentSnapshot documentReference : queryDocumentSnapshots) {
                                                                        documentReference.getReference().update(data);
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }).setNeutralButton("Desbaneo parcial (5 puntos)", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map <String, Object> data = new HashMap<>();
                                data.put("baneado", false);
                                data.put("puntos", 5);

                                db.collection("usuarios").document(userID).update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(v.getContext(), "Usuario desbaneado", Toast.LENGTH_SHORT).show();
                                                holder.txtNAdvert.setText("5");
                                                holder.bBanUnbanUser.setText("Banear");
                                                holder.bBanUnbanUser.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ban(holder, userID);
                                                    }
                                                });

                                                db.collection("proyectos").whereEqualTo("userID", userID).get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                if(queryDocumentSnapshots.size() > 0){
                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("activo", true);
                                                                    for (DocumentSnapshot documentReference : queryDocumentSnapshots) {
                                                                        documentReference.getReference().update(data);
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });
    }

    private void ban(@NonNull AdapterUser.ViewHolder holder, String userID){
        holder.bBanUnbanUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("¿Seguro que desea banear al usuario?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map <String, Object> data = new HashMap<>();
                                data.put("baneado", true);
                                data.put("puntos", 0);

                                db.collection("usuarios").document(userID).update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(v.getContext(), "Usuario baneado", Toast.LENGTH_SHORT).show();
                                                holder.bBanUnbanUser.setText("Desbanear");
                                                holder.bBanUnbanUser.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        desban(holder, userID);
                                                    }
                                                });

                                                db.collection("proyectos").whereEqualTo("userID", userID).get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                if(queryDocumentSnapshots.size() > 0){
                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("activo", false);
                                                                    for (DocumentSnapshot documentReference : queryDocumentSnapshots) {
                                                                        documentReference.getReference().update(data);
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtEmail, txtName, txtTCreates, txtTBaned, txtNAdvert;
        private ImageView imgAdvert, imgDelete, imgVer;
        private ProgressBar pBTCreates, pBBanned, pBAdvert;
        private Button bBanUnbanUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmailUserRecycler);
            txtName = (TextView) itemView.findViewById(R.id.txtNameUserRecycler);
            txtTCreates = (TextView) itemView.findViewById(R.id.txtNTestCreate);
            txtTBaned = (TextView) itemView.findViewById(R.id.txtNTestDelete);
            txtNAdvert = (TextView) itemView.findViewById(R.id.txtNPuntos);
            pBTCreates = (ProgressBar) itemView.findViewById(R.id.pBTCreate);
            pBBanned = (ProgressBar) itemView.findViewById(R.id.pBTBaned);
            pBAdvert = (ProgressBar) itemView.findViewById(R.id.pBNAdviser);
            bBanUnbanUser = (Button) itemView.findViewById(R.id.bBanUnbanUser);

        }
    }
}
