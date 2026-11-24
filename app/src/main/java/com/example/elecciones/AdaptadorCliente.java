package com.example.elecciones;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorCliente extends ArrayAdapter<Candidato> {
    public AdaptadorCliente(Context context, ArrayList<Candidato> candidatos) {
        super(context, android.R.layout.simple_spinner_item, candidatos);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layoutFila = super.getView(position, convertView, parent);
        Candidato candidato = getItem(position);
        TextView lblFila = layoutFila.findViewById(android.R.id.text1);
        lblFila.setText(candidato.toString());
        return layoutFila;
    }
}
