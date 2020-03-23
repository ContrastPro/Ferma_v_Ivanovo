package com.fermaivanovo.MyFarm.ItemDecoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecorationStartEnd extends RecyclerView.ItemDecoration {

    private int space;

    public ItemDecorationStartEnd(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        boolean isLast = position == state.getItemCount() - 1;
        if (isLast) {
            outRect.right = space;
            outRect.left = 0;
        }
        if (position == 0) {
            outRect.left = space;
            if (!isLast)
                outRect.right = 0;
        }
    }
}
