package com.samet.sauwallpaper.Database.LocalDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.samet.sauwallpaper.Database.Recents;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Taiyab on 03-09-2018.
 */
@Dao
public interface RecenteDAO {
    @Query("SELECT * FROM recents ORDER BY saveTime DESC LIMIT 10")
    Flowable<List<Recents>> getAllRecentes();

    @Insert
    void  insertRecents(Recents... recents);
    @Update
    void  updateRecents(Recents... recents);
    @Delete
    void  deleteRecents(Recents... recents);
    @Query("DELETE FROM recents")
    void  deleteAllRecents();
}
