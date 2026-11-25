package com.example.elecciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VotarActivity extends AppCompatActivity /*implements View.OnClickListener*/ {
    private AsistenteBD asisBD;
    //private TextView lblVotosRestantes;
    private Spinner spCandidatos;

    private Set<Candidato> votados;
    private Usuario us;
    private Candidato candidato;
    //private String votosRestantes;

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
        /*votosRestantes = getString(R.string.StringLblVotosRestantes) + " " + Usuario.NUMERO_VOTOS;
        lblVotosRestantes = findViewById(R.id.lblVotosRestantes);
        lblVotosRestantes.setText(votosRestantes);*/

        BotonLimitado miBoton = BotonLimitado.newInstance(getString(R.string.StringVotar), Usuario.NUMERO_VOTOS, true);
        miBoton.setListener(new BotonLimitado.ListenerBtnLim() {
            @Override
            public boolean isValidClick(View v) {
                return validarClick(v);
            }
            @Override
            public void onInvalidClick(View v) {
                actionInvalidClick(v);
            }
            @Override
            public void onValidClick(View v) {
                actionClick(v);
            }
            @Override
            public void onLastClick(View v) {
                guardarYSalir(v);
            }
        });
        //Button btnVotar = findViewById(R.id.btnVotar);
        //btnVotar.setOnClickListener(this);

        spCandidatos = findViewById(R.id.spCandidatos);

        List<Candidato> candidatos = asisBD.getCandidatos();
        candidatos.add(0, new Candidato(-1, "-- Selecciona un candidato --", null));

        ArrayAdapter<Candidato> adapter = new ArrayAdapter<Candidato>(this, android.R.layout.simple_spinner_item, candidatos) {
            @Override
            public View getDropDownView(int position, View convertView, @NonNull android.view.ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    // Oculta la opción "-- Selecciona un candidato --" en el menú desplegable
                    v.setVisibility(View.GONE);
                    v.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0));
                } else {
                    v.setVisibility(View.VISIBLE);
                    v.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCandidatos.setAdapter(adapter);

        votados = new LinkedHashSet<>(Usuario.NUMERO_VOTOS);
    }

    /*@Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnVotar) {
            Candidato candidato = (Candidato) spCandidatos.getSelectedItem();
            if (candidato == null || candidato.getId() == -1) {
                Toast.makeText(getApplicationContext(), "Selecciona algún candidato", Toast.LENGTH_SHORT).show();
            } else {
                if (addVoto(candidato)) {
                    ArrayAdapter<Candidato> adapter = (ArrayAdapter<Candidato>) spCandidatos.getAdapter();
                    if (adapter != null) {
                        adapter.remove(candidato); // Quita el candidato del adaptador
                        adapter.notifyDataSetChanged(); // Actualiza la lista visible
                    }
                    spCandidatos.setSelection(0);
                    Toast.makeText(this, "Voto añadido", Toast.LENGTH_SHORT).show();
                    votosRestantes = getString(R.string.StringLblVotosRestantes) + " " + (Usuario.NUMERO_VOTOS - votados.size());
                    lblVotosRestantes.setText(votosRestantes);
                    if (votados.size() == Usuario.NUMERO_VOTOS) {
                        us.setHaVotado(true);
                        if (actualizarBD()) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, "Error actualizando la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Selecciona un candidato que no hayas votado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/
    /*private boolean addVoto(Candidato candidato) {
        if (votados.size() != Usuario.NUMERO_VOTOS && votados.add(candidato)) {
            candidato.addVoto();
            return true;
        }
        return false;
    }*/
    private boolean validarClick(View v) {
        if (v.getId() == R.id.frmBtnLimitado){
            candidato = (Candidato) spCandidatos.getSelectedItem();
            return candidato != null && candidato.getId() != -1 && !votados.contains(candidato);
        }
        return false;
    }
    private void actionInvalidClick(View v){
        if (v.getId() == R.id.frmBtnLimitado){
            Toast.makeText(this, "Selecciona algún candidato", Toast.LENGTH_SHORT).show();
        }
    }
    private void actionClick(View v) {
        if (v.getId() == R.id.frmBtnLimitado){
            candidato = (Candidato) spCandidatos.getSelectedItem();
            if (candidato == null) return;
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
    private void guardarYSalir(View v){

        if (v.getId() == R.id.frmBtnLimitado){
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
