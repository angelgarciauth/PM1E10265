package com.example.pm1e10265;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaDataSource;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.pm1e10265.configuraciones.SQLiteConexion;
import com.example.pm1e10265.configuraciones.Transacciones;
import com.example.pm1e10265.tablas.Pais;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnGuardar,btnContactos,btnAddPais;
    EditText txtNombre,txtTelefono,txtNota;
    Spinner spPais;
    ArrayList<String> lista_pais;
    ArrayList<Pais> lista;
    SQLiteConexion conexion;
    ArrayAdapter<CharSequence> adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnContactos = (Button) findViewById(R.id.btnContactos);
        btnAddPais = (Button) findViewById(R.id.btnAddPais);

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtTelefono = (EditText) findViewById(R.id.txtTelefono);
        txtNota = (EditText) findViewById(R.id.txtNota);

        spPais = (Spinner) findViewById(R.id.spPais);


        recargarCombo();

        btnContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityContactos.class);
                startActivity(intent);
            }
        });

        btnAddPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityAgregarPais.class);
                startActivity(intent);
            }
        });

    }

    public void obtenerListaPais(){
        Pais pais = null;
        lista = new ArrayList<Pais>();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ Transacciones.tablapais,null);

        while(cursor.moveToNext()){
            pais = new Pais();

            pais.setId(cursor.getInt(0));
            pais.setNombre(cursor.getString(1));
            pais.setCodigo(cursor.getInt(2));
            lista.add(pais);
        }
        cursor.close();

        fillCombo();
    }

    private void fillCombo(){
        lista_pais = new ArrayList<String>();

        for (int i = 0; i < lista.size(); i++) {
            lista_pais.add(lista.get(i).getNombre()+" ("+
                    lista.get(i).getCodigo()+")");
        }

    }

    public void recargarCombo(){
        obtenerListaPais();
        adp = new ArrayAdapter(this,android.R.layout.simple_spinner_item,lista_pais);
        spPais.setAdapter(adp);
    }
}