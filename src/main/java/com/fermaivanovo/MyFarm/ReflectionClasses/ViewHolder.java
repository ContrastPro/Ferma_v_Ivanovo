package com.fermaivanovo.MyFarm.ReflectionClasses;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fermaivanovo.R;
import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
    }

    public void setDetails(Context ctx, String title, String image) {

        //Views
        TextView mTitleTv = mView.findViewById(R.id.mTitle);
        ImageView mImage = mView.findViewById(R.id.mImage);

        //set Data to views
        if (image == null) {
            Picasso.get()
                    .load(R.drawable.image_not_available)
                    //.resize(480, 240)
                    .fit()
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

        mTitleTv.setText(title);
    }

    private ViewHolder.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

}
