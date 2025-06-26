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

public class intro2 extends AppCompatActivity {
    ImageButton btnNext;
    Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro2);
        btnNext = findViewById(R.id.btnNext2);
        btnSkip = findViewById(R.id.btnSkip2);
        btnNext.setOnClickListener(v -> {
            startActivity(new Intent(intro2.this, intro3.class));
            finish();
        });
        btnSkip.setOnClickListener(v -> {
            finish();
        });

    }
}