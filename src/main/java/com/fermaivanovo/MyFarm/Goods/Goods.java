package com.fermaivanovo.MyFarm.Goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fermaivanovo.MyFarm.EcoFood.PersonalProfile;
import com.fermaivanovo.MyFarm.MyOrder.Order;
import com.fermaivanovo.MyFarm.ReflectionClasses.ModelGoods;
import com.fermaivanovo.MyFarm.ReflectionClasses.PostGoods;
import com.fermaivanovo.MyFarm.ReflectionClasses.ViewHolderGoods;
import com.fermaivanovo.MyFarm.loadData;
import com.fermaivanovo.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import es.dmoral.toasty.Toasty;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class Goods extends AppCompatActivity implements loadData {

    private StaggeredGridLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference, reff;
    private FirebaseRecyclerAdapter<ModelGoods, ViewHolderGoods> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<ModelGoods> options;
    private FirebaseAuth mAuth;

    private RotateLoading rotateLoading;
    private String KEY, URLIMAGE;
    private String TYPE, Right_1, Right_2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_goods);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rotateLoading = findViewById(R.id.rotateLoading);
        rotateLoading.start();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //FeaturedList
        mLinearLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), Order.class));
        return true;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String child = user.getUid();
            loadRights(child);
        } else {
            TYPE = "TYPE";
            Right_1 = "Right_1";
            Right_2 = "Right_2";
            loadGoods();
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
                    Right_2 = Objects.requireNonNull(dataSnapshot.child("aPJrHC5q4WKO").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        loadGoods();
    }

    private void loadGoods() {

        SharedPreferences sharedPreferences_2 = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
        String CurrentAddress = sharedPreferences_2.getString(ADDRESS, "");

        SharedPreferences sharedPreferences_3 = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
        String GoodsCategory = sharedPreferences_3.getString(GOODSCATEGORY, "");

        setTitle(GoodsCategory);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference("Goods").child("Ферма в Иваново").child(CurrentAddress).child(GoodsCategory);
        showData();
    }

    private void showData() {

        options = new FirebaseRecyclerOptions.Builder<ModelGoods>().setQuery(mReference, ModelGoods.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ModelGoods, ViewHolderGoods>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderGoods holder, int position, @NonNull ModelGoods model) {
                holder.setDetailsGoods(getApplicationContext(), model.getImage(), model.getTitle(), model.getPrice(), model.getDescription(),
                        model.getComposition(), model.getTerm(), model.getAvailability(), model.getCode());
                if (model.getTitle().length() > 0) {
                    rotateLoading.stop();
                }
            }

            @NonNull
            @Override
            public ViewHolderGoods onCreateViewHolder(@NonNull final ViewGroup parent, int i) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_item, parent, false);
                ViewHolderGoods viewHolderGoods = new ViewHolderGoods(itemView);
                viewHolderGoods.setOnClickListenerGoods(new ViewHolderGoods.ClickListenerGoods() {
                    @Override
                    public void onItemClickGoods(View view, int position) {

                        URLIMAGE = getItem(position).getImage();
                        final String Availability = getItem(position).getAvailability();
                        final String Title = getItem(position).getTitle();
                        final String price = getItem(position).getPrice();
                        String Price = getItem(position).getPrice();
                        String Description = getItem(position).getDescription();
                        String Composition = getItem(position).getComposition();
                        String Term = getItem(position).getTerm();

                        View viewDialog = getLayoutInflater().inflate(R.layout.alert_dialog_add_to_cart, null);
                        final Dialog dialog = new Dialog(Goods.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                        dialog.setContentView(viewDialog);

                        ImageView close = dialog.findViewById(R.id.close);
                        ImageView addToCart = dialog.findViewById(R.id.addToCart);

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        addToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //GroceryDBHelper dbHelper = new GroceryDBHelper(Menu.this);
                                //mDatabase = dbHelper.getWritableDatabase();


                                //if (!Restaurant.equals(RestaurantAdded)) {
                                //    editor_RestaurantAdded.putString(ADDEDRESTAURANT, Restaurant).apply();
                                //    mDatabase.execSQL("delete from " + GroceryContract.GroceryEntry.TABLE_NAME);
                                //}

                                int Price = Integer.parseInt(price.replaceAll("\\D+", ""));

                                if (!Availability.equals("Закончилось")) {
                                    //ContentValues cv = new ContentValues();
                                    //cv.put(GroceryContract.GroceryEntry.COLUMN_NAME, Title);
                                    //cv.put(GroceryContract.GroceryEntry.COLUMN_AMOUNT, Price);
                                    //mDatabase.insert(GroceryContract.GroceryEntry.TABLE_NAME, null, cv);

                                    Toast success = Toasty.success(getApplicationContext(),
                                            Title + ", добавлено к текущему заказу",
                                            Toast.LENGTH_SHORT);
                                    success.setGravity(Gravity.CENTER, 0, 0);
                                    success.show();
                                } else {
                                    Toast info = Toasty.info(getApplicationContext(),
                                            "Товар закончился на данный момент",
                                            Toast.LENGTH_SHORT);
                                    info.setGravity(Gravity.CENTER, 0, 0);
                                    info.show();
                                }
                            }
                        });

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        ImageView fragmentImage = dialog.findViewById(R.id.fragmentImage);
                        TextView fragmentTitle = dialog.findViewById(R.id.fragmentTitle);
                        TextView fragmentPrice = dialog.findViewById(R.id.fragmentPrice);
                        TextView fragmentDescription = dialog.findViewById(R.id.fragmentDescription);
                        TextView fragmentComposition = dialog.findViewById(R.id.fragmentComposition);
                        TextView fragmentTerm = dialog.findViewById(R.id.fragmentTerm);
                        TextView fragmentAvailability = dialog.findViewById(R.id.fragmentAvailability);

                        Picasso.get()
                                .load(URLIMAGE)
                                .fit()
                                .placeholder(R.drawable.logo)
                                .error(R.drawable.image_not_available)
                                .centerCrop()
                                .into(fragmentImage);
                        //
                        if (Title.length() > 0) {
                            fragmentTitle.setText(Title);
                        } else {
                            fragmentTitle.setText("---");
                        }
                        //
                        if (Price.length() > 0) {
                            fragmentPrice.setText(Price);
                        } else {
                            fragmentPrice.setText("0");
                        }
                        //
                        if (Description.length() > 0) {
                            fragmentDescription.setText(Description);
                        } else {
                            fragmentDescription.setText("Нет описания");
                        }
                        //
                        if (Composition.length() > 0) {
                            fragmentComposition.setText(Composition);
                        } else {
                            fragmentComposition.setText("Не указан");
                        }
                        //
                        if (Term.length() > 0) {
                            fragmentTerm.setText(Term);
                        } else {
                            fragmentTerm.setText("Неизвестно");
                        }
                        //
                        if (Availability.length() > 0) {
                            switch (Availability) {
                                case "В наличии":
                                    fragmentAvailability.setText(Availability);
                                    fragmentAvailability.setTextColor(Color.parseColor("#00A046"));
                                    break;
                                case "Заканчивается":
                                    fragmentAvailability.setText(Availability);
                                    fragmentAvailability.setTextColor(Color.parseColor("#FFA900"));
                                    break;
                                case "Закончилось":
                                    fragmentAvailability.setText(Availability);
                                    fragmentAvailability.setTextColor(Color.parseColor("#d32f2f"));
                                    break;
                                default:
                                    fragmentAvailability.setText("Неизвестно");
                                    fragmentAvailability.setTextColor(Color.parseColor("#757575"));
                            }
                        } else {
                            fragmentAvailability.setText("---");
                        }
                    }

                    @Override
                    public void onItemLongClickGoods(final View view, final int position) {

                        if (TYPE.equals(Right_1)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Goods.this);
                            builder.setNegativeButton("Отмена", null);
                            builder.setTitle("Выберите действие");

                            String[] order = {"Редактировать товар", "Удалить товар"};
                            builder.setItems(order, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:

                                            URLIMAGE = getItem(position).getImage();
                                            String Title = getItem(position).getTitle();
                                            String Price = getItem(position).getPrice();
                                            String Description = getItem(position).getDescription();
                                            String Composition = getItem(position).getComposition();
                                            String Term = getItem(position).getTerm();
                                            String Availability = getItem(position).getAvailability();
                                            String Code = getItem(position).getCode();

                                            String SWITCH = "EDIT";
                                            Intent intent = new Intent(view.getContext(), AddEditGoods.class);
                                            intent.putExtra("SWITCH", SWITCH);
                                            intent.putExtra("image", URLIMAGE);
                                            intent.putExtra("title", Title);
                                            intent.putExtra("price", Price);
                                            intent.putExtra("description", Description);
                                            intent.putExtra("composition", Composition);
                                            intent.putExtra("term", Term);
                                            intent.putExtra("availability", Availability);
                                            intent.putExtra("code", Code);
                                            startActivity(intent);
                                            break;
                                        case 1:

                                            AlertDialog.Builder builder = new AlertDialog.Builder(Goods.this);
                                            builder.setTitle("Удаление");
                                            builder.setMessage("Вы точно хотите удалить?");
                                            builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    KEY = getItem(position).getCode();
                                                    URLIMAGE = getItem(position).getImage();

                                                    Query mQueryRemove = mReference.orderByChild("code").equalTo(KEY);
                                                    mQueryRemove.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                ds.getRef().removeValue();
                                                            }
                                                            Toast success = Toasty.success(getApplicationContext(),
                                                                    "Товар успешно удалён!",
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
                        if (TYPE.equals(Right_2)) {
                            KEY = getItem(position).getCode();

                            AlertDialog.Builder builder_seller = new AlertDialog.Builder(Goods.this);
                            builder_seller.setNegativeButton("Отмена", null);
                            builder_seller.setTitle("Наличие товара");

                            String[] order_seller = {"В наличии", "Заканчивается", "Закончилось"};
                            builder_seller.setItems(order_seller, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            Query mQuery_1 = mReference.orderByChild("code").equalTo(KEY);
                                            mQuery_1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        ds.getRef().child("availability").setValue("В наличии");
                                                    }
                                                    Toast success = Toasty.success(getApplicationContext(),
                                                            "Изменения вступят в силу в ближайшее время",
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
                                            break;
                                        case 1:
                                            Query mQuery_2 = mReference.orderByChild("code").equalTo(KEY);
                                            mQuery_2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        ds.getRef().child("availability").setValue("Заканчивается");
                                                    }
                                                    Toast success = Toasty.success(getApplicationContext(),
                                                            "Изменения вступят в силу в ближайшее время",
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
                                            break;
                                        case 2:
                                            Query mQuery_3 = mReference.orderByChild("code").equalTo(KEY);
                                            mQuery_3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        ds.getRef().child("availability").setValue("Закончилось");
                                                    }
                                                    Toast success = Toasty.success(getApplicationContext(),
                                                            "Изменения вступят в силу в ближайшее время",
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
                                            break;
                                    }
                                }
                            });
                            AlertDialog dialog_seller = builder_seller.create();
                            dialog_seller.show();
                        }
                    }
                });
                return viewHolderGoods;
            }
        };
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
