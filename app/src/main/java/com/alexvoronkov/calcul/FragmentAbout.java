package com.alexvoronkov.calcul;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
//класс окна о программе
public class FragmentAbout extends DialogFragment {
    String versionName = BuildConfig.VERSION_NAME;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String text = getString(R.string.text_about);
        String author = getString(R.string.author);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(R.string.app_name)
                .setIcon(R.drawable.ic_launcher)
                .setMessage("Версия: " + versionName + " " + text + author)
                .create();
    }
}
