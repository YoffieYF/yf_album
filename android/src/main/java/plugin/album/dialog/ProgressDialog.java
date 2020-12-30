package plugin.album.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import plugin.album.R;


public class ProgressDialog extends Dialog {
    private static ProgressDialog sProgressDialog;

    public static void showDialog(Context context) {
        if (sProgressDialog != null) {
            sProgressDialog.dismiss();
        }
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.show();
    }

    public static void dismissDialog() {
        if (sProgressDialog != null) {
            sProgressDialog.dismiss();
            sProgressDialog = null;
        }
    }

    public ProgressDialog(@NonNull Context context) {
        super(context, R.style.Widget_ProgressDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_dialog);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
