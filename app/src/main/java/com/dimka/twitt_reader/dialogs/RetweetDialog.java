package com.dimka.twitt_reader.dialogs;

import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.dimka.twitt_reader.R;

public class RetweetDialog extends DialogFragment implements DialogInterface.OnClickListener{

    public interface onButtonsClickListener{
        void onQuoteButtonClick();
    }

    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.retweet_dialog_title);
        builder.setMessage(R.string.retweet_dialog_message);
        builder.setPositiveButton(R.string.retweet_dialog_retweet,this);
        builder.setNegativeButton(R.string.retweet_dialog_cancel,this);
        builder.setNeutralButton(R.string.retweet_dialog_quote,this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case Dialog.BUTTON_POSITIVE:
                break;
            case Dialog.BUTTON_NEGATIVE:
                break;
            case Dialog.BUTTON_NEUTRAL:
                ((onButtonsClickListener)getActivity()).onQuoteButtonClick();
                break;
        }
        dismiss();
    }
}
