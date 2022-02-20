package com.example.pm1e10265;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pm1e10265.configuraciones.Funciones;
import com.example.pm1e10265.configuraciones.SQLiteConexion;
import com.example.pm1e10265.configuraciones.Transacciones;
import com.example.pm1e10265.tablas.Contactos;

import java.util.ArrayList;
import java.util.Arrays;

public class ActivityContactos extends AppCompatActivity {
    Button  btnCompartir, btnActualizar, btnEliminar, btnVerImagen;
    ImageButton btnAtras;
    ListView lista;
    ArrayList<Contactos> listaContactos;
    ArrayList<String> arregloContactos;
    SQLiteConexion conexion;
    EditText txtBuscar,txtId;
    ArrayAdapter<CharSequence> adp;

    Contactos contact = new Contactos();

    // DOBLE CLICK
    int contador = 0;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

        btnAtras = (ImageButton) findViewById(R.id.btnAtras);
        btnCompartir = (Button) findViewById(R.id.btnCompartir);
        btnActualizar = (Button) findViewById(R.id.btnActualizarContacto);
        btnEliminar = (Button) findViewById(R.id.btnEliminarContacto);
        btnVerImagen = (Button) findViewById(R.id.btnVerImagen);
        lista = (ListView) findViewById(R.id.listContactos);

        txtBuscar = (EditText)findViewById(R.id.txtBuscarContacto);

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
                finish();
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int fila, long l) {
                contact = listaContactos.get(fila);
                contador++;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(contador == 2) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityContactos.this);
                            builder.setMessage("¿Desea llamar a " +contact.getNombre())
                                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            llamar();
                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    }).show();
                        }
                        contador = 0;
                    }
                }, 500);
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

    private void llamar() {
        String[] code = contact.getPais().split("\\(");
        String codePais = "tel:+"+code[1].substring(0,3);
        String numero = codePais+""+contact.getTelefono().toString();

        Toast.makeText(this, "Numero: "+contact.getTelefono().toString(), Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(ActivityContactos.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityContactos.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(numero));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llamar();
            } else {
                Toast.makeText(this, "Permisos Denegado", Toast.LENGTH_LONG).show();
            }
        }

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
        adp = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, arregloContactos);
        lista.setAdapter(adp);
    }

}