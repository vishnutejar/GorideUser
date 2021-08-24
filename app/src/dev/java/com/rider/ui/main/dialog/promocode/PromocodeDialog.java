package com.rider.ui.main.dialog.promocode;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.medy.retrofitwrapper.WebRequest;
import com.rider.R;
import com.rider.appBase.AppBaseDialogFragment;

public class PromocodeDialog extends AppBaseDialogFragment {

    private EditText et_promocode;
    private TextView tv_cancel, tv_apply;
    private PromocodeListener promocodeListener;

    private String promocode;

    public String getPromocode() {
        return et_promocode.getText().toString().trim();
    }

    public void setPromocodeListener(PromocodeListener promocodeListener) {
        this.promocodeListener = promocodeListener;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_promocode;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.windowAnimations = R.style.PromocodeDialogStyle;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void initializeComponent() {
        et_promocode = getView().findViewById(R.id.et_promocode);
        tv_apply = getView().findViewById(R.id.tv_apply);
        tv_cancel = getView().findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(this);
        tv_apply.setOnClickListener(this);
    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_apply:
                applyPromocode();
                break;
        }
    }

    private void applyPromocode() {
        String promocode = et_promocode.getText().toString().trim();
        if (!isValidString(promocode)) {
            showErrorMessage("Please enter promocode");
            return;
        }
        if (promocodeListener!=null){
            promocodeListener.onPromocodeEntered(this);
        }
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {

        super.onWebRequestResponse(webRequest);
        dismissProgressBar();
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {


            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                webRequest.showInvalidResponse(msg);
            }
        }
    }

    public interface PromocodeListener{
        void onPromocodeEntered(PromocodeDialog promocodeDialog);
    }


}
