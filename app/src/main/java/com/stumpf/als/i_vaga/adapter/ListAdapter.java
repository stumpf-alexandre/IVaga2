package com.stumpf.als.i_vaga.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.Garage;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Garage> {
    private ArrayList<Garage> garagens;
    private Context c;
    public ListAdapter(Context c, ArrayList<Garage> object) {
        super(c, 0, object);
        this.c = c;
        this.garagens = object;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (garagens != null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.detail_list, parent, false);
            AppCompatImageView img = view.findViewById(R.id.listImg);
            AppCompatTextView rua = view.findViewById(R.id.listRua);
            AppCompatTextView num = view.findViewById(R.id.listNum);
            AppCompatTextView comp = view.findViewById(R.id.listComp);
            AppCompatTextView bairro = view.findViewById(R.id.listBairro);
            AppCompatTextView valor = view.findViewById(R.id.valorLista);
            Garage garagem = garagens.get(position);

            rua.setText(garagem.getRua());
            num.setText(String.valueOf(garagem.getNumero()));
            comp.setText(garagem.getComplemento());
            bairro.setText(garagem.getBairro());
            valor.setText(String.valueOf(garagem.getValor()));
        }
        return view;
    }
}
