package com.example.whatwhy.Vistas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatwhy.Vistas.Formularios.FormularioReportActivity;
import com.example.whatwhy.Modelos.Resultado;
import com.example.whatwhy.R;
import com.example.whatwhy.Vistas.Listas.ListaPreguntasActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DatosTestActivity extends AppCompatActivity {
    private TextView txtTitulo, txtAutor, txtScore;
    private ImageView imgPortada, imgLike, imgReport, imgDelete, imgEditTest;
    private ProgressBar pBTxtTitleTesData, pBTxtAutorData, pBImgEditTest, pBImgTestLike;

    private Button bDoTest;

    private FirebaseFirestore db;
    private FirebaseUser userAuth;

    private String userID, proyecID, creator;

    private Boolean like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_test);

        txtTitulo = (TextView) findViewById(R.id.txtTitleTesData);
        txtAutor = (TextView) findViewById(R.id.txtAutorData);
        txtScore = (TextView) findViewById(R.id.txtMaxScore);
        imgPortada = (ImageView) findViewById(R.id.imgTestData);
        imgLike = (ImageView) findViewById(R.id.imgTestLike);
        imgReport = (ImageView) findViewById(R.id.imgReportData);
        imgDelete = (ImageView) findViewById(R.id.imgDeleteTest);
        imgEditTest = (ImageView) findViewById(R.id.imgEditTest);
        pBTxtTitleTesData = (ProgressBar) findViewById(R.id.pBTxtTitleTesData);
        pBTxtAutorData = (ProgressBar) findViewById(R.id.pBTxtAutorData);
        pBImgEditTest = (ProgressBar) findViewById(R.id.pBImgEditTest);
        pBImgTestLike = (ProgressBar) findViewById(R.id.pBImgTestLike);
        bDoTest = (Button) findViewById(R.id.bDoTest);

        //Inicializo la base de datos y el usuario actual
        db = FirebaseFirestore.getInstance();
        userAuth = FirebaseAuth.getInstance().getCurrentUser();

        //Inicializo el like
        imgPortada.setImageResource(R.drawable.imgprincipal);

        //Obtengo el id del proyecto
        proyecID = getIntent().getStringExtra("idProyecto");

        //Cargo los datos
        cargarDatos();

        cargarScore();

        //Listener para cambiar el like
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLike();
            }
        });

        //Listener para ir al formulario de reportes
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DatosTestActivity.this, FormularioReportActivity.class);
                i.putExtra("idProyecto", proyecID);
                startActivity(i);
            }
        });

        //Listener para editar el proyecto
        imgEditTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DatosTestActivity.this, EditarTestActivity.class);
                i.putExtra("proyectoID", proyecID);
                startActivity(i);
            }
        });

        //Listener para eliminar el proyecto
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DatosTestActivity.this);
                builder.setTitle("Eliminar Proyecto");
                builder.setMessage("¿Estás seguro de que deseas eliminar este proyecto?");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarProyecto();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();

            }
        });

        //Listener para ir al formulario de preguntas
        bDoTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DatosTestActivity.this, ListaPreguntasActivity.class);
                i.putExtra("idProyecto", proyecID);
                startActivity(i);
            }
        });

    }

    //Carga los datos del proyecto
    private void cargarDatos(){
        //Obtengo los datos del proyecto
        db.collection("proyectos").document(proyecID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot proyectoSnapshot) {
                //Verifico que el proyecto exista
                if (proyectoSnapshot.exists()) {
                    //Obtengo los datos del proyecto
                    txtTitulo.setText(proyectoSnapshot.getString("nombre"));
                    pBTxtTitleTesData.setVisibility(View.GONE);
                    txtTitulo.setVisibility(View.VISIBLE);
                    //userID = proyectoSnapshot.getString("userID");
                    userID = userAuth.getUid();
                    String creatorID = proyectoSnapshot.getString("userID");
                    creator = proyectoSnapshot.get("userID", String.class);

                    //Obtengo la imagen del tema
                    cargarImagen(proyectoSnapshot.getString("tema"));

                    //Verifico si el usuario es el creador del proyecto para mostrar los botones
                    if(userID.equals(creatorID)){
                        imgEditTest.setVisibility(View.VISIBLE);
                        imgDelete.setVisibility(View.VISIBLE);
                    }

                    //Obtengo el nombre del creador del proyecto
                    db.collection("usuarios").document(creatorID).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            txtAutor.setText("Creador: " + documentSnapshot.getString("nombre"));
                                            pBTxtAutorData.setVisibility(View.GONE);
                                            txtAutor.setVisibility(View.VISIBLE);
                                        }
                                    });

                    //Obtengo los datos del usuario actual
                    db.collection("usuarios").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot usuarioSnapshot) {
                            verificarLike(usuarioSnapshot.getId(), proyecID, new VerificarLikeCallback() {
                                @Override
                                public void onCallback(boolean favoritoExiste) {
                                    //Obtengo el estado del like
                                    refreshLike(favoritoExiste);
                                    pBImgTestLike.setVisibility(View.GONE);
                                    imgLike.setVisibility(View.VISIBLE);

                                }
                            });
                            //Verifico si el usuario es administrador para mostrar el botón de eliminar
                            if (usuarioSnapshot.exists()) {
                                if(usuarioSnapshot.get("rol").toString().equals("1")){
                                    imgDelete.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Toast.makeText(DatosTestActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(DatosTestActivity.this, "El proyecto no existe", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DatosTestActivity.this, "Error al cargar el proyecto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Carga la imagen del tema
    private void cargarImagen(String imagen){
        switch (imagen) {
            case "Default":
                imgPortada.setImageResource(R.drawable.imgprincipal);
                pBImgEditTest.setVisibility(View.GONE);
                imgPortada.setVisibility(View.VISIBLE);
                break;
            case "Ciencia":
                imgPortada.setImageResource(R.drawable.img_ciencia);
                pBImgEditTest.setVisibility(View.GONE);
                imgPortada.setVisibility(View.VISIBLE);
                break;
            case "Geografia":
                imgPortada.setImageResource(R.drawable.img_geografia);
                pBImgEditTest.setVisibility(View.GONE);
                imgPortada.setVisibility(View.VISIBLE);
                break;
            case "Informática":
                imgPortada.setImageResource(R.drawable.img_informatica);
                pBImgEditTest.setVisibility(View.GONE);
                imgPortada.setVisibility(View.VISIBLE);
                break;
            case "Naturaleza":
                imgPortada.setImageResource(R.drawable.img_naturaleza);
                pBImgEditTest.setVisibility(View.GONE);
                imgPortada.setVisibility(View.VISIBLE);
                break;
            case "Literatura":
                imgPortada.setImageResource(R.drawable.img_literatura);
                pBImgEditTest.setVisibility(View.GONE);
                imgPortada.setVisibility(View.VISIBLE);
                break;
            default:
                imgPortada.setImageResource(R.drawable.imgprincipal);
                pBImgEditTest.setVisibility(View.GONE);
                imgPortada.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarScore();
        cargarDatos();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Elimina el proyecto y sus preguntas y respuestas
    private void eliminarProyecto() {
        //Elimino los favoritos del proyecto
        db.collection("favoritos").whereEqualTo("projectId", proyecID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentReference : queryDocumentSnapshots) {
                            documentReference.getReference().delete();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DatosTestActivity.this, "Error al borrar de favoritos", Toast.LENGTH_SHORT).show();
                    }
                });

        //Elimino los resultados del proyecto
        db.collection("resultados").whereEqualTo("projectID", proyecID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentReference : queryDocumentSnapshots) {
                            documentReference.getReference().delete();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DatosTestActivity.this, "Error al borrar de resultados", Toast.LENGTH_SHORT).show();
                    }
                });

        //Elimino los reportes del proyecto
        db.collection("reportes").whereEqualTo("projectID", proyecID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentReference : queryDocumentSnapshots) {
                            documentReference.getReference().delete();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DatosTestActivity.this, "Error al borrar de favoritos", Toast.LENGTH_SHORT).show();
                    }
                });

        DocumentReference projectRef = db.collection("proyectos").document(proyecID);

        //Eliminar preguntas y respuestas asociadas
        projectRef.collection("preguntas").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference questionRef = document.getReference();

                                //Eliminar respuestas asociadas
                                questionRef.collection("respuestas").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot responseDoc : task.getResult()) {
                                                        responseDoc.getReference().delete();
                                                    }
                                                }
                                            }
                                        });

                                //Eliminar la pregunta
                                questionRef.delete();
                            }

                            //Eliminar el proyecto después de eliminar todas las preguntas y respuestas
                            projectRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(DatosTestActivity.this, "Proyecto eliminado con éxito", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DatosTestActivity.this, "Error al eliminar el proyecto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(DatosTestActivity.this, "Error al obtener las preguntas: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void cargarScore(){

        //Cargar los datos del maxima puntuación en el test
        db.collection("resultados")
                .whereEqualTo("projectID", proyecID)
                .whereEqualTo("userID", userAuth.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            Resultado maxResult;
                            maxResult = queryDocumentSnapshots.getDocuments().get(0).toObject(Resultado.class);
                            int maxScore = maxResult.getScore();

                            db.collection("proyectos").document(proyecID)
                                    .collection("preguntas").get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            txtScore.setText("Max Score: " + maxScore + "/" + queryDocumentSnapshots.size());
                                            txtScore.setTextSize(30);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                        }else{
                            txtScore.setText("No hay resultados");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void changeLike() {
        // Cambia el valor de 'like' después de que se complete la operación
        like = !like;

        if (like) {
            String documentId = db.collection("favoritos").document().getId();

            Map<String, Object> favorito = new HashMap<>();
            favorito.put("userId", userAuth.getUid());
            favorito.put("projectId", proyecID);

            db.collection("favoritos").document(documentId).set(favorito)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            refreshLike(like);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // No inviertas el valor de like aquí
                            Toast.makeText(DatosTestActivity.this, "Error al guardar el favorito: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            //Si el like se desactiva, elimina el documento de favoritos
            db.collection("favoritos").whereEqualTo("userId", userAuth.getUid()).whereEqualTo("projectId", proyecID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                documentSnapshot.getReference().delete();
                            }
                            refreshLike(like);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DatosTestActivity.this, "Error al eliminar el favorito: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //Actualizo el like
    private void refreshLike(@NonNull Boolean newLike) {
        like = newLike;
        if(like){
            imgLike.setImageResource(R.drawable.like);
        }else{
            imgLike.setImageResource(R.drawable.notlike);
        }
    }

    //Verifica si el proyecto ya está en favoritos
    private void verificarLike(String userId, String projectId, VerificarLikeCallback callback) {
        //Toast.makeText(DataTest.this, userId, Toast.LENGTH_SHORT).show();
        db.collection("favoritos")
                .whereEqualTo("userId", userId)
                .whereEqualTo("projectId", projectId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean favoritoExiste = !task.getResult().isEmpty();
                            // Llama al método de devolución de llamada con el booleano
                            callback.onCallback(favoritoExiste);
                        } else {
                            // Si la consulta falla, llama al método de devolución de llamada con false
                            callback.onCallback(false);
                        }
                    }
                });
    }

    //Interfaz de devolución de llamada para verificar si el favorito existe
    private interface VerificarLikeCallback {
        void onCallback(boolean favoritoExiste);
    }

}