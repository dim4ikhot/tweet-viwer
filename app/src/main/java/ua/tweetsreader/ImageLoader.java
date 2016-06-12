package ua.tweetsreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/*
 * Created by Dimka on 07.06.2016.
 */
public class ImageLoader extends AsyncTask<String,Void,Void> {

    Context context;
    Bitmap bmp;
    ImageView headerImage;

    public ImageLoader(Context ctx, ImageView headerImg){
        context = ctx;
        headerImage = headerImg;
    }

    @Override
    protected Void doInBackground(String... params) {
        String profileImage = params[0];
        try {
            bmp = Picasso.with(context).load(profileImage).get();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void onPostExecute(Void result){
        if(bmp != null) {
            headerImage.setImageBitmap(bmp);
        }
    }
}
