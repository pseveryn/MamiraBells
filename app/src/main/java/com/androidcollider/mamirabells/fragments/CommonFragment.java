package com.androidcollider.mamirabells.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.androidcollider.mamirabells.App;
import com.androidcollider.mamirabells.MainActivity;
import com.androidcollider.mamirabells.R;
import com.androidcollider.mamirabells.Utils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.LinkedList;

/**
 * Created by s.parkhomenko on 04.06.2015.
 */
public abstract class CommonFragment extends Fragment {

    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mTracker = App.tracker;
        mTracker.setScreenName(getRealTag());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onResume(){
        super.onResume();
        while(!pendingTransactions.isEmpty()){
            Utils.DBG("Executing pending transaction");
            pendingTransactions.pollFirst().run();
        }
    }

    public boolean onBack(){
        return false;
    }

    public String getRealTag(){
        return this.getClass().getName();
    }

    protected void addFragment(CommonFragment f){
        ((MainActivity) getActivity()).addFragment(f);
    }

    protected void replaceFragment(CommonFragment f){
        ((MainActivity) getActivity()).replaceFragment(f);
    }

    protected void finish(){
        tryExecuteTransaction(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().popBackStack();
            }
        });
    }

    protected void popAll(){
        getFragmentManager().popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public abstract String getTitle();

    private LinkedList<Runnable> pendingTransactions = new LinkedList<>();

    protected void tryExecuteTransaction(Runnable runnable){
        if(isResumed()){
            runnable.run();
        }else{
            Utils.DBG("Scheduling pending transaction");
            pendingTransactions.addLast(runnable);
        }
    }

}
