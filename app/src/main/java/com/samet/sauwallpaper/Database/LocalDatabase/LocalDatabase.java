package com.samet.sauwallpaper.Database.LocalDatabase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.samet.sauwallpaper.Database.Recents;

import static com.samet.sauwallpaper.Database.LocalDatabase.LocalDatabase.DATABASE_VERSION;


/**
 * Created by Taiyab on 03-09-2018.
 */

@Database(entities = Recents.class,version = DATABASE_VERSION,exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase{
    public static final int DATABASE_VERSION=2;
    public static final String DATABSE_NAME="TYBWALLPAPER";

    public abstract  RecenteDAO recenteDAO();

    private static LocalDatabase instance;

    public static LocalDatabase getInstance(Context context)
    {
        if (instance ==null)
        {
            instance =Room.databaseBuilder(context,LocalDatabase.class,DATABSE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
