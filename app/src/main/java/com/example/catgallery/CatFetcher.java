package com.example.catgallery;

import android.net.Uri;
import android.util.Log;

import com.example.catgallery.models.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CatFetcher
{
    private static final String TAG = "CatFetcher";
    private static final String API_KEY = "d49cd574-c7c0-48e2-a363-0fb4fa07caa5";

    public byte[] getURLBytes(String urlSpec) throws IOException
    {
        URL url = new URL(urlSpec);
        //Set up HTTP connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("x-api-key", API_KEY);

        try
        {
            //set uo input and out put
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);

            }
            //continuosly read data from the input stream to the output stream
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, bytesRead);

            }
            out.close();
            return out.toByteArray();
        } finally
        {
            connection.disconnect();
        }
    }

    public String getURLString(String urlSpec) throws IOException
    {
        return new String(getURLBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems()
    {
        List<GalleryItem> items = new ArrayList<>();
        try
        {
            String url = Uri.parse("https://api.thecatapi.com/v1/images/search")
                    .buildUpon()
                    .appendQueryParameter("size", "thumb")
                    .appendQueryParameter("limit", "100")
                    .build().toString();

            String jsonString = getURLString(url);
            Log.i(TAG, "Recevied JSON: " + jsonString);
            parseItems(items,jsonString);
        } catch (IOException ieo)
        {
            Log.e(TAG, "Failed to Recevied JSON: " + ieo);
        }catch (JSONException je)
        {
            Log.e(TAG, "Failed to parse JSON ", je);
        }
        return items;
    }

    public void parseItems(List<GalleryItem> items, String jsonString) throws IOException, JSONException
    {
        JSONArray catsJSONArray = new JSONArray(jsonString);

        for (int  i = 0; i < catsJSONArray.length(); i++)
        {
            JSONObject catJSONObject = catsJSONArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setID(catJSONObject.getString("id"));
            item.setURL(catJSONObject.getString("url"));

            items.add(item);
        }

    }

}
