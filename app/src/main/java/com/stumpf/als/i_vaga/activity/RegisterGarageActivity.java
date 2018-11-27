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
public class RegisterGarageActivity extends AppCompatActivity {
    private TextInputEditText txtrua;
    private TextInputEditText txtnumero;
    private TextInputEditText txtcomplemento;
    private TextInputEditText txtbairro;
    private TextInputEditText txtcidade;
    private TextInputEditText txtvalor;
    private SwitchCompat garagemOnOff;
    private AppCompatTextView txtOnOff;
    private ContentLoadingProgressBar progressBarGarage;
    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private Garage garagem;
    private String emailLogado;
    private CardView btnCadastrarLocalizacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_garage);
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        txtrua = findViewById(R.id.ruaGaragem);
        txtnumero = findViewById(R.id.numeroGaragem);
        txtcomplemento = findViewById(R.id.complementoGaragem);
        txtbairro = findViewById(R.id.bairroGaragem);
        txtcidade = findViewById(R.id.cidadeGaragem);
        txtvalor = findViewById(R.id.valoGaragem);
        garagemOnOff = findViewById(R.id.switch_on_off);
        txtOnOff = findViewById(R.id.on_off);
        btnCadastrarLocalizacao = findViewById(R.id.cadastroGaragem);
        progressBarGarage = findViewById(R.id.progress_bar_register_garagem);
        btnCadastrarLocalizacao.setVisibility(View.VISIBLE);
        progressBarGarage.setVisibility(View.GONE);
        emailLogado = autenticacao.getCurrentUser().getEmail();
        if (Services.checkInternet(this)) {
            btnCadastrarLocalizacao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!txtrua.getText().toString().equals("") && !txtnumero.getText().toString().equals("") && !txtbairro.getText().toString().equals("") && !txtcidade.getText().toString().equals("") && !txtvalor.getText().toString().equals("")) {
                        progressBarGarage.setVisibility(View.VISIBLE);
                        btnCadastrarLocalizacao.setVisibility(View.GONE);
                        garagem = new Garage();
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
                        garagemOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    garagem.setGaragem(true);
                                    txtOnOff.setText(getString(R.string.on));
                                } else {
                                    garagem.setGaragem(false);
                                    txtOnOff.setText(getString(R.string.off));
                                }
                            }
                        });
                        garagem.setForeingnKeyUser(emailLogado);
                        insereCadastroGaragem(garagem);
                    } else {
                        btnCadastrarLocalizacao.setVisibility(View.VISIBLE);
                        progressBarGarage.setVisibility(View.GONE);
                        if (txtrua.getText().toString().equals("")) {
                            Toast.makeText(RegisterGarageActivity.this, getString(R.string.rua_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (txtnumero.getText().toString().equals("")) {
                            Toast.makeText(RegisterGarageActivity.this, getString(R.string.numero_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (txtbairro.getText().toString().equals("")) {
                            Toast.makeText(RegisterGarageActivity.this, getString(R.string.bairro_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (txtcidade.getText().toString().equals("")) {
                            Toast.makeText(RegisterGarageActivity.this, getString(R.string.cidade_vazio), Toast.LENGTH_LONG).show();
                        }
                        else if (txtvalor.getText().toString().equals("")) {
                            Toast.makeText(RegisterGarageActivity.this, getString(R.string.valor_vazio), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } else {
            btnCadastrarLocalizacao.setVisibility(View.VISIBLE);
            progressBarGarage.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            emptyEditText(txtrua, txtnumero, txtcomplemento, txtbairro, txtcidade, txtvalor);
            finish();
        }
    }
    private boolean insereCadastroGaragem(Garage garagem) {
        try {
            reference = ConfigurationFirebase.getFirebase().child("usuarios/").child("garagens");
            String key = reference.push().getKey();
            garagem.setKeyGaragem(key);
            reference.child(key).setValue(garagem);
            startActivity(new Intent(this, UserActivity.class));
            finish();
            emptyEditText(txtrua, txtnumero, txtcomplemento, txtbairro, txtcidade, txtvalor);
            return true;
        } catch (Exception e) {
            btnCadastrarLocalizacao.setVisibility(View.VISIBLE);
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
}