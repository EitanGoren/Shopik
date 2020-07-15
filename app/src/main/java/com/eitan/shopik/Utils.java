package com.eitan.shopik;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class Utils {

    public static class DialogBox extends AppCompatDialogFragment{

        private String type,gender;
        int likes_left;

        public DialogBox(String type,String gender,int likes_left){
            this.gender = gender;
            this.type = type;
            this.likes_left = likes_left;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setView(R.layout.com_facebook_smart_device_dialog_fragment).setMessage(likes_left + " additional likes are needed!" + System.lineSeparator() +
                    "In order to give you the best matches possible ")
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setCancelable(false)
            .setIcon(R.drawable.ic_notifications_active_black_24dp);
            return builder.create();
        }
    }
}
