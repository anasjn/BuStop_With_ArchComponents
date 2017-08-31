package com.pfc.android.archcomponent.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pfc.android.archcomponent.R;
import com.pfc.android.archcomponent.api.ApiResponse;
import com.pfc.android.archcomponent.db.AppDatabase;
import com.pfc.android.archcomponent.model.DefaultLocation;
import com.pfc.android.archcomponent.repository.LocalRepository;
import com.pfc.android.archcomponent.repository.LocalRepositoryImpl;
import com.pfc.android.archcomponent.repository.RemoteRepository;
import com.pfc.android.archcomponent.repository.RemoteRepositoryImpl;
import com.pfc.android.archcomponent.util.FavouriteApplication;
import com.pfc.android.archcomponent.util.LocationLiveData;
import com.pfc.android.archcomponent.vo.ArrivalsEntity;
import com.pfc.android.archcomponent.vo.ArrivalsFormatedEntity;
import com.pfc.android.archcomponent.vo.FavouriteEntity;
import com.pfc.android.archcomponent.vo.StopLocationEntity;
import com.pfc.android.archcomponent.vo.StopPointsEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ana on 29/08/17.
 */

public class UnifiedModelView extends AndroidViewModel {

    private final String TAG = UnifiedModelView.class.getName();

    private MutableLiveData<List<ArrivalsFormatedEntity>> mMutableArrivalsFormated;
    private MutableLiveData<List<FavouriteEntity>> mFavouritesMutableLiveData;
    private MutableLiveData<DefaultLocation> mLocationMutableLiveData;
//    private MutableLiveData<StopPointsEntity> mStopPointMutableLiveData;

    @Inject
    public AppDatabase database;

    RemoteRepository mRemoteRepository;
    LocalRepository mLocalRepository;

    //List of favourites in order to check if the arrival is a favourite or not
    private List<FavouriteEntity> favourites = new ArrayList<>();

    //user and password
    String app_id=getApplication().getString(R.string.api_transport_id);
    String app_key=getApplication().getString(R.string.api_transport_key);


    public UnifiedModelView(Application application) {
        super(application);
        ((FavouriteApplication)application).getFavComponent().inject(this);
        this.mMutableArrivalsFormated = new MutableLiveData<>();
        this.mFavouritesMutableLiveData = new MutableLiveData<>();
        this.mRemoteRepository = new RemoteRepositoryImpl();
        this.mLocalRepository = new LocalRepositoryImpl(database);
    }


//    public LiveData<DefaultLocation> getLocation(Context context) {
//        if (mLocationMutableLiveData == null) {
//            mLocationMutableLiveData = new LocationLiveData(context);
//        }
//        return mLocationMutableLiveData;
//    }
//

    public void setmMutableLiveDataFavourites() {
        favourites = mLocalRepository.getFavourites();
        Log.v(TAG, "favourites en unifiedViewMOder "+favourites);
        mFavouritesMutableLiveData.setValue(favourites);
    }

    public MutableLiveData<List<FavouriteEntity>> getmMutableLiveDataFavourites(){
        return mFavouritesMutableLiveData;
    }

    public void addFavourite(FavouriteEntity favouriteEntity) {
        new AsyncTask<FavouriteEntity, Integer, Void>() {
            @Override
            protected Void doInBackground(FavouriteEntity... params) {
                Log.v(TAG,"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ unifiedViewModel addFavourite params "+params[0].getmLineId());
                database.favouriteDao().addFavourite(params[0]);
                return null;
            }
        }.execute(favouriteEntity);
    }

    public void deleteFavourite(FavouriteEntity favouriteEntity) {
        new AsyncTask<FavouriteEntity, Integer, Void>() {
            @Override
            protected Void doInBackground(FavouriteEntity... params) {
                Log.v(TAG,"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ unifiedViewModel delete params "+params[0].getmLineId());
                database.favouriteDao().deleteFavourite(params[0]);
                return null;
            }
        }.execute(favouriteEntity);
    }

    public MutableLiveData<List<ArrivalsFormatedEntity>> getmMutableArrivalsFormated(){
        return mMutableArrivalsFormated;
    }

//    public void loadStopInformation(@NonNull String app_id, @NonNull String app_key, @NonNull double lat, @NonNull double lon, int radious) {
//        mRemoteRepository.getStopLocation(app_id,app_key,lat, lon, radious, new Callback<StopLocationEntity> () {
//            @Override
//            public void onResponse(Call<StopLocationEntity> call, Response<StopLocationEntity> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<StopLocationEntity> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }

    public void getArrivalInformation(String naptanId) {
        mRemoteRepository.getArrivalInformation(naptanId, app_id, app_key, new Callback<List<ArrivalsEntity>>() {
            @Override
            public void onResponse(Call<List<ArrivalsEntity>> call, Response<List<ArrivalsEntity>> response) {
                List<ArrivalsFormatedEntity> arrivalsFormatedEntity = convert(response.body());
                mMutableArrivalsFormated.setValue(arrivalsFormatedEntity);
            }

            @Override
            public void onFailure(Call<List<ArrivalsEntity>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public List<ArrivalsFormatedEntity> convert(List<ArrivalsEntity> arrivals) {
        List<ArrivalsFormatedEntity> arrivalsformated = new ArrayList<>();
        if (arrivals != null && arrivals.size() > 0) {
            ArrayList<Integer> listtimes = new ArrayList<Integer>();
            listtimes.add(secondsToMinutes(arrivals.get(0).getTimeToStation()));
            String lineId = arrivals.get(0).getLineId();
            boolean fav = isFav(arrivals.get(0));
            ArrivalsFormatedEntity aformated = new ArrivalsFormatedEntity(arrivals.get(0).get$type(), arrivals.get(0).getNaptanId(), lineId, arrivals.get(0).getStopLetter(), arrivals.get(0).getStationName(), arrivals.get(0).getPlatformName(), arrivals.get(0).getDestinationName(), listtimes, fav);
            arrivalsformated.add(aformated);
            List<Integer> times = null;
            int j = 0;
            String aux = "";
            ArrivalsEntity arrivalAux = null;
            int position = 0;
            int timesInMinutes = 0;
            for (int i = 1; i < arrivals.size(); i++) {
                arrivalAux = arrivals.get(i);
                lineId = arrivalAux.getLineId();
                if (arrivalsformated.size() > 0) {
                    position = arrivalsformated.size() - 1;
                }
                aux = arrivalsformated.get(position).getLineId();
                timesInMinutes = secondsToMinutes(arrivalAux.getTimeToStation());
                if (lineId.equals(aux)) {
                    times = arrivalsformated.get(position).getTimeToStation();
                    times.add(timesInMinutes);
                    arrivalsformated.get(position).setTimeToStation(times);
                } else {
                    listtimes = new ArrayList<Integer>();
                    listtimes.add(timesInMinutes);
                    fav = isFav(arrivalAux);
                    aformated = new ArrivalsFormatedEntity(arrivalAux.get$type(), arrivalAux.getNaptanId(), arrivalAux.getLineId(), arrivalAux.getStopLetter(), arrivalAux.getStationName(), arrivalAux.getPlatformName(), arrivalAux.getDestinationName(), listtimes,fav);
                    arrivalsformated.add(aformated);
                }
            }

        }
        return arrivalsformated;
    }

    private int secondsToMinutes(int seconds){
        int minutes = 0;
        minutes = seconds/60;
        return minutes;
    }

    private boolean isFav(ArrivalsEntity arrival){
        for (FavouriteEntity fav :
                favourites) {
            if (fav.getmLineId().equals(arrival.getLineId()) && fav.getmNaptanId().equals(arrival.getNaptanId())){
                return true;
            }
        }
        return false;
    }
}