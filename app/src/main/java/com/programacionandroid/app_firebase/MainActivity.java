package com.programacionandroid.app_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

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
        botonBuscar();
        botonModificar();
//        botonRegistrar();
        botonEliminar();
        //listarContactos();

    }

    private void botonModificar() {
        btnModificarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtIdV.getText().toString().trim().isEmpty()
                        || txtNombreV.getText().toString().trim().isEmpty()
                        || txtTelefonoV.getText().toString().trim().isEmpty()
                        || txtCorreoV.getText().toString().trim().isEmpty()
                )
                {
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete los campos faltantes para actualizar!!", Toast.LENGTH_SHORT).show();

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

                            boolean res2 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("nombre").getValue().toString().equalsIgnoreCase(nombre)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "El nombre ("+nombre+") ya existe.\nimposible modificar!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                            if(res2 == false){
                                String aux = Integer.toString(id);
                                boolean res = false;
                                for(DataSnapshot x : snapshot.getChildren()){
                                    if(x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                        res = true;
                                        ocultarTeclado();
                                        x.getRef().child("nombre").setValue(nombre);
                                        x.getRef().child("telefono").setValue(telefono);
                                        x.getRef().child("correo").setValue(correo);
                                        limpiar();
                                        listarContactos();
                                        break;
                                    }
                                }

                                if(res == false){
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "ID ("+aux+") no encontrado.\nimposible modificar!!!!", Toast.LENGTH_SHORT).show();
                                    txtIdV.setText("");
                                    txtNombreV.setText("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });

    }

    private void listarContactos() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName());

        ArrayList<Agenda> listaagenda = new ArrayList<Agenda>();
        ArrayAdapter<Agenda> adapter = new ArrayAdapter <Agenda> (MainActivity.this, android.R.layout.simple_list_item_1, listaagenda);
        lvDatosV.setAdapter(adapter);


        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            //ocurre cuando se agregar un nuevo registro
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Agenda agenda = snapshot.getValue(Agenda.class);
                listaagenda.add(agenda);
                adapter.notifyDataSetChanged();
            }

            @Override
            // ocurre cuando se modifica o elimina un registro
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        lvDatosV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Agenda agenda = listaagenda.get(i);
                AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                a.setCancelable(true);
                a.setTitle("Contacto Seleccionado");
                String msg = "ID : " + agenda.getId() +"\n\n";
                msg += "NOMBRE : " + agenda.getNombre();
                a.setMessage(msg);
                a.show();
            }
        });


    }

    private void botonBuscar() {
        btnBuscarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtIdV.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Digite el ID del contacto a buscar!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtIdV.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName());
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String aux = Integer.toString(id);
                            boolean res = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                //if(aux.equalsIgnoreCase(x.child("id").getValue().toString()))
                                if(x.child("id").getValue().toString() == aux){
                                    res = true;
                                    ocultarTeclado();
                                    txtNombreV.setText(x.child("nombre").getValue().toString());
                                    txtTelefonoV.setText(x.child("telefono").getValue().toString());
                                    txtCorreoV.setText(x.child("correo").getValue().toString());
                                    break;
                                }else{
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "ID ("+aux+") No encontrado!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            /*if(res == false){

                            }*/
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    }

    private void botonEliminar() {
        btnEliminarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtIdV.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Digite el ID del contacto a eliminar!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtIdV.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName());
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String aux = Integer.toString(id);
                            final boolean[] res = {false};
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(aux.equalsIgnoreCase(x.child("id").getValue().toString())){
                                    AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("Pregunta");
                                    a.setMessage("¿Está seguro(a) de querer eliminar el registro?");
                                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });

                                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            res[0] = true;
                                            ocultarTeclado();
                                            x.getRef().removeValue();
                                            listarContactos();
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }

                            if(res[0] == false){
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "ID ("+aux+") No encontrado.\nimposible eliminar!!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    }

    private void botonRegistrar(){
        btnRegistrarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtIdV.getText().toString().trim().isEmpty()
                        || txtNombreV.getText().toString().trim().isEmpty()
                        || txtTelefonoV.getText().toString().trim().isEmpty()
                        || txtCorreoV.getText().toString().trim().isEmpty())
                {
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete los campos faltantes!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtIdV.getText().toString());
                    String nombre = txtNombreV.getText().toString();
                    String telefono = txtTelefonoV.getText().toString();
                    String correo = txtCorreoV.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance(); // conexion a la base de datos
                    DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName()); // referencia a la base de datos agenda

                    // evento de firebase que genera la tarea de insercion
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           /* String aux = Integer.toString(id);
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
                            }*/
                            Agenda agenda = new Agenda(id, nombre, telefono, correo);
                            dbref.push().setValue(agenda);
                            ocultarTeclado();
                            Toast.makeText(MainActivity.this, "Contacto registrado correctamente!!", Toast.LENGTH_SHORT).show();
                            limpiar();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.


            }
        });
    } // Cierra el método botonRegistrar.

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

