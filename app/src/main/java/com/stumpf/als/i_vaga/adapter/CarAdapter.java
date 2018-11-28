package com.stumpf.als.i_vaga.adapter;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.Car;
import java.util.ArrayList;
public class CarAdapter extends ArrayAdapter<Car> {
    private ArrayList<Car> carros;
    private Context c;
    public CarAdapter(Context c, ArrayList<Car> object){
        super(c, 0, object);
        this.c = c;
        this.carros = object;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (carros != null){
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.detail_list_car, parent, false);
            AppCompatTextView txtPlaca = view.findViewById(R.id.placaListaCarro);
            Car carro = carros.get(position);
            txtPlaca.setText(carro.getPlaca());
        }
        return view;
    }
    @Override
    public int getCount() {
        return carros.size();
    }
}