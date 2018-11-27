package com.stumpf.als.i_vaga.activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.User;
import com.stumpf.als.i_vaga.helper.Services;
public class RegisterLoginActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;
    private TextInputEditText emailCadastro;
    private TextInputEditText senhaCadastro;
    private TextInputEditText confirmarCadastro;
    private TextInputEditText nomeCadastro;
    private TextInputEditText sobrenomeCadastro;
    private CardView btnCadastroLogin;
    private AppCompatTextView btnTextoLogin;
    private AppCompatRadioButton buttonGaragem;
    private AppCompatRadioButton buttonCarro;
    private RadioGroup radioGroup;
    private ContentLoadingProgressBar progressBar;
    private User usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);
        emailCadastro = findViewById(R.id.emailRegister);
        senhaCadastro = findViewById(R.id.senhaRegister);
        confirmarCadastro = findViewById(R.id.senhaConfirmarRegister);
        nomeCadastro = findViewById(R.id.nomeRegister);
        sobrenomeCadastro = findViewById(R.id.sobrenomeRegister);
        btnCadastroLogin = findViewById(R.id.btnRegisterLogin);
        btnTextoLogin = findViewById(R.id.btnLogoutLogin);
        buttonGaragem = findViewById(R.id.rbgaragem);
        buttonCarro = findViewById(R.id.rbcarro);
        radioGroup = findViewById(R.id.radioGroup);
        progressBar = findViewById(R.id.progress_bar_register_login);
        btnCadastroLogin.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if (Services.checkInternet(this)) {
            btnCadastroLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!emailCadastro.getText().toString().equals("") && !senhaCadastro.getText().toString().equals("") && !confirmarCadastro.getText().toString().equals("") && !nomeCadastro.getText().toString().equals("") && !sobrenomeCadastro.getText().toString().equals("")) {
                        if (senhaCadastro.getText().toString().equals(confirmarCadastro.getText().toString())) {
                            progressBar.setVisibility(View.VISIBLE);
                            btnCadastroLogin.setVisibility(View.GONE);
                            usuario = new User();
                            usuario.setEmail(emailCadastro.getText().toString().trim());
                            usuario.setSenha(senhaCadastro.getText().toString());
                            usuario.setNome(nomeCadastro.getText().toString());
                            usuario.setSobrenome(sobrenomeCadastro.getText().toString());
                            usuario.setImagem("");
                            cadastroLogin();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnCadastroLogin.setVisibility(View.VISIBLE);
                            Toast.makeText(RegisterLoginActivity.this, getString(R.string.senha_nao_confirmada), Toast.LENGTH_LONG).show();
                            emptyEditTextSenha(senhaCadastro, confirmarCadastro);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnCadastroLogin.setVisibility(View.VISIBLE);
                        if (emailCadastro.getText().toString().equals("")) {
                            Toast.makeText(RegisterLoginActivity.this, getString(R.string.email_vazio), Toast.LENGTH_LONG).show();
                        } else if (senhaCadastro.getText().toString().equals("")) {
                            Toast.makeText(RegisterLoginActivity.this, getString(R.string.senha_vazio), Toast.LENGTH_LONG).show();
                        } else if (confirmarCadastro.getText().toString().equals("")) {
                            Toast.makeText(RegisterLoginActivity.this, getString(R.string.confirmacao_vazio), Toast.LENGTH_LONG).show();
                        } else if (nomeCadastro.getText().toString().equals("")) {
                            Toast.makeText(RegisterLoginActivity.this, getString(R.string.nome_vazio), Toast.LENGTH_LONG).show();
                        } else if (sobrenomeCadastro.getText().toString().equals("")) {
                            Toast.makeText(RegisterLoginActivity.this, getString(R.string.sobrenome_vazio), Toast.LENGTH_LONG).show();
                        }
                    }
                }

            });
            btnTextoLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(new Intent(RegisterLoginActivity.this, LoginActivity.class));
                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            finish();
            emptyEditText(emailCadastro, senhaCadastro, confirmarCadastro, nomeCadastro, sobrenomeCadastro, buttonGaragem, buttonCarro);
        }
    }
    private void cadastroLogin(){
        if (buttonGaragem.isChecked()) {
            autenticacao = ConfigurationFirebase.getFirebaseAuth();
            autenticacao.createUserWithEmailAndPassword(
                    usuario.getEmail().toString(),
                    usuario.getSenha().toString()
            ).addOnCompleteListener(RegisterLoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        insereCadastroLogin(usuario);
                        finish();
                        startActivity(new Intent(RegisterLoginActivity.this, RegisterGarageActivity.class));
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnCadastroLogin.setVisibility(View.VISIBLE);
                        String erro = task.getException().toString();
                        Services.opcoesErro(getBaseContext(), erro);
                    }
                }
            });
        }
        else if (buttonCarro.isChecked()){
            autenticacao = ConfigurationFirebase.getFirebaseAuth();
            autenticacao.createUserWithEmailAndPassword(
                    usuario.getEmail().toString(),
                    usuario.getSenha().toString()
            ).addOnCompleteListener(RegisterLoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        insereCadastroLogin(usuario);
                        finish();
                        startActivity(new Intent(RegisterLoginActivity.this, RegisterCarActivity.class));
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnCadastroLogin.setVisibility(View.VISIBLE);
                        String erro = task.getException().toString();
                        Services.opcoesErro(getBaseContext(), erro);
                    }
                }
            });
        }
        else {
            progressBar.setVisibility(View.GONE);
            btnCadastroLogin.setVisibility(View.VISIBLE);
            Toast.makeText(RegisterLoginActivity.this, getString(R.string.radio_button), Toast.LENGTH_LONG).show();
        }
    }
    private boolean insereCadastroLogin(User usuario){
        try {
            referencia = ConfigurationFirebase.getFirebase().child("usuarios");
            String key = referencia.push().getKey();
            usuario.setKeyUsuario(key);
            referencia.child(key).setValue(usuario);
            Toast.makeText(RegisterLoginActivity.this, getString(R.string.dados_login), Toast.LENGTH_LONG).show();
            emptyEditText(emailCadastro, senhaCadastro, confirmarCadastro, nomeCadastro, sobrenomeCadastro, buttonGaragem, buttonCarro);
            return true;
        }catch (Exception e){
            Toast.makeText(RegisterLoginActivity.this, getString(R.string.erro_dados), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            emptyEditText(emailCadastro, senhaCadastro, confirmarCadastro, nomeCadastro, sobrenomeCadastro, buttonGaragem, buttonCarro);
            return false;
        }
    }
    private void emptyEditText(TextInputEditText text1, TextInputEditText text2, TextInputEditText text3, TextInputEditText text4, TextInputEditText text5, RadioButton button1, RadioButton button2) {
        text1.setText(null);
        text2.setText(null);
        text3.setText(null);
        text4.setText(null);
        text5.setText(null);
        button1.setChecked(false);
        button2.setChecked(false);
    }
    private void emptyEditTextSenha(TextInputEditText text1, TextInputEditText text2){
        text1.setText(null);
        text2.setText(null);
    }
}