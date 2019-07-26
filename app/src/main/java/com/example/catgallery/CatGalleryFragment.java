package com.example.catgallery;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.catgallery.models.GalleryItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CatGalleryFragment extends Fragment
{
    private static final String TAG = "CatGalleryFragment";
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<CatHolder> mThumbnailDownloader;

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>
    {

        @Override
        protected List<GalleryItem> doInBackground(Void... params)
        {
            return new CatFetcher().fetchItems();

        }
        @Override
        protected void onPostExecute(List<GalleryItem> items)
        {
            mItems = items;
            setUpAdapter();
        }
    }

    private class CatHolder extends RecyclerView.ViewHolder
    {
        private ImageView mItemImageView;

        public CatHolder(View itemView)
        {
            super(itemView);
            mItemImageView = (ImageView) itemView;
        }
        public void bindDrawable(Drawable drawable)
        {
            mItemImageView.setImageDrawable(drawable);
        }

    }
    private class CatAdapter extends RecyclerView.Adapter<CatHolder>
    {
        private List<GalleryItem> mGalleryItems;

        public CatAdapter(List<GalleryItem> galleryItems)
        {
            mGalleryItems = galleryItems;
        }
        @NonNull
        @Override
        public CatHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_item_gallery, parent, false);

            return new CatHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CatHolder catHolder, int position)
        {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeHolder = getResources().getDrawable(R.drawable.missing_image);
            catHolder.bindDrawable(placeHolder);
            mThumbnailDownloader.queueThumbnail(catHolder, galleryItem.getURL());
        }

        @Override
        public int getItemCount()
        {
            return mGalleryItems.size();
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

        mThumbnailDownloader = new ThumbnailDownloader<>();
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "The background thread has started.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_cat_gallery, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.cat_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setUpAdapter();
        return v;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Backgorund Thread destroyed");
    }
    private void setUpAdapter()
    {
        if (isAdded())
        {
            mRecyclerView.setAdapter(new CatAdapter(mItems));
        }
    }
}
