package ua.tweetsreader;

import android.os.AsyncTask;

import ua.tweetsreader.adapters.TweetsViewAdapter;
import ua.tweetsreader.gson.status.CommonStatusClass;
import ua.tweetsreader.retrofit.TwitterRest;

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
        TwitterRest service = Internet.getInstance().getTwitterRest();
        if (!isRetweeted) {
            try {
                Call<CommonStatusClass> retweetCall = service.retweetStatus(statusId);
                successRetweeted = retweetCall.execute().body();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Call<CommonStatusClass> unretweetCall = service.unretweetStatus(statusId);
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
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
