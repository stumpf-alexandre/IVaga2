package com.stumpf.als.i_vaga.activity;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.User;
import com.stumpf.als.i_vaga.helper.Services;
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private TextInputEditText textEmail;
    private TextInputEditText textSenha;
    private CardView btnLogin;
    private AppCompatTextView btnTextoCadastro;
    private AppCompatTextView btnTextRecuperarSenha;
    private ContentLoadingProgressBar progressBarLogin;
    private AlertDialog alerta;
    User usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textEmail = findViewById(R.id.email);
        textSenha = findViewById(R.id.senha);
        btnLogin = findViewById(R.id.btnLogin);
        btnTextoCadastro = findViewById(R.id.btnCadastro);
        btnTextRecuperarSenha = findViewById(R.id.btnRecuperarSenha);
        progressBarLogin = findViewById(R.id.progress_bar_login);
        progressBarLogin.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
        permissao();
        final TextInputEditText editText = new TextInputEditText(this);
        editText.setHint(getString(R.string.exemplo_email));
        if (Services.checkInternet(this)) {
            if (userLogado()) {
                finish();
                startActivity(new Intent(getBaseContext(), UserActivity.class));
                Toast.makeText(LoginActivity.this, getString(R.string.usuario), Toast.LENGTH_LONG).show();
                emptyEditText(textEmail, textSenha);
            } else {
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (textEmail.getText().toString().equals("") && !textSenha.getText().toString().equals("")) {
                            Toast.makeText(LoginActivity.this, getString(R.string.email_vazio), Toast.LENGTH_LONG).show();
                            emptyEditText(textEmail, textSenha);
                        }
                        if (!textEmail.getText().toString().equals("") && textSenha.getText().toString().equals("")) {
                            Toast.makeText(LoginActivity.this, getString(R.string.senha_vazio), Toast.LENGTH_LONG).show();
                            emptyEditText(textEmail, textSenha);
                        }
                        if (!textEmail.getText().toString().equals("") && !textSenha.getText().toString().equals("")) {
                            if (Services.checkInternet(LoginActivity.this)) {
                                progressBarLogin.setVisibility(View.VISIBLE);
                                btnLogin.setVisibility(View.GONE);
                                usuario = new User();
                                usuario.setEmail(textEmail.getText().toString());
                                usuario.setSenha(textSenha.getText().toString());
                                validaLogin();
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
                                emptyEditText(textEmail, textSenha);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.email_senha_vazio), Toast.LENGTH_LONG).show();
                            emptyEditText(textEmail, textSenha);
                        }
                    }
                });
                btnTextoCadastro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(LoginActivity.this, RegisterLoginActivity.class));
                        emptyEditText(textEmail, textSenha);
                    }
                });
                btnTextRecuperarSenha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle(getString(R.string.recuperar_senha));
                        builder.setMessage(getString(R.string.informar_email));
                        builder.setView(editText);
                        if (!editText.getText().equals("")) {
                            builder.setPositiveButton(getString(R.string.recuperar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    autenticacao = FirebaseAuth.getInstance();
                                    String emailRecuperar = editText.getText().toString();
                                    autenticacao.sendPasswordResetEmail(emailRecuperar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = getIntent();
                                                Toast.makeText(LoginActivity.this, getString(R.string.receber_mensagem), Toast.LENGTH_LONG).show();
                                                finish();
                                                startActivity(intent);
                                            } else {
                                                String erro = task.getException().toString();
                                                Services.opcoesErro(getBaseContext(), erro);
                                                Intent intent = getIntent();
                                                finish();
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.preencher_campo_email), Toast.LENGTH_LONG).show();
                        }
                        alerta = builder.create();
                        alerta.show();
                    }
                });
            }
        }
        else {
            Toast.makeText(LoginActivity.this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
        }
    }
    private void validaLogin() {
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail().toString(), usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                    startActivity(new Intent(LoginActivity.this, UserActivity.class));
                    Toast.makeText(LoginActivity.this, getString(R.string.email_senha_valido), Toast.LENGTH_LONG).show();
                    emptyEditText(textEmail, textSenha);
                }
                else {
                    progressBarLogin.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    String erro = task.getException().toString();
                    Services.opcoesErro(getBaseContext(), erro);
                }
            }
        });
    }
    public Boolean userLogado(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            return true;
        }
        else {
            return false;
        }
    }
    private void emptyEditText(TextInputEditText text1, TextInputEditText text2) {
        text1.setText(null);
        text2.setText(null);
    }
    public void permissao(){
        int permission_all = 1;
        String [] permission = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permission, permission_all);
    }
}