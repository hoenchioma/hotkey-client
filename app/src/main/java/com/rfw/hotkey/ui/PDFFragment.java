package com.rfw.hotkey.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.rfw.hotkey.R;


public class PDFFragment extends Fragment implements View.OnClickListener {

    private ImageButton fullScreenButton, upButton,  leftButton, downButton, righButton;
    private Button zoomInButton,zoomOutButton,fitHeightButton,fitWidthButton;
    private Boolean isFullScreen;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pdf, container, false);
        initialization(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }
    private void initialization(View rootView) {
        isFullScreen = false;
        fitWidthButton = rootView.findViewById(R.id.fitWidthButtonID);
        fullScreenButton = rootView.findViewById(R.id.fullScreenButtonID);
        upButton = rootView.findViewById(R.id.upButtonID);
        zoomInButton = rootView.findViewById(R.id.fromTheBeginningButtonID);
        zoomOutButton = rootView.findViewById(R.id.fromThisSlideButtonID);
        leftButton = rootView.findViewById(R.id.leftButtonID);
        downButton = rootView.findViewById(R.id.downButtonID);
        righButton = rootView.findViewById(R.id.rightButtonID);
        fullScreenButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        righButton.setOnClickListener(this);
        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

    }
}
