
package com.rfw.hotkey.ui.pdf;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.rfw.hotkey.R;


public class PDFMoreDialog extends AppCompatDialogFragment implements View.OnClickListener {
    private ImageButton acrobatReaderButton;
    private ImageButton evinceButton;
    private PDFFragment pdfFragment;

    PDFMoreDialog(PDFFragment pdfFragment) {
        this.pdfFragment = pdfFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getContext(), R.layout.dialog_pdf_find_page, null);
        initialization(view);

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

                    }
                });

        return  builder.create();
    }

    private void initialization(View rootView){
        acrobatReaderButton = rootView.findViewById(R.id.adobeAcrobatReaderID);
        evinceButton = rootView.findViewById(R.id.evinceID);
        acrobatReaderButton.setOnClickListener(this);
        evinceButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.adobeAcrobatReaderID:
                pdfFragment.setPlatform(1);
                Log.d("pdf_more",String.valueOf(pdfFragment.getPlatform()));
                Log.d("pdf_more","adobe acrobat reader");
                break;
            case R.id.evinceID:
                pdfFragment.setPlatform(2);
                Log.d("pdf_more",String.valueOf(pdfFragment.getPlatform()));
                break;
        }
    }
}

