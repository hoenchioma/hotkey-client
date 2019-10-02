package com.rfw.hotkey.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.rfw.hotkey.R;


public class ExtraFragment extends Fragment implements View.OnClickListener{


    private Button  macrosButton ;
    private ImageButton pptButton,multimediaButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_extra, container, false);
        initialization(rootView);
        return rootView;
    }
    void initialization(View rootView){
        pptButton = rootView.findViewById(R.id.pptButtonID);
        macrosButton = rootView.findViewById(R.id.macrosButtonID);
        multimediaButton = rootView.findViewById(R.id.multimediaButtonID);

        pptButton.setOnClickListener(this);
        macrosButton.setOnClickListener(this);
        multimediaButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pptButtonID) {
            replaceFragment(new PowerPointFragment());
        }
        if (id == R.id.macrosButtonID) {
            replaceFragment(new MacrosFragment());
        }
        if (id == R.id.multimediaButtonID) {
            replaceFragment(new MultimediaFragment());
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer,fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }
}
