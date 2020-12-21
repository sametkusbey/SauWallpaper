package com.samet.sauwallpaper.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.samet.sauwallpaper.Interface.ItemClickListener;
import com.samet.sauwallpaper.R;

/**
 * Created by Taiyab on 29-07-2018.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView category_name;
    public ImageView background_image;

    ItemClickListener itemClickListener;



    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CategoryViewHolder(View itemView) {
        super(itemView);
        background_image= (ImageView)itemView.findViewById(R.id.image);
        category_name =(TextView)itemView.findViewById(R.id.name);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());

    }


}
