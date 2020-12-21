package com.samet.sauwallpaper.Database.DataSource;

import com.samet.sauwallpaper.Database.Recents;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Taiyab on 03-09-2018.
 */

public interface IRecentsDataSource {
    Flowable<List<Recents>> getAllRecentes();
    void  insertRecents(Recents... recents);
    void  updateRecents(Recents... recents);
    void  deleteRecents(Recents... recents);
    void  deleteAllRecents();
}
