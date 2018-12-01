package com.stumpf.als.i_vaga.adapter;
import android.content.Context;
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
        reference.child("carros").orderByChild("keyCarro").equalTo(item.getKeyCar()).addValueEventListener(new ValueEventListener() {
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
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return carroList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected AppCompatTextView txtPlacaCarro;
        protected LinearLayoutCompat linearLayout;
        public ViewHolder (View itemView) {
            super(itemView);
            txtPlacaCarro = itemView.findViewById(R.id.placaListaCarro);
            linearLayout = itemView.findViewById(R.id.listaCar);
        }
    }
}