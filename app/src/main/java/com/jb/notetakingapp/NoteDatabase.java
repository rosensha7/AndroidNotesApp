package com.jb.notetakingapp;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance; //singleton instance

    public abstract NoteDAO noteDao(); //used to access out DAO. Implemented by room automatically

    //instance initializer
    public static synchronized NoteDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "node_database")
                    .fallbackToDestructiveMigration() //in case of version increment, this call will delete our DB and rebuild it from scratch
                    .addCallback(roomCallback) //populate database
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDAO noteDAO;

        private PopulateDbAsyncTask(NoteDatabase db){
            noteDAO = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids){
            noteDAO.insert(new Note("Title 1", "Description 1", 1));
            noteDAO.insert(new Note("Title 2", "Description 2", 2));
            noteDAO.insert(new Note("Title 3", "Description 3", 3));

            return null;
        }
    }
}
