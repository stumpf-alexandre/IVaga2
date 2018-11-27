package com.stumpf.als.i_vaga.activity;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.classes.Car;
import com.stumpf.als.i_vaga.helper.Services;
public class RegisterCarActivity extends AppCompatActivity {
    private TextInputEditText placa;
    private CardView btnCadastroCarro;
    private ContentLoadingProgressBar progressBar;
    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private Car carro;
    private String emailLogado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        placa = findViewById(R.id.placaRegisterCarro);
        btnCadastroCarro = findViewById(R.id.cadastroCarro);
        progressBar = findViewById(R.id.progress_bar_register_car);
        btnCadastroCarro.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        emailLogado = autenticacao.getCurrentUser().getEmail();
        if (Services.checkInternet(this)) {
            btnCadastroCarro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!placa.getText().toString().equals("")) {
                        progressBar.setVisibility(View.VISIBLE);
                        btnCadastroCarro.setVisibility(View.GONE);
                        carro = new Car();
                        carro.setPlaca(placa.getText().toString().toUpperCase());
                        carro.setForeignKeyUser(emailLogado);
                        insereCadastroCarro(carro);
                    } else {
                        btnCadastroCarro.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterCarActivity.this, getString(R.string.placa_vazio), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            btnCadastroCarro.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            finish();
            emptyEditText(placa);
        }
    }
    private boolean insereCadastroCarro(Car car) {
        try {
            reference = ConfigurationFirebase.getFirebase().child("usuarios/").child("carros");
            String key = reference.push().getKey();
            carro.setKeyCar(key);
            reference.child(key).setValue(car);
            startActivity(new Intent(this, UserActivity.class));
            Toast.makeText(this, getString(R.string.dados_carro), Toast.LENGTH_LONG).show();
            finish();
            emptyEditText(placa);
            return true;
        } catch (Exception e) {
            btnCadastroCarro.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.erro_dados), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            emptyEditText(placa);
            return false;
        }
    }
    private void emptyEditText(TextInputEditText text1) {
        text1.setText(null);
    }
}