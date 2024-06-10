package com.github.captainayan.accountlite;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.NotificationHelper;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.layout.font.FontProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class StatementDownloadActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {
    private static final String TAG = "STATEMENT_DOWNLOAD_ACT";
    private MaterialToolbar toolbar;
    private WebView webView;
    private Button downloadButton;
    private ChipGroup chipGroup;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 123;
    private String[] perms = {WRITE_EXTERNAL_STORAGE};

    private String desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, " +
            "like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    private String html, csv, fileName;

    private enum FileType {
        PDF, CSV
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement_download);

        NotificationHelper.createNotificationChannel(this);

        html = getIntent().getStringExtra("html");
        csv = getIntent().getStringExtra("csv");
        fileName = getIntent().getStringExtra("filename");

        chipGroup = (ChipGroup) findViewById(R.id.chipGroup);

        if (html != null) { // for PDF
            Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.selection_chip, chipGroup, false);
            chip.setText("PDF");
            chip.setId(FileType.PDF.ordinal());
            chip.setChecked(true);
            chipGroup.addView(chip);
        }
        if (csv != null) { // for CSV
            Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.selection_chip, chipGroup, false);
            chip.setText("CSV");
            chip.setId(FileType.CSV.ordinal());
            chipGroup.addView(chip);
        }

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setUserAgentString(desktopUserAgent);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.setWebViewClient(new WebViewClient());
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        webView.setInitialScale(75);

        downloadButton = (MaterialButton) findViewById(R.id.download);
        downloadButton.setOnClickListener(this);

        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatementDownloadActivity.this.finish();
            }
        });
    }

    private void downloadPDF() {
        // The following code works but has margin clipping issue
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!documentsDir.exists()) documentsDir.mkdirs();
        File file = new File(documentsDir, fileName+".pdf");
        try {
            ConverterProperties prop = new ConverterProperties();
            FontProvider fp = new FontProvider();
            fp.addFont("res/font/font.ttf", PdfEncodings.IDENTITY_H);
            fp.addFont("res/font/fontbold.ttf", PdfEncodings.IDENTITY_H);
            prop.setFontProvider(fp);

            HtmlConverter.convertToPdf(html, Files.newOutputStream(file.toPath()), prop);

            showFileSavedNotification();
        } catch (IOException e) {
            Toast.makeText(this, R.string.error_message_pdf_creation_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadCSV() {
        FileOutputStream fos = null;
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!documentsDir.exists()) documentsDir.mkdirs();
        File file = new File(documentsDir, fileName+".csv");
        try {
            fos = new FileOutputStream(file);
            fos.write(csv.getBytes());
            fos.flush();

            showFileSavedNotification();
        } catch (IOException e) {
            Toast.makeText(this, R.string.error_message_pdf_creation_failed, Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        startDownload();
    }

    private void startDownload() {
        if (chipGroup.getCheckedChipId() == FileType.PDF.ordinal()) downloadPDF();
        else if (chipGroup.getCheckedChipId() == FileType.CSV.ordinal()) downloadCSV();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, R.string.error_message_storage_permission_denied, Toast.LENGTH_SHORT).show();
    }

    private void showFileSavedNotification() {
        Random random = new Random();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "file_save_channel")
                .setSmallIcon(R.drawable.icon_splash)
                .setContentTitle(getString(R.string.file_saved_success_title))
                .setContentText(getString(R.string.file_saved_success_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(random.nextInt(), builder.build());
        }
    }

    @Override
    public void onClick(View view) {
        // if app has permission, save the file
        if (EasyPermissions.hasPermissions(this, perms)) {
            startDownload();
        }
        // otherwise request permission
        else {
            EasyPermissions.requestPermissions(this, getString(R.string.storage_permission_rationale), WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }
}