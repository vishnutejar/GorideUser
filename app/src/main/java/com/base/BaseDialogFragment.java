package com.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebRequestErrorDialog;
import com.medy.retrofitwrapper.WebServiceResponseListener;


/**
 * Created by bitu on 15/8/17.
 */

public abstract class  BaseDialogFragment extends DialogFragment
        implements View.OnClickListener, WebServiceResponseListener {


    WebRequestErrorDialog errorMessageDialog;
    FragmentManager childFm;
    private View view;

    public FragmentManager getChildFm () {
        return childFm;
    }

    public FragmentTransaction getNewChildFragmentTransaction () {
        return getChildFm().beginTransaction();
    }

    public void clearChildFragmentBackStack () {
        clearChildFragmentBackStack(0);
    }

    public void clearChildFragmentBackStack (int pos) {
        if (childFm.getBackStackEntryCount() > pos) {
            try {
                childFm.popBackStackImmediate(childFm.getBackStackEntryAt(pos).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (IllegalStateException e) {
            }
        }
    }

    public boolean handleOnBackPress () {
        if (getChildFm().getBackStackEntryCount() > 0) {
            getChildFm().popBackStackImmediate();
            return true;
        }
        return false;
    }


    public void changeChildFragment (Fragment f, boolean addBackStack,
                                     boolean clearAll, int pos, int enterAnim, int exitAnim,
                                     int enterAnimBackStack, int exitAnimBackStack,
                                     boolean isReplace) {
        if (getFragmentContainerResourceId() == -1) return;
        if (clearAll) {
            clearChildFragmentBackStack(pos);
        }
        if (f != null) {
            try {
                FragmentTransaction ft = getNewChildFragmentTransaction();
                ft.setCustomAnimations(enterAnim, exitAnim, enterAnimBackStack, exitAnimBackStack);
                if (isReplace) {
                    ft.replace(getFragmentContainerResourceId(), f, f.getClass().getSimpleName());
                } else {
                    ft.add(getFragmentContainerResourceId(), f, f.getClass().getSimpleName());
                }
                if (addBackStack) {
                    ft.addToBackStack(f.getClass().getSimpleName());
                }
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childFm = getChildFragmentManager();
        if (view == null) {
            setupFragmentViewByResource(inflater, container);
            initializeComponent();
        }
        notifyToActivity(1);
        return view;
    }

    public BaseFragment getLatestFragment () {

        Fragment fragment = getChildFm().findFragmentById(getFragmentContainerResourceId());
        if (fragment != null && fragment instanceof BaseFragment) {
            return ((BaseFragment) fragment);
        }

        return null;
    }

    @Override
    public void setupDialog (Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog view
        dialog.setContentView(layout);
        //setup dialog window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        // wlmp.gravity = Gravity.LEFT | Gravity.TOP;
        wlmp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        Dialog dialog = new Dialog(this.getActivity(), this.getTheme()) {
            @Override
            public void onBackPressed () {
                if (handleOnBackPress()) return;
                super.onBackPressed();
            }
        };
        return dialog;
    }

    public void callDialogBackPress () {
        getDialog().onBackPressed();
    }

    @Override
    public void onClick (View v) {

    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        notifyToActivity(2);
    }

    public LinearLayoutManager getVerticalLinearLayoutManager () {
        return new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
    }

    public void setupFragmentViewByResource (LayoutInflater inflater, @Nullable ViewGroup container) {

        view = inflater.inflate(getLayoutResourceId(), container, false);
    }

    @Nullable
    @Override
    public View getView () {
        return view;
    }

    private void notifyToActivity (int tag) {
        if (getActivity() == null) return;
        switch (tag) {

        }
    }


    @Override
    public void onWebRequestCall (WebRequest webRequest) {

    }

    @Override
    public void onWebRequestPreResponse(WebRequest webRequest) {

    }

    @Override
    public void onWebRequestResponse (WebRequest webRequest) {

    }

    public abstract int getLayoutResourceId ();

    public abstract void initializeComponent ();

    public abstract int getFragmentContainerResourceId ();


    public boolean checkValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }

    public boolean isValidActivity () {
        return getActivity() != null;
    }

    public boolean isValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }

    public void showErrorMessage (String msg) {
        if (errorMessageDialog == null) {
            errorMessageDialog = new WebRequestErrorDialog(getContext(), msg);
        } else if (errorMessageDialog.isShowing()) {
            errorMessageDialog.dismiss();
        }
        errorMessageDialog.setMsg(msg);
        errorMessageDialog.show();

    }

    public void updateViewVisibility (View view, int visibility) {
        if (view==null)return;
        if (view.getVisibility() != visibility)
            view.setVisibility(visibility);
    }
}
