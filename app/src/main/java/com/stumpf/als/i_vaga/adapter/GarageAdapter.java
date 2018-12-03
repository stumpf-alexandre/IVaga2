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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.activity.EditCarActivity;
import com.stumpf.als.i_vaga.activity.EditGarageActivity;
import com.stumpf.als.i_vaga.classes.Garage;
import java.util.ArrayList;
import java.util.List;
public class GarageAdapter extends RecyclerView.Adapter<GarageAdapter.ViewHolder> {
    private List<Garage> garagemList;
    private Context context;
    private DatabaseReference reference;
    private FirebaseAuth autenticacao ;
    private List<Garage> garagens;
    private Garage garagem;
    private String emailLogado;

    public GarageAdapter(List<Garage> l, Context c) {
        context = c;
        garagemList = l;
    }
    @Override
    public GarageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_list_garage, viewGroup, false);
        return new GarageAdapter.ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final GarageAdapter.ViewHolder holder, int position) {
        final Garage item = garagemList.get(position);
        garagens = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("garagens").orderByChild("keyGaragem").equalTo(item.getKeyGaragem()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                garagens.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    garagem = postSnapshot.getValue(Garage.class);
                    garagens.add(garagem);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        holder.txtRua.setText(context.getString(R.string.hint_rua) + " " + item.getRua());
        holder.txtNumero.setText(context.getString(R.string.hint_numero) + " " + String.valueOf(item.getNumero()));
        holder.txtBairro.setText(context.getString(R.string.hint_bairro) + " " + item.getBairro());
        if (item.getComplemento().equals("")) {
            holder.txtComplementoGaragem.setText("");
        } else {
            holder.txtComplementoGaragem.setText((context.getString(R.string.hint_complemento)) + " " + item.getComplemento());
        }
        holder.txtValorGaragem.setText((context.getString(R.string.diaria)) + " " + (context.getString(R.string.dinheiro)) + String.valueOf(item.getValor()));
        if (item.getGaragem()) {
            holder.txtMenssagem.setText(context.getString(R.string.on));
        } else {
            holder.txtMenssagem.setText(context.getString(R.string.off));
        }
        holder.txtRua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditGarageActivity.class);
                intent.putExtra("rua", item.getRua());
                intent.putExtra("numero", item.getNumero());
                intent.putExtra("complemento", item.getComplemento());
                intent.putExtra("bairro", item.getBairro());
                intent.putExtra("cidade", item.getCidade());
                intent.putExtra("valor", item.getValor());
                intent.putExtra("garagem", item.getGaragem());
                intent.putExtra("keyGaragem", item.getKeyGaragem());
                intent.putExtra("foreingnKeyUser", item.getForeingnKeyUser());
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }
    @Override
    public int getItemCount() {
        return garagemList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected AppCompatTextView txtRua;
        protected AppCompatTextView txtNumero;
        protected AppCompatTextView txtBairro;
        protected AppCompatTextView txtComplementoGaragem;
        protected AppCompatTextView txtValorGaragem;
        protected AppCompatTextView txtMenssagem;
        protected LinearLayoutCompat linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            txtRua = itemView.findViewById(R.id.ruaLista);
            txtNumero = itemView.findViewById(R.id.numeroList);
            txtBairro = itemView.findViewById(R.id.bairroLista);
            txtComplementoGaragem = itemView.findViewById(R.id.complementoLista);
            txtValorGaragem = itemView.findViewById(R.id.valorLista);
            txtMenssagem = itemView.findViewById(R.id.txtGaragem);
            linearLayout = itemView.findViewById(R.id.listaGaragem);
        }
    }
}