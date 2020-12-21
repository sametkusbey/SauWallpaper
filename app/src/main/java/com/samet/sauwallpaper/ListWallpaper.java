package com.samet.sauwallpaper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.airbnb.lottie.LottieAnimationView;
import com.samet.sauwallpaper.util.FIREBASECONSTANTS;
import com.samet.sauwallpaper.util.GDPR;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.samet.sauwallpaper.Common.Common;
import com.samet.sauwallpaper.Interface.ItemClickListener;
import com.samet.sauwallpaper.Model.WallpaperItem;
import com.samet.sauwallpaper.ViewHolder.ListWallpaperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;



public class ListWallpaper extends AppCompatActivity {




    Query query;
    AdView adView;
    SharedPreferences sharedPreferences;
    String status;
    FirebaseRecyclerOptions<WallpaperItem> options;
    FirebaseRecyclerAdapter<WallpaperItem,ListWallpaperViewHolder> adapter;
    LottieAnimationView progressBar;
    RecyclerView recyclerView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wallpaper);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        status = sharedPreferences.getString("status", "free");

        // Banner Ads
        if (status.equals("free")) {
            loadAdMobBannerAd();
            InitAds();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Common.CATEGORY_SELECTED);
        toolbar.setTitleTextColor(Color.WHITE);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         progressBar = (LottieAnimationView) findViewById(R.id.indeterminateBar);



        recyclerView = (RecyclerView) findViewById(R.id.recycler_list_wallpaper);
        recyclerView.setHasFixedSize(true);

        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);




          loadBackgroundList();




    }

    // AdMob app ID
    private void InitAds() {
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
    }
    // AdMob Banner Ads
    private void loadAdMobBannerAd() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(getResources().getString(R.string.admob_banner_id));
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(this)).build();
        adView = findViewById(R.id.adView);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int error) {
                if (status.equals("free")) {
                    adView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }
        });
    }


    private void loadBackgroundList() {
        query = FirebaseDatabase.getInstance(FIREBASECONSTANTS.firebase_db_url).getReference(Common.STR_WALLPAPER)
                .orderByChild("categoryId").equalTo(Common.CATEGORY_ID_SELECTED);
        options = new FirebaseRecyclerOptions.Builder<WallpaperItem>()
                .setQuery(query, WallpaperItem.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperViewHolder holder, int position, @NonNull final WallpaperItem model) {


                Picasso.get()
                        .load(model.getImageLink())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                        .centerCrop()
                        .into(holder.wallpaper, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
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
                                                progressBar.setVisibility(View.GONE);
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
                        Intent intent= new Intent(ListWallpaper.this,ViewWallpaper.class);
                        Common.select_background = model;
                        Common.select_beckground_key = adapter.getRef(position).getKey();
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
        super.onStop();
        if (adapter !=null)
            adapter.startListening();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish(); //Close  activity when click Back button
            return super.onOptionsItemSelected(item);



    }

}

