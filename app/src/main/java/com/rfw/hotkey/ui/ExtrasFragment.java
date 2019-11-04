package com.rfw.hotkey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.rfw.hotkey.R;
import com.rfw.hotkey.ui.live_screen.LiveScreenActivity;
import com.rfw.hotkey.ui.macros.MacrosFragment;
import com.rfw.hotkey.ui.multimedia.MultimediaFragment;
import com.rfw.hotkey.ui.pdf.PDFFragment;
import com.rfw.hotkey.ui.ppt.PowerPointFragment;


public class ExtrasFragment extends Fragment {
    private static final String TAG = "ExtrasFragment";

    private MaterialButton liveScreenButton;
    private MaterialButton macrosButton;
    private MaterialButton pptButton;
    private MaterialButton multimediaButton;
    private MaterialButton pdfButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
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

        pptButton       .setOnClickListener(view -> pushFragment(new PowerPointFragment() ));
        macrosButton    .setOnClickListener(view -> pushFragment(new MacrosFragment()     ));
        multimediaButton.setOnClickListener(view -> pushFragment(new MultimediaFragment() ));
        pdfButton       .setOnClickListener(view -> pushFragment(new PDFFragment()        ));

        liveScreenButton.setOnClickListener(view -> startActivity(new Intent(getActivity(), LiveScreenActivity.class)));
    }

    private void pushFragment(@NonNull Fragment fragment) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.pushFragment(fragment);
    }
}
