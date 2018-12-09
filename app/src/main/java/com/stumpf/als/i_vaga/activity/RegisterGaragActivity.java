package com.stumpf.als.i_vaga.activity;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.Garage;
import com.stumpf.als.i_vaga.helper.Services;
public class RegisterGaragActivity extends AppCompatActivity {
    private TextInputEditText txtrua;
    private TextInputEditText txtnumero;
    private TextInputEditText txtcomplemento;
    private TextInputEditText txtbairro;
    private TextInputEditText txtcidade;
    private TextInputEditText txtvalor;
    private SwitchCompat garagemOnOff;
    private AppCompatTextView txtOnOff;
    private boolean btn;
    private String txt;
    private ContentLoadingProgressBar progressBarGarage;
    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private Garage garagem;
    private String emailLogado;
    private CardView btnCadastrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_garag);
        //garagemOnOff.setChecked(garagem.getGaragem());
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        txtrua = findViewById(R.id.ruaRegisterGarage);
        txtnumero = findViewById(R.id.numeroRegisterGarage);
        txtcomplemento = findViewById(R.id.complementoRegisterGarage);
        txtbairro = findViewById(R.id.bairroRegisterGarage);
        txtcidade = findViewById(R.id.cidadeRegisterGarage);
        txtvalor = findViewById(R.id.valoRegisterGarage);
        garagemOnOff = findViewById(R.id.switch_on_offRegisterGarage);
        txtOnOff = findViewById(R.id.on_offRegisterGarage);
        btnCadastrar = findViewById(R.id.btnRegisterGaragem);
        progressBarGarage = findViewById(R.id.progres_bar_register_garagem);
        btnCadastrar.setVisibility(View.VISIBLE);
        progressBarGarage.setVisibility(View.GONE);
        emailLogado = autenticacao.getCurrentUser().getEmail();
        if (Services.checkInternet(this)) {
            if (btn){
                txtOnOff.setText(getString(R.string.on));
            }
            else {
                txtOnOff.setText(getString(R.string.off));
            }
            btnCadastrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!txtrua.getText().toString().equals("") && !txtnumero.getText().toString().equals("") && !txtbairro.getText().toString().equals("") && !txtcidade.getText().toString().equals("") && !txtvalor.getText().toString().equals("")) {
                        progressBarGarage.setVisibility(View.VISIBLE);
                        btnCadastrar.setVisibility(View.GONE);
                        garagem = new Garage();
                        garagemOnOff.setChecked(garagem.getGaragem());
                        garagem.setRua(txtrua.getText().toString());
                        garagem.setNumero(Long.parseLong(txtnumero.getText().toString()));
                        if (!txtcomplemento.getText().toString().equals("")) {
                            garagem.setComplemento(txtcomplemento.getText().toString());
                        } else {
                            garagem.setComplemento("");
                        }
                        garagem.setBairro(txtbairro.getText().toString());
                        garagem.setCidade(txtcidade.getText().toString());
                        garagem.setValor(Double.parseDouble(txtvalor.getText().toString()));
                        garagem.setGaragem(btn);
                        garagem.setForeingnKeyUser(emailLogado);
                        insereCadastroGaragem(garagem);
                    } else {
                        btnCadastrar.setVisibility(View.VISIBLE);
                        progressBarGarage.setVisibility(View.GONE);
                        if (txtrua.getText().toString().equals("")) {
                            Toast.makeText(RegisterGaragActivity.this, getString(R.string.rua_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (txtnumero.getText().toString().equals("")) {
                            Toast.makeText(RegisterGaragActivity.this, getString(R.string.numero_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (txtbairro.getText().toString().equals("")) {
                            Toast.makeText(RegisterGaragActivity.this, getString(R.string.bairro_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (txtcidade.getText().toString().equals("")) {
                            Toast.makeText(RegisterGaragActivity.this, getString(R.string.cidade_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (txtvalor.getText().toString().equals("")) {
                            Toast.makeText(RegisterGaragActivity.this, getString(R.string.valor_vazio), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            garagemOnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (garagemOnOff.isChecked()) {
                        btn = true;
                        txtOnOff.setText(getString(R.string.on));
                    } else {
                        btn = false;
                        txtOnOff.setText(getString(R.string.off));
                    }
                }
            });
        } else {
            btnCadastrar.setVisibility(View.VISIBLE);
            progressBarGarage.setVisibility(View.GONE);
            abreUser();
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            emptyEditText(txtrua, txtnumero, txtcomplemento, txtbairro, txtcidade, txtvalor);
        }
    }
    private boolean insereCadastroGaragem(Garage gar) {
        try {
            reference = ConfigurationFirebase.getFirebase().child("usuarios/").child("garagens");
            String key = reference.push().getKey();
            garagem.setKeyGaragem(key);
            reference.child(key).setValue(gar);
            abreUser();
            Toast.makeText(this, getString(R.string.dados_garagem), Toast.LENGTH_LONG).show();
            emptyEditText(txtrua, txtnumero, txtcomplemento, txtbairro, txtcidade, txtvalor);
            return true;
        } catch (Exception e) {
            btnCadastrar.setVisibility(View.VISIBLE);
            progressBarGarage.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.erro_dados), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            emptyEditText(txtrua, txtnumero, txtcomplemento, txtbairro, txtcidade, txtvalor);
            return false;
        }
    }
    private void emptyEditText(TextInputEditText text1, TextInputEditText text2, TextInputEditText text3, TextInputEditText text4,TextInputEditText text5, TextInputEditText text6 ) {
        text1.setText(null);
        text2.setText(null);
        text3.setText(null);
        text4.setText(null);
        text5.setText(null);
        text6.setText(null);
    }
    private void abreUser(){
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}