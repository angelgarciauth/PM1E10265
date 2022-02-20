package com.example.pm1e10265;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pm1e10265.configuraciones.SQLiteConexion;
import com.example.pm1e10265.configuraciones.Transacciones;
import com.example.pm1e10265.tablas.Pais;

public class ActivityAgregarPais extends AppCompatActivity {
    EditText txtNPais,txtCodigoPais,txtBuscarPais,txtId;
    Button btnGuardarPais,btnBuscarPais,btnEliminar,btnEditar;
    SQLiteConexion conexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_pais);

        txtNPais = (EditText) findViewById(R.id.txtNPais);
        txtCodigoPais = (EditText) findViewById(R.id.txtCodigoPais);
        txtBuscarPais = (EditText) findViewById(R.id.txtBuscarPais);
        txtId = (EditText) findViewById(R.id.txtidAgregarPais);

        btnGuardarPais = (Button) findViewById(R.id.btnGuardarPais);
        btnBuscarPais= (Button) findViewById(R.id.btnBuscarPais);
        btnEliminar = (Button) findViewById(R.id.btnEliminarPais);
        btnEditar = (Button) findViewById(R.id.btnEditarPais);

        btnEliminar.setVisibility(View.GONE);
        btnEditar.setVisibility(View.GONE);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

        btnGuardarPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String respuesta=validar();

                if(!respuesta.equals("OK")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAgregarPais.this);
                    builder.setTitle("Mensaje Advertencia");
                    builder.setMessage(respuesta);
                    builder.setPositiveButton("Aceptar", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{
                    AgregarPais();

                }
            }
        });

        btnBuscarPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Buscar();

            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAgregarPais.this);
                builder.setMessage("Esta seguro(a) de borrar este pais?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                eliminarPais();
                                LimpiarPantalla();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditarPais();
                LimpiarPantalla();
            }
        });

    }

    private void AgregarPais() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(Transacciones.nombrepais,txtNPais.getText().toString());
        valores.put(Transacciones.codigopais,txtCodigoPais.getText().toString());

        Long resultado = db.insert(Transacciones.tablapais,Transacciones.idpais,valores);
        Toast.makeText(getApplicationContext(),"Registro ingresado con exito!! Codigo", Toast.LENGTH_LONG).show();

        db.close();
        LimpiarPantalla();
    }

    private void LimpiarPantalla() {
        txtNPais.setText("");
        txtCodigoPais.setText("");
    }

    private void Buscar() {
        SQLiteDatabase db = conexion.getWritableDatabase();

        String [] params = { txtBuscarPais.getText().toString() };
        String [] fields = {
                Transacciones.idpais,
                Transacciones.nombrepais,
                Transacciones.codigopais,
        };

        String whereCondition = Transacciones.nombrepais + "=?";

        try {

            Cursor data = db.query(Transacciones.tablapais, fields, whereCondition,params, null, null, null);
            if(data.getCount()>0){
                data.moveToFirst(); // moveToFirst = Obtener solo el primer elemento
                txtId.setText(data.getString(0));
                txtNPais.setText(data.getString(1));
                txtCodigoPais.setText(data.getString(2));
                Toast.makeText(getApplicationContext(), "Consultado con exito", Toast.LENGTH_LONG).show();
                btnGuardarPais.setVisibility(View.GONE);
                btnEliminar.setVisibility(View.VISIBLE);
                btnEditar.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(getApplicationContext(), "Elemento no encontrado ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Log.w("ANGEL", "Buscar "+txtNPais.getText().toString(), null);
        }
    }


    public void eliminarPais(){
        boolean correc = false;
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        long resultado = db.delete(Transacciones.tablapais,Transacciones.idpais+"=?", new String[]{txtId.getText().toString()});
        db.close();
    }

    private void EditarPais() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(Transacciones.idpais, txtId.getText().toString());
        valores.put(Transacciones.nombrepais,txtNPais.getText().toString());
        valores.put(Transacciones.codigopais,txtCodigoPais.getText().toString());

        long resultado = db.replace(Transacciones.tablapais,Transacciones.idpais,valores);
        Toast.makeText(getApplicationContext(),"Registro Editado con exito", Toast.LENGTH_LONG).show();
        db.close();
    }



    private String validar() {
        String respuesta = "OK";

        if (txtNPais.getText().length() == 0) {
            respuesta = "Por favor ingresar el nombre del pais";
        } else if (txtCodigoPais.getText().length() == 0) {
            respuesta = "Por favor ingresar el codigo del pais";
        }
            return respuesta;
        }
    }


