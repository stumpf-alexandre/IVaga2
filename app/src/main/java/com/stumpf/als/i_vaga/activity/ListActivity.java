package com.stumpf.als.i_vaga.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.adapter.GarageAdapter;
import com.stumpf.als.i_vaga.classes.Car;
import com.stumpf.als.i_vaga.classes.Garage;
import com.stumpf.als.i_vaga.helper.Services;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private ListView list;
    private ArrayAdapter<Garage> adapterGarageList;
    ValueEventListener valueEventListener;
    private ArrayList<Garage> garagens;
    private Garage garagem;
    private String citi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        reference = ConfigurationFirebase.getFirebase();
        if (Services.checkInternet(this)) {
            carregarTodaList();
        }
        else {
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
        }
    }
    private void carregarTodaList(){
        garagens = new ArrayList<>();
        adapterGarageList = new GarageAdapter(this, garagens);
        list.setAdapter(adapterGarageList);
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("usuarios/").child("garagens").orderByChild("cidade").equalTo(citi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                garagens.clear();
                for (DataSnapshot postSnapshotGar : dataSnapshot.getChildren()){
                    String gar = reference.push().getKey();
                    garagem = postSnapshotGar.getValue(Garage.class);
                    garagens.add(garagem);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
