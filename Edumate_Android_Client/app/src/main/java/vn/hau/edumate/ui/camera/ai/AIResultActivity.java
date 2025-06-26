package vn.hau.edumate.ui.camera.ai;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import vn.hau.edumate.R;
import vn.hau.edumate.databinding.ActivityAiResultBinding;


public class AIResultActivity extends AppCompatActivity {

    private static final String TAG = "AIResultActivity";
    private ActivityAiResultBinding binding;
    private AIResultViewModel viewModel;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAiResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(AIResultViewModel.class);

        setupWebView();

        if (!setCroppedImage()) {
            return;
        }

        observeResponse();
        observeLoading();

        binding.btnBack.setOnClickListener(v -> {
            if(!Boolean.TRUE.equals(viewModel.getLoading().getValue())) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupWebView() {
        try {
            WebSettings webSettings = binding.webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setDisplayZoomControls(false);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            webSettings.setDomStorageEnabled(true);

            binding.webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d(TAG, "WebView page finished loading");
                    resizeWebViewToContent();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up WebView", e);
        }
    }

    private void resizeWebViewToContent() {
        try {
            String script =
                    "(function() { " +
                            "var body = document.body;" +
                            "var html = document.documentElement;" +
                            "var height = Math.max(" +
                            "body.scrollHeight, body.offsetHeight," +
                            "html.clientHeight, html.scrollHeight, html.offsetHeight" +
                            ");" +
                            "return height;" +
                            "})();";

            binding.webView.evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    try {
                        String cleanValue = value.replace("\"", "");
                        int contentHeight = Integer.parseInt(cleanValue);

                        float density = getResources().getDisplayMetrics().density;
                        int webViewHeight = (int) (contentHeight * density);

                        int minHeight = (int) (200 * density);
                        webViewHeight = Math.max(webViewHeight, minHeight);

                        int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * 50);

                        Log.d(TAG, "Content height: " + contentHeight + "px, WebView height: " + webViewHeight + "px, Max height: " + maxHeight + "px");

                        if (webViewHeight > maxHeight) {
                            webViewHeight = maxHeight;
                            enableWebViewScrolling();
                            Log.d(TAG, "Content is too long, enabling WebView scrolling");
                        } else {
                            disableWebViewScrolling();
                            Log.d(TAG, "Content fits, disabling WebView scrolling");
                        }

                        ViewGroup.LayoutParams webViewParams = binding.webView.getLayoutParams();
                        webViewParams.height = webViewHeight;
                        binding.webView.setLayoutParams(webViewParams);

                        ViewGroup.LayoutParams frameParams = binding.webFrame.getLayoutParams();
                        frameParams.height = webViewHeight;
                        binding.webFrame.setLayoutParams(frameParams);


                        binding.webFrame.requestLayout();

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing content height: " + value, e);
                        setDefaultWebViewHeight();
                    } catch (Exception e) {
                        Log.e(TAG, "Error resizing WebView", e);
                        setDefaultWebViewHeight();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in resizeWebViewToContent", e);
            setDefaultWebViewHeight();
        }
    }

    private void enableWebViewScrolling() {
        try {
            binding.webView.setVerticalScrollBarEnabled(true);
            binding.webView.setHorizontalScrollBarEnabled(false);
            String scrollScript =
                    "javascript:(function() {" +
                            "var style = document.createElement('style');" +
                            "style.innerHTML = 'body { overflow-y: auto !important; height: auto !important; }';" +
                            "document.head.appendChild(style);" +
                            "})()";
            binding.webView.loadUrl(scrollScript);
        } catch (Exception e) {
            Log.e(TAG, "Error enabling WebView scrolling", e);
        }
    }

    private void disableWebViewScrolling() {
        try {
            binding.webView.setVerticalScrollBarEnabled(false);
            binding.webView.setHorizontalScrollBarEnabled(false);
            String noScrollScript =
                    "javascript:(function() {" +
                            "var style = document.createElement('style');" +
                            "style.innerHTML = 'body { overflow: hidden !important; }';" +
                            "document.head.appendChild(style);" +
                            "})()";
            binding.webView.loadUrl(noScrollScript);
        } catch (Exception e) {
            Log.e(TAG, "Error disabling WebView scrolling", e);
        }
    }

    private void setDefaultWebViewHeight() {
        try {
            float density = getResources().getDisplayMetrics().density;
            int defaultHeight = (int) (400 * density);

            ViewGroup.LayoutParams webViewParams = binding.webView.getLayoutParams();
            webViewParams.height = defaultHeight;
            binding.webView.setLayoutParams(webViewParams);

            ViewGroup.LayoutParams frameParams = binding.webFrame.getLayoutParams();
            frameParams.height = defaultHeight;
            binding.webFrame.setLayoutParams(frameParams);

            binding.webFrame.requestLayout();

            enableWebViewScrolling();

        } catch (Exception e) {
            Log.e(TAG, "Error setting default WebView height", e);
        }
    }

    private boolean setCroppedImage() {
        try {
            imageUri = getIntent().getParcelableExtra("resultUri");
            Log.d(TAG, "Received imageUri: " + (imageUri != null ? imageUri.toString() : "null"));

            if(imageUri == null) {
                Log.e(TAG, "ImageUri is null");
                showErrorAndFinish("Không thể lấy ảnh đã cắt");
                return false;
            }

            if (!isValidUri(imageUri)) {
                Log.e(TAG, "Invalid URI: " + imageUri);
                showErrorAndFinish("URI ảnh không hợp lệ");
                return false;
            }

            try {
                binding.croppedImg.setImageURI(imageUri);
                Log.d(TAG, "Image set successfully");
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error setting image", e);
                showErrorAndFinish("Không thể hiển thị ảnh");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in setCroppedImage", e);
            showErrorAndFinish("Lỗi khi xử lý ảnh");
            return false;
        }
    }

    private boolean isValidUri(Uri uri) {
        try {
            if ("file".equals(uri.getScheme())) {
                File file = new File(Objects.requireNonNull(uri.getPath()));
                return file.exists() && file.canRead();
            }

            if ("content".equals(uri.getScheme())) {
                try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                    return inputStream != null;
                } catch (Exception e) {
                    Log.e(TAG, "Cannot open content URI", e);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error validating URI", e);
            return false;
        }
    }

    private void observeResponse(){
        try {
            viewModel.processImageAndAsk(this, imageUri);

            viewModel.getResponse().observe(this, r -> {
                try {
                    if(r != null && !r.trim().isEmpty()){
                        String htmlContent = createResponsiveHtmlContent(r);
                        binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

                        binding.webView.setVisibility(View.VISIBLE);
                        binding.loadingContainer.setVisibility(View.GONE);

                        Log.d(TAG, "Response loaded successfully");
                    } else {
                        Log.e(TAG, "Empty or null response");
                        showErrorAndFinish("Không thể lấy câu trả lời");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading response", e);
                    showErrorAndFinish("Lỗi khi hiển thị kết quả");
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in observeResponse", e);
            showErrorAndFinish("Lỗi khi xử lý yêu cầu");
        }
    }

    private String createResponsiveHtmlContent(String markdownContent) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        Node document = parser.parse(markdownContent);

        String htmlBodyContent = renderer.render(document);

        return "<!DOCTYPE html><html><head>"
                + "<meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/katex@0.16.4/dist/katex.min.css'>"
                + "<script defer src='https://cdn.jsdelivr.net/npm/katex@0.16.4/dist/katex.min.js'></script>"
                + "<script defer src='https://cdn.jsdelivr.net/npm/katex@0.16.4/dist/contrib/auto-render.min.js' "
                + "onload='renderMathInElement(document.body,{delimiters:["
                + "{left:\"$$\",right:\"$$\",display:true},{left:\"$\",right:\"$\",display:false}"
                + "]})'></script>"
                + "<style>"
                + "body{padding:16px;font-size:18px;line-height:1.6;color:#222;margin:0;}"
                + "body,html{overflow-x:hidden;word-wrap:break-word;}"
                + "/* Đảm bảo scroll hoạt động khi cần */\n"
                + "html,body{height:auto;min-height:100%;}"
                + "</style>"
                + "</head><body>"
                + htmlBodyContent
                + "</body></html>";
    }

    private void observeLoading(){
        try {
            viewModel.getLoading().observe(this, isLoading -> {
                if (binding != null) {
                    if (isLoading) {
                        binding.loadingContainer.setVisibility(View.VISIBLE);
                        binding.webView.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in observeLoading", e);
        }
    }

    private void showErrorAndFinish(String message) {
        runOnUiThread(() -> {
            try {
                new AlertDialog.Builder(this)
                        .setTitle("Lỗi")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            } catch (Exception e) {
                Log.e(TAG, "Error showing dialog", e);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!Boolean.TRUE.equals(viewModel.getLoading().getValue())) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (binding != null) {
                binding.webView.destroy();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
        }
        super.onDestroy();
    }
}