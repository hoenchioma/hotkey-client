package com.rfw.hotkey.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.rfw.hotkey.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class EditMacrosFragment extends Fragment {
    private static final String TAG = "EditMacrosFragment";
    private  int index;
    private Button saveButton;
    private Button cancelButton;
    private TextView buttonName;


    private String[] array_keys = {
            "0","1", "2", "3", "4", "5","6", "7","8", "9", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    private List<String> listSource = new ArrayList<>();
    private List<String> macroButtons = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_macros, container, false);
        saveButton = (Button)v.findViewById(R.id.saveMacroButtonid);
        cancelButton = (Button)v.findViewById(R.id.calcelButtonid);
        buttonName = (TextView)v.findViewById(R.id.macroNameFieldeid);

        Bundle bundle = getArguments();
        if(bundle != null){
            index = bundle.getInt("index");
            setUpList();
            GridView gridView =(GridView) v.findViewById(R.id.macroGridid);
            MacroGridViewAdapter adapter = new MacroGridViewAdapter(listSource,inflater.getContext(), macroButtons);
            gridView.setAdapter(adapter);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonName.getText().toString().isEmpty()){
                    /*JSONObject macroKey = new JSONObject();
                    try {
                        macroKey.put("name", buttonName.getText().toString());
                        macroKey.put("keys", macroButtons);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);

                    String macroFile = sharedPref.getString("macroObject", null);
                    if (macroFile != null) {
                        try {
                            JSONObject jsonMacro = new JSONObject(macroFile);
                            jsonMacro.getJSONArray("macroArray");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("ipAddress", ipAddress);
                    editor.apply();*/
                }
            }
        });

        return v;
    }

    private void setUpList() {
        for(String item : array_keys){
            listSource.add(item);
        }
    }
    private void saveNewMacroData(){
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        String macroFile = sharedPref.getString("macroObject", null);
        JSONArray jsonArray;
        if (macroFile != null) {
            try {
                JSONObject jsonMacro = new JSONObject(macroFile);

                jsonArray = jsonMacro.getJSONArray("macroArray");

                JSONObject macroKey = new JSONObject();
                try {
                    macroKey.put("name", buttonName.getText().toString());
                    JSONArray newMacroKeys = new JSONArray();
                    for(int i =0; i < macroButtons.size(); i++){
                        newMacroKeys.put(macroButtons.get(i).toString());
                    }
                    macroKey.put("keys", newMacroKeys);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}
