package com.fermaivanovo.MyFarm.StartActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fermaivanovo.MyFarm.EcoFood.PersonalProfile;
import com.fermaivanovo.MyFarm.ItemDecoration.ItemDecorationStartEndReverse;
import com.fermaivanovo.MyFarm.ItemDecoration.ItemDecorationTopBottom;
import com.fermaivanovo.MyFarm.MyOrder.Order;
import com.fermaivanovo.MyFarm.ReflectionClasses.Model;
import com.fermaivanovo.MyFarm.ReflectionClasses.ViewHolder;
import com.fermaivanovo.MyFarm.ReflectionClasses.ViewHolderNews;
import com.fermaivanovo.MyFarm.Settings.Settings;
import com.fermaivanovo.MyFarm.Water.Water;
import com.fermaivanovo.MyFarm.loadData;
import com.fermaivanovo.R;
import com.fermaivanovo.MyFarm.ReflectionClasses.ModelNews;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import io.github.yavski.fabspeeddial.FabSpeedDial;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class StartActivity extends AppCompatActivity implements loadData {

    //DATABASE
    private LinearLayoutManager mLinearLayoutManagerNews;
    private RecyclerView mRecyclerViewNews;
    private FirebaseDatabase mFirebaseDatabaseNews;
    private DatabaseReference mReferenceNews;
    private FirebaseRecyclerAdapter<ModelNews, ViewHolderNews> firebaseRecyclerAdapterNews;
    private FirebaseRecyclerOptions<ModelNews> optionsNews;

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Model> options;

    private DatabaseReference reff;
    private ImageView mWater;
    private RotateLoading rotateLoading;
    private CardView CardViewNews, CardViewCategories;
    private FirebaseAuth mAuth;
    private FabSpeedDial fabSpeedDial;

    private String TYPE, Right_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_start);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Соединение…\t\t\t\t\t\t\t");
        rotateLoading = findViewById(R.id.rotateLoading);
        rotateLoading.start();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        CardViewNews = findViewById(R.id.CardViewNews);
        CardViewNews.setVisibility(View.INVISIBLE);
        CardViewCategories = findViewById(R.id.CardViewCategories);
        CardViewCategories.setVisibility(View.INVISIBLE);

        mWater = findViewById(R.id.mWater);
        fabSpeedDial = findViewById(R.id.fab_speed_dial);

        mLinearLayoutManagerNews = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLinearLayoutManagerNews.setReverseLayout(true);
        mLinearLayoutManagerNews.setStackFromEnd(true);
        mRecyclerViewNews = findViewById(R.id.recyclerViewNews);

        //Margin Start|End
        int space_news = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics());
        mRecyclerViewNews.addItemDecoration(new ItemDecorationStartEndReverse(space_news));

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recyclerView);

        //Margin Top|Bottom
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                getResources().getDisplayMetrics());
        mRecyclerView.addItemDecoration(new ItemDecorationTopBottom(space));

        loadFirebaseData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapterNews != null) {
            firebaseRecyclerAdapterNews.startListening();
        }
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String child = user.getUid();
            loadRights(child);
        } else {
            TYPE = "TYPE";
            Right_1 = "Right_1";
        }
    }

    private void loadRights(String child) {
        reff = FirebaseDatabase.getInstance().getReference("Users").child(child);
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TYPE = Objects.requireNonNull(dataSnapshot.child("type").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reff = FirebaseDatabase.getInstance().getReference("Rights");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Right_1 = Objects.requireNonNull(dataSnapshot.child("wqfBauMxm2Cj").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFirebaseData() {
        mFirebaseDatabaseNews = FirebaseDatabase.getInstance();
        mReferenceNews = mFirebaseDatabaseNews.getReference("News");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference("Addresses").child("Ферма в Иваново");
        showData();
        showEcoFood();
        loadImage();
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if (menuItem.getTitle().equals("Корзина")) {
                    startActivity(new Intent(getApplicationContext(), Order.class));
                }
                if (menuItem.getTitle().equals("Настройки")) {
                    startActivity(new Intent(getApplicationContext(), Settings.class));
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });
    }

    private void loadImage() {
        reff = FirebaseDatabase.getInstance().getReference("Static References");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String water = Objects.requireNonNull(dataSnapshot.child("water").getValue()).toString();
                    Picasso.get()
                            .load(water)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.image_not_available)
                            .into(mWater);
                    CardViewCategories.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        databaseError.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void showData() {

        optionsNews = new FirebaseRecyclerOptions.Builder<ModelNews>().setQuery(mReferenceNews, ModelNews.class).build();

        firebaseRecyclerAdapterNews = new FirebaseRecyclerAdapter<ModelNews, ViewHolderNews>(optionsNews) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderNews holder, int position, @NonNull ModelNews model) {
                holder.setDetailsNews(getApplicationContext(), model.getImage(), model.getTitle(), model.getDescription(), model.getDate(), model.getCode());
                if (model.getTitle().length() > 0) {
                    CardViewNews.setVisibility(View.VISIBLE);
                    rotateLoading.stop();
                    setTitle(R.string.app_name);
                }
            }

            @NonNull
            @Override
            public ViewHolderNews onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
                ViewHolderNews viewHolderNews = new ViewHolderNews(itemView);
                viewHolderNews
                        .setOnClickListenerNews(new ViewHolderNews.ClickListenerNews() {
                            @Override
                            public void onItemClickNews(View view, int position) {

                                View viewDialog = getLayoutInflater().inflate(R.layout.alert_dialog_news, null);
                                final Dialog dialog = new Dialog(StartActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                                dialog.setContentView(viewDialog);

                                ImageView close = dialog.findViewById(R.id.close);

                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.show();

                                ImageView fragmentImage = dialog.findViewById(R.id.fragmentImage);
                                TextView fragmentTitle = dialog.findViewById(R.id.fragmentTitle);
                                TextView fragmentDate = dialog.findViewById(R.id.fragmentDate);
                                TextView fragmentDescription = dialog.findViewById(R.id.fragmentDescription);

                                String Image = getItem(position).getImage();
                                String Title = getItem(position).getTitle();
                                String Date = getItem(position).getDate();
                                String Description = getItem(position).getDescription();

                                Picasso.get()
                                        .load(Image)
                                        .fit()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.image_not_available)
                                        .centerInside()
                                        .into(fragmentImage);

                                fragmentTitle.setText(Title);
                                fragmentDate.setText(Date);
                                fragmentDescription.setText(Description);
                            }

                            @Override
                            public void onItemLongClickGoods(final View view, final int position) {

                                if (TYPE.equals(Right_1)) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                                    builder.setNegativeButton("Отмена", null);
                                    builder.setTitle("Выберите действие");

                                    String[] order = {"Редактировать пост", "Удалить пост"};
                                    builder.setItems(order, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    String Image = getItem(position).getImage();
                                                    String Title = getItem(position).getTitle();
                                                    String Description = getItem(position).getDescription();
                                                    String Code = getItem(position).getCode();

                                                    Intent intent = new Intent(getApplicationContext(), AddEditNews.class);
                                                    intent.putExtra("image", Image);
                                                    intent.putExtra("title", Title);
                                                    intent.putExtra("description", Description);
                                                    intent.putExtra("code", Code);
                                                    intent.putExtra("ACTION", "EDIT");
                                                    startActivity(intent);
                                                    break;
                                                case 1:

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                                                    builder.setTitle("Удаление");
                                                    builder.setMessage("Вы точно хотите удалить?");
                                                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            String KEY = getItem(position).getCode();
                                                            String URLIMAGE = getItem(position).getImage();

                                                            Query mQueryRemove = mReferenceNews.orderByChild("code").equalTo(KEY);
                                                            mQueryRemove.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                        ds.getRef().removeValue();
                                                                    }
                                                                    Toast success = Toasty.success(getApplicationContext(),
                                                                            "Пост успешно удалён!",
                                                                            Toast.LENGTH_SHORT);
                                                                    success.setGravity(Gravity.CENTER, 0, 0);
                                                                    success.show();
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    Toast toast = Toast.makeText(getApplicationContext(),
                                                                            databaseError.getMessage(), Toast.LENGTH_LONG);
                                                                    toast.show();
                                                                }
                                                            });

                                                            StorageReference mStorageReference = getInstance().getReferenceFromUrl(URLIMAGE);
                                                            mStorageReference.delete()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast success = Toasty.success(getApplicationContext(),
                                                                                    "Изображение удалено",
                                                                                    Toast.LENGTH_SHORT);
                                                                            success.setGravity(Gravity.CENTER, 0, 0);
                                                                            success.show();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast error = Toasty.error(getApplicationContext(),
                                                                                    "Ошибка удаления Изображения",
                                                                                    Toast.LENGTH_SHORT);
                                                                            error.setGravity(Gravity.CENTER, 0, 0);
                                                                            error.show();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                                    builder.setNegativeButton("Нет", null);
                                                    builder.create().show();
                                                    break;
                                            }
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        });
                return viewHolderNews;
            }
        };
        mRecyclerViewNews.setLayoutManager(mLinearLayoutManagerNews);
        firebaseRecyclerAdapterNews.startListening();
        mRecyclerViewNews.setAdapter(firebaseRecyclerAdapterNews);
    }

    private void showEcoFood() {

        options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(mReference, Model.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Model model) {
                holder.setDetails(getApplicationContext(), model.getTitle(), model.getImage());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_location_item, parent, false);
                ViewHolder viewHolder = new ViewHolder(itemView);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        String Title = getItem(position).getTitle();
                        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(ADDRESS, Title).apply();
                        startActivity(new Intent(getApplicationContext(), PersonalProfile.class));
                    }
                });
                return viewHolder;
            }
        };
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void openShop(View v) {
        startActivity(new Intent(getApplicationContext(), Water.class));
    }
}
