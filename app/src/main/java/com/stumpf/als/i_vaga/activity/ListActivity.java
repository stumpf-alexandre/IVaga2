package com.stumpf.als.i_vaga.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private ArrayList<Garage> garagens = new ArrayList<Garage>();
    private Garage garagem;
    private String emailLogado;
    private AppCompatTextView txt_pesq;
    private AppCompatImageButton btn_pesq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        emailLogado = autenticacao.getCurrentUser().getEmail();
        reference = ConfigurationFirebase.getFirebase();
        txt_pesq = findViewById(R.id.txtPesq);
        btn_pesq = findViewById(R.id.btnPesq);
        list = findViewById(R.id.listPesq);
        if (Services.checkInternet(this)) {
            btn_pesq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventEdit();
                }
            });
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void eventEdit() {
        txt_pesq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String txt = txt_pesq.getText().toString();
                searchWord(txt);
            }
        });
    }
    private void searchWord(String txt) {
        reference = FirebaseDatabase.getInstance().getReference();
        Query query;
        if (txt.equals("")){
            query = reference.child("usuarios/").child("garagens").orderByChild("cidade");
            garagens.clear();
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        Garage gar = postSnapshot.getValue(Garage.class);
                        garagens.add(gar);
                    }
                    adapterGarageList = new ArrayAdapter<Garage>(ListActivity.this, android.R.layout.simple_list_item_1, garagens);
                    list.setAdapter(adapterGarageList);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else {
            query = reference.child("usuarios/").child("garagens").orderByChild("cidade").startAt(txt).endAt(txt + "uf8ff");
            garagens.clear();
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        Garage gar = postSnapshot.getValue(Garage.class);
                        garagens.add(gar);
                    }
                    adapterGarageList = new ArrayAdapter<Garage>(ListActivity.this, android.R.layout.simple_list_item_1, garagens);
                    list.setAdapter(adapterGarageList);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        searchWord("");
    }
}
