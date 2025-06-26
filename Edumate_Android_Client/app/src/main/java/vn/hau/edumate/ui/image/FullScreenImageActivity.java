package vn.hau.edumate.ui.image;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import vn.hau.edumate.R;
import vn.hau.edumate.ui.adapter.FullScreenImageAdapter;

public class FullScreenImageActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private List<String> imageUrls;
    private TextView imagePositonOverlay;
    private int startPosition;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full_screen_image);
        viewPager = findViewById(R.id.viewPager_fullScreen_image);
        imagePositonOverlay = findViewById(R.id.image_position_overlay);
        imageUrls = getIntent().getStringArrayListExtra("imageUrls");
        startPosition = getIntent().getIntExtra("position", 0);
        imagePositonOverlay.setText(startPosition+1+"/"+imageUrls.size());
        // them slide chuyen so nua
        FullScreenImageAdapter adapter = new FullScreenImageAdapter(imageUrls, this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition, false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                imagePositonOverlay.setText(position+1+"/"+imageUrls.size());
            }
        });
    }
}