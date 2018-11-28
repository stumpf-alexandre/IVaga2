package com.stumpf.als.i_vaga.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.Garage;

import java.util.ArrayList;

public class GarageAdapter extends ArrayAdapter<Garage> {
    private ArrayList<Garage> garagens;
    private Context c;
    public GarageAdapter(Context c, ArrayList<Garage> object) {
        super(c, 0, object);
        this.c = c;
        this.garagens = object;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (garagens != null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.detail_list_garage, parent, false);
            AppCompatTextView txtRua = view.findViewById(R.id.ruaLista);
            AppCompatTextView txtNumero = view.findViewById(R.id.numeroList);
            AppCompatTextView txtComplemento = view.findViewById(R.id.complementoLista);
            AppCompatTextView txtBairro = view.findViewById(R.id.bairroLista);
            AppCompatTextView txtValor = view.findViewById(R.id.valorLista);
            Garage garagem = garagens.get(position);
            txtRua.setText(garagem.getRua());
            txtNumero.setText(String.valueOf(garagem.getNumero()));
            txtComplemento.setText(garagem.getComplemento());
            txtBairro.setText(garagem.getBairro());
            txtValor.setText(String.valueOf(garagem.getValor()));
        }
        return view;
    }
    @Override
    public int getCount() {
        return garagens.size();
    }
}