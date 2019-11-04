package com.rfw.hotkey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.rfw.hotkey.R;
import com.rfw.hotkey.ui.live_screen.LiveScreenActivity;
import com.rfw.hotkey.ui.macros.MacrosFragment;
import com.rfw.hotkey.ui.multimedia.MultimediaFragment;
import com.rfw.hotkey.ui.pdf.PDFFragment;
import com.rfw.hotkey.ui.ppt.PowerPointFragment;

import java.util.Objects;


public class ExtrasFragment extends Fragment {
    private static final String TAG = "ExtrasFragment";

    private MaterialButton liveScreenButton;
    private MaterialButton macrosButton;
    private MaterialButton pptButton;
    private MaterialButton multimediaButton;
    private MaterialButton pdfButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_extras, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        pptButton = rootView.findViewById(R.id.pptButton);
        macrosButton = rootView.findViewById(R.id.macrosButton);
        multimediaButton = rootView.findViewById(R.id.multimediaButton);
        pdfButton = rootView.findViewById(R.id.pdfButton);
        liveScreenButton = rootView.findViewById(R.id.liveScreenButton);

        pptButton.setOnClickListener(view -> replaceFragment(new PowerPointFragment()));
        macrosButton.setOnClickListener(view -> replaceFragment(new MacrosFragment()));
        multimediaButton.setOnClickListener(view -> replaceFragment(new MultimediaFragment()));
        pdfButton.setOnClickListener(view -> replaceFragment(new PDFFragment()));
        liveScreenButton.setOnClickListener(view -> startActivity(new Intent(getActivity(), LiveScreenActivity.class)));
    }

    private void replaceFragment(Fragment fragment) {
        ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(fragment);

//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frameContainer,fragment);
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        fragmentTransaction.commit();
    }
}
