package com.rfw.hotkey.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

public class PDFFindPageDialog extends AppCompatDialogFragment {
    private TextInputEditText pageNumberEditText;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.pdf_find_page_dialog,null);
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
                        Log.d("PDF_Dialog",number);
                        sendMessageToServer(number,"page");
                    }
                });
        pageNumberEditText = view.findViewById(R.id.pdf_pageNumberTextInputID);
        return  builder.create();
    }

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
}
