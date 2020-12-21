package com.samet.sauwallpaper.Database.DataSource;

import com.samet.sauwallpaper.Database.Recents;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Taiyab on 03-09-2018.
 */

public class RecentRepository implements  IRecentsDataSource{


    private IRecentsDataSource mLocalDataSource;
    private static RecentRepository instance;

    public RecentRepository(IRecentsDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }
    public static  RecentRepository getInstance(IRecentsDataSource mLocalDataSource) {
        if (instance== null)
            instance =new RecentRepository(mLocalDataSource);
        return instance;
    }

    @Override
    public Flowable<List<Recents>> getAllRecentes() {
        return mLocalDataSource.getAllRecentes();
    }

    @Override
    public void insertRecents(Recents... recents) {
        mLocalDataSource.insertRecents(recents);

    }

    @Override
    public void updateRecents(Recents... recents) {
        mLocalDataSource.updateRecents(recents);


    }

    @Override
    public void deleteRecents(Recents... recents) {
        mLocalDataSource.deleteRecents(recents);
    }

    @Override
    public void deleteAllRecents() {
        mLocalDataSource.deleteAllRecents();

    }
}
