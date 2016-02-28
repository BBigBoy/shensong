package com.fuyang.shensong;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fuyang.shensong.util.DialogUtil;

public class ErrorDialog extends DialogFragment implements View.OnClickListener {
    TextView errorTextView;
    Button btnOk, btnCancel;
    String errText=null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        View layoutView = DialogUtil.createDialogView(context,
                R.layout.dialog_err, 0.6f, 0.6f);
        errorTextView = (TextView) layoutView.findViewById(R.id.err_text);
        if(this.errText!=null){
            errorTextView.setText(this.errText);
        }
        btnOk = (Button) layoutView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        btnCancel = (Button) layoutView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        AlertDialog alertdialog = new AlertDialog.Builder(getActivity())
                .create();
        alertdialog.setView(layoutView, 0, 0, 0, 0);
        return alertdialog;
    }

    public void setErrText(final String errText) {
        this.errText=errText;
    }

    public void setBtnOkClickListener(View.OnClickListener clickListener) {
        if (btnOk != null) {
            btnOk.setOnClickListener(clickListener);
        }
    }

    public void setBtnCancelClickListener(View.OnClickListener clickListener) {
        if (btnCancel != null) {
            btnCancel.setOnClickListener(clickListener);
        }
    }

    @Override
    public void onClick(View v) {
        ErrorDialog.this.dismiss();
    }
}
