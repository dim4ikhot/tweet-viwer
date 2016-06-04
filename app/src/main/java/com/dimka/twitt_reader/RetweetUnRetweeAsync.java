package com.dimka.twitt_reader;

import android.os.AsyncTask;

import com.dimka.twitt_reader.list_adapters.TweetsViewAdapter;
import com.dimka.twitt_reader.pojo_classes.status.CommonStatusClass;

import retrofit2.Call;

public class RetweetUnRetweeAsync extends AsyncTask<Long,Void,Void> {

    boolean isRetweeted;
    CommonStatusClass successRetweeted;
    TweetsViewAdapter adapter;

    public RetweetUnRetweeAsync(boolean isRetweeted, TweetsViewAdapter adp){
        this.isRetweeted = isRetweeted;
        adapter = adp;
    }

    @Override
    protected Void doInBackground(Long... params) {
        long statusId = params[0];
        if (!isRetweeted) {
            try {
                Call<CommonStatusClass> retweetCall = Internet.service.retweetStatus(statusId);
                successRetweeted = retweetCall.execute().body();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Call<CommonStatusClass> unretweetCall =  Internet.service.unretweetStatus(statusId);
            try{
                successRetweeted = unretweetCall.execute().body();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(successRetweeted != null){
               /* for(CommonStatusClass status: homeStatuses){
                    if(status.getId() == successRetweeted.getId()){
                        int id = homeStatuses.indexOf(status);
                        homeStatuses.set(id, successRetweeted);
                        break;
                    }
                }*/
        }
        return null;
    }

    @Override
    public void onPostExecute(Void result){
        adapter.notifyDataSetChanged();
    }
}
