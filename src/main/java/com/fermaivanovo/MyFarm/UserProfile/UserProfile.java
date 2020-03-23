package com.fermaivanovo.MyFarm.UserProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fermaivanovo.MyFarm.Goods.CategoriesGoods;
import com.fermaivanovo.MyFarm.StartActivity.AddEditNews;
import com.fermaivanovo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reff;
    private CircleImageView profileImage;
    private Button signOut, addGoodsBtn, addNewsBtn, addProfileBtn, addCategoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_user_profile);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        profileImage = findViewById(R.id.profileImage);

        addNewsBtn = findViewById(R.id.addNewsBtn);
        addGoodsBtn = findViewById(R.id.addGoodsBtn);
        addProfileBtn = findViewById(R.id.addProfileBtn);
        addCategoryBtn= findViewById(R.id.addCategoryBtn);
        signOut = findViewById(R.id.signOut);

        onClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater_admin = getMenuInflater();
        inflater_admin.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String child = user.getUid();
            setData(child);
        }
    }

    private void setData(String child) {
        reff = FirebaseDatabase.getInstance().getReference("Users").child(child);
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                    String login = Objects.requireNonNull(dataSnapshot.child("login").getValue()).toString();
                    String type = Objects.requireNonNull(dataSnapshot.child("type").getValue()).toString();
                    Picasso.get()
                            .load(image)
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.progress_image)
                            .error(R.drawable.image_not_available)
                            .into(profileImage);
                    TextView mLogin = findViewById(R.id.mLogin);
                    mLogin.setText(login);
                    setTitle(type);
                    loadRights(type);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadRights(final String type) {
        reff = FirebaseDatabase.getInstance().getReference("Rights");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String rights = Objects.requireNonNull(dataSnapshot.child("wqfBauMxm2Cj").getValue()).toString();
                    if (type.equals(rights)) {
                        addNewsBtn.setVisibility(View.VISIBLE);
                        addGoodsBtn.setVisibility(View.VISIBLE);
                        addProfileBtn.setVisibility(View.VISIBLE);
                        addCategoryBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onClick() {
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                builder.setTitle("Будет выполнен выход из аккаунта");
                builder.setMessage("Хотите продолжить?");
                builder.setNegativeButton("Нет", null);
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                });
                builder.create().show();
            }
        });

        addGoodsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoods();
            }
        });

        addNewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNews();
            }
        });

        addProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void addNews() {
        Intent intent = new Intent(getApplicationContext(), AddEditNews.class);
        intent.putExtra("ACTION", "ADD");
        startActivity(intent);
    }


    private void addGoods() {
        Intent intent = new Intent(getApplicationContext(), CategoriesGoods.class);
        intent.putExtra("ACTION", "ADD");
        startActivity(intent);
    }


}
