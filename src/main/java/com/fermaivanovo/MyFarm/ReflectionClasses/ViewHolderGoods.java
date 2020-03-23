package com.fermaivanovo.MyFarm.ReflectionClasses;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fermaivanovo.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ViewHolderGoods extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolderGoods(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListenerGoods.onItemClickGoods(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListenerGoods.onItemLongClickGoods(view, getAdapterPosition());
                return true;
            }
        });
    }

    public void setDetailsGoods(Context ctx, String image, String title, String price, String description, String composition, String term, String availability, String code) {

        //Views
        ImageView mImage = mView.findViewById(R.id.mImage);
        TextView mTitle = mView.findViewById(R.id.mTitle);
        TextView mPrice = mView.findViewById(R.id.mPrice);
        TextView mAvailability = mView.findViewById(R.id.mAvailability);

        //set Data to views
        if (image == null) {
            Picasso.get()
                    .load(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into(mImage);
        } else {
            Picasso.get()
                    .load(image)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.image_not_available)
                    .into(mImage);
        }
        //
        if (title == null) {
            mTitle.setText("Заголовок");
        } else {
            mTitle.setText(title);
        }
        //
        if (price == null) {
            mPrice.setText("0");
        } else {
            mPrice.setText(price);
        }
        //
        if (availability == null) {
            mAvailability.setText("Неизвестно");
            mAvailability.setTextColor(Color.parseColor("#757575"));
        } else {
            switch (availability) {
                case "В наличии":
                    mAvailability.setText("В наличии");
                    mAvailability.setTextColor(Color.parseColor("#00A046"));
                    break;
                case "Заканчивается":
                    mAvailability.setText("Заканчивается");
                    mAvailability.setTextColor(Color.parseColor("#FFA900"));
                    break;
                case "Закончилось":
                    mAvailability.setText("Закончилось");
                    mAvailability.setTextColor(Color.parseColor("#d32f2f"));
                    break;
                default:
                    mAvailability.setText("Неизвестно");
                    mAvailability.setTextColor(Color.parseColor("#757575"));
            }
        }
    }

    private ViewHolderGoods.ClickListenerGoods mClickListenerGoods;

    public interface ClickListenerGoods {

        void onItemClickGoods(View view, int position);

        void onItemLongClickGoods(View view, int position);

    }

    public void setOnClickListenerGoods(ViewHolderGoods.ClickListenerGoods clickListenerGoods) {
        mClickListenerGoods = clickListenerGoods;
    }

}
