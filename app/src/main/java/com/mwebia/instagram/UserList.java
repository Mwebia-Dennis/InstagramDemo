package com.mwebia.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserList extends AppCompatActivity {


    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;
    ArrayList<UsersInfo> usersInfo = new ArrayList<>();
    ListView listview;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        //initialising our variables
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        listview = findViewById(R.id.listview);
        progressBar = findViewById(R.id.progressBar);


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Get all uploaded data then add to our arraylist then update our listview;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    UsersInfo userData = postSnapshot.getValue(UsersInfo.class);
                    usersInfo.add(userData);
                    Log.d("results",usersInfo.toString());
                   // listview.setAdapter(new UsersListAdapter(UserList.this,usersInfo));
                }

                progressBar.setVisibility(View.INVISIBLE);

           }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("download error",databaseError.getMessage());

                Toast.makeText(UserList.this,"sorry could not upload data,try again later",Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.INVISIBLE);
            }
        });






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.userlist_menu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout){

            mAuth.signOut();
            Intent intent = new Intent(UserList.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
