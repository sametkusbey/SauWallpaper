package com.samet.sauwallpaper.Database.LocalDatabase;

import com.samet.sauwallpaper.Database.DataSource.IRecentsDataSource;
import com.samet.sauwallpaper.Database.Recents;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Taiyab on 03-09-2018.
 */

public class RecentsDataSource implements IRecentsDataSource {


    private RecenteDAO recenteDAO;
    private static  RecentsDataSource instance;

    public RecentsDataSource(RecenteDAO recenteDAO) {
        this.recenteDAO = recenteDAO;
    }

    public static RecentsDataSource getInstance(RecenteDAO recenteDAO)
    {
       if (instance == null)
           instance = new RecentsDataSource(recenteDAO);
       return instance;
    }

    @Override
    public Flowable<List<Recents>> getAllRecentes() {
        return recenteDAO.getAllRecentes();
    }

    @Override
    public void insertRecents(Recents... recents) {
        recenteDAO.insertRecents(recents);

    }

    @Override
    public void updateRecents(Recents... recents) {
        recenteDAO.updateRecents(recents);

    }

    @Override
    public void deleteRecents(Recents... recents) {
        recenteDAO.deleteRecents(recents);

    }

    @Override
    public void deleteAllRecents() {

        recenteDAO.deleteAllRecents();

    }
}
