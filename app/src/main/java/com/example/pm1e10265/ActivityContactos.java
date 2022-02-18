package com.example.pm1e10265;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.pm1e10265.configuraciones.SQLiteConexion;
import com.example.pm1e10265.configuraciones.Transacciones;
import com.example.pm1e10265.tablas.Contactos;

import java.util.ArrayList;

public class ActivityContactos extends AppCompatActivity {
    Button btnAtras;
    ListView lista;
    ArrayList<Contactos> listaContactos;
    ArrayList<String> arregloContactos;
    SQLiteConexion conexion;
    EditText txtBuscar,txtId;
    ArrayAdapter<CharSequence> adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        conexion= new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

        btnAtras = (Button) findViewById(R.id.btnAtras);
        lista = (ListView) findViewById(R.id.listContactos);

        txtBuscar = (EditText)findViewById(R.id.txtBuscarContacto);
        txtId = (EditText) findViewById(R.id.txtIdVerContacto);

        ObtenerListaContactos();

        adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arregloContactos);
        lista.setAdapter(adp);

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adp.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {





            }
        });
    }

    private void ObtenerListaContactos(){
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos list_contactos = null;
        listaContactos = new ArrayList<Contactos>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ Transacciones.tablacontactos,null);

        while(cursor.moveToNext()){
            list_contactos = new Contactos();
            list_contactos.setId(cursor.getInt(0));
            list_contactos.setPais(cursor.getString(1));
            list_contactos.setNombre(cursor.getString(2));
            list_contactos.setTelefono(cursor.getInt(3));
            list_contactos.setNota(cursor.getString(4));
            //list_contactos.setImagen(cursor.getBlob(5));

            listaContactos.add(list_contactos);
        }

        cursor.close();;

        llenalista();

    }

    private void llenalista(){
        arregloContactos = new ArrayList<String>();
        for (int i=0;i<listaContactos.size();i++){
            arregloContactos.add(listaContactos.get(i).getNombre()+" | "+listaContactos.get(i).getTelefono());
        }
    }

    public void eliminarContacto(){
        boolean correc = false;
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        long resultado = db.delete(Transacciones.tablacontactos,Transacciones.id+"=?", new String[]{txtId.getText().toString()});
        db.close();
    }
}