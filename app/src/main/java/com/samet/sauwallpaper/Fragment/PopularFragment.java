package com.samet.sauwallpaper.Fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samet.sauwallpaper.Common.Common;
import com.samet.sauwallpaper.Interface.ItemClickListener;
import com.samet.sauwallpaper.Model.WallpaperItem;
import com.samet.sauwallpaper.R;
import com.samet.sauwallpaper.ViewHolder.ListWallpaperViewHolder;
import com.samet.sauwallpaper.ViewWallpaper;
import com.samet.sauwallpaper.util.FIREBASECONSTANTS;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class PopularFragment extends Fragment {


    RecyclerView recyclerView;

    FirebaseDatabase database;
    DatabaseReference categoryBackground;

    FirebaseRecyclerOptions<WallpaperItem> options;
    FirebaseRecyclerAdapter<WallpaperItem,ListWallpaperViewHolder> adapter;

    private static PopularFragment INSTANCE=null;

    public PopularFragment() {

        //Init Firebase

        database =  FirebaseDatabase.getInstance(FIREBASECONSTANTS.firebase_db_url);
        categoryBackground =database.getReference(Common.STR_WALLPAPER);


        Query query = categoryBackground.orderByChild("viewCount")
                .limitToLast(50); //get 50 item have biggest view count

        options = new FirebaseRecyclerOptions.Builder<WallpaperItem>()
                .setQuery(query,WallpaperItem.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperViewHolder holder, int position, @NonNull final WallpaperItem model) {
                Picasso.get()
                        .load(model.getImageLink())
                        .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                        .centerCrop()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(holder.wallpaper, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(model.getImageLink())
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
                        Intent intent= new Intent(getActivity(),ViewWallpaper.class);
                        Common.select_background = model;
                        Common.select_beckground_key = adapter.getRef(position).getKey();
                        Common.CATEGORY_SELECTED =model.getCategoryId();
                        startActivity(intent);



                    }
                });
            }

            @Override
            public ListWallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_wallpaper_item,null);

                return new ListWallpaperViewHolder(itemView);
            }
        };

    }


    public static PopularFragment getInstance()
    {
        if(INSTANCE == null)
            INSTANCE = new PopularFragment();
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular, container, false);
        recyclerView= (RecyclerView)view.findViewById(R.id.recycler_popular);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);


        loadPopularList();
        return view;
    }

    private void loadPopularList() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onStop() {
        if (adapter !=null)
            adapter.stopListening();
        adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter !=null)
            adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter !=null)
            adapter.startListening();
    }
}
