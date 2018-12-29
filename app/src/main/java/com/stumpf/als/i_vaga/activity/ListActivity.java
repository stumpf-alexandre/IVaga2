package com.stumpf.als.i_vaga.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.adapter.ListAdapter;
import com.stumpf.als.i_vaga.classes.Garage;
import com.stumpf.als.i_vaga.helper.Services;
import java.util.ArrayList;
import java.util.List;
public class ListActivity extends AppCompatActivity {
    private RecyclerView listRecycle;
    private DatabaseReference reference;
    private List<Garage> listGar;
    private ListAdapter arrayAdapter;
    private LinearLayoutManager linearList;
    private Garage garagem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listRecycle = findViewById(R.id.recyclViewList);
        if (Services.checkInternet(this)) {
            reference = ConfigurationFirebase.getFirebase();
            reference = FirebaseDatabase.getInstance().getReference();
            pesquisarCidade();
        }
        else {
            abrirLogin();
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
        }
    }
    private void pesquisarCidade() {
        listRecycle.setHasFixedSize(true);
        linearList = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        listRecycle.setLayoutManager(linearList);
        listGar = new ArrayList<>();
        reference.child("usuarios/").child("garagens").orderByChild("garagem").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listGar.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    garagem = postSnapshot.getValue(Garage.class);
                    listGar.add(garagem);
                }
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        arrayAdapter = new ListAdapter(listGar, this);
        listRecycle.setAdapter(arrayAdapter);
    }
    private void abrirLogin(){
        finish();
        startActivity(new Intent(this, UserActivity.class));
    }
}