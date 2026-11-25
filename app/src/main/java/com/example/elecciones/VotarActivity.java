package com.example.elecciones;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VotarActivity extends AppCompatActivity {
    private AsistenteBD asisBD;
    private Spinner spCandidatos;

    private Set<Candidato> votados;
    private Usuario us;
    private Candidato candidato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asisBD = new AsistenteBD(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_votar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BotonLimitado miBoton = (BotonLimitado) getSupportFragmentManager().findFragmentById(R.id.frmBtnLimitado);
        if (miBoton != null) {
            miBoton.setConfig(getString(R.string.StringVotar), Usuario.NUMERO_VOTOS, true);
            miBoton.setListener(new BotonLimitado.ListenerBtnLim() {
                @Override
                public boolean isValidClick(BotonLimitado fragment) {
                    return validarClick(fragment);
                }
                @Override
                public void onInvalidClick(BotonLimitado fragment) {
                    actionInvalidClick(fragment);
                }
                @Override
                public void onValidClick(BotonLimitado fragment) {
                    actionClick(fragment);
                }
                @Override
                public void onLastClick(BotonLimitado fragment) {
                    guardarYSalir(fragment);
                }
            });
        }

        spCandidatos = findViewById(R.id.spCandidatos);

        List<Candidato> candidatos = asisBD.getCandidatos();
        candidatos.add(0, new Candidato(-1, "-- Selecciona un candidato --", null));

        AdaptadorCandidato adapter = new AdaptadorCandidato(this, candidatos, false);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCandidatos.setAdapter(adapter);

        votados = new LinkedHashSet<>(Usuario.NUMERO_VOTOS);
    }
    private boolean validarClick(BotonLimitado fragment) {
        if (fragment.getId() == R.id.frmBtnLimitado) {
            candidato = (Candidato) spCandidatos.getSelectedItem();
            return candidato != null && candidato.getId() != -1 && !votados.contains(candidato);
        }
        return false;
    }
    private void actionInvalidClick(BotonLimitado fragment){
        if (fragment.getId() == R.id.frmBtnLimitado) {
            candidato = (Candidato) spCandidatos.getSelectedItem();
            if (candidato == null || candidato.getId() == -1) {
                Toast.makeText(this, "Selecciona algún candidato", Toast.LENGTH_SHORT).show();
            } else if (votados.contains(candidato)) {
                Toast.makeText(this, "Ya has seleccionado a este candidato", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void actionClick(BotonLimitado fragment) {
        if (fragment.getId() == R.id.frmBtnLimitado) {
            candidato = (Candidato) spCandidatos.getSelectedItem();
            if (candidato == null || candidato.getId() == -1) return;
            candidato.addVoto();
            votados.add(candidato);
            ArrayAdapter<Candidato> adapter = (ArrayAdapter<Candidato>) spCandidatos.getAdapter();
            if (adapter != null) {
                adapter.remove(candidato); // Quita el candidato del adaptador
                adapter.notifyDataSetChanged(); // Actualiza la lista visible
            }
            spCandidatos.setSelection(0);
            Toast.makeText(this, "Voto añadido", Toast.LENGTH_SHORT).show();
        }
    }
    private void guardarYSalir(BotonLimitado fragment){
        if (fragment.getId() == R.id.frmBtnLimitado) {
            us.setHaVotado(true);
            if (actualizarBD()) {
                setResult(RESULT_OK);
            } else {
                Toast.makeText(getApplicationContext(), "Error actualizando la base de datos", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }
    private boolean actualizarBD(){
        boolean hasSucceded;
        asisBD.beginTransaction();
        hasSucceded = asisBD.actualizarCandidatos(votados.toArray(new Candidato[0]));
        if (hasSucceded){
            hasSucceded = asisBD.actualizarUsuario(us);
        }
        asisBD.endTransaction(hasSucceded);
        return hasSucceded;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        intent.putExtra("activity","VOTAR");
        String NIF = intent.getStringExtra("NIF");
        us = asisBD.findUsuarioByNIF(NIF);
        if (us == null) {
            Toast.makeText(this,"Error cargando usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        asisBD.close();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        asisBD.close();
    }
}
