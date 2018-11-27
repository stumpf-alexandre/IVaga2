package com.stumpf.als.i_vaga.classes;
import com.google.firebase.database.Exclude;
public class User {
    private String keyUsuario;
    private String email;
    private String senha;
    private String nome;
    private String sobrenome;
    private String imagem;
    public String getKeyUsuario() {
        return keyUsuario;
    }
    public void setKeyUsuario(String keyUsuario) {
        this.keyUsuario = keyUsuario;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }
    @Exclude
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getSobrenome() {
        return sobrenome;
    }
    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }
    public String getImagem() {
        return imagem;
    }
    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}