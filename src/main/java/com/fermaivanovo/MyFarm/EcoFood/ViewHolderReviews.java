package com.fermaivanovo.MyFarm.EcoFood;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fermaivanovo.R;

public class ViewHolderReviews extends RecyclerView.ViewHolder {

    View mView;
    TextView mName, reviewsContent, mDate;
    RatingBar mRating;

    public ViewHolderReviews(@NonNull View itemView) {
        super(itemView);

        mName = itemView.findViewById(R.id.mName);
        reviewsContent = itemView.findViewById(R.id.reviewsContent);
        mDate = itemView.findViewById(R.id.mDate);
        mRating = itemView.findViewById(R.id.mRating);

        mView = itemView;
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListenerReviews.onItemClickReviews(view, getAdapterPosition());
            }
        });
    }


    private ViewHolderReviews.ClickListenerReviews mClickListenerReviews;

    public interface ClickListenerReviews {
        void onItemClickReviews(View view, int position);
    }

    public void setOnClickListenerReviews(ViewHolderReviews.ClickListenerReviews clickListenerReviews) {
        mClickListenerReviews = clickListenerReviews;
    }

}
