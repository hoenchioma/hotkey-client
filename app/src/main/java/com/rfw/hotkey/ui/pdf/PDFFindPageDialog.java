package com.rfw.hotkey.ui.pdf;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.rfw.hotkey.R;
import com.rfw.hotkey.net.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class PDFFindPageDialog extends AppCompatDialogFragment {
    private TextInputEditText pageNumberEditText;
    private static final String KEY_PDF_READER_PLATFORM = "pdfPlatform";
    PDFFragment pdfFragment = new PDFFragment();
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getContext(), R.layout.dialog_pdf_find_page, null);
        builder.setView(view)
                .setTitle("PDF")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String number = pageNumberEditText.getText().toString();
                        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
                        int platformINT = sharedPref.getInt(KEY_PDF_READER_PLATFORM, 1);
                        String platform = String.valueOf(platformINT);
                        Log.d("PDF_Dialog", number);
                        Log.d("PDF_Dialog", platform);
                        sendMessageToServer(number, "page",platform);
                    }
                });
        pageNumberEditText = view.findViewById(R.id.pdf_pageNumberTextInputID);
        return builder.create();
    }

    private void sendMessageToServer(String message, String action, String platform) {
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
