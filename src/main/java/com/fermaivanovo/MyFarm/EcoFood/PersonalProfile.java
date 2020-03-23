package com.fermaivanovo.MyFarm.EcoFood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fermaivanovo.MyFarm.Goods.CategoriesGoods;
import com.fermaivanovo.MyFarm.MyOrder.Order;
import com.fermaivanovo.MyFarm.ReflectionClasses.ModelProfile;
import com.fermaivanovo.MyFarm.ReflectionClasses.Post;
import com.fermaivanovo.MyFarm.ReflectionClasses.ViewHolderProfile;
import com.fermaivanovo.MyFarm.loadData;
import com.fermaivanovo.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.victor.loading.rotate.RotateLoading;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class PersonalProfile extends AppCompatActivity implements loadData {

    private LinearLayoutManager mLinearLayoutManagerProfile;
    private RecyclerView mRecyclerViewProfile;
    private FirebaseDatabase mFirebaseDatabaseProfile;
    private DatabaseReference mReferenceProfile;
    private FirebaseRecyclerAdapter<ModelProfile, ViewHolderProfile> firebaseRecyclerAdapterProfile;
    private FirebaseRecyclerOptions<ModelProfile> optionsProfile;

    private LinearLayoutManager mLinearLayoutManagerReviews;
    private RecyclerView mRecyclerViewReviews;
    private FirebaseDatabase mFirebaseDatabaseReviews;
    private DatabaseReference mReferenceReviews;
    private FirebaseRecyclerAdapter<Post, ViewHolderReviews> firebaseRecyclerAdapterReviews;
    private FirebaseRecyclerOptions<Post> optionsReviews;
    private FirebaseAuth mAuth;

    private CardView mCardTypeReviews;
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_personal_profile);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mCardTypeReviews = findViewById(R.id.mCardTypeReviews);
        rotateLoading = findViewById(R.id.rotateLoading);
        rotateLoading.start();
        loadAddresses();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapterProfile != null) {
            firebaseRecyclerAdapterProfile.startListening();
        }
        if (firebaseRecyclerAdapterReviews != null) {
            firebaseRecyclerAdapterReviews.startListening();
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String child = user.getUid();
        }
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


    private void loadAddresses() {

        Button moreReviews = findViewById(R.id.moreReviews);
        moreReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReviews();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
        String CurrentAddress = sharedPreferences.getString(ADDRESS, "");

        mLinearLayoutManagerProfile = new LinearLayoutManager(this);
        mRecyclerViewProfile = findViewById(R.id.mRecyclerViewProfile);

        mFirebaseDatabaseProfile = FirebaseDatabase.getInstance();
        mReferenceProfile = mFirebaseDatabaseProfile.getReference("Profiles").child("Ферма в Иваново").child(CurrentAddress);

        mLinearLayoutManagerReviews = new LinearLayoutManager(this);
        mLinearLayoutManagerReviews.setReverseLayout(true);
        mLinearLayoutManagerReviews.setStackFromEnd(true);
        mRecyclerViewReviews = findViewById(R.id.mRecyclerViewReviews);

        mFirebaseDatabaseReviews = FirebaseDatabase.getInstance();
        mReferenceReviews = mFirebaseDatabaseReviews.getReference("All Reviews").child("Ферма в Иваново").child(CurrentAddress).child("Отзывы");

        showData();
    }

    private void showData() {
        optionsProfile = new FirebaseRecyclerOptions.Builder<ModelProfile>().setQuery(mReferenceProfile, ModelProfile.class).build();
        firebaseRecyclerAdapterProfile = new FirebaseRecyclerAdapter<ModelProfile, ViewHolderProfile>(optionsProfile) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderProfile holder, int position, @NonNull ModelProfile model) {
                holder.setDetailsProfile(getApplicationContext(), model.getImage(), model.getTitle(), model.getAddress(), model.getPhone(), model.getEmail(), model.getDescription(), model.getMo(),
                        model.getTu(), model.getWe(), model.getTh(), model.getFr(), model.getSa(), model.getSu());
                if (model.getTitle().length() > 0) {
                    mCardTypeReviews.setVisibility(View.VISIBLE);
                    rotateLoading.stop();
                    displayReviews();
                }
            }

            @NonNull
            @Override
            public ViewHolderProfile onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
                ViewHolderProfile viewHolderProfile = new ViewHolderProfile(itemView);
                viewHolderProfile.setOnClickListenerProfile(new ViewHolderProfile.ClickListenerProfile() {

                    @Override
                    public void toMap(View view, int position) {
                        TextView mTitle = findViewById(R.id.mTitle);
                        TextView mAddress = findViewById(R.id.mAddress);
                        String Name = mTitle.getText().toString();
                        String Address = mAddress.getText().toString();

                        Uri location = Uri.parse("geo:0,0?q=" + Address);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                        PackageManager packageManager = getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (isIntentSafe) {
                            startActivity(mapIntent);
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Name.replace("&", "%26") + ",\t" + Address));
                            startActivity(browserIntent);
                        }
                    }

                    @Override
                    public void toGoods(View view, int position) {
                        startActivity(new Intent(getApplicationContext(), CategoriesGoods.class));
                    }

                    @Override
                    public void moreInformation(View view, int position) {
                        ExpandableRelativeLayout expandableLayoutType = findViewById(R.id.expandableLayoutInformation);
                        expandableLayoutType.toggle();
                    }

                    @SuppressLint("IntentReset")
                    @Override
                    public void sendEmail(View view, int position) {
                        TextView sentEmail = findViewById(R.id.mEmail);
                        String Email = sentEmail.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setData(Uri.parse("mailto:"));
                        String[] to = {Email};
                        intent.putExtra(Intent.EXTRA_EMAIL, to);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Отзывы/Предложения");
                        intent.setType("message/rfc822");
                        startActivity(Intent.createChooser(intent, "Выберите почтовый клиент"));
                    }

                    @Override
                    public void makeCall(View view, int position) {
                        TextView makeCall = findViewById(R.id.mPhone);
                        String Call = makeCall.getText().toString();
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", Call, null)));
                    }

                    @Override
                    public void moreDescription(View view, int position) {
                        ExpandableRelativeLayout expandableLayoutDescription = findViewById(R.id.expandableLayoutDescription);
                        expandableLayoutDescription.toggle();
                    }

                    @Override
                    public void moreTime(View view, int position) {
                        ExpandableRelativeLayout expandableLayoutTime = findViewById(R.id.expandableLayoutTime);
                        expandableLayoutTime.toggle();
                    }
                });
                return viewHolderProfile;
            }
        };
        ViewCompat.setNestedScrollingEnabled(mRecyclerViewProfile, false);
        mRecyclerViewProfile.setLayoutManager(mLinearLayoutManagerProfile);
        firebaseRecyclerAdapterProfile.startListening();
        mRecyclerViewProfile.setAdapter(firebaseRecyclerAdapterProfile);
    }


    private void postReviews() {

        View view = getLayoutInflater().inflate(R.layout.alert_dialog_reviews, null);
        final Dialog dialogReviews = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialogReviews.setContentView(view);

        ImageView closeDialog = view.findViewById(R.id.closeDialog);
        Button btn_post = view.findViewById(R.id.btn_post);
        TextView restaurantReviews = view.findViewById(R.id.restaurantReviews);
        restaurantReviews.setText("Ферма в Иваново");
        final TextView countSymbols = view.findViewById(R.id.countSymbols);
        final EditText editTextContent = view.findViewById(R.id.editTextContent);
        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                countSymbols.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        final RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogReviews.dismiss();
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = "Гость";
                String content = editTextContent.getText().toString().trim();
                String date = new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(new Date());
                float rating = ratingBar.getRating();

                //TIME
                SharedPreferences sharedPreferences = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int pastTime = sharedPreferences.getInt(PASTTIME, 0);

                Date currentDate = new Date();
                DateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.getDefault());
                String timeText = timeFormat.format(currentDate);
                int currentTime = Integer.parseInt(timeText);
                int Time = Math.abs(currentTime - pastTime);

                if (rating != 0 && Time >= 3000) {

                    if (content.trim().length() == 0) {

                        if (rating == 1.0) {
                            content = "Ужасно";
                        }
                        if (rating == 2.0) {
                            content = "Плохо";
                        }
                        if (rating == 3.0) {
                            content = "Нормально";
                        }
                        if (rating == 4.0) {
                            content = "Хорошо";
                        }
                        if (rating == 5.0) {
                            content = "Отлично";
                        }
                    }

                    Post post = new Post(title, content, date, rating);
                    mReferenceReviews.push().setValue(post);
                    firebaseRecyclerAdapterReviews.notifyDataSetChanged();

                    Toast success = Toasty.success(getApplicationContext(),
                            "Благодарим за отзыв!",
                            Toast.LENGTH_LONG);
                    success.setGravity(Gravity.CENTER, 0, 0);
                    success.show();
                    dialogReviews.dismiss();

                    //TIME
                    currentDate = new Date();
                    timeFormat = new SimpleDateFormat("HHmmss", Locale.getDefault());
                    timeText = timeFormat.format(currentDate);
                    editor.putInt(PASTTIME, pastTime = Integer.parseInt(timeText)).apply();

                } else {
                    if (rating == 0) {
                        Toast info = Toasty.info(getApplicationContext(),
                                "Пожалуйста поставьте рейтинг заведения",
                                Toast.LENGTH_LONG);
                        info.setGravity(Gravity.CENTER, 0, 0);
                        info.show();
                    } else {
                        Toast info = Toasty.info(getApplicationContext(),
                                "Коментарии можно оставлять раз в 30 минут",
                                Toast.LENGTH_LONG);
                        info.setGravity(Gravity.CENTER, 0, 0);
                        info.show();
                    }
                }

            }
        });

        dialogReviews.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReviews.show();
    }

    private void displayReviews() {

        optionsReviews = new FirebaseRecyclerOptions.Builder<Post>().setQuery(mReferenceReviews, Post.class).build();

        firebaseRecyclerAdapterReviews = new FirebaseRecyclerAdapter<Post, ViewHolderReviews>(optionsReviews) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderReviews holder, int position, @NonNull Post model) {
                holder.mName.setText(model.getTitle());
                holder.reviewsContent.setText(model.getContent());
                holder.mDate.setText(model.getDate());
                holder.mRating.setRating(model.getRating());
            }

            @NonNull
            @Override
            public ViewHolderReviews onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
                ViewHolderReviews viewHolderReviews = new ViewHolderReviews(itemView);
                viewHolderReviews.setOnClickListenerReviews(new ViewHolderReviews.ClickListenerReviews() {

                    @Override
                    public void onItemClickReviews(View view, int position) {
                        TextView reviewsContent = view.findViewById(R.id.reviewsContent);
                        reviewsContent.setMaxLines(Integer.MAX_VALUE);
                    }


                });
                return viewHolderReviews;
            }
        };
        ViewCompat.setNestedScrollingEnabled(mRecyclerViewReviews, false);
        mRecyclerViewReviews.setLayoutManager(mLinearLayoutManagerReviews);
        firebaseRecyclerAdapterReviews.startListening();
        mRecyclerViewReviews.setAdapter(firebaseRecyclerAdapterReviews);
    }

}
