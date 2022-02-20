package com.example.pm1e10265;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pm1e10265.configuraciones.Funciones;
import com.example.pm1e10265.configuraciones.SQLiteConexion;
import com.example.pm1e10265.configuraciones.Transacciones;
import com.example.pm1e10265.tablas.Contactos;
import com.example.pm1e10265.tablas.Pais;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityUpdateContact extends AppCompatActivity {

    EditText txtNombre, txtTelefono, txtNota, txtId;
    Spinner spPais;
    Button btnSelectImage, btnUpdateContact;
    ImageView viewImgCurrent, viewImgNew;

    ArrayList<String> lista_pais;
    ArrayList<Pais> lista;
    SQLiteConexion conexion;
    ArrayAdapter<CharSequence> adp;

    static final int PETICION_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;
    String currentPhotoPath;
    Bitmap imgContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);

        conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

        txtNombre = (EditText) findViewById(R.id.txtNameEdit);
        txtTelefono = (EditText) findViewById(R.id.txtPhoneEdit);
        txtNota = (EditText) findViewById(R.id.txtNotaEdit);
        txtId = (EditText) findViewById(R.id.txtId);
        spPais = (Spinner) findViewById(R.id.spCountryEdit);

        btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        btnUpdateContact = (Button) findViewById(R.id.btnUpdateContact);

        viewImgCurrent = (ImageView) findViewById(R.id.viewImgCurrent);
        viewImgNew = (ImageView) findViewById(R.id.viewImgNew);

        reloadCombo();

        Bundle dataSent = getIntent().getExtras();
        Contactos contact = null;

        if (dataSent != null) {
            contact = (Contactos) dataSent.getSerializable("img");
            viewImage(contact.getImagen());
            txtNombre.setText(contact.getNombre());
            txtNombre.setText(contact.getNombre());
            txtTelefono.setText(contact.getTelefono().toString());
            txtNota.setText(contact.getNota());
            txtId.setText(contact.getId().toString());
            spPais.setSelection(adp.getPosition(contact.getPais()));

        } else {
            Toast.makeText(this, "Occurrio un error inesperado al mostrar la imagen, intentelo de nuevo.", Toast.LENGTH_LONG).show();
        }

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermits();
            }
        });

        btnUpdateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = validateInput();

                if (!response.equals("OK")) {
                    Funciones.showAlert(response, ActivityUpdateContact.this);
                } else {
                    if (updateContact() > 0) {
                        clean();
                        Toast.makeText(ActivityUpdateContact.this, "Contacto actualizado correctamente", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ActivityUpdateContact.this, ActivityContactos.class);
                        startActivity(intent);
                    } else {
                        Funciones.showAlert("No se pudo actualizar el contacto", ActivityUpdateContact.this);
                    }
                }

            }
        });
    }

    private Long updateContact() {
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Transacciones.id, txtId.getText().toString());
        values.put(Transacciones.nombre, txtNombre.getText().toString());
        values.put(Transacciones.telefono, txtTelefono.getText().toString());
        values.put(Transacciones.nota, txtNota.getText().toString());
        values.put(Transacciones.pathImage, currentPhotoPath);
        values.put(Transacciones.pais, spPais.getSelectedItem().toString());

        ByteArrayOutputStream bay = new ByteArrayOutputStream(10480);
        imgContacto.compress(Bitmap.CompressFormat.JPEG, 0 , bay);
        byte[] bl = bay.toByteArray();
        values.put(Transacciones.imagen, bl);

        Long resultado = db.replace(Transacciones.tablacontactos, Transacciones.id, values);
        db.close();
        return resultado;
    }

    private void getPermits() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PETICION_CAM);
        }else{
            dispatchTakePictureIntent();
        }
    }

    private void viewImage(byte[] img) {
        Bitmap bitmap = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(img);
        bitmap = BitmapFactory.decodeStream(bais);

        viewImgCurrent.setImageBitmap(bitmap);
    }

    public void reloadCombo() {
        obtenerListaPais();
        adp = new ArrayAdapter(this,android.R.layout.simple_spinner_item,lista_pais);
        spPais.setAdapter(adp);
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

    private void fillCombo(){
        lista_pais = new ArrayList<String>();
        for (int i = 0; i < lista.size(); i++) {
            lista_pais.add(lista.get(i).getNombrePais()+" ("+
                    lista.get(i).getCodigoPais()+")");
        }
    }

    private void clean(){
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
    }

    private String validateInput(){
        String respuesta = "OK";

        if(viewImgNew.getDrawable() == null){
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
            viewImgNew.setImageBitmap(image);
        }
    }
}