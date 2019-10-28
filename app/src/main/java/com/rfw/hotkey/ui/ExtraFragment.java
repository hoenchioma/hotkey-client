package com.rfw.hotkey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;

import java.util.Objects;


public class ExtraFragment extends Fragment implements View.OnClickListener {
    private ImageButton liveScreenButton;
    private ImageButton macrosButton;
    private ImageButton pptButton;
    private ImageButton multimediaButton;
    private ImageButton pdfButton;
    private ImageButton gamepadButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_extra, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        pptButton = rootView.findViewById(R.id.pptButtonID);
        macrosButton = rootView.findViewById(R.id.macrosButtonID);
        multimediaButton = rootView.findViewById(R.id.multimediaButtonID);
        pdfButton = rootView.findViewById(R.id.pdfButtonID);
        liveScreenButton = rootView.findViewById(R.id.liveScreenButtonID);
        gamepadButton = rootView.findViewById(R.id.gamepadButtonID);

        pptButton.setOnClickListener(this);
        macrosButton.setOnClickListener(this);
        multimediaButton.setOnClickListener(this);
        pdfButton.setOnClickListener(this);
        liveScreenButton.setOnClickListener(this);
        gamepadButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.pptButtonID:
                replaceFragment(new PowerPointFragment());
                break;
            case R.id.macrosButtonID:
                replaceFragment(new MacrosFragment());
                break;
            case R.id.multimediaButtonID:
                replaceFragment(new MultimediaFragment());
                break;
            case R.id.pdfButtonID:
                replaceFragment(new PDFFragment());
                break;
            case R.id.liveScreenButtonID:
                startActivity(new Intent(getActivity(), LiveScreenActivity.class));
                break;
            case R.id.gamepadButtonID:
                startActivity(new Intent(getActivity(), GamepadActivity.class));
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(fragment);
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frameContainer,fragment);
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        fragmentTransaction.commit();
    }
}
