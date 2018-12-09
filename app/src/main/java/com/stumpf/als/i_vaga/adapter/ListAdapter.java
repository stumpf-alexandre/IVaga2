package com.stumpf.als.i_vaga.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.activity.CallMapsActivity;
import com.stumpf.als.i_vaga.classes.Garage;
import java.util.ArrayList;
import java.util.List;
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Garage> garList;
    private Context context;
    private DatabaseReference reference;
    private List<Garage> garages;
    private Garage gar;
    private TextView txt_address;
    public ListAdapter(List<Garage> lg, Context ccc){
        context = ccc;
        garList = lg;
    }
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_list, viewGroup, false);
        return new ListAdapter.ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        final Garage item = garList.get(position);
        garages = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("garagens").orderByChild("keyGaragem").equalTo(item.getKeyGaragem()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                garages.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    gar = postSnapshot.getValue(Garage.class);
                    garages.add(gar);
                    txt_address.setText(gar.getRua()
                            + ", " + gar.getNumero()
                            + ", " + gar.getBairro()
                            + ", " + gar.getCidade());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        holder.listRua.setText(context.getString(R.string.hint_rua) + " " + item.getRua());
        holder.listNum.setText(context.getString(R.string.numero) + " " + String.valueOf(item.getNumero()));
        holder.listBairro.setText(context.getString(R.string.hint_bairro) + " " + item.getBairro());
        if (item.getComplemento().equals("")) {
            holder.listComp.setText("");
        }
        else {
            holder.listComp.setText(context.getString(R.string.hint_complemento) + " " + item.getComplemento());
        }
        holder.listCidade.setText(context.getString(R.string.hint_cidade) + " " + item.getCidade());
        holder.listValor.setText(context.getString(R.string.valor) + " " + String.valueOf(item.getValor()));
        holder.linearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?q=" + txt_address.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }
    @Override
    public int getItemCount() {
        return garList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected AppCompatTextView listRua;
        protected AppCompatTextView listNum;
        protected AppCompatTextView listBairro;
        protected AppCompatTextView listComp;
        protected AppCompatTextView listCidade;
        protected AppCompatTextView listValor;
        protected LinearLayoutCompat linearList;
        public ViewHolder(View itemView) {
            super(itemView);
            listRua = itemView.findViewById(R.id.listRua);
            listNum = itemView.findViewById(R.id.listNum);
            listBairro = itemView.findViewById(R.id.listBairro);
            listComp = itemView.findViewById(R.id.listComp);
            listCidade = itemView.findViewById(R.id.listCidade);
            listValor = itemView.findViewById(R.id.listValor);
            linearList = itemView.findViewById(R.id.list);
        }
    }
}