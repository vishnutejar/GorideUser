package com.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utilities.FullHeightLinearLayoutManager;
import com.utilities.Utils;


/**
 * Created by bitu on 15/8/17.
 */

public abstract class  BaseFragment extends Fragment
        implements View.OnClickListener {

    protected FragmentManager childFm;
    private View view;

    @Override
    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childFm = getChildFragmentManager();
        setupFragmentViewByResource(inflater, container);
        initializeComponent();
        return view;
    }

    @Override
    public void onClick (View v) {

    }

    public synchronized void hideKeyboard () {
        if (!isValidActivity()) return;
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getActivity());
        }
        hideKeyboard(view);
    }

    public synchronized void hideKeyboard (View view) {
        if (view == null) {
            return;
        }
        if (!isValidActivity()) return;
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        notifyToActivity(2);
    }

    public LinearLayoutManager getVerticalLinearLayoutManager () {
        return new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
    }

    public LinearLayoutManager getFullHeightLinearLayoutManager () {
        return new FullHeightLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    public LinearLayoutManager getHorizontalLayoutManager () {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
    }

    public LinearLayoutManager getGridLayoutManager (int column) {
        return new GridLayoutManager(getActivity(), column);
    }


    public void setupFragmentViewByResource (LayoutInflater inflater, @Nullable ViewGroup container) {

        view = inflater.inflate(getLayoutResourceId(), container, false);
    }

    @Nullable
    @Override
    public View getView () {
        return view;
    }

    public void notifyToActivity (int tag) {
        if (getActivity() == null) return;
        switch (tag) {
            case 1:
                ((BaseActivity) getActivity()).onCreateViewFragment(this);
                break;
            case 2:
                ((BaseActivity) getActivity()).onDestroyViewFragment(this);
                break;
        }
    }

    public abstract int getLayoutResourceId ();

    public int getFragmentContainerResourceId (Fragment fragment) {
        return -1;
    }

    public abstract void initializeComponent ();

    public abstract void reInitializeComponent ();

    public void viewCreateFromBackStack () {

    }

    public FragmentManager getChildFm () {
        return childFm;
    }

    public FragmentTransaction getNewChildFragmentTransaction () {
        return getChildFm().beginTransaction();
    }

    public void clearBackStack (int pos) {
        if (getFragmentManager().getBackStackEntryCount() > pos) {
            try {
                getFragmentManager().popBackStackImmediate(
                        getFragmentManager().getBackStackEntryAt(pos).getId(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (IllegalStateException e) {
            }
        }
    }

    public void clearChildFragmentBackStack () {
        clearChildFragmentBackStack(0);
    }

    public void clearChildFragmentBackStack (int pos) {
        if (childFm.getBackStackEntryCount() > pos) {
            try {
                childFm.popBackStack(childFm.getBackStackEntryAt(pos).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
                                     boolean clearAll, int pos, boolean isReplace) {

        if (clearAll) {
            clearChildFragmentBackStack(pos);
        }
        if (getFragmentContainerResourceId(f) == -1) return;
        if (f != null) {
            try {
                FragmentTransaction ft = getNewChildFragmentTransaction();
                if (isReplace) {
                    ft.replace(getFragmentContainerResourceId(f), f, f.getClass().getSimpleName());
                } else {
                    ft.add(getFragmentContainerResourceId(f), f, f.getClass().getSimpleName());
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


    public void changeChildFragment (Fragment f, boolean addBackStack,
                                     boolean clearAll, int pos, int enterAnim, int exitAnim,
                                     int enterAnimBackStack, int exitAnimBackStack, boolean isReplace) {

        if (clearAll) {
            clearChildFragmentBackStack(pos);
        }
        if (getFragmentContainerResourceId(f) == -1) return;
        if (f != null) {
            try {
                FragmentTransaction ft = getNewChildFragmentTransaction();
                ft.setCustomAnimations(enterAnim, exitAnim, enterAnimBackStack, exitAnimBackStack);
                if (isReplace) {
                    ft.replace(getFragmentContainerResourceId(f), f, f.getClass().getSimpleName());
                } else {
                    ft.add(getFragmentContainerResourceId(f), f, f.getClass().getSimpleName());
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


    public boolean isValidActivity () {
        return getActivity() != null;
    }

    public boolean isValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }

    public void printLog (String msg) {
        if (Utils.isDebugBuild(getActivity()) && msg != null) {
            Log.e(getClass().getSimpleName(), msg);
        }
    }

    public boolean isValidObject (Object object) {
        return object != null;
    }

    public void showToast (String message) {
        if (message == null || message.trim().isEmpty()) return;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void printLog (String TAG, String method, String msg) {
        if (Utils.isDebugBuild(getActivity()))
            Log.e(TAG, method + ": " + msg);

    }


}
