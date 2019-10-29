package com.example.projectscandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    Button qrscan,docscan;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        //initialising variables
        qrscan = root.findViewById(R.id.qrScan);
        docscan=root.findViewById(R.id.docscan);

        qrscan.setOnClickListener(this);
        docscan.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        FragmentManager fragmentManager=getFragmentManager();
        switch (v.getId()){
            case R.id.qrScan:
                Intent intent=new Intent(activity,mainqr.class);
                startActivity(intent);
                break;

            case R.id.docscan:
                fragment = new DocScanFragment();
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                break;
        }

    }
}
