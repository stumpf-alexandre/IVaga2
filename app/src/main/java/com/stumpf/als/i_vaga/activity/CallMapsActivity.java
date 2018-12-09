package com.stumpf.als.i_vaga.activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.helper.Services;
public class CallMapsActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private String rua = "";
    private String numero = "";
    private String bairro = "";
    private String complemento = "";
    private String cidade = "";
    private String valor = "";
    private String keyGarage = "";
    private String foreignKeyUser = "";
    AppCompatTextView nomeReserva;
    AppCompatTextView enderecoReserva;
    AppCompatTextView valorReserva;
    CardView btnMaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_maps);
        if (Services.checkInternet(this)) {
            Intent intent = getIntent();
            nomeReserva = findViewById(R.id.nomeReserva);
            enderecoReserva = findViewById(R.id.enderecoReserva);
            valorReserva = findViewById(R.id.valorReserva);
            btnMaps = findViewById(R.id.btnReserva);
            rua = intent.getStringExtra("rua");
            numero = String.valueOf(intent.getLongExtra("numero", 0));
            bairro = intent.getStringExtra("bairro");
            complemento = intent.getStringExtra("complemento");
            cidade = intent.getStringExtra("cidade");
            valor = String.valueOf(intent.getDoubleExtra("valor", 0));
            keyGarage = intent.getStringExtra("keyGaragem");
            foreignKeyUser = intent.getStringExtra("foreingnKeyUser");
            enderecoReserva.setText(rua
                    + ", " + numero
                    + ", " + bairro
                    + ", " + cidade);
            valorReserva.setText(getString(R.string.diaria) + " " + valor);
            listUser();
            openGoogleMaps();
        }
        else {
            abreUser();
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
        }
    }
    private void listUser() {
        reference = ConfigurationFirebase.getFirebase();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("usuarios").orderByChild("email").equalTo(foreignKeyUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String nome = postSnapshot.child("nome").getValue().toString();
                    String sobrenome = postSnapshot.child("sobrenome").getValue().toString();
                    nomeReserva.setText(getString(R.string.nome_proprietario) + " " + nome + " " + sobrenome);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void openGoogleMaps(){
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUri = "http://maps.google.com/maps?q=" + enderecoReserva.getText().toString();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(strUri));
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                startActivity(intent);
                finish();
            }
        });
    }
    private void abreUser(){
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
