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


public class MacrosFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MacrosFragment";
    Switch macroState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_macros, container, false);
        Button[] macroButtons = new Button[40];
        macroButtons[0] = (Button) v.findViewById(R.id.macroid0);
        macroButtons[1] = (Button) v.findViewById(R.id.macroid1);
        macroButtons[2] = (Button) v.findViewById(R.id.macroid2);
        macroButtons[3] = (Button) v.findViewById(R.id.macroid3);
        macroButtons[4] = (Button) v.findViewById(R.id.macroid4);
        macroButtons[5] = (Button) v.findViewById(R.id.macroid5);
        macroButtons[6] = (Button) v.findViewById(R.id.macroid6);
        macroButtons[7] = (Button) v.findViewById(R.id.macroid7);
        macroButtons[8] = (Button) v.findViewById(R.id.macroid8);
        macroButtons[9] = (Button) v.findViewById(R.id.macroid9);
        macroButtons[10] = (Button) v.findViewById(R.id.macroid10);
        macroButtons[11] = (Button) v.findViewById(R.id.macroid11);
        macroButtons[12] = (Button) v.findViewById(R.id.macroid12);
        macroButtons[13] = (Button) v.findViewById(R.id.macroid13);
        macroButtons[14] = (Button) v.findViewById(R.id.macroid14);
        macroButtons[15] = (Button) v.findViewById(R.id.macroid15);
        macroButtons[16] = (Button) v.findViewById(R.id.macroid16);
        macroButtons[17] = (Button) v.findViewById(R.id.macroid17);
        macroButtons[18] = (Button) v.findViewById(R.id.macroid18);
        macroButtons[19] = (Button) v.findViewById(R.id.macroid19);
        macroButtons[20] = (Button) v.findViewById(R.id.macroid20);
        macroButtons[21] = (Button) v.findViewById(R.id.macroid21);
        macroButtons[22] = (Button) v.findViewById(R.id.macroid22);
        macroButtons[23] = (Button) v.findViewById(R.id.macroid23);
        macroButtons[24] = (Button) v.findViewById(R.id.macroid24);
        macroButtons[25] = (Button) v.findViewById(R.id.macroid25);
        macroButtons[26] = (Button) v.findViewById(R.id.macroid26);
        macroButtons[27] = (Button) v.findViewById(R.id.macroid27);
        macroButtons[28] = (Button) v.findViewById(R.id.macroid28);
        macroButtons[29] = (Button) v.findViewById(R.id.macroid29);


        macroState = (Switch) v.findViewById(R.id.macroSwitchid);

        for (int i = 0; i < 30; i++) {
            macroButtons[i].setOnClickListener(this);
        }

        return v;
    }

    void editMaroFargment(int index) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        EditMacrosFragment editMacro = new EditMacrosFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        editMacro.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameContainer, editMacro);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (!macroState.isChecked())
            Toast.makeText(getActivity(), "Macro pressed", Toast.LENGTH_SHORT).show();
        else{
            switch (v.getId()) {
                case R.id.macroid0:
                    editMaroFargment(0);
                    break;
                case R.id.macroid1:
                    editMaroFargment(1);
                    break;
                case R.id.macroid2:
                    editMaroFargment(2);
                    break;
                case R.id.macroid3:
                    editMaroFargment(3);
                    break;
                case R.id.macroid4:
                    editMaroFargment(4);
                    break;
                case R.id.macroid5:
                    editMaroFargment(5);
                    break;
                case R.id.macroid6:
                    editMaroFargment(6);
                    break;
                case R.id.macroid7:
                    editMaroFargment(7);
                    break;
                case R.id.macroid8:
                    editMaroFargment(8);
                    break;
                case R.id.macroid9:
                    editMaroFargment(9);
                    break;
                case R.id.macroid10:
                    editMaroFargment(10);
                    break;
                case R.id.macroid11:
                    editMaroFargment(11);
                    break;
                case R.id.macroid12:
                    editMaroFargment(12);
                    break;
                case R.id.macroid13:
                    editMaroFargment(13);
                    break;
                case R.id.macroid14:
                    editMaroFargment(14);
                    break;
                case R.id.macroid15:
                    editMaroFargment(15);
                    break;
                case R.id.macroid16:
                    editMaroFargment(16);
                    break;
                case R.id.macroid17:
                    editMaroFargment(17);
                    break;
                case R.id.macroid18:
                    editMaroFargment(18);
                    break;
                case R.id.macroid19:
                    editMaroFargment(19);
                    break;
                case R.id.macroid20:
                    editMaroFargment(20);
                    break;
                case R.id.macroid21:
                    editMaroFargment(21);
                    break;
                case R.id.macroid22:
                    editMaroFargment(22);
                    break;
                case R.id.macroid23:
                    editMaroFargment(23);
                    break;
                case R.id.macroid24:
                    editMaroFargment(24);
                    break;
                case R.id.macroid25:
                    editMaroFargment(25);
                    break;
                case R.id.macroid26:
                    editMaroFargment(26);
                    break;
                case R.id.macroid27:
                    editMaroFargment(27);
                    break;
                case R.id.macroid28:
                    editMaroFargment(28);
                    break;
                case R.id.macroid29:
                    editMaroFargment(29);
                    break;
            }
        }
    }
}
