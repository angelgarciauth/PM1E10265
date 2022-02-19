package com.example.pm1e10265;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pm1e10265.tablas.Contactos;

import java.io.ByteArrayInputStream;

public class ActivityViewImage extends AppCompatActivity {

    ImageView viewImgContact;
    TextView txtNameContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        viewImgContact = (ImageView) findViewById(R.id.imgViewContact);
        txtNameContact = (TextView) findViewById(R.id.txtNameContact);

        Bundle dataSent = getIntent().getExtras();
        Contactos contact = null;

        if (dataSent != null) {
            contact = (Contactos) dataSent.getSerializable("image");
            viewImage(contact.getImagen());
            txtNameContact.setText("Nombre Contacto: "+contact.getNombre());
        } else {
            Toast.makeText(this, "Occurrio un error inesperado al mostrar la imagen, intentelo de nuevo.", Toast.LENGTH_LONG).show();
        }

    }

    private void viewImage(byte[] img) {
        Bitmap bitmap = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(img);
        bitmap = BitmapFactory.decodeStream(bais);

        viewImgContact.setImageBitmap(bitmap);
    }
}