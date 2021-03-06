package com.pfc.android.archcomponent.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.pfc.android.archcomponent.db.AppDatabase;
import com.pfc.android.archcomponent.vo.ArrivalsEntity;
import com.pfc.android.archcomponent.vo.ArrivalsFormatedEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * LocalRepositoryImpl implements a base interface: LocalRepository
 * <p>
 * LocalRepositoryImpl is the implementation class for managing the communication
 * between the ViewModel and the Local backend: SQLite using ROOM.
 * <p>
 *
 * @author      Ana San Juan
 * @version     1.0
 * @since       1.0
 */
public class LocalRepositoryImpl implements LocalRepository {

    private final String tag = LocalRepositoryImpl.class.getName();

    private AppDatabase database;

    /**
     * Contructor with an Appdatabase parameter.
     * <p>
     * This method  allows the communication withe the database backend
     *
     */
    public LocalRepositoryImpl(AppDatabase database){
        this.database = database;
    }

    /**
     * Method that return a LiveData object without parameters. This method
     * made an async select in order to get all the elements that are in the database.
     * <p>
     * This method get a LiveData object with a list of ArrivalsFormatedEntitites.
     *
     * @return     a LiveData<List<ArrivalsFormatedEntity>> object giving to get all the favourites saved in the database.
     *
     */
    public LiveData<List<ArrivalsFormatedEntity>> getFavouritesLiveData(){
        return database.favouriteDao().getFavouritesLiveData();
    }

    /**
     * Method async with one parameter: ArrivalsFormatedEntity. This method
     * made an async insert to the database.
     * <p>
     * This method insert the favouriteEntity in the database.
     *
     * @param  favouriteEntity  a ArrivalsFormatedEntity object giving to add to the database.
     */
    public void addFavourite(ArrivalsFormatedEntity favouriteEntity) {
        new AsyncTask<ArrivalsFormatedEntity, Integer, Void>() {
            @Override
            protected Void doInBackground(ArrivalsFormatedEntity... params) {
                database.favouriteDao().addFavourite(params[0]);
                Log.d(tag, "element added");
                return null;
            }
        }.execute(favouriteEntity);
    }

    /**
     * Method async with one parameter: ArrivalsFormatedEntity. This method
     * made an async deletion to the database.
     * <p>
     * This method delete the favouriteEntity from the database.
     *
     * @param  favouriteEntity  a ArrivalsFormatedEntity object giving to delete from the database.
     */
    public void deleteFavourite(ArrivalsFormatedEntity favouriteEntity) {
        new AsyncTask<ArrivalsFormatedEntity, Integer, Void>() {
            @Override
            protected Void doInBackground(ArrivalsFormatedEntity... params) {
                database.favouriteDao().deleteFavourite(params[0]);
                Log.d(tag, "element deleted");
                return null;
            }
        }.execute(favouriteEntity);
    }

    /**
     * Method that retrieve is one element is or not in the database in async mode.
     *
     * @param arrival to check if is in favourites
     * @return true or false.
     */
    public boolean isFav(ArrivalsEntity arrival){
        try {
            return new isFavAsync(database).execute(arrival).get() > 0 ? true : false ;
        } catch (InterruptedException | ExecutionException ex) {
            Log.e(tag,"error"+ex.getMessage());
            return false;
        }
    }

    /**
     * Async class that allows us to make in background one request to the database.
     */
    private static class isFavAsync extends AsyncTask<ArrivalsEntity, Void, Integer>{

        private AppDatabase database;

        public isFavAsync(AppDatabase database) {
            this.database = database;
        }

        @Override
        protected Integer doInBackground(ArrivalsEntity... arrivalsEntities) {
            return database.favouriteDao().elementInFav(arrivalsEntities[0].getNaptanId(), arrivalsEntities[0].getLineId());
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

    }
}
