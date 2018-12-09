package com.stumpf.als.i_vaga.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.activity.EditCarActivity;
import com.stumpf.als.i_vaga.classes.Car;
import java.util.ArrayList;
import java.util.List;
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder>{
    private List<Car> carroList;
    private Context context;
    private DatabaseReference reference;
    private List<Car> carros;
    private Car carro;
    public CarAdapter (List<Car> lc, Context cc) {
        context = cc;
        carroList = lc;
    }
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_list_car, viewGroup, false);
        return new CarAdapter.ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(CarAdapter.ViewHolder holder, int position) {
        final Car item = carroList.get(position);
        carros = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("carros").orderByChild("keyCar").equalTo(item.getKeyCar()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carros.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    carro = postSnapshot.getValue(Car.class);
                    carros.add(carro);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        holder.txtPlacaCarro.setText((context.getString(R.string.hint_placa)) + " " + item.getPlaca());
        holder.linearLayoutCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditCarActivity.class);
                intent.putExtra("placa", item.getPlaca());
                intent.putExtra("keyCar", item.getKeyCar());
                intent.putExtra("foreignKeyUser", item.getForeignKeyUser());
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }
    @Override
    public int getItemCount() {
        return carroList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected AppCompatTextView txtPlacaCarro;
        protected LinearLayoutCompat linearLayoutCar;
        public ViewHolder (View itemView) {
            super(itemView);
            txtPlacaCarro = itemView.findViewById(R.id.placaListaCarro);
            linearLayoutCar = itemView.findViewById(R.id.listaCar);
        }
    }
}