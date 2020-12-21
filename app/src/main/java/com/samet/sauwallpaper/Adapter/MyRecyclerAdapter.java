package com.samet.sauwallpaper.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samet.sauwallpaper.Common.Common;
import com.samet.sauwallpaper.Database.Recents;
import com.samet.sauwallpaper.Interface.ItemClickListener;
import com.samet.sauwallpaper.Model.WallpaperItem;
import com.samet.sauwallpaper.R;
import com.samet.sauwallpaper.ViewHolder.ListWallpaperViewHolder;
import com.samet.sauwallpaper.ViewWallpaper;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Taiyab on 03-09-2018.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<ListWallpaperViewHolder> {


    private Context context;
    private List<Recents> recents;


    public MyRecyclerAdapter(Context context, List<Recents> recents) {
        this.context = context;
        this.recents = recents;
    }

    @Override
    public ListWallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_wallpaper_item,null);

        return new ListWallpaperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListWallpaperViewHolder holder, final int position) {


        Picasso.get()
                .load(recents.get(position).getImageLink())
                .placeholder(R.drawable.ic_thumbnail)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                .centerCrop()
                .into(holder.wallpaper, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(recents.get(position).getImageLink())
                                .placeholder(R.drawable.ic_thumbnail)
                                .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                                .centerCrop()
                                .error(R.drawable.ic_thumbnail)
                                .into(holder.wallpaper, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("Wallpaper error", "Could not fetch image");
                                    }
                                });
                    }
                });


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent= new Intent(context,ViewWallpaper.class);
                WallpaperItem wallpaperItem = new WallpaperItem();
                wallpaperItem.setCategoryId(recents.get(position).getCategoryId());
                wallpaperItem.setImageLink(recents.get(position).getImageLink());
                Common.select_background = wallpaperItem;
                Common.select_beckground_key =recents.get(position).getKey();
                context.startActivity(intent);



            }
        });
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }
}
