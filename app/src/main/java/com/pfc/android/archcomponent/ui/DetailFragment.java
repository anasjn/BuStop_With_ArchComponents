package com.pfc.android.archcomponent.ui;

import android.app.Application;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pfc.android.archcomponent.R;
import com.pfc.android.archcomponent.adapters.ArrivalAdapter;
import com.pfc.android.archcomponent.api.ApiResponse;
import com.pfc.android.archcomponent.api.ApiResponse2;
import com.pfc.android.archcomponent.db.AppDatabase;
import com.pfc.android.archcomponent.repository.LocalRepositoryImpl;
import com.pfc.android.archcomponent.util.FavouriteApplication;
import com.pfc.android.archcomponent.viewmodel.AddFavouriteViewModel;
import com.pfc.android.archcomponent.viewmodel.UnifiedModelView;
import com.pfc.android.archcomponent.vo.ArrivalsEntity;
import com.pfc.android.archcomponent.vo.ArrivalsFormatedEntity;
import com.pfc.android.archcomponent.vo.FavouriteEntity;
import com.pfc.android.archcomponent.vo.StopPointsEntity;
import com.pfc.android.archcomponent.model.CustomDetailClickListener;
import com.pfc.android.archcomponent.viewmodel.DetailViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ana on 16/08/17.
 */

public class DetailFragment extends LifecycleFragment {

    private final String TAG = DetailFragment.class.getName();

    protected ArrivalAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    private UnifiedModelView unifiedModelView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unifiedModelView = ViewModelProviders.of(getActivity()).get(UnifiedModelView.class);

        String naptanId = "";
        Bundle args = getArguments();
        if(args!=null){
            naptanId = args.getString("naptanId");
        }

        unifiedModelView.getArrivalInformation(naptanId);

        // Handle changes emitted by LiveData
        unifiedModelView.getmMutableArrivalsFormated().observe(this, new Observer<List<ArrivalsFormatedEntity>>() {
            @Override
            public void onChanged(@Nullable List<ArrivalsFormatedEntity> arrivalsFormatedEntities) {
                handleResponse(arrivalsFormatedEntities);
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.hasFixedSize();

        CustomDetailClickListener mDetailClickListener = new CustomDetailClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ArrivalsFormatedEntity arrival = mAdapter.getArrivalsList().get(position);
                Log.v(TAG,"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++en el click para guardar en la BBDD "+position +" isfav "+arrival.isFavourite());
                FavouriteEntity favourite = new FavouriteEntity (new Date(System.currentTimeMillis()),arrival.getLineId(),arrival.getPlatformName(),arrival.getDestinationName(),arrival.getNaptanId(),arrival.isFavourite());
                if(!arrival.isFavourite()){
                    Toast.makeText(getContext(), "Line: "+ arrival.getLineId()+ " in Platform: " +arrival.getPlatformName()+ " towards: "+arrival.getDestinationName()+ " has been added to Favourites", Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++add favourite "+favourite.getmNaptanId() +" , "+favourite.getmLineId());
                    unifiedModelView.addFavourite(favourite);
                    unifiedModelView.setmMutableLiveDataFavourites();
                }else{
                    Toast.makeText(getContext(), "Line: "+ arrival.getLineId()+ " in Platform: " +arrival.getPlatformName()+ " towards: "+arrival.getDestinationName()+ " has been deleted from Favourites", Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++delete favourite "+favourite.getmNaptanId() +" , "+favourite.getmLineId());
                    unifiedModelView.setmMutableLiveDataFavourites();
                    unifiedModelView.deleteFavourite(favourite);
//                    unifiedModelView.setmMutableLiveDataFavourites();
                }
            }
        };

        mAdapter = new ArrivalAdapter(getContext());
        mAdapter.setOnItemClickListener(mDetailClickListener);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    private void handleResponse(List<ArrivalsFormatedEntity> arrivals) {
        if (arrivals != null && arrivals.size()>0) {
            mAdapter.addArrivalInformation(arrivals);
        } else {
            mAdapter.clearArrivalInformation();
            Toast.makeText(
                    getContext(),
                    "No arrival information found for the searched stop.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

}
