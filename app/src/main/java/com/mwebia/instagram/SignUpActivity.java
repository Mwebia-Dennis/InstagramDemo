package com.mwebia.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private EditText userNameField;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.emailTV);
        passwordField = findViewById(R.id.passwordTV);
        confirmPasswordField = findViewById(R.id.confirmPasswordTV);
        userNameField = findViewById(R.id.userName);
        imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.loginTV);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ask permission from user if not granted by manifest

                checkPermission();

            }
        });
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString();
                String confirmPassword = confirmPasswordField.getText().toString();

                if (userNameField.getText().toString().isEmpty()){
                    userNameField.setError("enter your username");
                    userNameField.requestFocus();
                }
                else if (email.isEmpty()){
                    emailField.setError("enter your email");
                    emailField.requestFocus();
                }
                else if (password.isEmpty() || confirmPassword.isEmpty()){
                    passwordField.setError("enter your password");
                    confirmPasswordField.setError("re enter your password");
                    passwordField.requestFocus();
                }
                else if (!Objects.equals(password, confirmPassword)){

                    confirmPasswordField.setError("your passwords do not match");
                    confirmPasswordField.requestFocus();
                }
                else if (password.length() != 6){

                    passwordField.setError("your password must be at least 6 characters");
                    passwordField.requestFocus();
                }
                else {

                    signInToFirebase(email,password);
                }
            }
        });
    }
    private void signInToFirebase(String email,String password){

        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                saveImageToFirebase();
                Intent intent = new Intent(SignUpActivity.this,UserList.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("signin error", e.getMessage());
                Toast.makeText(SignUpActivity.this, "Failed Sign in,please try again later", Toast.LENGTH_SHORT).show();
            }
        });

    }

    int requestCodeInt = 123;
    private void checkPermission(){

        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},requestCodeInt);
                return;
            }
        }

        loadImageFromDevice();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == requestCodeInt){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                loadImageFromDevice();
            }else {
                Toast.makeText(this,"could not get access to your images",Toast.LENGTH_LONG).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadImageFromDevice(){


        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1) && (resultCode == RESULT_OK) && (data != null)&& (data.getData() != null)){

            Uri imageUri = data.getData();
            try {

                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                imageView.setImageBitmap(imageBitmap);


            } catch (IOException e) {

                e.printStackTrace();

            }


        }
    }

    private void saveImageToFirebase() {

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        String imagePath = System.currentTimeMillis() + (".png");
        final StorageReference imageRef = mStorageRef.child(imagePath);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        imageRef.putBytes(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.i("imageStorageError",e.getMessage());

                Toast.makeText(getApplicationContext(),"sorry could not upload your profile picture",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata().getReference() != null){
                    Task<Uri> downloadUrl =taskSnapshot.getStorage().getDownloadUrl();
                    if (currentUser!= null){

                        mDatabaseRef.child(currentUser.getUid()).child("username").setValue(userNameField.getText().toString());
                        mDatabaseRef.child(currentUser.getUid()).child("profilePicture").setValue(downloadUrl.toString());
                    }

                }

            }
        });
    }

}
