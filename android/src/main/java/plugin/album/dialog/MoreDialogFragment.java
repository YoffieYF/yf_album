package plugin.album.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import plugin.album.R;
import plugin.album.utils.AllowLossDialogFragment;

public class MoreDialogFragment extends AllowLossDialogFragment {
    private static final String TAG = "MoreDialogFragment";
    private OnMenuItemClick mListener;

    public static MoreDialogFragment show(FragmentActivity activity, OnMenuItemClick listener) {
        if (activity.getSupportFragmentManager() == null) {
            return null;
        }
        MoreDialogFragment fragment = (MoreDialogFragment) (activity).getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new MoreDialogFragment();
            fragment.mListener = listener;
        }

        fragment.showAllowingStateLoss(activity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.Widget_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_menu_more, container, false);
        view.findViewById(R.id.area_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDown();
                dismissAllowingStateLoss();
            }
        });

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        setCancelable(true);

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.AnimBottom);

        dialogWindow.setGravity(Gravity.BOTTOM);
        return dialog;
    }

    public static void dismiss(FragmentActivity activity) {
        FragmentManager manager = (activity).getSupportFragmentManager();
        if (manager == null) {
            return;
        }
        Fragment fragment = (activity).getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) {
            ((MoreDialogFragment) fragment).dismissAllowingStateLoss();
        }
    }


    public interface OnMenuItemClick {
        void onDown();
    }
}
