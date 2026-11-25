package com.example.elecciones;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AdaptadorCandidato extends ArrayAdapter<Candidato> {
    private final boolean esResultados;

    public AdaptadorCandidato(Context context, List<Candidato> candidatos) {
        this(context, candidatos, false);
    }

    public AdaptadorCandidato(Context context, List<Candidato> candidatos, boolean esResultados) {
        super(context, android.R.layout.simple_spinner_item, candidatos);
        this.esResultados = esResultados;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Candidato candidato = getItem(position);

        if (candidato != null) {
            TextView textView = view.findViewById(android.R.id.text1);
            if (textView != null) {
                if (esResultados) {
                    // Permitir que el texto salte de línea si es largo para evitar que se corte
                    textView.setSingleLine(false);
                    textView.setMaxLines(2);
                    String info = candidato.toString() + " - Votos: " + candidato.getNumVotos();
                    textView.setText(info);
                } else {
                    // En el Spinner solo mostramos las siglas del partido
                    textView.setText(obtenerTextoSpinner(candidato));
                }
            }

            int colorFondo = getColorFondo(candidato);
            view.setBackgroundColor(colorFondo);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        Candidato candidato = getItem(position);

        if (candidato != null) {
            if (position == 0 && candidato.getId() == -1) {
                view.setVisibility(View.GONE);
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            } else {
                view.setVisibility(View.VISIBLE);
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                TextView textView = view.findViewById(android.R.id.text1);
                if (textView != null) {
                    textView.setText(obtenerTextoSpinner(candidato));
                }

                int colorFondo = getColorFondo(candidato);
                view.setBackgroundColor(colorFondo);
            }
        }

        return view;
    }

    private String obtenerTextoSpinner(Candidato candidato) {
        if (candidato == null) return "";
        if (candidato.getPartido() != null && candidato.getPartido().getSiglas() != null) {
            return candidato.getNombre() + " (" + candidato.getPartido().getSiglas() + ")";
        }
        return candidato.getNombre();
    }

    private int getColorFondo(Candidato candidato) {
        if (candidato == null || candidato.getId() == -1 || candidato.getPartido() == null || candidato.getPartido().getColor() == null) {
            return Color.LTGRAY;
        }
        return candidato.getPartido().getColor().getColorInt();
    }
}
