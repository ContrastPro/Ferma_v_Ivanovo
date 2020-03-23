package com.fermaivanovo.MyFarm.UserProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.fermaivanovo.MyFarm.Goods.Goods;
import com.fermaivanovo.MyFarm.ReflectionClasses.SignUp;
import com.fermaivanovo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class SignInUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference, reff;
    private FirebaseDatabase mFirebaseDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //NO TITLE
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.a_sign_in_up);
        //HIDE ACTION BAR
        getSupportActionBar().hide();
        //Set Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setData();
    }

    private void setData() {
        progressDialog = new ProgressDialog(SignInUp.this);
        progressDialog.setCancelable(false);

        final ViewFlipper mFlipper = findViewById(R.id.mFlipper);
        Button signUpView = findViewById(R.id.signUpView);
        Button signInView = findViewById(R.id.signInView);
        ImageView close = findViewById(R.id.close);

        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlipper.setInAnimation(SignInUp.this, R.anim.sign_slide_in_top);
                mFlipper.setOutAnimation(SignInUp.this, R.anim.sign_slide_out_bottom);
                mFlipper.showNext();
            }
        });

        signInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlipper.setInAnimation(SignInUp.this, R.anim.sign_slide_out_top);
                mFlipper.setOutAnimation(SignInUp.this, R.anim.sign_slide_in_bottom);
                mFlipper.showPrevious();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // [END Элементы управления диалогом]


        // [START Вход в профиль]
        final TextInputLayout mEmail_in = findViewById(R.id.mEmail_in);
        final TextInputLayout mPassword_in = findViewById(R.id.mPassword_in);
        final TextInputEditText emailEditText_in = findViewById(R.id.emailEditText_in);
        final TextInputEditText passwordEditText_in = findViewById(R.id.passwordEditText_in);
        CardView signIn_in = findViewById(R.id.signIn_in);

        emailEditText_in.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail_in.setErrorEnabled(false);
                mPassword_in.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText_in.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail_in.setErrorEnabled(false);
                mPassword_in.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signIn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail_in.getEditText().getText().toString().trim();
                final String password = mPassword_in.getEditText().getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    if (email.isEmpty()) {
                        mEmail_in.setError("Поле не может быть пустым");
                    } else {
                        mEmail_in.setErrorEnabled(false);
                    }
                    if (password.isEmpty()) {
                        mPassword_in.setError("Поле не может быть пустым");
                    } else {
                        mPassword_in.setErrorEnabled(false);
                    }
                } else {
                    mEmail_in.setErrorEnabled(false);
                    mPassword_in.setErrorEnabled(false);
                    signIn(email, password);
                }

            }
        });
        // [END Вход в профиль]


        // [START Регистрация]
        final TextInputLayout mLogin_up = findViewById(R.id.mLogin_up);
        final TextInputEditText loginEditText_up = findViewById(R.id.loginEditText_up);
        final TextInputLayout mEmail_up = findViewById(R.id.mEmail_up);
        final TextInputLayout mPassword_up = findViewById(R.id.mPassword_up);
        final TextInputEditText emailEditText_up = findViewById(R.id.emailEditText_up);
        final TextInputEditText passwordEditText_up = findViewById(R.id.passwordEditText_up);
        CardView signUp_up = findViewById(R.id.signUp_up);

        loginEditText_up.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLogin_up.setErrorEnabled(false);
                mEmail_up.setErrorEnabled(false);
                mPassword_up.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailEditText_up.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLogin_up.setErrorEnabled(false);
                mEmail_up.setErrorEnabled(false);
                mPassword_up.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText_up.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLogin_up.setErrorEnabled(false);
                mEmail_up.setErrorEnabled(false);
                mPassword_up.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUp_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String login = mLogin_up.getEditText().getText().toString().trim();
                final String email = mEmail_up.getEditText().getText().toString().trim();
                final String password = mPassword_up.getEditText().getText().toString().trim();

                if (login.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    //
                    if (login.isEmpty()) {
                        mLogin_up.setError("Поле не может быть пустым");
                    } else {
                        mLogin_up.setErrorEnabled(false);
                    }
                    //
                    if (email.isEmpty()) {
                        mEmail_up.setError("Поле не может быть пустым");
                    } else {
                        mEmail_up.setErrorEnabled(false);
                    }
                    //
                    if (password.isEmpty()) {
                        mPassword_up.setError("Поле не может быть пустым");
                    } else {
                        mPassword_up.setErrorEnabled(false);
                    }

                } else {

                    mLogin_up.setErrorEnabled(false);
                    mEmail_up.setErrorEnabled(false);
                    mPassword_up.setErrorEnabled(false);

                    if (password.length() < 6) {
                        mPassword_up.setError("Пароль должен содержать не менее 6 символов");
                    } else {
                        mPassword_up.setErrorEnabled(false);
                        if (!email.contains("@.") && !email.contains(". ") && email.contains("@") && email.contains(".")) {
                            signUp(login, email, password);
                        } else {
                            mEmail_up.setError("Укажите действительный адрес электронной почты");
                            // Неверный формат email
                        }
                    }
                }

            }
        });
    }


    private void signUp(final String login, final String email, final String password) {

        progressDialog.setTitle("Пожалуйста подождите, идёт регистрация...");
        progressDialog.show();

        reff = FirebaseDatabase.getInstance().getReference("Rights");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String TYPE = Objects.requireNonNull(dataSnapshot.child("r69esHABMwDi").getValue()).toString();

                    //[START Регистрация]
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mFirebaseDatabase = FirebaseDatabase.getInstance();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        SignUp signUp = new SignUp(login, email, "IMAGE", TYPE);
                                        mDatabaseReference.setValue(signUp)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        // Sign in success, update UI with the signed-in user's information
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(getApplicationContext(), UserProfile.class));
                                                        finish();
                                                    }
                                                });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        progressDialog.dismiss();
                                        TextInputLayout mEmail_up = findViewById(R.id.mEmail_up);
                                        mEmail_up.setError("Профиль с таким адрессом уже существует");
                                    }
                                }
                            });
                    //[END Регистрация]

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signIn(final String email, String password) {

        progressDialog.setTitle("Пожалуйста подождите...");
        progressDialog.show();

        final Button forgotPassword = findViewById(R.id.forgotPassword);

        //[START Вход]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), UserProfile.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            forgotPassword.setVisibility(View.VISIBLE);
                            forgotPassword.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    resetPassword(email);
                                }
                            });
                            TextInputLayout mEmail_in = findViewById(R.id.mEmail_in);
                            mEmail_in.setError("\t");
                            TextInputLayout mPassword_in = findViewById(R.id.mPassword_in);
                            mPassword_in.setError("Неверный адресс электронной почты или пароль");
                        }
                    }
                });
        //[END Вход]
    }

    public void resetPassword(String email) {

        View viewDialog = getLayoutInflater().inflate(R.layout.alert_dialog_reset_password, null);
        final Dialog dialog = new Dialog(SignInUp.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setContentView(viewDialog);

        ImageView close = dialog.findViewById(R.id.close);
        Button resetEmail = dialog.findViewById(R.id.resetEmail);
        final TextInputLayout editEmailReset = dialog.findViewById(R.id.editEmailReset);
        editEmailReset.getEditText().setText(email);
        TextInputEditText editEmailInput = dialog.findViewById(R.id.editEmailInput);
        editEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editEmailReset.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        resetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EMAIL = editEmailReset.getEditText().getText().toString().trim();

                if (!EMAIL.isEmpty()) {
                    if (!EMAIL.contains("@.") && !EMAIL.contains(". ") && EMAIL.contains("@") && EMAIL.contains(".")) {
                        String close = sendEmail(EMAIL);
                        if (close.equals("close")) {
                            dialog.dismiss();
                        }
                    } else {
                        editEmailReset.setError("Укажите действительный адрес электронной почты");
                        // Неверный формат email
                    }
                } else {
                    editEmailReset.setError("Поле не может быть пустым");
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private String sendEmail(String EMAIL) {
        progressDialog.setTitle("Пожалуйста подождите...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(EMAIL).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(SignInUp.this);
                builder.setTitle("Сброс пароля");
                builder.setMessage("На ваш почтовый ящик было отправленно письмо с сылкой для сброса пароля");
                builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        return "close";
    }


}
