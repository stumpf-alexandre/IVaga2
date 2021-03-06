package com.stumpf.als.i_vaga.activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.Garage;
import com.stumpf.als.i_vaga.helper.Services;
public class EditGarageActivity extends AppCompatActivity {
    private TextInputEditText editRua;
    private TextInputEditText editNumero;
    private TextInputEditText editComplemento;
    private TextInputEditText editBairro;
    private TextInputEditText editCidade;
    private TextInputEditText editValor;
    private boolean btn;
    private String txtRua = "";
    private String txtNumero = "";
    private String txtComplemento = "";
    private String txtBairro = "";
    private String txtCidade = "";
    private String txtValor = "";
    private boolean txtGaragem;
    private String txtKeyGarage = "";
    private String txtForeignKeyUser = "";
    private SwitchCompat editGaragemOnOff;
    private AppCompatTextView editTextOnOff;
    private ContentLoadingProgressBar progressBarGarage;
    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private Garage garagem;
    private String emailLogado;
    private CardView btnEditGaragem;
    private CardView btnDeletGaragem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_garage);
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        editRua = findViewById(R.id.ruaEdit);
        editNumero = findViewById(R.id.numeroEdit);
        editComplemento = findViewById(R.id.complementoEdit);
        editBairro = findViewById(R.id.bairroEdit);
        editCidade = findViewById(R.id.cidadeEdit);
        editValor = findViewById(R.id.valoEdit);
        editGaragemOnOff = findViewById(R.id.switch_on_offEdit);
        editTextOnOff = findViewById(R.id.on_offEdit);
        btnEditGaragem = findViewById(R.id.editGarage);
        btnDeletGaragem = findViewById(R.id.deletGarage);
        progressBarGarage = findViewById(R.id.progress_bar_edit_garage);
        btnEditGaragem.setVisibility(View.VISIBLE);
        btnDeletGaragem.setVisibility(View.VISIBLE);
        progressBarGarage.setVisibility(View.GONE);
        emailLogado = autenticacao.getCurrentUser().getEmail();
        if (Services.checkInternet(this)) {
            Intent intent = getIntent();
            txtRua = intent.getStringExtra("rua");
            txtNumero = String.valueOf(intent.getLongExtra("numero", 0));
            txtComplemento = intent.getStringExtra("complemento");
            txtBairro = intent.getStringExtra("bairro");
            txtCidade = intent.getStringExtra("cidade");
            txtValor = String.valueOf(intent.getDoubleExtra("valor", 0));
            txtGaragem = intent.getBooleanExtra("garagem", false);
            txtKeyGarage = intent.getStringExtra("keyGaragem");
            txtForeignKeyUser = intent.getStringExtra("foreingnKeyUser");
            editRua.setText(txtRua);
            editNumero.setText(txtNumero);
            editComplemento.setText(txtComplemento);
            editBairro.setText(txtBairro);
            editCidade.setText(txtCidade);
            editValor.setText(txtValor);
            if (txtGaragem == true) {
                editGaragemOnOff.setChecked(txtGaragem);
                editTextOnOff.setText(getString(R.string.on));
            } else {
                editGaragemOnOff.setChecked(txtGaragem);
                editTextOnOff.setText(getString(R.string.off));
            }
            btnEditGaragem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editValor.getText().toString().equals("")) {
                        progressBarGarage.setVisibility(View.VISIBLE);
                        btnEditGaragem.setVisibility(View.GONE);
                        btnDeletGaragem.setVisibility(View.GONE);
                        garagem = new Garage();
                        garagem.setRua(editRua.getText().toString());
                        garagem.setNumero(Long.parseLong(editNumero.getText().toString()));
                        if (!editComplemento.getText().toString().equals("")) {
                            garagem.setComplemento(editComplemento.getText().toString());
                        } else {
                            garagem.setComplemento("");
                        }
                        garagem.setBairro(editBairro.getText().toString());
                        garagem.setCidade(editCidade.getText().toString());
                        garagem.setValor(Double.parseDouble(editValor.getText().toString()));
                        garagem.setGaragem(btn);
                        garagem.setKeyGaragem(txtKeyGarage);
                        garagem.setForeingnKeyUser(txtForeignKeyUser);
                        atualizarDados(garagem);
                    } else {
                        progressBarGarage.setVisibility(View.GONE);
                        btnEditGaragem.setVisibility(View.VISIBLE);
                        btnDeletGaragem.setVisibility(View.VISIBLE);
                        if (editRua.getText().toString().equals("")) {
                            Toast.makeText(EditGarageActivity.this, getString(R.string.rua_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (editNumero.getText().toString().equals("")) {
                            Toast.makeText(EditGarageActivity.this, getString(R.string.numero_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (editBairro.getText().toString().equals("")) {
                            Toast.makeText(EditGarageActivity.this, getString(R.string.bairro_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (editCidade.getText().toString().equals("")) {
                            Toast.makeText(EditGarageActivity.this, getString(R.string.cidade_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (editValor.getText().toString().equals("")) {
                            Toast.makeText(EditGarageActivity.this, getString(R.string.valor_vazio), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            btnDeletGaragem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    abrirDialogDelete();
                }
            });
            editGaragemOnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editGaragemOnOff.isChecked()) {
                        btn = true;
                        editTextOnOff.setText(getString(R.string.on));
                    } else {
                        btn = false;
                        editTextOnOff.setText(getString(R.string.off));
                    }
                }
            });
        }
        else {
            btnEditGaragem.setVisibility(View.VISIBLE);
            btnDeletGaragem.setVisibility(View.VISIBLE);
            progressBarGarage.setVisibility(View.GONE);
            abreUser();
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            emptyEditText(editRua, editNumero, editComplemento, editBairro, editCidade, editValor);
        }
    }
    private void removerDadosGaragem(){
        reference = ConfigurationFirebase.getFirebase();
        reference.child("usuarios/").child("garagens").orderByChild("keyGaragem").equalTo(txtKeyGarage).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    final Garage gar = postSnapshot.getValue(Garage.class);
                    reference = ConfigurationFirebase.getFirebase();
                    reference.child("usuarios/").child("garagens").child(gar.getKeyGaragem()).removeValue();
                    abreUser();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private boolean atualizarDados(final Garage garage){
        btnEditGaragem.setEnabled(false);
        try {
            reference = ConfigurationFirebase.getFirebase().child("usuarios/").child("garagens");
            reference.child(txtKeyGarage).setValue(garage);
            Toast.makeText(this, getString(R.string.edit_dados), Toast.LENGTH_LONG).show();
            abreUser();
        }
        catch (Exception e) {
            btnEditGaragem.setVisibility(View.VISIBLE);
            btnDeletGaragem.setVisibility(View.VISIBLE);
            progressBarGarage.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.not_edit_dados), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return true;
    }
    private void abrirDialogDelete(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_delete);
        final CardView btnSim = dialog.findViewById(R.id.sim);
        final CardView btnNao = dialog.findViewById(R.id.nao);
        btnSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removerDadosGaragem();
                Toast.makeText(EditGarageActivity.this, getString(R.string.delet_dados), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        btnNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abreUser();
                Toast.makeText(EditGarageActivity.this, getString(R.string.not_edit_dados), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void abreUser(){
        finish();
        startActivity(new Intent(this, LoginActivity.class));
        emptyEditText(editRua, editNumero, editComplemento, editBairro, editCidade, editValor);
    }
    private void emptyEditText(TextInputEditText text1, TextInputEditText text2, TextInputEditText text3, TextInputEditText text4,TextInputEditText text5, TextInputEditText text6 ) {
        text1.setText(null);
        text2.setText(null);
        text3.setText(null);
        text4.setText(null);
        text5.setText(null);
        text6.setText(null);
    }
}