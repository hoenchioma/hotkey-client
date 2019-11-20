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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.rfw.hotkey.R;
import com.rfw.hotkey.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Fragment which gives the user a layout to set a
 * combination of  keyboard keys for creating a macro key
 *
 * @author Farhan Kabir
 */

public class EditMacrosFragment extends Fragment {
    private static final String TAG = "EditMacrosFragment";
    private int keyIndex;
    private boolean added;
    private SharedPreferences sharedPref;

    private ArrayList<String> selectedKeys;
    private GridView gridView;
    private Button saveMacroButton;
    private Button cancelMacroButton;
    private EditText macroNameText;

    /**
     * Keyboard layout for any keyboard keys
     * to simulate any key of keyboard.
     * Add new keys and map them in server accordingly.
     */

    static final String[] keyboardKeys = new String[]{
            "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9",

            "ESC", "ALT", "CTRL", "SHIFT", "DEL",
            "INS", "HOME", "END", "PGUP", "PGDN",

            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y",
            "Z"

    };

    private final List<String> keyboardLayout = new ArrayList<String>(Arrays.asList(keyboardKeys));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_macros, container, false);
        sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        selectedKeys = new ArrayList<String>();
        gridView = (GridView) v.findViewById(R.id.macroKeyboardID);
        saveMacroButton = (Button) v.findViewById(R.id.saveMacroButtonID);
        cancelMacroButton = (Button) v.findViewById(R.id.cancelMacroButtonID);
        macroNameText = (EditText) v.findViewById(R.id.macroNameTextID);
        Bundle bundle = getArguments();
        keyIndex = -1;
        if (bundle != null) {
            added = false;
            keyIndex = bundle.getInt("keyIndex");
            try {
                JSONObject jsonObject = new JSONObject(sharedPref.getString("macroKey" + Integer.toString(keyIndex), null));
                int keySize = jsonObject.getInt("size");
                for (int i = 0; i < keySize; i++)
                    selectedKeys.add(jsonObject.getString(Integer.toString(i)));
                macroNameText.setText(jsonObject.getString("name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            keyIndex = Integer.parseInt(sharedPref.getString("macroKeySize", "0"));
            macroNameText.setText("");
            added = true;
        }

        gridView.setAdapter(new ArrayAdapter<String>(
                inflater.getContext(), android.R.layout.simple_list_item_1, keyboardLayout) {
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

                tv.setText(keyboardLayout.get(position));

                if (!selectedKeys.contains(keyboardLayout.get(position)))
                    tv.setBackgroundResource(R.color.colorPrimary);
                else tv.setBackgroundResource(R.color.colorAccent);

                return tv;
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String buttonName = parent.getItemAtPosition(position).toString();
                if (!selectedKeys.contains(buttonName)) {
                    gridView.getChildAt(position).setBackgroundResource(R.color.colorAccent);
                    selectedKeys.add(buttonName);
                } else {
                    gridView.getChildAt(position).setBackgroundResource(R.color.colorPrimary);
                    selectedKeys.remove(buttonName);
                }
            }
        });

        saveMacroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!macroNameText.getText().toString().equals("")) {
                    JSONObject macroKey = new JSONObject();
                    try {
                        macroKey.put("type", "macro");
                        macroKey.put("name", macroNameText.getText().toString());
                        macroKey.put("size", Integer.toString(selectedKeys.size()));
                        for (int i = 0; i < selectedKeys.size(); i++) {
                            macroKey.put(Integer.toString(i), selectedKeys.get(i));
                        }

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("macroKey" + Integer.toString(keyIndex), macroKey.toString());
                        if (added) editor.putString("macroKeySize", Integer.toString(keyIndex + 1));
                        editor.apply();
                        Toast.makeText(getContext(), "Macro Saved", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Macro Name Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelMacroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        return v;

    }

}
