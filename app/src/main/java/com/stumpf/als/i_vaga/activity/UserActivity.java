package com.stumpf.als.i_vaga.activity;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private ListView listViewCar;
    private ListView listViewGarage;
    private ArrayAdapter<Car> adapterCar;
    private ArrayAdapter<Garage> adapterGarage;
    ValueEventListener valueEventListener;
    private ArrayList<Garage> garagens;
    private ArrayList<Car> carros;
    private Garage garagem;
    private Car carro;
    private int cont = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        permissao();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = findViewById(R.id.floatingButton);
        fab.setVisibility(View.VISIBLE);
        listViewCar = findViewById(R.id.listViewListCar);
        listViewGarage = findViewById(R.id.listViewListGarage);
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
            listViewCar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editPerfilCar();
                }
            });
            listViewGarage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editPerfilGarage();
                }
            });
            fab.setImageResource(R.drawable.ic_photo_camera_black_24dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.setVisibility(View.GONE);
                    if(cont <=0){
                        fab.setImageResource(R.drawable.ic_photo_camera_black_24dp);
                        abrirFoto();
                        cont++;
                        fab.setVisibility(View.VISIBLE);
                    }
                    else {
                        fab.setImageResource(R.drawable.ic_backup_black_24dp);
                        uploadFotoUser();
                        cont = 0;
                    }
                }
            });
            //carregarImage();
            carregarTodosCarros();
            carregarTodasGaragens();
        }
        else {
            Toast.makeText(this, getString(R.string.erro_internet), Toast.LENGTH_LONG).show();
            abrirLogin();
        }
    }
    private void carregarTodosCarros() {
        carros = new ArrayList<>();
        adapterCar = new CarAdapter(this, carros);
        listViewCar.setAdapter(adapterCar);
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("usuarios/").child("carros").orderByChild("foreignKeyUser").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carros.clear();
                for (DataSnapshot postSnapshotCar : dataSnapshot.getChildren()){
                    carro = postSnapshotCar.getValue(Car.class);
                    carros.add(carro);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void carregarTodasGaragens(){
        garagens = new ArrayList<>();
        adapterGarage = new GarageAdapter(this, garagens);
        listViewGarage.setAdapter(adapterGarage);
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("usuarios/").child("garagens").orderByChild("foreingnKeyUser").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                garagens.clear();
                for (DataSnapshot postSnapshotGar : dataSnapshot.getChildren()){
                    garagem = postSnapshotGar.getValue(Garage.class);
                    garagens.add(garagem);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    //@Override
    //protected void onStop() {
        //super.onStop();
        //reference.removeEventListener(valueEventListener);
    //}
    //@Override
    //protected void onStart() {
        //super.onStart();
        //reference.addValueEventListener(valueEventListener);
    //}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_gar){
            if (adapterGarage.getCount() <=1) {
                finish();
                startActivity(new Intent(UserActivity.this, RegisterGarageActivity.class));
            }
            else {
                abrirLogin();
                Toast.makeText(this, getString(R.string.num_excedido), Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.action_add_car){
            if (adapterGarage.getCount() <=1) {
                finish();
                startActivity(new Intent(UserActivity.this, RegisterCarActivity.class));
            }
            else {
                abrirLogin();
                Toast.makeText(this, getString(R.string.num_excedido), Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.action_edit_user){
            if (Services.checkInternet(this)) {
                editPerfilUser();
            }
            else {
                Toast.makeText(this, getString(R.string.num_excedido), Toast.LENGTH_LONG).show();
                abrirLogin();
            }
        }
        else if (id == R.id.action_list_gar){
            if (Services.checkInternet(this)) {
                abrirList();
            }
            else {
                Toast.makeText(this, getString(R.string.num_excedido), Toast.LENGTH_LONG).show();
                abrirLogin();
            }
        }
        else if (id == R.id.action_exit){
            FirebaseAuth.getInstance().signOut();
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
                    bundle.putString("origem", "Editar Dados");
                    bundle.putString("nome", usuario.getNome());
                    bundle.putString("sobrenome", usuario.getSobrenome());
                    bundle.putString("email", usuario.getEmail());
                    bundle.putString("keyUsuario", usuario.getKeyUsuario());
                    bundle.putString("imagem", usuario.getImagem());
                    intent.putExtras(bundle);
                    finish();
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void editPerfilCar(){
        reference = ConfigurationFirebase.getFirebase();
        reference.child("usuarios/").child("carros").orderByChild("foreignKeyUser").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    carro = postSnapshot.getValue(Car.class);
                    final Intent intent = new Intent(UserActivity.this, EditCarActivity.class);
                    final Bundle bundle = new Bundle();
                    bundle.putString("origem", "Editar Dados Carro");
                    bundle.putString("placa", carro.getPlaca());
                    bundle.putString("keyCar", carro.getKeyCar());
                    bundle.putString("foreignKeyUser", carro.getForeignKeyUser());
                    intent.putExtras(bundle);
                    finish();
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void editPerfilGarage(){
        reference = ConfigurationFirebase.getFirebase();
        reference.child("usuarios/").child("garagens").orderByChild("foreingnKeyUser").equalTo(emailLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    garagem = postSnapshot.getValue(Garage.class);
                    final Intent intent = new Intent(UserActivity.this, EditGarageActivity.class);
                    final Bundle bundle = new Bundle();
                    bundle.putString("origem", "Editar Dados Garagem");
                    bundle.putString("rua", garagem.getRua());
                    bundle.putLong("numero", garagem.getNumero());
                    bundle.putString("complemento", garagem.getComplemento());
                    bundle.putString("bairro", garagem.getBairro());
                    bundle.putString("cidade", garagem.getCidade());
                    bundle.putDouble("valor", garagem.getValor());
                    bundle.putString("keyGaragem", garagem.getKeyGaragem());
                    bundle.putString("foreingnKeyUser", garagem.getForeingnKeyUser());
                    bundle.putBoolean("garagem", garagem.getGaragem());
                    intent.putExtras(bundle);
                    finish();
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void abrirLogin(){
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
    private void abrirList(){
        finish();
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selecione_imagem)), 123);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int width = metrics.widthPixels / 2;
        final int height = metrics.heightPixels / 4;
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 123){
                if (data != null) {
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
        final DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getFragmentManager(),"");
        final StorageReference montarImagem = storageReference.child("fotoPerfilUsuario/" + emailLogado + System.currentTimeMillis() + ".jpg");
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
                    databaseFotoUser(usuario);
                    Toast.makeText(UserActivity.this, getString(R.string.foto_cadastrada), Toast.LENGTH_LONG).show();
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
            reference = ConfigurationFirebase.getFirebase();
            reference.child("usuarios").child("imagem").setValue(usuario);
        }catch (Exception e){
            erro = getString(R.string.erro_dados);
            e.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.erro) + erro, Toast.LENGTH_LONG).show();
        return true;
    }
    public void permissao(){
        int permission_all = 1;
        String [] permission = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permission, permission_all);
    }
}