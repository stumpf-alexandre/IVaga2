package com.stumpf.als.i_vaga.activity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.stumpf.als.i_vaga.DAO.ConfigurationFirebase;
import com.stumpf.als.i_vaga.R;
import com.stumpf.als.i_vaga.adapter.CarAdapter;
import com.stumpf.als.i_vaga.adapter.GarageAdapter;
import com.stumpf.als.i_vaga.classes.Car;
import com.stumpf.als.i_vaga.classes.Garage;
import com.stumpf.als.i_vaga.classes.User;
import com.stumpf.als.i_vaga.helper.DialogProgress;
import com.stumpf.als.i_vaga.helper.Services;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
public class UserActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private User usuario;
    private AppCompatTextView txt_email;
    private AppCompatTextView txt_sobrenome;
    private String emailLogado;
    private String url_imagem;
    private ImageView imagem;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerView recyclerViewGaragem;
    private RecyclerView recyclerViewCarro;
    private GarageAdapter adapterGarage;
    private CarAdapter adapterCar;
    private List<Garage> garagens;
    private List<Car> carros;
    private LinearLayoutManager linearManagerCar;
    private LinearLayoutManager linearManagerGarage;
    private Garage garagem;
    private Car carro;
    private String key;
    private int cont = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        autenticacao = ConfigurationFirebase.getFirebaseAuth();
        reference = ConfigurationFirebase.getFirebase();
        storageReference = ConfigurationFirebase.getFirebaseStorageReference();
        storage = ConfigurationFirebase.getFirebaseStorage();
        emailLogado = autenticacao.getCurrentUser().getEmail();
        imagem = findViewById(R.id.imagemPerfil);
        txt_email = findViewById(R.id.emailUser);
        txt_sobrenome = findViewById(R.id.sobrenomeUser);
        appBarLayout = findViewById(R.id.appBar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.floatingButton);
        fab.setVisibility(View.VISIBLE);
        recyclerViewCarro = findViewById(R.id.recycleViewListCar);
        recyclerViewGaragem = findViewById(R.id.recycleViewListGarage);
        if (Services.checkInternet(this)) {
            reference = FirebaseDatabase.getInstance().getReference();
            reference.child("usuarios").orderByChild("email").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        collapsingToolbarLayout.setTitle(postSnapshot.child("nome").getValue().toString());
                        toolbar.setTitle(postSnapshot.child("nome").getValue().toString());
                        txt_sobrenome.setText(getString(R.string.hint_sobrenome) + " " + postSnapshot.child("sobrenome").getValue().toString());
                        txt_email.setText(getString(R.string.hint_email) + " " + postSnapshot.child("email").getValue().toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            fab.setImageResource(R.drawable.ic_photo_camera_black_24dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cont <= 0) {
                        abrirFoto();
                    }
                    else {
                        uploadFotoUser();
                        fab.setVisibility(View.GONE);
                    }
                }
            });
            //carregarImage();
            carregarTodosCarros();
            carregarTodasGaragens();
        }
        else {
            abrirLogin();
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
        }
    }
    private void carregarTodosCarros() {
        recyclerViewCarro.setHasFixedSize(true);
        linearManagerCar = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewCarro.setLayoutManager(linearManagerCar);
        carros = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("usuarios/").child("carros").orderByChild("foreignKeyUser").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carros.clear();
                for (DataSnapshot postSnapshotCar : dataSnapshot.getChildren()){
                    carro = postSnapshotCar.getValue(Car.class);
                    carros.add(carro);
                }
                adapterCar.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        adapterCar = new CarAdapter(carros, this);
        recyclerViewCarro.setAdapter(adapterCar);
    }
    private void carregarTodasGaragens(){
        recyclerViewGaragem.setHasFixedSize(true);
        linearManagerGarage = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewGaragem.setLayoutManager(linearManagerGarage);
        garagens = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("usuarios/").child("garagens").orderByChild("foreingnKeyUser").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                garagens.clear();
                for (DataSnapshot postSnapshotGar : dataSnapshot.getChildren()){
                    garagem = postSnapshotGar.getValue(Garage.class);
                    garagens.add(garagem);
                }
                adapterGarage.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        adapterGarage = new GarageAdapter(garagens, this);
        recyclerViewGaragem.setAdapter(adapterGarage);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_gar){
            //if (adapterGarage.getItemCount() <= 2) {
                //finish();
                startActivity(new Intent(this, RegisterGaragActivity.class));
            //}
            //else {
                //Toast.makeText(this, getString(R.string.num_excedido), Toast.LENGTH_LONG).show();
            //}
        }
        else if (id == R.id.action_add_car){
            //if (adapterCar.getItemCount() <= 1) {
                //finish();
                startActivity(new Intent(this, RegisterCarActivity.class));
            //}
            //else {
                //Toast.makeText(this, getString(R.string.num_excedido), Toast.LENGTH_LONG).show();
            //}
        }
        else if (id == R.id.action_edit_user){
            if (Services.checkInternet(this)) {
                editPerfilUser();
            }
            else {
                abrirLogin();
                Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();

            }
        }
        else if (id == R.id.action_list_gar){
            if (Services.checkInternet(this)) {
                abrirList();
            }
            else {
                abrirLogin();
                Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.action_exit){
            FirebaseAuth.getInstance().signOut();
            finish();
            abrirLogin();
        }
        return super.onOptionsItemSelected(item);
    }
    private void editPerfilUser(){
        reference = ConfigurationFirebase.getFirebase();
        reference.child("usuarios").orderByChild("email").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    usuario = postSnapshot.getValue(User.class);
                    final Intent intent = new Intent(UserActivity.this, EditUserActivity.class);
                    final Bundle bundle = new Bundle();
                    bundle.putString("origem", "editarDados");
                    bundle.putString("nome", usuario.getNome());
                    bundle.putString("sobrenome", usuario.getSobrenome());
                    bundle.putString("email", usuario.getEmail());
                    bundle.putString("keyUsuario", usuario.getKeyUsuario());
                    bundle.putString("imagem", usuario.getImagem());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    //finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void abrirLogin(){
        //finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
    private void abrirList(){
        //finish();
        startActivity(new Intent(this, ListActivity.class));
    }
    private void carregarImage(){
        reference = ConfigurationFirebase.getFirebase();
        reference.child("usuarios").orderByChild("email").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    url_imagem = postSnapshot.child("imagem").getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        FirebaseStorage stor = FirebaseStorage.getInstance();
        final StorageReference storageReference = stor.getReferenceFromUrl(url_imagem);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int height = (metrics.heightPixels/4);
        final int width = (metrics.widthPixels/2);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(url_imagem).resize(height, width).centerInside().into(imagem);
                fab.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fab.setVisibility(View.VISIBLE);
            }
        });
    }
    private void abrirFoto(){
        cont++;
        fab.setImageResource(R.drawable.ic_photo_camera_black_24dp);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        fab.setVisibility(View.GONE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selecione_imagem)), 123);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int width = metrics.widthPixels / 2;
        final int height = metrics.heightPixels / 4;
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 123){
                if (data != null) {
                    fab.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.ic_backup_black_24dp);
                    Uri imagemSelecionada = data.getData();
                    Picasso.get().load(imagemSelecionada.toString()).resize(height, width).centerCrop().into(imagem);
                }
                else {
                    Toast.makeText(this, getString(R.string.erro_imagem), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void uploadFotoUser(){
        cont = 0;
        final DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getFragmentManager(),"");
        final StorageReference montarImagem = storageReference.child("fotoPerfilUsuario/" + emailLogado + ".jpg");
        imagem.setDrawingCacheEnabled(true);
        imagem.buildDrawingCache();
        Bitmap bitmap = imagem.getDrawingCache();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte [] data = byteArray.toByteArray();
        UploadTask uploadTask = montarImagem.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                return montarImagem.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> task) {
                if (task.isSuccessful()){
                    dialogProgress.dismiss();
                    Uri uri = task.getResult();
                    url_imagem = uri.toString();
                    usuario = new User();
                    usuario.setImagem(url_imagem);

                }
                else {
                    dialogProgress.dismiss();
                    Toast.makeText(UserActivity.this, getString(R.string.foto_nao_cadastrada), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private boolean databaseFotoUser(final User usuario){
        String erro = "";
        try {
            reference = ConfigurationFirebase.getFirebase().child("usuarios");
            reference.child(key).setValue(usuario);
            Toast.makeText(UserActivity.this, getString(R.string.foto_cadastrada), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            erro = getString(R.string.erro_dados);
            Toast.makeText(this, getString(R.string.erro) + erro, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return true;
    }
}