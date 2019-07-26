package com.example.catgallery.models;

public class GalleryItem
{
    private String mID;
    private String mURL;

    @Override
    public String toString()
    {
        return mURL;
    }

    public String getID()
    {
        return mID;
    }

    public void setID(String mID)
    {
        this.mID = mID;
    }

    public String getURL()
    {
        return mURL;
    }

    public void setURL(String mURL)
    {
        this.mURL = mURL;
    }
}
