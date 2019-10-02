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
import android.widget.Switch;
import android.widget.Toast;

import com.rfw.hotkey.R;


public class MacrosFragment extends Fragment {
    private static final String TAG = "MacrosFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_macros, container, false);
        Button[][] macroButtons = new Button[11][4];
        final int idVal = R.id.macro11id;
        for(int i = 1; i <= 10; i++)
            for(int j = 1; j <=3; j++) macroButtons[i][j]=(Button)v.findViewById(idVal+i);
        Switch macroState = (Switch) v.findViewById(R.id.macroSwitchid);

        for(int i = 1; i <= 10; i++)
            for(int j = 1; j <=3; j++){
                macroButtons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!macroState.isChecked())Toast.makeText(getActivity(),"Macro pressed", Toast.LENGTH_SHORT).show();
                        else{
                            //Toast.makeText(getActivity(),"Macro edited", Toast.LENGTH_SHORT).show();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frameContainer,new EditMacrosFragment());
                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            fragmentTransaction.commit();
                        }
                    }
                });
            }

        return v;
    }
}
