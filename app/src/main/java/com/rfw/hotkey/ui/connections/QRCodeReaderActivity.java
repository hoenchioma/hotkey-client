package com.rfw.hotkey.ui.connections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.vision.barcode.Barcode;
import com.notbytes.barcode_reader.BarcodeReaderFragment;
import com.rfw.hotkey.R;

import java.io.Serializable;
import java.util.List;

public class QRCodeReaderActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {
    public static final String KEY_CAPTURED_BARCODE = "key_captured_barcode";
    public static final String KEY_CAPTURED_RAW_BARCODE = "key_captured_raw_barcode";
    public static final String KEY_ERROR = "key_error";
    private static final String KEY_AUTO_FOCUS = "key_auto_focus";
    private static final String KEY_USE_FLASH = "key_use_flash";
    private static final String KEY_BARCODE_CHECKER = "key_checker";

    private boolean autoFocus = false;
    private boolean useFlash = false;
    private BarcodeChecker barcodeChecker = null;

    private BarcodeReaderFragment mBarcodeReaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        Intent intent = this.getIntent();
        if (intent != null) {
            this.autoFocus = intent.getBooleanExtra(KEY_AUTO_FOCUS, this.autoFocus);
            this.useFlash = intent.getBooleanExtra(KEY_USE_FLASH, this.useFlash);
            this.barcodeChecker = (BarcodeChecker) intent.getSerializableExtra(KEY_BARCODE_CHECKER);
        }
        mBarcodeReaderFragment = attachBarcodeReaderFragment();
    }

    private BarcodeReaderFragment attachBarcodeReaderFragment() {
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        BarcodeReaderFragment fragment = BarcodeReaderFragment.newInstance(this.autoFocus, this.useFlash);
        fragment.setListener(this);
        fragmentTransaction.replace(R.id.fm_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        return fragment;
    }

    public static Intent getLaunchIntent(Context context, boolean autoFocus, boolean useFlash,
                                       @Nullable BarcodeChecker checker) {
        Intent intent = new Intent(context, QRCodeReaderActivity.class);
        intent.putExtra(KEY_USE_FLASH, useFlash);
        intent.putExtra(KEY_AUTO_FOCUS, autoFocus);
        intent.putExtra(KEY_BARCODE_CHECKER, checker);
        return intent;
    }

    private boolean checkBarcode(Barcode barcode) {
        if (barcodeChecker != null) return barcodeChecker.apply(barcode);
        else return true;
    }

    private void exitWithResult(Barcode barcode) {
        Intent intent = new Intent();
        intent.putExtra(KEY_CAPTURED_BARCODE, barcode);
        intent.putExtra(KEY_CAPTURED_RAW_BARCODE, barcode.rawValue);
        this.setResult(Activity.RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onScanned(Barcode barcode) {
        if (this.mBarcodeReaderFragment != null) {
            this.mBarcodeReaderFragment.pauseScanning();
        }
        if (barcode != null && checkBarcode(barcode)) {
            exitWithResult(barcode);
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {
        if (this.mBarcodeReaderFragment != null) {
            this.mBarcodeReaderFragment.pauseScanning();
        }
        for (Barcode barcode : list) {
            if (barcode != null && checkBarcode(barcode)) {
                exitWithResult(barcode);
                break;
            }
        }
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPermissionDenied() {
        Intent intent = new Intent();
        intent.putExtra(KEY_ERROR, "Camera permission denied");
        this.setResult(Activity.RESULT_CANCELED, intent);
        this.finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public interface BarcodeChecker extends Function<Barcode, Boolean>, Serializable {}
}
