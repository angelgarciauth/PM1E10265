package com.example.pm1e10265;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.pm1e10265.configuraciones.Funciones;
import com.example.pm1e10265.configuraciones.SQLiteConexion;
import com.example.pm1e10265.configuraciones.Transacciones;
import com.example.pm1e10265.tablas.Contactos;

import java.util.ArrayList;

public class ActivityContactos extends AppCompatActivity {
    Button btnAtras, btnCompartir, btnActualizar, btnEliminar, btnVerImagen;
    ListView lista;
    ArrayList<Contactos> listaContactos;
    ArrayList<String> arregloContactos;
    SQLiteConexion conexion;
    EditText txtBuscar,txtId;
    ArrayAdapter<CharSequence> adp;

    Contactos contact = new Contactos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        conexion= new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

        btnAtras = (Button) findViewById(R.id.btnAtras);
        btnCompartir = (Button) findViewById(R.id.btnCompartir);
        btnActualizar = (Button) findViewById(R.id.btnActualizarContacto);
        btnEliminar = (Button) findViewById(R.id.btnEliminarContacto);
        btnVerImagen = (Button) findViewById(R.id.btnVerImagen);
        lista = (ListView) findViewById(R.id.listContactos);

        txtBuscar = (EditText)findViewById(R.id.txtBuscarContacto);
        txtId = (EditText) findViewById(R.id.txtIdVerContacto);

        reloadListView();

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
                contact = listaContactos.get(i);
            }
        });

        // EVENTOS BOTONES
        btnVerImagen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (contact != null && contact.getImagen() != null) {
                    viewImage();
                } else {
                    Funciones.showAlert("Debes seleccionar un contacto para poder mostrar su imagen.", ActivityContactos.this);
                }
            }

        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (contact != null && contact.getId() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityContactos.this);
                    builder.setMessage("¿Esta seguro de borrar este contacto?")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (eliminarContacto() > 0) {
                                contact = null;
                                reloadListView();
                                Toast.makeText(ActivityContactos.this, "Contacto Eliminado Correctamente.", Toast.LENGTH_SHORT).show();
                            }else {
                                Funciones.showAlert("Error al eliminar el contacto, intente nuevamente", ActivityContactos.this);
                            }
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
                }else {
                    Funciones.showAlert("Debe seleccionar un registro para eliminarlo.", ActivityContactos.this);
                }
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contact != null && contact.getId() != null) {
                    updateContact();
                } else {
                    Funciones.showAlert("Debes seleccionar un contacto para poder actualizarlo.", ActivityContactos.this);
                }
            }
        });

        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contact != null && contact.getNombre() != null && contact.getTelefono() != null) {
                    String contacto = "Nombre Contacto: "+contact.getNombre()+"\nTelefono: "+contact.getTelefono().toString();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, contacto);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, "Compartir Contacto");
                    startActivity(shareIntent);

                } else {
                    Funciones.showAlert("Debes seleccionar un contacto para poder compartilo.", ActivityContactos.this);
                }
            }
        });
    }

    private void updateContact() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("img", contact);
        Intent intent = new Intent(ActivityContactos.this, ActivityUpdateContact.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void viewImage() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("image", contact);

        Intent intent = new Intent(getApplicationContext(), ActivityViewImage.class);
        intent.putExtras(bundle);
        startActivity(intent);
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
            list_contactos.setImagen(cursor.getBlob(6));

            listaContactos.add(list_contactos);
        }

        cursor.close();;
        llenalista();
    }

    private void llenalista(){
        arregloContactos = new ArrayList<String>();
        for (int i=0; i< listaContactos.size();i++){
            arregloContactos.add(listaContactos.get(i).getNombre()+" | "+listaContactos.get(i).getTelefono());
        }
    }

    public Long eliminarContacto(){
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        long resultado = db.delete(Transacciones.tablacontactos,Transacciones.id+"=?", new String[]{contact.getId().toString()});
        db.close();
        return resultado;
    }

    public void reloadListView() {
        ObtenerListaContactos();
        adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arregloContactos);
        lista.setAdapter(adp);
    }

}