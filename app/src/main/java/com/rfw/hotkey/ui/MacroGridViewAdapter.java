package com.rfw.hotkey.ui;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;

import java.util.Iterator;
import java.util.List;

class MacroGridViewAdapter extends BaseAdapter {

    List<String> listSource;
    Context macroContext;
    List<String> macroButtons;

    public MacroGridViewAdapter(List<String> listSource, Context macroContext, List<String> macroButtons) {
        this.listSource = listSource;
        this.macroContext = macroContext;
        this.macroButtons = macroButtons;
    }

    @Override
    public int getCount() {
        return listSource.size();
    }

    @Override
    public Object getItem(int position) {
        return listSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if(convertView == null){
            button = new Button(macroContext);
            button.setLayoutParams(new GridView.LayoutParams(150, 150));
            button.setPadding(8,8,8,8);
            button.setText(listSource.get(position));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String buttonName = button.getText().toString();
                    if(!macroButtons.contains(buttonName)) {
                        button.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
                        macroButtons.add(buttonName);
                    }
                    else{
                        /*Iterator itr = macroButtons.iterator();
                        while (itr.hasNext())
                        {
                            int x = (Integer)itr.next();
                            if (buttonName.equals(x))
                                itr.remove();
                        }*/
                        macroButtons.remove(buttonName);
                        button.getBackground().clearColorFilter();
                    }

                }
            });
        }
        else
            button = (Button)convertView;
        return button;
    }
}
