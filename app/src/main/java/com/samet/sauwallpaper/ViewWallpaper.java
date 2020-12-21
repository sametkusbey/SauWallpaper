package com.samet.sauwallpaper;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.samet.sauwallpaper.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;



import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.samet.sauwallpaper.Common.Common;
import com.samet.sauwallpaper.Database.DataSource.RecentRepository;
import com.samet.sauwallpaper.Database.LocalDatabase.LocalDatabase;
import com.samet.sauwallpaper.Database.LocalDatabase.RecentsDataSource;
import com.samet.sauwallpaper.Database.Recents;
import com.samet.sauwallpaper.Helper.SaveImageHelper;
import com.samet.sauwallpaper.Model.WallpaperItem;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samet.sauwallpaper.util.FIREBASECONSTANTS;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ViewWallpaper extends AppCompatActivity{
    CollapsingToolbarLayout collapsingToolbarLayout;
    CoordinatorLayout rootLayout;
    ImageView btnsave,btnsetwallpaper,btnshare,btnedit,imageView;
    private InterstitialAd interstitialAd;
    SharedPreferences sharedPreferences;
    String status;



    //Room Database
    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;
    TextView log;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Common.PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    AlertDialog dialog = new SpotsDialog(ViewWallpaper.this);
                    dialog.show();
                    dialog.setMessage("Processing");

                    String fileName = UUID.randomUUID().toString() + ".jpeg";

                    Picasso.get()
                            .load(Common.select_background.getImageLink())
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName,
                                    "Wallpaper"));
                } else
                    Toast.makeText(this, "You need accept this Permission to download image", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        status = sharedPreferences.getString("status", "free");



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(String.valueOf(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView textcategory =(TextView) findViewById(R.id.textcategory);
        textcategory.setText(Common.select_background.getCategoryId());



        TextView report = (TextView) findViewById(R.id.report);
        report.setText(Html.fromHtml("<center><p><font face=\"Myriad Pro\" size=\"3\" color=\"#acacac\">Uygulamamızda bulunan tüm görseller stock fotoğraf olarak ifade edilen telifsiz görsellerdir. Görsellerin tamamı uygulamamızın veritabanından gelmektedir. Bir yanlışlık olduğunu düşünüyorsanız geri bildirim gönderin.  </font><font face=\"Myriad Pro\" color=\"#ee4561\"><a href=\"mailto:samet.kusbey@berayazilim.net\" >BİLDİR</a></font></p></center>"));
        report.setMovementMethod(LinkMovementMethod.getInstance());
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");

                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getResources().getString(R.string.drawer_email)});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ViewWallpaper.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });




        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Init RoomDatabase

        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(this);
        recentRepository =RecentRepository.getInstance(RecentsDataSource.getInstance(database.recenteDAO()));


        btnsave =(ImageView) findViewById(R.id.btnsave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override public void onClick(View v) {
                if (status.equals("free")) {
                    loadInterstitialAd();
                    showInterstitialAd();
                }
                if
                (ActivityCompat.checkSelfPermission(ViewWallpaper.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQUEST_CODE);
                } else {
                    AlertDialog dialog = new SpotsDialog(ViewWallpaper.this);
                    dialog.show();
                    dialog.setMessage("Please Waiting");

                    String fileName = UUID.randomUUID().toString() + ".jpeg";
                    Picasso.get()
                            .load(Common.select_background.getImageLink())
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName,
                                    "wallpaper"));

                }
            }
        });



        btnsetwallpaper =(ImageView) findViewById(R.id.btnsetwallpaper);
        btnsetwallpaper.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override

            public void onClick(View v) {
                if (status.equals("free")) {
                    loadInterstitialAd();
                    showInterstitialAd();
                }
                Drawable mDrawable =imageView.getDrawable();
                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Zoom", null);
                Uri uri = Uri.parse(path);
                startActivity(new Intent(Intent.ACTION_ATTACH_DATA, Uri.parse(path)));



            }
        });


        btnshare =(ImageView) findViewById(R.id.btnshare);
        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("free")) {
                    loadInterstitialAd();
                    showInterstitialAd();
                }
                Drawable mDrawable =imageView.getDrawable();
                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Share", null);
                Uri uri = Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Share Image"));

            }
        });

        btnedit=(ImageView) findViewById(R.id.btnedit);
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("free")) {
                    loadInterstitialAd();
                    showInterstitialAd();
                }
                Drawable mDrawable =imageView.getDrawable();
                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Zoom", null);
                Uri uri = Uri.parse(path);
                startActivity(new Intent(Intent.ACTION_EDIT, Uri.parse(path)));
            }
        });





        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpadedAppbar);
        collapsingToolbarLayout.setTitle(Common.select_background.getCategoryId());


        imageView = (ImageView) findViewById(R.id.imageThumb);
        Picasso.get()
                .load(Common.select_background.getImageLink())
                .into(imageView);


        addToRecents();


        increaseViewCount();


    }
    private void loadInterstitialAd() {

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }


        Log.d("TAG", "showAd");
        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }



    private void showInterstitialAd() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }



    private void increaseViewCount() {
        FirebaseDatabase.getInstance(FIREBASECONSTANTS.firebase_db_url)
                .getReference(Common.STR_WALLPAPER)
                .child(Common.select_beckground_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("viewCount"))
                        {
                            WallpaperItem wallpaperItem =dataSnapshot.getValue(WallpaperItem.class);
                            long count =wallpaperItem.getViewCount()+1;
                            //Update
                            Map<String,Object> update_view =new HashMap<>();
                            update_view.put("viewCount",count);

                            FirebaseDatabase.getInstance(FIREBASECONSTANTS.firebase_db_url)
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.select_beckground_key)
                                    .updateChildren(update_view)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewWallpaper.this, "Cannot update view count", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else
                        {
                            Map<String,Object> update_view =new HashMap<>();
                            update_view.put("viewCount",Long.valueOf(1));

                            FirebaseDatabase.getInstance(FIREBASECONSTANTS.firebase_db_url)
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.select_beckground_key)
                                    .updateChildren(update_view)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewWallpaper.this, "Cannot set default view count", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToRecents() {

        Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                Recents recents = new Recents(Common.select_background.getImageLink(),
                        Common.select_background.getCategoryId(),
                        String.valueOf(System.currentTimeMillis()),
                        Common.select_beckground_key);
                recentRepository.insertRecents(recents);
                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("ERROR", throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
        compositeDisposable.add(disposable);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (status.equals("free")) {
            loadInterstitialAd();
        }
    }

    @Override
    protected void onDestroy() {
        Picasso.get().cancelRequest(target);
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (status.equals("free")) {
            showInterstitialAd();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish(); //Close  activity when click Back button
        return super.onOptionsItemSelected(item);
    }



}