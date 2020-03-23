package com.fermaivanovo.MyFarm.StartActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fermaivanovo.MyFarm.Goods.AddEditGoods;
import com.fermaivanovo.MyFarm.ReflectionClasses.PostGoods;
import com.fermaivanovo.MyFarm.ReflectionClasses.PostNews;
import com.fermaivanovo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class AddEditNews extends AppCompatActivity {

    private String ACTION;
    private TextInputLayout mTitle, mDescription;
    private TextInputEditText mTitleEdit, mDescriptionEdit;
    private ImageView mImage;

    //Database
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    //Storage
    private StorageReference mStorageReference;
    private String mStoragePath = "News/", KEY;

    //Uri
    private Uri mFilePathUri;

    private ProgressDialog progressDialog;

    private boolean checkPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_add_edit_news);
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ACTION = getIntent().getStringExtra("ACTION");

        mImage = findViewById(R.id.mImage);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewDialog = getLayoutInflater().inflate(R.layout.alert_dialog_full_screen, null);
                final Dialog dialog = new Dialog(AddEditNews.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                dialog.setContentView(viewDialog);

                ImageView imageFullScreen = dialog.findViewById(R.id.imageFullScreen);

                Drawable mDrawable = mImage.getDrawable();
                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                imageFullScreen.setImageBitmap(mBitmap);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openMedia();
                return true;
            }
        });


        mTitle = findViewById(R.id.mTitle);
        mDescription = findViewById(R.id.mDescription);
        mTitleEdit = findViewById(R.id.mTitleEdit);
        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDescriptionEdit = findViewById(R.id.mDescriptionEdit);
        mDescriptionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDescription.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("News");

        progressDialog = new ProgressDialog(AddEditNews.this);
        progressDialog.setCancelable(false);

        if (ACTION.equals("ADD")) {
            setTitle("Добавить новость");
            KEY = getPassword();
            openMedia();
        } else {
            editNews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater_admin = getMenuInflater();
        inflater_admin.inflate(R.menu.confirm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (ACTION.equals("ADD")) {
            uploadData();
        } else {
            uploadEditedData();
        }
        return true;
    }

    private void uploadData() {

        final String title = mTitle.getEditText().getText().toString().trim();
        final String description = mDescription.getEditText().getText().toString().trim();
        final String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());

        if (title.isEmpty() || description.isEmpty()) {
            if (title.isEmpty()) {
                mTitle.setError("Поле не может быть пустым");
            }
            if (description.isEmpty()) {
                mDescription.setError("Поле не может быть пустым");
            }
        } else {
            resizeImage();
            if (mFilePathUri != null && checkPermission == true) {
                progressDialog.setTitle("Завершение...");
                progressDialog.show();

                StorageReference storageReference2nd = mStorageReference.child(mStoragePath + title + ".jpeg");
                storageReference2nd.putFile(mFilePathUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();

                                Toast success = Toasty.success(getApplicationContext(),
                                        "Новость успешно опубликована!",
                                        Toast.LENGTH_SHORT);
                                success.setGravity(Gravity.CENTER, 0, 0);
                                success.show();

                                PostNews postNews = new PostNews(downloadUrl.toString(), title, description, date, KEY);
                                String imageUploadId = mDatabaseReference.push().getKey();
                                mDatabaseReference.child(imageUploadId).setValue(postNews);
                                progressDialog.dismiss();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast error = Toasty.error(AddEditNews.this,
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

    private void uploadEditedData() {

        final String title = mTitle.getEditText().getText().toString().trim();
        final String description = mDescription.getEditText().getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            if (title.isEmpty()) {
                mTitle.setError("Поле не может быть пустым");
            }
            if (description.isEmpty()) {
                mDescription.setError("Поле не может быть пустым");
            }
        } else {

            Query mQuery = mDatabaseReference.orderByChild("code").equalTo(KEY);
            mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().child("title").setValue(title);
                        ds.getRef().child("description").setValue(description);
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

    private void editNews() {

        setTitle("Редактировать пост");
        String Image = getIntent().getStringExtra("image");
        String Title = getIntent().getStringExtra("title");
        String Description = getIntent().getStringExtra("description");
        KEY = getIntent().getStringExtra("code");

        Picasso.get()
                .load(Image)
                .fit()
                .placeholder(R.drawable.logo)
                .error(R.drawable.image_not_available)
                .centerCrop()
                .into(mImage);
        mTitle.getEditText().setText(Title);
        mDescription.getEditText().setText(Description);
    }

    private void openMedia() {
        CropImage.activity()
                .start(AddEditNews.this);
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
