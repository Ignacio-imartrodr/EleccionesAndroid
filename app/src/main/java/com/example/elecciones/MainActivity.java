package com.example.elecciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    AsistenteBD asisBD;
    private ActivityResultLauncher<Intent> rsltLauncher;
    private EditText eTNIF;
    private EditText eTPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asisBD = new AsistenteBD(this);
        rsltLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                cambiarActivity(o);
            }
        });
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        eTNIF = findViewById(R.id.eTNIF);
        eTPassword = findViewById(R.id.eTPassword);
        Button btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(this);
        Button btnResultados = findViewById(R.id.btnResultados);
        btnResultados.setOnClickListener(this);
        Button btnReset = findViewById(R.id.btnResetTablas);
        btnReset.setOnClickListener(this);
    }

    private void cambiarActivity(ActivityResult o) {
        if (o.getData() != null) {
            String activity = o.getData().getStringExtra("activity");
            if (activity == null){
                return;
            }
            if (activity.equals("VOTAR")) {
                if (o.getResultCode() == RESULT_OK) {
                    Toast.makeText(this, "Gracias por votar", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error votando, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }
            }
            if (activity.equals("RESULTADOS")){
                Toast.makeText(this, "Vuelva a entrar para mostrar resultados más actualizados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        String NIF = eTNIF.getText().toString();
        String pswrd = eTPassword.getText().toString();
        boolean isValid = false;
        Usuario us = null;
        if (!NIF.isEmpty() && !pswrd.isEmpty()) {
            //TODO: validar estructura NIF
            us = asisBD.findUsuarioByNIF(NIF);
            if (us != null && us.getPassword().equals(pswrd)) {
                isValid = true;
            }
        } else {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.btnLogIn) {
            if (isValid) {
                if (us.haVotado()) {
                    Toast.makeText(this, "Este usuario ya ha votado y no puede repetir", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, VotarActivity.class);
                    intent.putExtra("NIF", NIF);
                    rsltLauncher.launch(intent);
                }
            } else {
                Toast.makeText(this, "Revise el usuario y la contraseña", Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.btnResultados){
            if(isValid && us.haVotado()) {
                Intent intent = new Intent(this, ResultadosActivity.class);
                rsltLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Debe registrarse y votar antes de ver como van los resultados", Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.btnResetTablas){
            if (NIF.equals("Ignacio") && pswrd.equals("abc123.")){
                if (asisBD.resetAllTablas()) {
                    Toast.makeText(this, "Tablas reseteadas", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error reseteando tablas", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No tienes permisos para resetear las tabla", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
