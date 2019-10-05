package com.rfw.hotkey.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;


public class ExtraFragment extends Fragment implements View.OnClickListener{


    private Button pptButton, macrosButton, multimediaButton;
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
        multimediaButton.setOnClickListener(this);
        macrosButton.setOnClickListener(this);
        multimediaButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pptButtonID) {

        }
        if (id == R.id.macrosButtonID) {

        }
        if (id == R.id.multimediaButtonID) {

        }
    }
}
