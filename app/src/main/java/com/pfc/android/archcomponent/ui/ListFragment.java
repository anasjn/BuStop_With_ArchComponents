package com.pfc.android.archcomponent.ui;

import android.app.Activity;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;

import com.pfc.android.archcomponent.R;
import com.pfc.android.archcomponent.adapters.DataAdapter;
import com.pfc.android.archcomponent.api.StopPointsEntity;
import com.pfc.android.archcomponent.model.DefaultLocation;
import com.pfc.android.archcomponent.viewmodel.ListLocationsViewModel;
import com.pfc.android.archcomponent.viewmodel.LocationViewModel;

import android.arch.lifecycle.ViewModelProviders;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by ana on 12/08/17.
 */

public class ListFragment extends LifecycleFragment {

    private static final String TAG = ListFragment.class.getName();

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    LiveData<DefaultLocation> liveData = null;

    private static final String FRACTIONAL_FORMAT = "%.4f";
    private static final String ACCURACY_FORMAT = "%.1fm";

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected DataAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private ListLocationsViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ListLocationsViewModel.class);

        //user and password
        String app_id=getString(R.string.api_transport_id);
        String app_key=getString(R.string.api_transport_key);

        // Initialize dataset.
        LocationViewModel lViewModel =  ViewModelProviders.of(this).get(LocationViewModel.class);
        liveData = lViewModel.getLocation(getContext());

        liveData.observe(this,new Observer<DefaultLocation>(){
            @Override
            public void onChanged(@Nullable DefaultLocation defaultLocation){
                updateLocation(defaultLocation);
//                Log.v(TAG, "liveData loadStopInformation +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ "+defaultLocation.getLatitude() );
//                Log.v(TAG, "liveData loadStopInformation +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ "+defaultLocation.getLongitude() );
//                Log.v(TAG, "liveData loadStopInformation +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ "+defaultLocation.getAccuracy() );
                //mViewModel.loadStopInformation(defaultLocation.getLatitude(), defaultLocation.getLongitude(), (int) defaultLocation.getAccuracy());
                mViewModel.loadStopInformation(app_id,app_key,51.509865,-0.118092,200);
                //mViewModel.loadStopInformation(51.500782092628455,-0.12462615966796875,200);

            }
        });

        // Handle changes emitted by LiveData
        mViewModel.getApiResponse().observe(this, apiResponse -> {
            if (apiResponse.getError() != null) {
                handleError(apiResponse.getError());
                Log.v(TAG, "handleError distinto null ");
            } else {
                handleResponse(apiResponse.getStopLocation());
                Log.v(TAG, "handleResponse ");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mAdapter = new DataAdapter();
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);


//        mLinearLayoutRadioButton = (RadioButton) rootView.findViewById(R.id.linear_layout_rb);
//        mLinearLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
//            }
//        });
//
//        mGridLayoutRadioButton = (RadioButton) rootView.findViewById(R.id.grid_layout_rb);
//        mGridLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
//            }
//        });

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if(mRecyclerView!=null) {
            // If a layout manager has already been set, get current scroll position.
            if (mRecyclerView.getLayoutManager() != null) {
                scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
            }

            switch (layoutManagerType) {
                case GRID_LAYOUT_MANAGER:
                    mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                    mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                    break;
                case LINEAR_LAYOUT_MANAGER:
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                    break;
                default:
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
            }

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.scrollToPosition(scrollPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }


    private void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE
        );
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void handleResponse(List<StopPointsEntity> stoppoints) {
//        setProgress(false);
        if (stoppoints != null && stoppoints.size()>0) {
            Log.v(TAG,"hr stoopoints "+stoppoints.size());
            mAdapter.addStopInformation(stoppoints);
        } else {
            mAdapter.clearStopInformation();
            Toast.makeText(
                    getContext(),
                    "No stop information found for the searched repository.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void handleError(Throwable error) {
      //  setProgress(false);
        mAdapter.clearStopInformation();
        Log.e(TAG, "error occured: " + error.toString());
        Toast.makeText(getContext(), "Oops! Some error occured.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Generates Strings for RecyclerView's adapter.
     */

    public void updateLocation(DefaultLocation defaultLocation) {
        String latitudeString = createFractionString(defaultLocation.getLatitude());
        String longitudeString = createFractionString(defaultLocation.getLongitude());
        String accuracyString = createAccuracyString(defaultLocation.getAccuracy());
        accuracyString = "200";
        defaultLocation = new DefaultLocation(Double.parseDouble(latitudeString),Double.parseDouble(longitudeString),Integer.parseInt(accuracyString));
//        Log.v(TAG, "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++updateLocation mDefaultLocation latitude "+ defaultLocation.getLatitude()+" addMarkers mDefaultLocation longitude "+defaultLocation.getLongitude());
    }

    private String createFractionString(double fraction) {
        return String.format(Locale.getDefault(), FRACTIONAL_FORMAT, fraction);
    }

    private String createAccuracyString(float accuracy) {
        return String.format(Locale.getDefault(), ACCURACY_FORMAT, accuracy);
    }


}
