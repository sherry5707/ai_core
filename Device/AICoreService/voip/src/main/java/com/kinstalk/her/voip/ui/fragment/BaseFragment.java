package com.kinstalk.her.voip.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
    protected View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBaseDatas(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getRootLayoutRes(), null);
        initViews(rootView, savedInstanceState);
        initActions(rootView);
        initDatas(rootView);
        return rootView;
    }

    protected void initBaseDatas(Bundle savedInstanceState) {

    }

    protected abstract int getRootLayoutRes();

    protected abstract void initViews(View contentView, Bundle savedInstanceState);

    protected abstract void initActions(View contentView);

    protected abstract void initDatas(View contentView);

}
