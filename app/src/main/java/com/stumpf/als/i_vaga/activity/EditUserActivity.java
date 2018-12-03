package com.stumpf.als.i_vaga.activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.User;
import com.stumpf.als.i_vaga.helper.Services;
public class EditUserActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private TextInputEditText nomeEdit;
    private TextInputEditText sobrenomeEdit;
    private TextInputEditText senhaEdit;
    private TextInputEditText confirmarEdit;
    private CardView btnEditUser;
    private String txtOrigem = "";
    private String txtNome = "";
    private String txtSobrenome = "";
    private String txtEmail = "";
    private String txtKeyUsuario = "";
    private String txtImagem = "";
    private ContentLoadingProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        nomeEdit = findViewById (R.id.nomeEdit);
        sobrenomeEdit = findViewById(R.id.sobrenomeEdit);
        senhaEdit = findViewById(R.id.senhaEdit);
        confirmarEdit = findViewById(R.id.senhaConfirmarEdit);
        btnEditUser = findViewById(R.id.buttonEditUser);
        progressBar = findViewById(R.id.progress_bar_edit);
        progressBar.setVisibility(View.GONE);
        btnEditUser.setVisibility(View.VISIBLE);
        if (Services.checkInternet(this)) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            txtOrigem = bundle.getString("origem");
            if (txtOrigem.equals("editarDados")) {
                txtNome = bundle.getString("nome");
                txtSobrenome = bundle.getString("sobrenome");
                txtEmail = bundle.getString("email");
                txtKeyUsuario = bundle.getString("keyUsuario");
                txtImagem = bundle.getString("imagem");
                nomeEdit.setText(txtNome);
                sobrenomeEdit.setText(txtSobrenome);
                btnEditUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!nomeEdit.getText().toString().equals("") && !sobrenomeEdit.getText().toString().equals("") && !senhaEdit.getText().toString().equals("") && !confirmarEdit.getText().toString().equals("")) {
                            if (senhaEdit.getText().toString().equals(confirmarEdit.getText().toString())) {
                                progressBar.setVisibility(View.VISIBLE);
                                btnEditUser.setVisibility(View.GONE);
                                User usuario = new User();
                                usuario.setNome(nomeEdit.getText().toString());
                                usuario.setSobrenome(sobrenomeEdit.getText().toString());
                                usuario.setEmail(txtEmail);
                                usuario.setSenha(senhaEdit.getText().toString());
                                usuario.setKeyUsuario(txtKeyUsuario);
                                usuario.setImagem(txtImagem);
                                atualizarDados(usuario);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                btnEditUser.setVisibility(View.VISIBLE);
                                Toast.makeText(EditUserActivity.this, getString(R.string.senha_nao_confirmada), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnEditUser.setVisibility(View.VISIBLE);
                            if (nomeEdit.getText().toString().equals("")) {
                                Toast.makeText(EditUserActivity.this, getString(R.string.nome_vazio), Toast.LENGTH_LONG).show();
                            } else if (sobrenomeEdit.getText().toString().equals("")) {
                                Toast.makeText(EditUserActivity.this, getString(R.string.sobrenome_vazio), Toast.LENGTH_LONG).show();
                            } else if (senhaEdit.getText().toString().equals("")) {
                                Toast.makeText(EditUserActivity.this, getString(R.string.senha_vazio), Toast.LENGTH_LONG).show();
                            } else if (confirmarEdit.getText().toString().equals("")) {
                                Toast.makeText(EditUserActivity.this, getString(R.string.confirmacao_vazio), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        }
        else {
            progressBar.setVisibility(View.GONE);
            btnEditUser.setVisibility(View.VISIBLE);
            abreUser();
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();

        }
    }
    private boolean atualizarDados(final User usuario){
        btnEditUser.setEnabled(false);
        String erro = "";
        try {
            reference = ConfigurationFirebase.getFirebase().child("usuarios");
            atualizarSenha(usuario.getSenha());
            reference.child(txtKeyUsuario).setValue(usuario);
            Toast.makeText(this, getString(R.string.edit_dados) + erro, Toast.LENGTH_LONG).show();
            abreUser();
        }
        catch (Exception e) {
            erro = getString(R.string.erro_cadastro);
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.erro) + erro, Toast.LENGTH_LONG).show();
        }
        return true;
    }
    private void atualizarSenha(String senhaNova){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(senhaNova).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("NOVA_SENHA_ATUALIZADA", "Senha atualizada com sucesso");
                }
            }
        });
    }
    private void abreUser(){
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

}