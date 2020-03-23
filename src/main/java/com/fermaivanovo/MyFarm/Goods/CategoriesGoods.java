package com.fermaivanovo.MyFarm.Goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fermaivanovo.MyFarm.EcoFood.PersonalProfile;
import com.fermaivanovo.MyFarm.ReflectionClasses.Model;
import com.fermaivanovo.MyFarm.ReflectionClasses.ViewHolder;
import com.fermaivanovo.MyFarm.loadData;
import com.fermaivanovo.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.victor.loading.rotate.RotateLoading;

public class CategoriesGoods extends AppCompatActivity implements loadData {

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Model> options;

    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_categories);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Наша продукция");

        rotateLoading = findViewById(R.id.rotateLoading);
        rotateLoading.start();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference("Categories");
        showCategories();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    private void showCategories() {

        options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(mReference, Model.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Model model) {
                holder.setDetails(getApplicationContext(), model.getTitle(), model.getImage());
                if (model.getTitle().length() > 0) {
                    rotateLoading.stop();
                }
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item, parent, false);
                ViewHolder viewHolder = new ViewHolder(itemView);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {


                        String EDIT = getIntent().getStringExtra("ACTION");
                        if (EDIT == null) {
                            String Title = getItem(position).getTitle();
                            SharedPreferences sharedPreferences = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(GOODSCATEGORY, Title).apply();
                            startActivity(new Intent(getApplicationContext(), Goods.class));
                        } else {
                            String SWITCH = "ADD";
                            String Title = getItem(position).getTitle();
                            SharedPreferences sharedPreferences = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(GOODSCATEGORY, Title).apply();
                            Intent intent = new Intent(view.getContext(), AddEditGoods.class);
                            intent.putExtra("SWITCH", SWITCH);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                return viewHolder;
            }
        };
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}
