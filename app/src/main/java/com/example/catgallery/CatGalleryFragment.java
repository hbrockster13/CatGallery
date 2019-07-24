package com.example.catgallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

public class CatGalleryFragment extends Fragment
{
    private static final String TAG = "CatGalleryFragment";

    private class FetchItemsTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                String result = new CatFetcher().getURLString("https://api.thecatapi.com/v1/images/search");
                Log.i(TAG, "Fetched Contents of URL: " + result);


            } catch (IOException ioe)
            {
                Log.i(TAG, "Failed to fetch URL: ", ioe);
            }

            return null;
        }
    }

    public static Fragment newInstance()
    {
        return new CatGalleryFragment();
    }

    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_cat_gallery, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        return v;
    }
}
