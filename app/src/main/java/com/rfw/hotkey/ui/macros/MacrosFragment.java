package com.rfw.hotkey.ui.macros;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MacrosFragment extends Fragment {
    private static final String TAG = "MacrosFragment";
    private SharedPreferences sharedPref;

    GridView gridView;
    ImageView addMacro;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);

        View v = inflater.inflate(R.layout.fragment_macros, container, false);
        List<String> macroKeys = new ArrayList<String>();
        String macroFile = sharedPref.getString("macroKeySize", "");
        if (!macroFile.equals("")) {
            int macroCnt = Integer.parseInt(macroFile);
            try {
                for (int i = 0; i < macroCnt; i++) {

                    JSONObject jsonMacro = new JSONObject(sharedPref.getString("macroKey" + Integer.toString(i), null));

                    macroKeys.add(jsonMacro.getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        gridView = (GridView) v.findViewById(R.id.macroGridID);
        addMacro = (ImageView) v.findViewById(R.id.addMacroButtonID);

        gridView.setAdapter(new ArrayAdapter<String>(
                inflater.getContext(), android.R.layout.simple_list_item_1, macroKeys) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
                );
                tv.setLayoutParams(lp);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                tv.setLayoutParams(params);
                tv.setGravity(Gravity.CENTER);
                tv.setText(macroKeys.get(position));
                return tv;
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject packet = null;
                try {
                    packet = new JSONObject(sharedPref.getString("macroKey" + Integer.toString(position), null));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ConnectionManager.getInstance().sendPacket(packet);

            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                EditMacrosFragment editMacro = new EditMacrosFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("keyIndex", position);
                editMacro.setArguments(bundle);
                pushFragment(editMacro);
                return false;
            }
        });

        addMacro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMacrosFragment editMacro = new EditMacrosFragment();
                pushFragment(editMacro);
            }
        });

        return v;

    }

    private void pushFragment(@NonNull Fragment fragment) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.pushFragment(fragment);
    }
}