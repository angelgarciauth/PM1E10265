package com.example.pm1e10265.configuraciones;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pm1e10265.MainActivity;

public class Funciones {

    public static void showAlert(String respuesta, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mensaje Advertencia");
        builder.setMessage(respuesta);
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
