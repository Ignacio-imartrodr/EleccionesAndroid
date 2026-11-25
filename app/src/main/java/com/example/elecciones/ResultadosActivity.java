package com.example.elecciones;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class ResultadosActivity extends AppCompatActivity {
    private AsistenteBD asisBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asisBD = new AsistenteBD(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultados);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        List<Candidato> candidatos = asisBD.getCandidatos();
        ListView listaCandidatos = findViewById(R.id.listaCandidatos);
        AdaptadorCandidato adapter = new AdaptadorCandidato(this, candidatos, true);
        listaCandidatos.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        getIntent().putExtra("activity", "RESULTADOS");
        setResult(RESULT_OK);
    }
}