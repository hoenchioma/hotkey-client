package com.rfw.hotkey.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.rfw.hotkey.R;


public class MultimediaFragment extends Fragment {
    private static final String TAG = "MultimediaFragment";

    private boolean playState = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_multimedia, container, false);

        ImageView playIcon = (ImageView) v.findViewById(R.id.playid);
        ImageView fForward = (ImageView) v.findViewById(R.id.fForwardid);
        ImageView fRewind = (ImageView) v.findViewById(R.id.fRewindid);
        ImageView volumeIcon = (ImageView) v.findViewById(R.id.volumeid);
        ImageView nextIcon = (ImageView) v.findViewById(R.id.nextid);
        ImageView prevIcon = (ImageView) v.findViewById(R.id.previd);
        SeekBar volumeControl = (SeekBar) v.findViewById(R.id.volumeControlid);

        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playState){
                    playIcon.setImageResource(R.drawable.ic_pause_24dp);
                    playState = false;
                }
                else{
                    playIcon.setImageResource(R.drawable.ic_play_arrow_24dp);
                    playState = true;
                }
            }
        });

        prevIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Previous Track",Toast.LENGTH_SHORT).show();
            }
        });

        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Next Track",Toast.LENGTH_SHORT).show();
            }
        });

        fForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Fast Forward Track",Toast.LENGTH_SHORT).show();
            }
        });

        fRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Fasr Rewind Track",Toast.LENGTH_SHORT).show();
            }
        });

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress){
                    case 100:
                        volumeIcon.setImageResource(R.drawable.ic_volume_up_24dp);
                        break;
                    case 0:
                        volumeIcon.setImageResource(R.drawable.ic_volume_mute_24dp);
                        break;
                    default:
                        volumeIcon.setImageResource(R.drawable.ic_volume_down_24dp);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return v;
    }
}
