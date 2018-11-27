package com.stumpf.als.i_vaga.helper;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.stumpf.als.i_vaga.R;
import static android.content.Context.CONNECTIVITY_SERVICE;
public class Services {
    public static boolean checkInternet(Context context) {
        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo informacao = conexao.getActiveNetworkInfo();
        if (informacao != null && informacao.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    public static void opcoesErro(Context context, String erro) {
        if (erro.contains("least 6 characters")) {
            Toast.makeText(context, R.string.senha_forte, Toast.LENGTH_LONG).show();
        } else if (erro.contains("address is badly")) {
            Toast.makeText(context, R.string.email_invalido, Toast.LENGTH_LONG).show();
        } else if (erro.contains("address is already")) {
            Toast.makeText(context, R.string.email_cadastrado, Toast.LENGTH_LONG).show();
        } else if (erro.contains("interrupted connection")) {
            Toast.makeText(context, R.string.erro_cadastro, Toast.LENGTH_LONG).show();
        } else if (erro.contains("password is invalid")) {
            Toast.makeText(context, R.string.senha_invalido, Toast.LENGTH_LONG).show();
        } else if (erro.contains("There is no user")) {
            Toast.makeText(context, R.string.email_nao_cadastrado, Toast.LENGTH_LONG).show();
        } else if (erro.contains("INVALID_EMAIL")) {
            Toast.makeText(context, R.string.email_nao_valido, Toast.LENGTH_LONG).show();
        } else if (erro.contains("EMAIL_NOT_FOUND")) {
            Toast.makeText(context, R.string.email_nao_cadastrado, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, erro, Toast.LENGTH_LONG).show();
        }
    }
}