package com.fermaivanovo.MyFarm.Goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fermaivanovo.MyFarm.ReflectionClasses.PostGoods;
import com.fermaivanovo.MyFarm.loadData;
import com.fermaivanovo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class AddEditGoods extends AppCompatActivity implements loadData {

    private ImageView mImage;
    private TextInputLayout mTitle, mPrice, mDescription, mComposition, mTerm;
    private Spinner coloredSpinner;
    private String Availability;
    private String SWITCH, KEY;

    //Database
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    //Storage
    private StorageReference mStorageReference;
    private String mStoragePath = "Goods/";

    //Uri
    private Uri mFilePathUri;

    private ProgressDialog progressDialog;

    private boolean checkPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_add_edit_goods);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loadData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater_admin = getMenuInflater();
        inflater_admin.inflate(R.menu.confirm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (SWITCH.equals("ADD")) {
            uploadData();
        } else {
            uploadEditedData();
        }
        return true;
    }

    private void loadData() {

        mImage = findViewById(R.id.mImage);
        mTitle = findViewById(R.id.mTitle);
        mPrice = findViewById(R.id.mPrice);
        coloredSpinner = findViewById(R.id.coloredSpinner);
        mDescription = findViewById(R.id.mDescription);
        mComposition = findViewById(R.id.mComposition);
        mTerm = findViewById(R.id.mTerm);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewDialog = getLayoutInflater().inflate(R.layout.alert_dialog_full_screen, null);
                final Dialog dialog = new Dialog(AddEditGoods.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                dialog.setContentView(viewDialog);

                ImageView imageFullScreen = dialog.findViewById(R.id.imageFullScreen);

                Drawable mDrawable = mImage.getDrawable();
                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                imageFullScreen.setImageBitmap(mBitmap);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        SharedPreferences sharedPreferences_2 = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
        String CurrentAddress = sharedPreferences_2.getString(ADDRESS, "");

        SharedPreferences sharedPreferences_3 = getSharedPreferences(SETTINGSFARM, MODE_PRIVATE);
        String GoodsCategory = sharedPreferences_3.getString(GOODSCATEGORY, "");

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Goods").child("Ферма в Иваново").child("улица Сахарова, 1д").child(GoodsCategory);

        progressDialog = new ProgressDialog(AddEditGoods.this);
        progressDialog.setCancelable(false);

        SWITCH = getIntent().getStringExtra("SWITCH");
        if (SWITCH.equals("ADD")) {
            addGoods();
        } else {
            editGoods();
        }
    }

    private void uploadData() {

        final String finalTitle = mTitle.getEditText().getText().toString().trim();
        final String finalPrice = mPrice.getEditText().getText().toString().trim();

        if (finalTitle.isEmpty() || finalPrice.isEmpty()) {
            if (finalTitle.isEmpty()) {
                mTitle.setError("Поле не может быть пустым");
            } else {
                mTitle.setErrorEnabled(false);
            }
            if (finalPrice.isEmpty()) {
                mPrice.setError("Поле не может быть пустым");
            } else {
                mPrice.setErrorEnabled(false);
            }
        } else {
            mTitle.setErrorEnabled(false);
            mPrice.setErrorEnabled(false);
            if (finalPrice.length() > 7) {
                mPrice.setError("Слишком большая цена");
            } else {
                mPrice.setErrorEnabled(false);
                resizeImage();
                if (mFilePathUri != null && checkPermission == true) {
                    progressDialog.setTitle("Завершение...");
                    progressDialog.show();

                    StorageReference storageReference2nd = mStorageReference.child(mStoragePath + finalTitle + ".jpeg");
                    storageReference2nd.putFile(mFilePathUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    Uri downloadUrl = urlTask.getResult();

                                    String title = finalTitle;
                                    String price = finalPrice;
                                    String description = mDescription.getEditText().getText().toString().trim();
                                    String composition = mComposition.getEditText().getText().toString().trim();
                                    String term = mTerm.getEditText().getText().toString().trim();
                                    String code = KEY;

                                    if (description.length() == 0) {
                                        description = "Нет описания";
                                    }
                                    if (composition.length() == 0) {
                                        composition = "Не указан";
                                    }
                                    if (term.length() == 0) {
                                        term = "Неизвестно";
                                    }
                                    if (code.length() == 0) {
                                        code = getPassword();
                                    }

                                    Toast success = Toasty.success(getApplicationContext(),
                                            "Товар успешно добавлен!",
                                            Toast.LENGTH_SHORT);
                                    success.setGravity(Gravity.CENTER, 0, 0);
                                    success.show();

                                    PostGoods postGoods = new PostGoods(downloadUrl.toString(), title, Availability, price, description, composition, term, code);
                                    String imageUploadId = mDatabaseReference.push().getKey();
                                    mDatabaseReference.child(imageUploadId).setValue(postGoods);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast error = Toasty.error(AddEditGoods.this,
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT);
                                    error.setGravity(Gravity.CENTER, 0, 0);
                                    error.show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.setTitle("Идет публикация...");
                                }
                            });
                } else {
                    if (mFilePathUri == null) {
                        Toast error = Toasty.error(getApplicationContext(),
                                "Вы не выбрали изображение!",
                                Toast.LENGTH_SHORT);
                        error.setGravity(Gravity.CENTER, 0, 0);
                        error.show();
                        openMedia();
                    }
                }

            }
        }
    }

    private void uploadEditedData() {

        final String title = mTitle.getEditText().getText().toString().trim();
        final String price = mPrice.getEditText().getText().toString().trim();
        String description = mDescription.getEditText().getText().toString().trim();
        String composition = mComposition.getEditText().getText().toString().trim();
        String term = mTerm.getEditText().getText().toString().trim();
        if (description.length() == 0) {
            description = "Нет описания";
        }
        if (composition.length() == 0) {
            composition = "Не указан";
        }
        if (term.length() == 0) {
            term = "Неизвестно";
        }
        if (title.isEmpty() || price.isEmpty()) {
            if (title.isEmpty()) {
                mTitle.setError("Поле не может быть пустым");
            } else {
                mTitle.setErrorEnabled(false);
            }
            if (price.isEmpty()) {
                mPrice.setError("Поле не может быть пустым");
            } else {
                mPrice.setErrorEnabled(false);
            }
        } else {
            mTitle.setErrorEnabled(false);
            mPrice.setErrorEnabled(false);
            if (price.length() > 7) {
                mPrice.setError("Слишком большая цена");
            } else {
                mPrice.setErrorEnabled(false);

                final String finalDescription = description;
                final String finalComposition = composition;
                final String finalTerm = term;

                Query mQuery = mDatabaseReference.orderByChild("code").equalTo(KEY);
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().child("title").setValue(title);
                            ds.getRef().child("price").setValue(price);
                            ds.getRef().child("availability").setValue(Availability);
                            ds.getRef().child("description").setValue(finalDescription);
                            ds.getRef().child("composition").setValue(finalComposition);
                            ds.getRef().child("term").setValue(finalTerm);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                databaseError.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }

                });
                Toast success = Toasty.success(getApplicationContext(),
                        "Изменения вступят в силу в ближайшее время",
                        Toast.LENGTH_SHORT);
                success.setGravity(Gravity.CENTER, 0, 0);
                success.show();
                finish();
            }
        }
    }

    private void editGoods() {

        setTitle("Редактировать товар");
        String Image = getIntent().getStringExtra("image");
        final String Title = getIntent().getStringExtra("title");
        String Price = getIntent().getStringExtra("price");
        final String Description = getIntent().getStringExtra("description");
        String Composition = getIntent().getStringExtra("composition");
        String Term = getIntent().getStringExtra("term");
        Availability = getIntent().getStringExtra("availability");
        KEY = getIntent().getStringExtra("code");

        Picasso.get()
                .load(Image)
                .fit()
                .placeholder(R.drawable.logo)
                .error(R.drawable.image_not_available)
                .centerCrop()
                .into(mImage);
        mTitle.getEditText().setText(Title);
        mPrice.getEditText().setText(Price);
        mDescription.getEditText().setText(Description);
        mComposition.getEditText().setText(Composition);
        mTerm.getEditText().setText(Term);

        setSpinner();
        switch (Availability) {
            case "В наличии":
                coloredSpinner.setSelection(0);
                break;
            case "Заканчивается":
                coloredSpinner.setSelection(1);
                break;
            case "Закончилось":
                coloredSpinner.setSelection(2);
                break;
        }
    }

    private void addGoods() {

        setTitle("Добавить товар");
        setSpinner();

        mImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openMedia();
                return true;
            }
        });
        KEY = getPassword();
        openMedia();
    }

    private void setSpinner() {
        // Настраиваем адаптер
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Availability, R.layout.color_spiner);
        adapter.setDropDownViewResource(R.layout.color_dropdown_spinner);
        // Вызываем адаптер
        coloredSpinner.setAdapter(adapter);
        //Устанавливаем цвет треугольника Spinner
        coloredSpinner.getBackground().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);

        coloredSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Availability = "В наличии";
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#00A046"));
                        break;
                    case 1:
                        Availability = "Заканчивается";
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#FFA900"));
                        break;
                    case 2:
                        Availability = "Закончилось";
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#d32f2f"));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
            if (resultCode == RESULT_OK) {

                mFilePathUri = result.getUri();
                mImage.setImageURI(mFilePathUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception errorToast = result.getError();
                Toast error = Toasty.error(getApplicationContext(),
                        "" + errorToast,
                        Toast.LENGTH_SHORT);
                error.setGravity(Gravity.CENTER, 0, 0);
                error.show();
            }
        }
    }


    private void resizeImage() {

        Bitmap bitmap;
        try {
            if (mFilePathUri != null) {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFilePathUri);
                Bitmap bitmapResize = Bitmap.createScaledBitmap(bitmap, 1280, 960, true);

                //Разрешение на сохранение
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, 1);
                        checkPermission = false;
                    } else {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmapResize.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String safeRoot = MediaStore.Images.Media.insertImage(getContentResolver(), bitmapResize, "Title", null);
                        mFilePathUri = Uri.parse(safeRoot);
                        checkPermission = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openMedia() {
        CropImage.activity()
                .start(AddEditGoods.this);
    }

    @NonNull
    private String getPassword() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz|!£$%&/=@#".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    checkPermission = true;
                } else {
                    checkPermission = false;
                    Toast error = Toasty.error(getApplicationContext(),
                            "Нет разрешения на сохранение изображений",
                            Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.CENTER, 0, 0);
                    error.show();
                }
                break;
        }

    }
}
