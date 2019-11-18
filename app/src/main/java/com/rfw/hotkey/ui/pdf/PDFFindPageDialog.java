package com.rfw.hotkey.ui.pdf;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.rfw.hotkey.R;

import java.lang.ref.WeakReference;
import java.util.Objects;
/**
 * @author  Shadman Wadith
 */
public class PDFFindPageDialog extends AppCompatDialogFragment {
    private TextInputEditText pageNumberEditText;
    private WeakReference<PDFFragment> pdfFragment;

    PDFFindPageDialog(PDFFragment pdfFragment) {
        this.pdfFragment = new WeakReference<>(pdfFragment);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
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
                        int platformINT = pdfFragment.get().getPlatform();
                        String platform = String.valueOf(platformINT);
                        Log.d("PDF_Dialog", number);
                        Log.d("PDF_Dialog", platform);
                        pdfFragment.get().sendMessageToServer(number, "page", platform);
                    }
                });
        pageNumberEditText = view.findViewById(R.id.pdf_pageNumberTextInputID);
        return builder.create();
    }
}
