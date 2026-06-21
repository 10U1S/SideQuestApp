package com.sq.extern;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CompletionActivity extends BaseActivity {

    private ImageView ivPolaroid;
    private TextView tvInfo;
    private View rarityIndicator;
    private int questUid;
    private Uri photoUri;
    private String currentPhotoPath;
    private String questTitle;
    private int questDuration;

    private Button btnTakePhoto;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    com.bumptech.glide.Glide.with(this).load(photoUri).into(ivPolaroid);
                    showPolaroidDetails();
                    savePhotoToDatabase();
                    
                    // Button deaktivieren und ausgrauen
                    btnTakePhoto.setEnabled(false);
                    btnTakePhoto.setAlpha(0.5f);
                    btnTakePhoto.setText("Foto gespeichert");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);

        questUid = getIntent().getIntExtra("QUEST_UID", -1);
        questTitle = getIntent().getStringExtra("QUEST_TITLE");
        questDuration = getIntent().getIntExtra("QUEST_DURATION", 0);

        ivPolaroid = findViewById(R.id.iv_polaroid_photo);
        tvInfo = findViewById(R.id.tv_polaroid_info);
        rarityIndicator = findViewById(R.id.rarity_indicator);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        Button btnBackToStart = findViewById(R.id.btn_back_to_start);

        btnTakePhoto.setOnClickListener(v -> openCamera());

        btnBackToStart.setOnClickListener(v -> {
            Intent intent = new Intent(CompletionActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        addPressAnimation(btnTakePhoto, btnBackToStart);
    }

    private void showPolaroidDetails() {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        String displayTitle = (questTitle != null) ? questTitle : "Quest abgeschlossen";
        
        tvInfo.setText(displayTitle + " \n " + date);
        tvInfo.setVisibility(View.VISIBLE);
        
        // Rarity Farbe am Polaroid anzeigen
        int color;
        if (questDuration <= 60) color = 0xFF3498DB; // Blue
        else if (questDuration <= 240) color = 0xFF9B59B6; // Purple
        else color = 0xFFF1C40F; // Gold
        
        applyRarityGlow(rarityIndicator, color);
        rarityIndicator.setVisibility(View.VISIBLE);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.sq.extern.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraLauncher.launch(takePictureIntent);
            }
        } catch (IOException ex) {
            Toast.makeText(this, "Fehler beim Erstellen der Datei", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void savePhotoToDatabase() {
        if (questUid != -1 && currentPhotoPath != null) {
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
            AppDatabase.getInstance(this).questDao().completeQuest(questUid, currentPhotoPath, date);
            Toast.makeText(this, "Polaroid gespeichert!", Toast.LENGTH_SHORT).show();
        }
    }
}
