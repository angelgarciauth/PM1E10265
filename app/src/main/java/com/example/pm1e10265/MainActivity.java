package com.example.pm1e10265;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaDataSource;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pm1e10265.configuraciones.Funciones;
import com.example.pm1e10265.configuraciones.SQLiteConexion;
import com.example.pm1e10265.configuraciones.Transacciones;
import com.example.pm1e10265.tablas.Pais;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btnGuardar,btnContactos,btnAddPais,btnFoto;
    EditText txtNombre,txtTelefono,txtNota;
    Spinner spPais;
    ArrayList<String> lista_pais;
    ArrayList<Pais> lista;
    SQLiteConexion conexion;
    ArrayAdapter<CharSequence> adp;

    static final int PETICION_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;
    String currentPhotoPath;
    Bitmap imgContacto;
    ImageView ObjImagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnContactos = (Button) findViewById(R.id.btnContactos);
        btnAddPais = (Button) findViewById(R.id.btnAddPais);
        btnFoto = (Button)findViewById(R.id.btnFoto);

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtTelefono = (EditText) findViewById(R.id.txtTelefono);
        txtNota = (EditText) findViewById(R.id.txtNota);

        spPais = (Spinner) findViewById(R.id.spPais);

        ObjImagen = (ImageView)findViewById(R.id.imgFoto);
        recargarCombo();

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

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

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String respuesta = validar();

                if(!respuesta.equals("OK")){
                    Funciones.showAlert(respuesta, MainActivity.this);
                }else{
                    guardarContacto();
                    limpiar();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        recargarCombo();
    }

    public void obtenerListaPais(){
        Pais pais = null;
        lista = new ArrayList<Pais>();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ Transacciones.tablapais,null);

        while(cursor.moveToNext()){
            pais = new Pais();

            pais.setIdP(cursor.getInt(0));
            pais.setNombrePais(cursor.getString(1));
            pais.setCodigoPais(cursor.getInt(2));
            lista.add(pais);
        }
        cursor.close();

        fillCombo();
    }

    private void permisos(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PETICION_CAM);
        }else{
            dispatchTakePictureIntent();
        }
    }

    private void fillCombo(){
        lista_pais = new ArrayList<String>();

        for (int i = 0; i < lista.size(); i++) {
            lista_pais.add(lista.get(i).getNombrePais()+" ("+
                    lista.get(i).getCodigoPais()+")");
        }

    }

    private void guardarContacto(){
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Transacciones.pais,spPais.getSelectedItem().toString());
        values.put(Transacciones.nombre, txtNombre.getText().toString());
        values.put(Transacciones.telefono, txtTelefono.getText().toString());
        values.put(Transacciones.nota, txtNota.getText().toString());
        values.put(Transacciones.pathImage, currentPhotoPath);


        ByteArrayOutputStream bay = new ByteArrayOutputStream(10480);

        imgContacto.compress(Bitmap.CompressFormat.JPEG, 0 , bay);

        byte[] bl = bay.toByteArray();

        values.put(Transacciones.imagen, bl);

        Long result = db.insert(Transacciones.tablacontactos, Transacciones.id, values);

        Toast.makeText(getApplicationContext(), "Contacto creado exitosamente "
                ,Toast.LENGTH_LONG).show();

        db.close();
    }

    private void limpiar(){
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_CAM){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Se necesitan permisos a la camara", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_PIC_REQUEST && resultCode == RESULT_OK){

            Bitmap image = BitmapFactory.decodeFile(currentPhotoPath);

            imgContacto = image;
            ObjImagen.setImageBitmap(image);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.toString();
            }
            // Continue only if the File was successfully created
            try {
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.pm1e10265.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    startActivityForResult(takePictureIntent, TAKE_PIC_REQUEST);
                }
            }catch (Exception e){
                Log.i("Error", "dispatchTakePictureIntent: " + e.toString());
            }
        }
    }

    public void recargarCombo(){
        obtenerListaPais();
        adp = new ArrayAdapter(this,android.R.layout.simple_spinner_item,lista_pais);
        spPais.setAdapter(adp);
    }

    private String validar(){
        String respuesta = "OK";

        if(ObjImagen.getDrawable() == null){
            respuesta = "Por favor ingresar una imagen de contacto";
        }else if(txtNombre.getText().length() == 0){
            respuesta = "Por favor ingresar el nombre";
        }else if(txtTelefono.getText().length() == 0){
            respuesta = "Por favor ingresar el numero de Telefono";
        }else if(txtNota.getText().length() == 0){
            respuesta = "Por favor ingresar una nota";
        }
        return respuesta;
    }
}