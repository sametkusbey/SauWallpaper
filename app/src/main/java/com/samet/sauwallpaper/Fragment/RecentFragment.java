package com.samet.sauwallpaper.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.samet.sauwallpaper.Adapter.MyRecyclerAdapter;
import com.samet.sauwallpaper.Database.DataSource.RecentRepository;
import com.samet.sauwallpaper.Database.LocalDatabase.LocalDatabase;
import com.samet.sauwallpaper.Database.LocalDatabase.RecentsDataSource;
import com.samet.sauwallpaper.Database.Recents;
import com.samet.sauwallpaper.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class RecentFragment extends Fragment {

    private static RecentFragment INSTANCE=null;
    Context context;
    RecyclerView recyclerView;
    List<Recents> recentsList;
    MyRecyclerAdapter adapter;


    //Room Database
    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;


    @SuppressLint("ValidFragment")
    public RecentFragment(Context context) {

        // Required empty public constructor
        this.context =context;

        //Init RoomDatabase
        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(context);
        recentRepository =RecentRepository.getInstance(RecentsDataSource.getInstance(database.recenteDAO()));


    }


    public static RecentFragment getInstance(Context context)
    {
        if(INSTANCE == null)
            INSTANCE = new RecentFragment(context);
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        recyclerView =(RecyclerView)view.findViewById(R.id.recycler_recent);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recentsList = new ArrayList<>();
        adapter =new MyRecyclerAdapter(context,recentsList);
        recyclerView.setAdapter(adapter);

        loadRecents();
        return view;


    }

    private void loadRecents() {

        Disposable disposable = recentRepository.getAllRecentes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Recents>>() {
                    @Override
                    public void accept(List<Recents> recents) throws Exception {
                        onGetAllRecentsSuccess(recents);
                        LottieAnimationView progressBar = (LottieAnimationView) getActivity().findViewById(R.id.indeterminateBar);
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("error",throwable.getMessage());

                    }
                });
        compositeDisposable.add(disposable);
    }

    private void onGetAllRecentsSuccess(List<Recents> recents) {
        recentsList.clear();
        recentsList.addAll(recents);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}
