package com.stumpf.als.i_vaga.activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.Car;
import com.stumpf.als.i_vaga.helper.Services;
public class EditCarActivity extends AppCompatActivity {
    private TextInputEditText placa;
    private CardView btnEditCarro;
    private CardView btnDeletCarro;
    private String txtOrigem = "";
    private String txtPlaca = "";
    private String txtKeyCar = "";
    private String txtForeignKeyUser = "";
    private ContentLoadingProgressBar progressBar;
    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private Car carro;
    private String emailLogado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        placa = findViewById(R.id.placaRegisterCarro);
        btnEditCarro = findViewById(R.id.editCarro);
        btnDeletCarro = findViewById(R.id.deletCarro);
        progressBar = findViewById(R.id.progress_bar_register_car);
        btnDeletCarro.setVisibility(View.VISIBLE);
        btnDeletCarro.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        emailLogado = autenticacao.getCurrentUser().getEmail();
        if (Services.checkInternet(this)) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            txtOrigem = bundle.getString("origem");
            if (txtOrigem.equals("editarDadosCarro")) {
                txtPlaca = bundle.getString("placa");
                txtKeyCar = bundle.getString("keyCar");
                txtForeignKeyUser = bundle.getString("foreignKeyUser");
                placa.setText(txtPlaca);
                btnEditCarro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!placa.getText().toString().equals("")) {
                            progressBar.setVisibility(View.VISIBLE);
                            btnEditCarro.setVisibility(View.GONE);
                            btnDeletCarro.setVisibility(View.GONE);
                            carro = new Car();
                            carro.setPlaca(placa.getText().toString());
                            carro.setKeyCar(txtKeyCar);
                            carro.setForeignKeyUser(txtForeignKeyUser);
                            atualizarDados(carro);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnEditCarro.setVisibility(View.VISIBLE);
                            btnDeletCarro.setVisibility(View.VISIBLE);
                            Toast.makeText(EditCarActivity.this, getString(R.string.placa_vazio), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                btnDeletCarro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abrirDialogDelete();
                    }
                });
            }
        }
        else {
            btnEditCarro.setVisibility(View.VISIBLE);
            btnDeletCarro.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            finish();
            emptyEditText(placa);
        }


    }
    private boolean atualizarDados(final Car carro){
        btnEditCarro.setEnabled(false);
        String erro = "";
        try {
            reference = ConfigurationFirebase.getFirebase().child("usuarios/").child("carros");
            reference.child(txtForeignKeyUser).setValue(carro);
            Toast.makeText(this, getString(R.string.edit_dados) + erro, Toast.LENGTH_LONG).show();
            abreUser();
        }
        catch (Exception e) {
            btnEditCarro.setVisibility(View.VISIBLE);
            btnDeletCarro.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            erro = getString(R.string.erro_cadastro);
            e.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.erro) + erro, Toast.LENGTH_LONG).show();
        return true;
    }
    private void removerDadosCarro(){
        reference = ConfigurationFirebase.getFirebase();
        reference.child("usuarios/").child("carros").orderByChild("keyCar").equalTo(txtKeyCar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    final Car car = postSnapshot.getValue(Car.class);
                    reference = ConfigurationFirebase.getFirebase();
                    reference.child("usuarios/").child("carros").child(car.getKeyCar()).removeValue();
                    abreUser();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void abrirDialogDelete(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_delete);
        final CardView btnSim = dialog.findViewById(R.id.sim);
        final CardView btnNao = dialog.findViewById(R.id.nao);
        btnSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removerDadosCarro();
                dialog.dismiss();
            }
        });
        btnNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abreUser();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void abreUser(){
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
    private void emptyEditText(TextInputEditText text1) {
        text1.setText(null);
    }
}