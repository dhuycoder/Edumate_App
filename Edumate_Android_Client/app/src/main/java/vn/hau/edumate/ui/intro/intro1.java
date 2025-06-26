package vn.hau.edumate.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.hau.edumate.R;

public class intro1 extends AppCompatActivity {
    ImageButton btnNext;
    Button btnBackGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro1);
        btnNext = findViewById(R.id.btn_next);
        btnBackGuide = findViewById(R.id.btnBackGuide);
        btnNext.setOnClickListener(v -> {
            startActivity(new Intent(intro1.this, intro2.class));
            finish();
        });
        btnBackGuide.setOnClickListener(v -> {
            finish();
        });


    }
}