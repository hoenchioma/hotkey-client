package com.rfw.hotkey.ui.macros;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class EditMacrosFragment extends Fragment {
    private static final String TAG = "EditMacrosFragment";
    private  int index;
    private Button saveButton;
    private Button cancelButton;
    private TextInputEditText buttonName;


    private String[] array_keys_num = {
            "0","1", "2", "3", "4", "5","6", "7","8", "9"
    };
    private String[] array_keys_char = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"//, "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    private String[] array_keys_sp = {
            "ESC", "ALT", "CTRL", "SHIFT", "DEL", "INS", "HOME", "END", "PGUP", "PGDN"
    };

    private List<String> listSourceNum = new ArrayList<>();
    private List<String> listSourceChar = new ArrayList<>();
    private List<String> listSourceSP = new ArrayList<>();
    private List<String> macroButtons = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_macros, container, false);
        saveButton = (Button)v.findViewById(R.id.saveMacroButtonID);
        cancelButton = (Button)v.findViewById(R.id.calcelButtonid);
        buttonName = (TextInputEditText) v.findViewById(R.id.macroNameEditTextID);

        Bundle bundle = getArguments();
        if(bundle != null){
            index = bundle.getInt("index");
            setUpList();
            GridView gridViewNum =(GridView) v.findViewById(R.id.macroGridNumID);
            GridView gridViewChar =(GridView) v.findViewById(R.id.macroGridCharID);
            GridView gridViewSP =(GridView) v.findViewById(R.id.macroGridSPID);
            MacroGridViewAdapter adapterNum = new MacroGridViewAdapter(listSourceNum,inflater.getContext(), macroButtons);
            MacroGridViewAdapter adapterChar = new MacroGridViewAdapter(listSourceChar,inflater.getContext(), macroButtons);
            MacroGridViewAdapter adapterSP = new MacroGridViewAdapter(listSourceSP,inflater.getContext(), macroButtons);
            gridViewNum.setAdapter(adapterNum);
            gridViewChar.setAdapter(adapterChar);
            gridViewSP.setAdapter(adapterSP);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonName.getText().toString().isEmpty()){
                    saveNewMacroData();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Cancelled Pressed", Toast.LENGTH_SHORT).show();
                macroButtons.clear();
                buttonName.setText("");
            }
        });

        return v;
    }

    private void setUpList() {
        listSourceNum.addAll(Arrays.asList(array_keys_num));
        for(String item : array_keys_char){
            listSourceChar.add(item.toUpperCase());
        }
        listSourceSP.addAll(Arrays.asList(array_keys_sp));
    }
    private void saveNewMacroData(){
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        String macroFile = sharedPref.getString("macroObject", null);
            try {
                JSONObject jsonMacro;
                if (macroFile != null) jsonMacro = new JSONObject(macroFile);
                else jsonMacro = new JSONObject();

                JSONObject macroKey = new JSONObject();
                try {
                    macroKey.put("type", "macro");
                    macroKey.put("name", buttonName.getText().toString());
                    macroKey.put("size", Integer.toString(macroButtons.size()));
                    for(int i =0; i < macroButtons.size(); i++){
                        macroKey.put(Integer.toString(i), macroButtons.get(i).toString());
                    }
                    jsonMacro.put(Integer.toString(index), macroKey);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("macroObject", jsonMacro.toString());
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


    }
}
