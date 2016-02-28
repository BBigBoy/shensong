package com.fuyang.shensong;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;

import com.fuyang.shensong.util.DialogUtil;

import me.fichardu.circleprogress.CircleProgress;

public class ConnectDialog extends DialogFragment {
    private CircleProgress mProgressView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        View layoutView = DialogUtil.createDialogView(context,
                R.layout.dialog_connet, 0.6f, 0.6f);
        AlertDialog alertdialog = new AlertDialog.Builder(getActivity())
                .create();
        alertdialog.setView(layoutView, 0, 0, 0, 0);
        mProgressView = (CircleProgress) layoutView.findViewById(R.id.progress);
        return alertdialog;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        mProgressView.startAnim();
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        mProgressView.stopAnim();
        Log.i("ConnectDialogonPause", "ConnectDialogonPause");
        super.onPause();
    }
}
