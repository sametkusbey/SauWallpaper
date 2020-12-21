package com.samet.sauwallpaper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;
import com.samet.sauwallpaper.util.GDPR;
import com.samet.sauwallpaper.util.IabHelper;
import com.samet.sauwallpaper.util.IabResult;
import com.samet.sauwallpaper.util.Inventory;
import com.samet.sauwallpaper.util.Purchase;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.samet.sauwallpaper.Adapter.FragmentAdapter;
import com.samet.sauwallpaper.Common.Common;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;
import java.util.List;


    public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private ViewPager viewPager;
    private TabLayout tabLayout;
    private AdView adView;
    private InterstitialAd interstitialAd;
        static final String TAG = "HomeActivity";
        static final String SKU_REMOVE_ADS = "remove_ads";
        // (arbitrary) request samet for the purchase flow
        static final int RC_REQUEST = 10111;
        IabHelper mHelper;
        String status= "";
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2PE2BW8k6gADcLKKpOpx+Z0MhG0MX5D/bnH8QPwaFOhqByOr2c/WZB2mpgx3jWG59i5ysAG0wizY84Z/DwSBR9LcGumAgyJ4ABArYlJJOE7RqrCcyStXQXSXGOmMiHDN1jG2BScAO1imtIerJ1wZj0cz41axGQwHeU1WSBYxDjdpL0lq3tnwxUxWihfSb9zmD2t4Wtql/WLt6k5kLA2XwnZFhs+QN/T7kQFOkS7VET87UAo2j2bu6geG84E+xp3/vLvWMq3Ud+IK55dpieJz/iIkb7B7PhK7yu3bC0lr3qRSJAmwuRQKdDKdIRvl4bOqg8aL6e2+nwyDQQyrODrk8wIDAQAB";
        Boolean isAdsDisabled = false;
        String payload = "ANY_PAYLOAD_STRING";
        public static SharedPreferences sharedPreferences;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case Common.PERMISSION_REQUEST_CODE:
            {
                if (grantResults.length > 0 && grantResults [0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"İzin verildi.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Görseli indirmek için izin vermelisiniz.",Toast.LENGTH_SHORT).show();
            }break;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        LottieAnimationView progressBar = (LottieAnimationView) findViewById(R.id.indeterminateBar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        status = sharedPreferences.getString("status", "free");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        initToolbar();
        initComponent();




        //Request Runtime permission
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQUEST_CODE);
        }


        //Remove ADS


        // Create the helper, passing it our context and the public key to
        // verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set
        // this to false).
        mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    // complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed off in the meantime? If so, quit.
                if (mHelper == null)
                    return;

                // IAB is fully set up. Now, let's get an inventory of stuff we
                // own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Interstitial Ads
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(HomeActivity.this)).build();
        adView.loadAd(GDPR.getAdRequest(HomeActivity.this));
    }

        private void initAds() {
            MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("Orientation", "LANDSCAPE MODE");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i("Orientation", "PORTRAIT MODE");
        }
    }

    private void initComponent() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        FragmentAdapter adapter= new FragmentAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.getTabAt(0).setIcon(R.drawable.ic_category);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_fire);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_clock);


        // set icon color pre-selected
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

        // Listener that's called when we finish querying the items and
        // subscriptions we own
        IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {
                Log.d(TAG, "Query inventory finished.");

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null)
                    return;

                // Is it a failure?
                if (result.isFailure()) {
                    // complain("Failed to query inventory: " + result);
                    return;
                }

                Log.d(TAG, "Query inventory was successful.");

                /*
                 * Check for items we own. Notice that for each purchase, we check
                 * the developer payload to see if it's correct! See
                 * verifyDeveloperPayload().
                 */

                // Do we have the premium upgrade?
                Purchase removeAdsPurchase = inventory.getPurchase(SKU_REMOVE_ADS);
                isAdsDisabled = (removeAdsPurchase != null && verifyDeveloperPayload(removeAdsPurchase));

                if (inventory.hasPurchase(SKU_REMOVE_ADS)) {


                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("status", "purchased");//Creating our pager adapter
                    editor.apply();
                    adView.setVisibility(View.GONE);

                } else {
                    loadAdMobBannerAd();
                    loadInterstitialAd();
                    showInterstitialAd();
                    initAds();
                }

                Log.d(TAG, "User has "
                        + (isAdsDisabled ? "REMOVED ADS"
                        : "NOT REMOVED ADS"));


                Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            }
        };


        // User clicked the "Remove Ads" button.
        private void purchaseRemoveAds() {
            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    mHelper.launchPurchaseFlow(HomeActivity.this, SKU_REMOVE_ADS,
                            RC_REQUEST, mPurchaseFinishedListener, payload);

                }
            });
        }


        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
                    + data);
            if (mHelper == null)
                return;

            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.d(TAG, "onActivityResult handled by IABUtil.");
            }

        }



        /**
         * Verifies the developer payload of a purchase.
         */
        boolean verifyDeveloperPayload(Purchase p) {
            String payload = p.getDeveloperPayload();

            /*
             * TODO: verify that the developer payload of the purchase is correct.
             * It will be the same one that you sent when initiating the purchase.
             *
             * WARNING: Locally generating a random string when starting a purchase
             * and verifying it here might seem like a good approach, but this will
             * fail in the case where the user purchases an item on one device and
             * then uses your app on a different device, because on the other device
             * you will not have access to the random string you originally
             * generated.
             *
             * So a good developer payload has these characteristics:
             *
             * 1. If two different users purchase an item, the payload is different
             * between them, so that one user's purchase can't be replayed to
             * another user.
             *
             * 2. The payload must be such that you can verify it even when the app
             * wasn't the one who initiated the purchase flow (so that items
             * purchased by the user on one device work on other devices owned by
             * the user).
             *
             * Using your own server to store and verify developer payloads across
             * app installations is recommended.
             */
            return true;
        }



        // Callback for when a purchase is finished
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                Log.d(TAG, "Purchase finished: " + result + ", purchase: "
                        + purchase);

                // if we were disposed of in the meantime, quit.
                if (mHelper == null)
                    return;

                if (result.isFailure()) {
                    complain("Error purchasing: " + result);
                    return;
                }
                if (!verifyDeveloperPayload(purchase)) {
                    complain("Error purchasing. Authenticity verification failed.");
                    return;
                }

                Log.d(TAG, "Purchase successful.");

                if (purchase.getSku().equals(SKU_REMOVE_ADS)) {
                    // bought the premium upgrade!
                    isAdsDisabled = true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("status", "purchased");
                    editor.apply();
                    removeAds();

                }
            }
        };


        private void removeAds() {
            // "Upgrade" button is only visible if the user is not premium
            findViewById(R.id.adView).setVisibility(isAdsDisabled ? View.GONE : View.VISIBLE);


        }



        void complain(String message) {
            Log.e(TAG, "**** TrivialDrive Error: " + message);
            alert("Error: " + message);
        }

        void alert(final String message) {
            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    AlertDialog.Builder bld = new AlertDialog.Builder(HomeActivity.this);
                    bld.setMessage(message);
                    bld.setNeutralButton("OK", null);
                    Log.d(TAG, "Showing alert dialog: " + message);
                    bld.create().show();
                }
            });
        }



    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

        private void loadAdMobBannerAd() {
            adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(HomeActivity.this)).build();
            adView.loadAd(GDPR.getAdRequest(HomeActivity.this));
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
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





    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return  mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            final String appPackageName=getApplication().getPackageName();
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Here's an amazing and free app for HD wallpapers! DOWNLOAD NOW! http://play.google.com/store/apps/details?id=" + appPackageName);
            startActivity(Intent.createChooser(sharingIntent, "Share App via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.drawer_rate) {
            final String appName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
        }
        else if (id == R.id.drawer_privacypolicy) {

            String url =getResources().getString(R.string.privacy_policy);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        } else if (id == R.id.drawer_share){
            final String appPackageName=getApplication().getPackageName();
            String shareBody = "Download "+getString(R.string.app_name)+" From :  "+"http://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        } else if (id == R.id.drawer_request) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/html");
            intent.setData(Uri.parse("mailto:" + getString(R.string.drawer_email)));
            intent.putExtra(Intent.EXTRA_SUBJECT, "HD Wallpapers: Request/Submit Wallpapers");
            intent.putExtra(Intent.EXTRA_TEXT, "Request/Submit wallpapers...");
            Intent chooserIntent = Intent.createChooser(intent, "Request Via");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                startActivity(chooserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id == R.id.drawer_about) {
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.remove_ads) {
            purchaseRemoveAds();

        } else if (id == R.id.drawer_exit) {
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
        @Override
        public void onStart() {
            super.onStart();

        }


        @Override
        public void onPause() {
            // Pause the AdView.
            adView.pause();

            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();

            // Resume the AdView.
            adView.resume();
        }



        @Override
        protected void onDestroy() {
            adView.destroy();
            // very important:
            Log.d(TAG, "Destroying helper.");
            if (mHelper != null) {
                mHelper.dispose();
                mHelper = null;
            }
            super.onDestroy();
        }


        @Override
        public void onStop() {
            super.onStop();
        }


    }
