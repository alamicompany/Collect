package com.alamicompany.collect;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by AC04 on 03.05.17.
 */


public class LoadAsyncData extends AsyncTask {

    MainActivity main;


    public LoadAsyncData(MainActivity main) {

        this.main = main;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            main.getPostTags();
            main.getUserInfo();
            main.getPostPhotos();
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return  params;
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();

        main.showProgressBar();

    }

    @Override
    protected void onPostExecute(Object o) {
        //super.onPostExecute(o);
        main.displayData();
        main.init();
        main.hideProgressBar();
    }


}




