package com.vst.image.vehiclestimageclassifier;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by sooorajjj on 27/9/17.
 */

public class GridViewAdapter extends BaseAdapter {


    //Imageloader to load images
    private ImageLoader imageLoader;

    //Context
    private Context context;

    //ArrayList for Storing image urls and titles
    private ArrayList<String> ids;
    private ArrayList<String> folder_names;
    private ArrayList<String> file_names;
    private ArrayList<String> urls;

    public GridViewAdapter (Context context, ArrayList<String> ids, ArrayList<String> folder_names,
                            ArrayList<String> file_names, ArrayList<String> urls){
        //Getting all the values
        this.context = context;
        this.ids = ids;
        this.folder_names = folder_names;
        this.file_names = file_names;
        this.urls = urls;
    }
    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int i) {
        return urls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //Creating a linear layout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //NetworkImageView
        NetworkImageView networkImageView = new NetworkImageView(context);

        //Initializing ImageLoader
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
        imageLoader.get(urls.get(i), ImageLoader.getImageListener(networkImageView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        //Setting the image url to load
        networkImageView.setImageUrl(urls.get(i),imageLoader);

        //Creating a textview to show the title
        TextView textView = new TextView(context);
        textView.setText(file_names.get(i));

        //Scaling the imageview
        networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        networkImageView.setLayoutParams(new GridView.LayoutParams(200,200));

        //Adding views to the layout
        linearLayout.addView(textView);
        linearLayout.addView(networkImageView);

        //Returnint the layout
        return linearLayout;
    }
}
