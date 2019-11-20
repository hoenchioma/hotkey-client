package com.rfw.hotkey.ui.pdf;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;
import com.rfw.hotkey.util.misc.LoopedExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.rfw.hotkey.util.Utils.getIntPref;

/**
 * Fragment that helps user to control PDF like Adobe Acrobat Reader and Evince Reader
 * feature : page change , zoom in and out, go to desired page
 *
 * @author Shadman Wadith
 */
public class PDFFragment extends Fragment implements View.OnClickListener {
    private static final String KEY_PDF_READER_PLATFORM = "pdfPlatform";

    private static final long BUTTON_PRESS_DELAY = 100;
    private static final int PLATFORM_ADOBE = 1;
    private static final int PLATFORM_EVINCE = 2;

    private LinearLayout pdfButtonLayout;
    private RelativeLayout pdfPlatformLayout;
    private ImageButton fullScreenButton;
    private ImageButton pdfMoreButton;
    private ImageButton upButton;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton leftButton;
    private ImageButton downButton;
    private ImageButton rightButton;
    private ImageButton findPageButton;
    private ImageButton acrobatReaderButton;
    private ImageButton evinceButton;
    private int platform;
    private Button fitHeightButton, fitWidthButton;
    private boolean isFullScreen;
    private LoopedExecutor buttonPresser = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pdf, container, false);
        initialization(rootView);

        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN ) {

                    Log.d("LoopExecutor", "it works");
                    if (buttonPresser == null) {
                        buttonPresser = new LoopedExecutor(BUTTON_PRESS_DELAY) {
                            @Override
                            public void task() {
                                Log.d("LoopExecutor", "it works");
                                sendMessageToServer("UP", "modifier", String.valueOf(getPlatform()));
                            }
                        };
                        buttonPresser.start();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (buttonPresser != null) {
                        buttonPresser.end();
                        buttonPresser = null;
                    }
                }
                return false;
            }
        });
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    Log.d("LoopExecutor", "it works");
                    if (buttonPresser == null) {
                        buttonPresser = new LoopedExecutor(BUTTON_PRESS_DELAY) {
                            @Override
                            public void task() {
                                Log.d("LoopExecutor", "it works");
                                sendMessageToServer("DOWN", "modifier", String.valueOf(getPlatform()));
                            }
                        };
                        buttonPresser.start();
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (buttonPresser != null) {
                        buttonPresser.end();
                        buttonPresser = null;
                    }
                }

                return false;
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initialization(View rootView) {
        isFullScreen = false;
        pdfButtonLayout = rootView.findViewById(R.id.pdfButtonsLayoutID);
        pdfPlatformLayout = rootView.findViewById(R.id.pdfPlatformLayoutID);
        pdfMoreButton = rootView.findViewById(R.id.pdfMoreButtonID);
        findPageButton = rootView.findViewById(R.id.pdf_findPageButtonID);
        fitWidthButton = rootView.findViewById(R.id.pdf_fitWidthButtonID);
        fitHeightButton = rootView.findViewById(R.id.pdf_fitHeightButtonID);
        fullScreenButton = rootView.findViewById(R.id.pdf_fullScreenButtonID);
        upButton = rootView.findViewById(R.id.pdf_upButtonID);
        zoomInButton = rootView.findViewById(R.id.pdf_zoomInButtonID);
        zoomOutButton = rootView.findViewById(R.id.pdf_zoomOutButtonID);
        leftButton = rootView.findViewById(R.id.pdf_leftButtonID);
        downButton = rootView.findViewById(R.id.pdf_downButtonID);
        rightButton = rootView.findViewById(R.id.pdf_rightButtonID);
        acrobatReaderButton = rootView.findViewById(R.id.adobeAcrobatReaderID);
        evinceButton = rootView.findViewById(R.id.evinceID);
        acrobatReaderButton.setOnClickListener(this);
        evinceButton.setOnClickListener(this);
        pdfMoreButton.setOnClickListener(this);
        findPageButton.setOnClickListener(this);
        fitHeightButton.setOnClickListener(this);
        fitWidthButton.setOnClickListener(this);
        fullScreenButton.setOnClickListener(this);
        //upButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        //downButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);

        //downButton.setOnTouchListener(this);
        pdfPlatformLayout.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.pdfMoreButtonID:
                pdfPlatformLayout.setVisibility(View.VISIBLE);
                pdfButtonLayout.setVisibility(View.INVISIBLE);
                findPageButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.pdf_findPageButtonID:
                //TODO Make a dialog
                openDialog();
                break;
            case R.id.pdf_fullScreenButtonID:
                if (!isFullScreen) {
                    Toast.makeText(getActivity(), "Full Screen Mode", Toast.LENGTH_SHORT).show();
                    sendMessageToServer("fullscreen", "modifier", String.valueOf(getPlatform()));
                    fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);
                    isFullScreen = true;
                } else {
                    sendMessageToServer("ESC", "modifier", String.valueOf(getPlatform()));
                    Log.d("onclick", "ESC");
                    fullScreenButton.setImageResource(R.drawable.ic_fullscreen_white_24dp);
                    Toast.makeText(getActivity(), "Normal Mode", Toast.LENGTH_SHORT).show();
                    isFullScreen = false;
                }
                break;
            case R.id.pdf_fitHeightButtonID:
                sendMessageToServer("fit_h", "modifier", String.valueOf(getPlatform()));
                Log.d("onclick", "FIT HEIGHT");
                Log.d("pdf m", String.valueOf(getPlatform()));
                break;
            case R.id.pdf_fitWidthButtonID:
                sendMessageToServer("fit_w", "modifier", String.valueOf(getPlatform()));
                Log.d("onclick", "FIT WIDTH");
                break;
            case R.id.pdf_zoomInButtonID:
                sendMessageToServer("zoom_in", "modifier", String.valueOf(getPlatform()));
                Log.d("onclick", "ZOOM IN");
                break;
            case R.id.pdf_zoomOutButtonID:
                sendMessageToServer("zoom_out", "modifier", String.valueOf(getPlatform()));
                Log.d("onclick", "ZOOM OUT");
                break;
            case R.id.pdf_upButtonID:
                // sendMessageToServer("modifier");
                sendMessageToServer("UP", "modifier", String.valueOf(getPlatform()));
                Log.d("onclick", "UP");
                break;
            case R.id.pdf_leftButtonID:
                //sendMessageToServer("modifier");
                sendMessageToServer("LEFT", "modifier", String.valueOf(getPlatform()));
                Log.d("onclick", "LEFT");
                break;
            case R.id.pdf_rightButtonID:
                //sendMessageToServer("modifier");
                sendMessageToServer("RIGHT", "modifier", String.valueOf(getPlatform()));
                Log.d("onclick", "RIGHT");
                break;
            case R.id.adobeAcrobatReaderID:
                platform = PLATFORM_ADOBE;
                setPlatform(platform);
                Log.d("PDF More", String.valueOf(getPlatform()));
                pdfPlatformLayout.setVisibility(View.INVISIBLE);
                pdfButtonLayout.setVisibility(View.VISIBLE);
                findPageButton.setVisibility(View.VISIBLE);
                Log.d("pdf_more", "adobe acrobat reader");
                break;
            case R.id.evinceID:
                platform = PLATFORM_EVINCE;
                setPlatform(platform);
                pdfPlatformLayout.setVisibility(View.INVISIBLE);
                pdfButtonLayout.setVisibility(View.VISIBLE);
                findPageButton.setVisibility(View.VISIBLE);
                Log.d("PDF More", String.valueOf(getPlatform()));
                break;

        }
        Log.d("PDF More", String.valueOf(getPlatform()));
    }

    private void openDialog() {
        PDFFindPageDialog pdfFindPageDialog = new PDFFindPageDialog(this);
        assert getFragmentManager() != null;
        pdfFindPageDialog.show(getFragmentManager(), "Goto Page Dialog");
    }

    int getPlatform() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        return getIntPref(sharedPref, getString(R.string.settings_key_pdf_platform), PLATFORM_ADOBE);
    }

    void setPlatform(int platform) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        sharedPref.edit()
                .putString(getString(R.string.settings_key_pdf_platform), String.valueOf(platform)) // has to be saved as string
                .apply();
    }

    /**
     * sends the message of specified action to Connection Manager
     *
     * @param message message (key press type)
     * @param action  type of the message
     */
    void sendMessageToServer(String message, String action, String platform) {
        JSONObject packet = new JSONObject();

        try {
            packet.put("type", "pdf");
            packet.put("action", action);
            packet.put("platform", platform);
            packet.put("key", message);

            ConnectionManager.getInstance().sendPacket(packet);

        } catch (JSONException e) {
            Log.e("PDFFragment", "sendMessageToServer: error sending key-press", e);
        }
    }
}
