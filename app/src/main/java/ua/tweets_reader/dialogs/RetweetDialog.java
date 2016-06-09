package ua.tweets_reader.dialogs;

import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ua.tweets_reader.Internet;
import com.dimka.twitt_reader.R;

public class RetweetDialog extends DialogFragment implements DialogInterface.OnClickListener{

    public interface onButtonsClickListener{
        void onQuoteButtonClick();
        void onRetweetButtonClick();
    }
    @NonNull
    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.retweet_dialog_title);
        builder.setMessage(R.string.retweet_dialog_message);
        if(!Internet.getInstance().getCurrentStatus().getRetweeted()) {
            builder.setPositiveButton(R.string.retweet_dialog_retweet, this);
        }
        else{
            builder.setPositiveButton(R.string.retweet_dialog_unretweet, this);
        }
        builder.setNegativeButton(R.string.retweet_dialog_cancel,this);
        builder.setNeutralButton(R.string.retweet_dialog_quote,this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case Dialog.BUTTON_POSITIVE:
                ((onButtonsClickListener)getActivity()).onRetweetButtonClick();
                break;
            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
            case Dialog.BUTTON_NEUTRAL:
                ((onButtonsClickListener)getActivity()).onQuoteButtonClick();
                break;
        }
        dismiss();
    }
}
