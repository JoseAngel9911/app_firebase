package com.programacionandroid.app_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText txtIdV, txtNombreV, txtTelefonoV, txtCorreoV;
    Button btnBuscarV, btnModificarV, btnEliminarV, btnRegistrarV;
    ListView lvDatosV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtIdV = (EditText) findViewById(R.id.txtId);
        txtNombreV = (EditText) findViewById(R.id.txtNombre);
        txtTelefonoV = (EditText) findViewById(R.id.txtTelefono);
        txtCorreoV = (EditText) findViewById(R.id.txtCorreo);

        btnBuscarV =  (Button) findViewById(R.id.btnBuscar);
        btnModificarV = (Button) findViewById(R.id.btnModificar);
        btnEliminarV = (Button) findViewById(R.id.btnEliminar);
        btnRegistrarV = (Button) findViewById(R.id.btnRegistrar);
        lvDatosV = findViewById(R.id.lvDatos);

        this.botonRegistrar();

    }

    private void botonRegistrar(){
        btnRegistrarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtNombreV.getText().toString().trim().isEmpty() || txtIdV.getText().toString().trim().isEmpty() ||
                txtCorreoV.getText().toString().trim().isEmpty() || txtTelefonoV.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete los campos", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtIdV.getText().toString());
                    String nombre = txtNombreV.getText().toString();
                    String telefono = txtTelefonoV.getText().toString();
                    String correo = txtCorreoV.getText().toString();


                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String aux = Integer.toString(id);
                            boolean res1 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                    res1 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "Error, el ID ("+aux+") ya existe!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            boolean res2 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("nombre").getValue().toString().equalsIgnoreCase(nombre)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "Error, el nombre ("+nombre+") ya existe!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if(res1 == false && res2 == false){
                                Agenda agenda = new Agenda(id, nombre, telefono, correo);
                                dbref.push().setValue(agenda);
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "Contacto registrado correctamente!!", Toast.LENGTH_SHORT).show();
                                limpiar();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void limpiar() {
        txtIdV.setText("");
        txtNombreV.setText("");
        txtCorreoV.setText("");
        txtTelefonoV.setText("");
    }

    public void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imn = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imn.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}

