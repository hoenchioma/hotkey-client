package com.rfw.hotkey.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;


public class PDFFragment extends Fragment implements View.OnClickListener {

    private ImageButton fullScreenButton, upButton,zoomInButton,zoomOutButton,  leftButton, downButton, righButton,findPageButton;
    private Button fitHeightButton,fitWidthButton;
    private Boolean isFullScreen;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pdf, container, false);
        initialization(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }
    private void initialization(View rootView) {
        isFullScreen = false;
        findPageButton = rootView.findViewById(R.id.pdf_findPageButtonID);
        fitWidthButton = rootView.findViewById(R.id.pdf_fitWidthButtonID);
        fitHeightButton = rootView.findViewById(R.id.pdf_fitHeightButtonID);
        fullScreenButton = rootView.findViewById(R.id.pdf_fullScreenButtonID);
        upButton = rootView.findViewById(R.id.pdf_upButtonID);
        zoomInButton = rootView.findViewById(R.id.pdf_zoomInButtonID);
        zoomOutButton = rootView.findViewById(R.id.pdf_zoomOutButtonID);
        leftButton = rootView.findViewById(R.id.pdf_leftButtonID);
        downButton = rootView.findViewById(R.id.pdf_downButtonID);
        righButton = rootView.findViewById(R.id.pdf_rightButtonID);
        findPageButton.setOnClickListener(this);
        fitHeightButton.setOnClickListener(this);
        fitWidthButton.setOnClickListener(this);
        fullScreenButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        righButton.setOnClickListener(this);
        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.pdf_findPageButtonID:
                //TODO Make a dialog
                openDialog();
                break;
            case R.id.pdf_fullScreenButtonID:

                if (!isFullScreen) {
                    //sendMessageToServer("F5", "modifier");
                    //Log.d("onclick", "F5");
                    //fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);
                    Toast.makeText(getActivity(), "Full Screen Mode", Toast.LENGTH_SHORT).show();
                    sendMessageToServer("fullscreen","modifier");
                    fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);
                    isFullScreen = true;
                } else {
                    sendMessageToServer("ESC", "modifier");
                    Log.d("onclick", "ESC");
                    fullScreenButton.setImageResource(R.drawable.ic_fullscreen_white_24dp);
                    Toast.makeText(getActivity(), "Normal Mode", Toast.LENGTH_SHORT).show();
                    isFullScreen = false;
                }

                break;
            case R.id.pdf_fitHeightButtonID:
                sendMessageToServer("fit_h", "modifier");
                Log.d("onclick", "FIT HEIGHT");
                break;
            case R.id.pdf_fitWidthButtonID:
                sendMessageToServer("fit_w", "modifier");
                Log.d("onclick", "FIT WIDTH");
                break;
            case R.id.pdf_zoomInButtonID:
                sendMessageToServer("zoom_in", "modifier");
                Log.d("onclick", "ZOOM IN");
                break;
            case R.id.pdf_zoomOutButtonID:
                sendMessageToServer("zoom_out", "modifier");
                Log.d("onclick", "ZOOM OUT");
                break;
            case R.id.pdf_upButtonID:
                // sendMessageToServer("modifier");
                sendMessageToServer("UP", "modifier");
                Log.d("onclick", "UP");
                break;
            case R.id.pdf_leftButtonID:
                //sendMessageToServer("modifier");
                sendMessageToServer("LEFT", "modifier");
                Log.d("onclick", "LEFT");
                break;
            case R.id.pdf_downButtonID:
                // sendMessageToServer("modifier");
                sendMessageToServer("DOWN", "modifier");
                Log.d("onclick", "DOWN");
                break;
            case R.id.pdf_rightButtonID:
                //sendMessageToServer("modifier");
                sendMessageToServer("RIGHT", "modifier");
                Log.d("onclick", "RIGHT");
                break;
        }
    }

    /**
     * sends the message of specified action to Connection Manager
     *
     * @param message message (key press type)
     * @param action  type of the message
     */
    private void sendMessageToServer(String message, String action) {
        JSONObject packet = new JSONObject();

        try {
            packet.put("type", "pdf");
            packet.put("action", action);
            packet.put("key", message);

            ConnectionManager.getInstance().sendPacket(packet);

        } catch (JSONException e) {
            Log.e("PDFFragment", "sendMessageToServer: error sending key-press", e);
        }
    }
    public void openDialog(){
            PDFFindPageDialog pdfFindPageDialog = new PDFFindPageDialog();
            pdfFindPageDialog.show(getFragmentManager(),"Goto Page Dialog");
    }
}
