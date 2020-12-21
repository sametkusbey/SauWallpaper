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
import com.samet.sauwallpaper.ListWallpaper;
import com.samet.sauwallpaper.Model.CategoryItem;
import com.samet.sauwallpaper.R;
import com.samet.sauwallpaper.ViewHolder.CategoryViewHolder;

import com.samet.sauwallpaper.util.FIREBASECONSTANTS;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    //Firebase

    FirebaseDatabase database;
    DatabaseReference categoryBackground;

    //FirebaseUI Adapter
    FirebaseRecyclerOptions<CategoryItem> options;
    FirebaseRecyclerAdapter<CategoryItem,CategoryViewHolder> adapter;

    //View
    RecyclerView recyclerView;


    private static CategoryFragment INSTANCE=null;


    public CategoryFragment() {

        database =FirebaseDatabase.getInstance(FIREBASECONSTANTS.firebase_db_url);
        categoryBackground =database.getReference(Common.STR_CATEGORY_BACKGROUND);


        options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                .setQuery(categoryBackground,CategoryItem.class)
                .build();



        adapter =new FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position, @NonNull final CategoryItem model) {

                Picasso.get()
                        .load(model.getImageLink())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(holder.background_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                  //Try again online if cache failed
                                Picasso.get()
                                        .load(model.getImageLink())
                                        .placeholder(R.drawable.ic_thumbnail)
                                        .error(R.drawable.ic_thumbnail)
                                        .into(holder.background_image, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.e("Wallpaper Error","Coudn't fetch image");

                                            }
                                        });
                            }
                        });

                holder.category_name.setText(model.getName());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Common.CATEGORY_ID_SELECTED = adapter.getRef(position).getKey(); // get key of item
                        Common.CATEGORY_SELECTED =model.getName();
                        Intent intent =new Intent(getActivity(), ListWallpaper.class);
                        startActivity(intent);
                    }
                });

            }


            @Override
            public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_category_item,parent,false);
                return new CategoryViewHolder(itemView);
            }
        };
    }

    public static CategoryFragment getInstance()
    {
     if(INSTANCE == null)
         INSTANCE = new CategoryFragment();
      return INSTANCE;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView =(RecyclerView)view.findViewById(R.id.recycler_category);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(gridLayoutManager);


        setCategory();
        return view;

    }

    private void setCategory() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
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

    @Override
    public void onStop() {
        if (adapter !=null)
            adapter.stopListening();
        super.onStop();
    }

}
