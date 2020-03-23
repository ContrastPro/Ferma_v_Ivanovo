package com.fermaivanovo.MyFarm.ReflectionClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fermaivanovo.R;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewHolderProfile extends RecyclerView.ViewHolder {


    View mView;

    CardView mCardContacts, mCardTime, goodsCategories;

    LinearLayout phoneLinearLayout, emailLinearLayout, moLinearLayout,
            tuLinearLayout, weLinearLayout, thLinearLayout, frLinearLayout, saLinearLayout, suLinearLayout;

    ImageView image_goMap, moreInformation, moreDescription, moreTime;

    ExpandableRelativeLayout expandableLayoutInformation, expandableLayoutDescription, expandableLayoutTime;

    public interface ClickListenerProfile {

        void toGoods(View view, int position);

        void toMap(View view, int position);

        void moreInformation(View view, int position);

        void sendEmail(View view, int position);

        void makeCall(View view, int position);

        void moreDescription(View view, int position);

        void moreTime(View view, int position);
    }

    public ViewHolderProfile(@NonNull View itemView) {
        super(itemView);

        mView = itemView;
        image_goMap = itemView.findViewById(R.id.image_goMap);
        goodsCategories = itemView.findViewById(R.id.goodsCategories);
        mCardContacts = itemView.findViewById(R.id.mCardContacts);
        phoneLinearLayout = itemView.findViewById(R.id.phoneLinearLayout);
        emailLinearLayout = itemView.findViewById(R.id.emailLinearLayout);

        mCardTime = itemView.findViewById(R.id.mCardTime);
        moLinearLayout = itemView.findViewById(R.id.moLinearLayout);
        tuLinearLayout = itemView.findViewById(R.id.tuLinearLayout);
        weLinearLayout = itemView.findViewById(R.id.weLinearLayout);
        thLinearLayout = itemView.findViewById(R.id.thLinearLayout);
        frLinearLayout = itemView.findViewById(R.id.frLinearLayout);
        saLinearLayout = itemView.findViewById(R.id.saLinearLayout);
        suLinearLayout = itemView.findViewById(R.id.suLinearLayout);

        moreInformation = itemView.findViewById(R.id.mInformation);
        moreDescription = itemView.findViewById(R.id.moreDescription);
        moreTime = itemView.findViewById(R.id.mTime);


        expandableLayoutInformation = itemView.findViewById(R.id.expandableLayoutInformation);
        if (expandableLayoutInformation.isExpanded()) {
            moreInformation.setRotation(-180);
        }
        expandableLayoutInformation.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                moreInformation.animate().rotation(-180);
            }

            @Override
            public void onPreClose() {
                moreInformation.animate().rotation(0);
            }
        });

        expandableLayoutDescription = itemView.findViewById(R.id.expandableLayoutDescription);
        if (expandableLayoutDescription.isExpanded()) {
            moreDescription.setRotation(-180);
        }
        expandableLayoutDescription.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                moreDescription.animate().rotation(-180);
            }

            @Override
            public void onPreClose() {
                moreDescription.animate().rotation(0);
            }
        });

        expandableLayoutTime = itemView.findViewById(R.id.expandableLayoutTime);
        if (expandableLayoutTime.isExpanded()) {
            moreTime.setRotation(-180);
        }
        expandableLayoutTime.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                moreTime.animate().rotation(-180);
            }

            @Override
            public void onPreClose() {
                moreTime.animate().rotation(0);
            }
        });

        image_goMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClickListenerProfile.toMap(view, getAdapterPosition());

            }
        });
        goodsCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClickListenerProfile.toGoods(view, getAdapterPosition());

            }
        });

        moreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClickListenerProfile.moreInformation(view, getAdapterPosition());

            }
        });

        moreDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClickListenerProfile.moreDescription(view, getAdapterPosition());

            }
        });

        moreTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClickListenerProfile.moreTime(view, getAdapterPosition());

            }
        });

        emailLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListenerProfile.sendEmail(view, getAdapterPosition());
            }
        });

        phoneLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListenerProfile.makeCall(view, getAdapterPosition());
            }
        });

    }

    public void setDetailsProfile(Context ctx, String image, String title, String address, String phone, String email, String description,
                                  String mo, String tu, String we, String th, String fr, String sa, String su) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        String colorFont = "#FF5252";
        int textSize = 18;

        //Views
        ImageView mImage = mView.findViewById(R.id.mImage);
        TextView mTitle = mView.findViewById(R.id.mTitle);
        TextView mAddress = mView.findViewById(R.id.mAddress);
        ImageView ourGoods = mView.findViewById(R.id.ourGoods);
        TextView mPhone = mView.findViewById(R.id.mPhone);
        TextView mEmail = mView.findViewById(R.id.mEmail);
        TextView mDescription = mView.findViewById(R.id.mDescription);
        TextView mMo = mView.findViewById(R.id.mMo);
        TextView mTu = mView.findViewById(R.id.mTu);
        TextView mWe = mView.findViewById(R.id.mWe);
        TextView mTh = mView.findViewById(R.id.mTh);
        TextView mFr = mView.findViewById(R.id.mFr);
        TextView mSa = mView.findViewById(R.id.mSa);
        TextView mSu = mView.findViewById(R.id.mSu);

        TextView Monday = mView.findViewById(R.id.Monday);
        TextView Tuesday = mView.findViewById(R.id.Tuesday);
        TextView Wednesday = mView.findViewById(R.id.Wednesday);
        TextView Thursday = mView.findViewById(R.id.Thursday);
        TextView Friday = mView.findViewById(R.id.Friday);
        TextView Saturday = mView.findViewById(R.id.Saturday);
        TextView Sunday = mView.findViewById(R.id.Sunday);

        //Set Data to views
        if (image == null) {
            Picasso.get()
                    .load(R.drawable.image_not_available)
                    .fit()
                    //.resize(480, 240)
                    .centerCrop()
                    .into(mImage);
        } else {
            Picasso.get()
                    .load(image)
                    .fit()
                    //.resize(480, 240)
                    .centerCrop()
                    .error(R.drawable.image_not_available)
                    .into(mImage);
        }
        //
        mTitle.setText(title);
        //
        mAddress.setText(address);
        //
        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/ferma-v-ivanovo.appspot.com/o/StartActivity%2Four_goods.jpg?alt=media&token=8a70ddee-7069-4a2b-9521-f4df7ab65f53")
                .fit()
                .centerCrop()
                .error(R.drawable.image_not_available)
                .into(ourGoods);
        mDescription.setText(description);
        //
        if (phone == null) {
            phoneLinearLayout.setVisibility(View.GONE);
        } else {
            mCardContacts.setVisibility(View.VISIBLE);
            phoneLinearLayout.setVisibility(View.VISIBLE);
            mPhone.setText(phone);
        }
        //
        if (email == null) {
            emailLinearLayout.setVisibility(View.GONE);
        } else {
            mCardContacts.setVisibility(View.VISIBLE);
            emailLinearLayout.setVisibility(View.VISIBLE);
            mEmail.setText(email);
        }
        //
        if (phone == null && email == null) {
            phoneLinearLayout.setVisibility(View.GONE);
            emailLinearLayout.setVisibility(View.GONE);
            mCardContacts.setVisibility(View.GONE);
        }
        //
        if (mo == null) {
            moLinearLayout.setVisibility(View.GONE);
        } else {
            moLinearLayout.setVisibility(View.VISIBLE);
            mMo.setText(mo);
            if (dayOfTheWeek.equals("Monday")) {
                Monday.setTextColor(Color.parseColor(colorFont));
                Monday.setTextSize(textSize);
                mMo.setTextColor(Color.parseColor(colorFont));
                mMo.setTextSize(textSize);
            }
        }
        //
        if (tu == null) {
            tuLinearLayout.setVisibility(View.GONE);
        } else {
            tuLinearLayout.setVisibility(View.VISIBLE);
            mTu.setText(tu);
            if (dayOfTheWeek.equals("Tuesday")) {
                Tuesday.setTextColor(Color.parseColor(colorFont));
                Tuesday.setTextSize(textSize);
                mTu.setTextColor(Color.parseColor(colorFont));
                mTu.setTextSize(textSize);
            }
        }
        //
        if (we == null) {
            weLinearLayout.setVisibility(View.GONE);
        } else {
            weLinearLayout.setVisibility(View.VISIBLE);
            mWe.setText(we);
            if (dayOfTheWeek.equals("Wednesday")) {
                Wednesday.setTextColor(Color.parseColor(colorFont));
                Wednesday.setTextSize(textSize);
                mWe.setTextColor(Color.parseColor(colorFont));
                mWe.setTextSize(textSize);
            }
        }
        //
        if (th == null) {
            thLinearLayout.setVisibility(View.GONE);
        } else {
            thLinearLayout.setVisibility(View.VISIBLE);
            mTh.setText(th);
            if (dayOfTheWeek.equals("Thursday")) {
                Thursday.setTextColor(Color.parseColor(colorFont));
                Thursday.setTextSize(textSize);
                mTh.setTextColor(Color.parseColor(colorFont));
                mTh.setTextSize(textSize);
            }
        }
        //
        if (fr == null) {
            frLinearLayout.setVisibility(View.GONE);
        } else {
            frLinearLayout.setVisibility(View.VISIBLE);
            mFr.setText(fr);
            if (dayOfTheWeek.equals("Friday")) {
                Friday.setTextColor(Color.parseColor(colorFont));
                Friday.setTextSize(textSize);
                mFr.setTextColor(Color.parseColor(colorFont));
                mFr.setTextSize(textSize);
            }
        }
        //
        if (sa == null) {
            saLinearLayout.setVisibility(View.GONE);
        } else {
            saLinearLayout.setVisibility(View.VISIBLE);
            mSa.setText(sa);
            if (dayOfTheWeek.equals("Saturday")) {
                Saturday.setTextColor(Color.parseColor(colorFont));
                Saturday.setTextSize(textSize);
                mSa.setTextColor(Color.parseColor(colorFont));
                mSa.setTextSize(textSize);
            }
        }
        //
        if (su == null) {
            suLinearLayout.setVisibility(View.GONE);
        } else {
            suLinearLayout.setVisibility(View.VISIBLE);
            mSu.setText(su);
            if (dayOfTheWeek.equals("Sunday")) {
                Sunday.setTextColor(Color.parseColor(colorFont));
                Sunday.setTextSize(textSize);
                mSu.setTextColor(Color.parseColor(colorFont));
                mSu.setTextSize(textSize);
            }
        }

        if (mo == null && tu == null && we == null && th == null && fr == null && sa == null && su == null) {
            mCardTime.setVisibility(View.GONE);
        } else {
            mCardTime.setVisibility(View.VISIBLE);
        }
    }

    private ViewHolderProfile.ClickListenerProfile mClickListenerProfile;

    public void setOnClickListenerProfile(ViewHolderProfile.ClickListenerProfile clickListenerProfile) {
        mClickListenerProfile = clickListenerProfile;
    }


}
