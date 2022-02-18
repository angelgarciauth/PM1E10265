package com.example.pm1e10265;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pm1e10265.configuraciones.SQLiteConexion;
import com.example.pm1e10265.configuraciones.Transacciones;
import com.example.pm1e10265.tablas.Pais;

public class ActivityAgregarPais extends AppCompatActivity {
    EditText txtNPais,txtCodigoPais;
    Button btnGuardarPais,btnBuscarPais;
    SQLiteConexion conexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_pais);

        txtNPais = (EditText) findViewById(R.id.txtNPais);
        txtCodigoPais = (EditText) findViewById(R.id.txtCodigoPais);

        btnGuardarPais = (Button) findViewById(R.id.btnGuardarPais);
        btnBuscarPais= (Button) findViewById(R.id.btnBuscarPais);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

        btnGuardarPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarPais();

            }
        });

        btnBuscarPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pais pais = new Pais();
                buscarPais(pais,txtNPais.getText().toString());

                txtNPais.setText(pais.getNombre());
                txtCodigoPais.setText(pais.getCodigo());
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

        Toast.makeText(getApplicationContext(),"Registro ingresado con exito!! Codigo"+resultado.toString(), Toast.LENGTH_LONG).show();

        db.close();

        LimpiarPantalla();


    }

    private void LimpiarPantalla() {
        txtNPais.setText("");
        txtCodigoPais.setText("");
    }

    private void buscarPais(Pais pais, String nombre){
        SQLiteDatabase bd = conexion.getReadableDatabase();

        Cursor cursor = bd.rawQuery("SELECT * FROM "+Transacciones.tablapais+" WHERE "+ Transacciones.nombrepais+"='"+nombre+"'",null);
        if(cursor.moveToFirst()){
            do{
                pais.setId(cursor.getInt(0));
                pais.setNombre(cursor.getString(1));
                pais.setCodigo(cursor.getInt(2));

            }while(cursor.moveToNext());
        }


    }


}