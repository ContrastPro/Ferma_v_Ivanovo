package com.fermaivanovo.MyFarm.ReflectionClasses;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fermaivanovo.R;
import com.squareup.picasso.Picasso;

public class ViewHolderNews extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolderNews(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListenerNews.onItemClickNews(view, getAdapterPosition());
            }
        });

        mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListenerNews.onItemLongClickGoods(view, getAdapterPosition());
                return true;
            }
        });
    }

    public void setDetailsNews(Context ctx, String image, String title, String description, String date, String code){

        ImageView mImage = mView.findViewById(R.id.mImage);
        TextView mTitle = mView.findViewById(R.id.mTitle);
        TextView mDescription = mView.findViewById(R.id.mDescription);
        TextView mDate = mView.findViewById(R.id.mDate);


        if (image == null) {
            Picasso.get()
                    .load(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into(mImage);
        } else {
            Picasso.get()
                    .load(image)
                    //.resize(480, 240)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.image_not_available)
                    .into(mImage);
        }

        mTitle.setText(title);
        mDescription.setText(description);
        mDate.setText(date);
    }

    private ViewHolderNews.ClickListenerNews mClickListenerNews;

    public interface ClickListenerNews {

        void onItemClickNews(View view, int position);

        void onItemLongClickGoods(View view, int position);
    }

    public void setOnClickListenerNews(ViewHolderNews.ClickListenerNews clickListenerNews) {
        mClickListenerNews = clickListenerNews;
    }
}
