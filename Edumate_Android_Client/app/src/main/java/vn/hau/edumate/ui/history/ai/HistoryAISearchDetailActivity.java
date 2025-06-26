package vn.hau.edumate.ui.history.ai;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.databinding.ActivityHistoryAiDetailBinding;
import vn.hau.edumate.ui.common.BaseActivity;

public class HistoryAISearchDetailActivity extends BaseActivity {

    private static final String TAG = "HistoryAISearchDetailActivity";

    private ActivityHistoryAiDetailBinding binding;

    private HistoryAISearchDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHistoryAiDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(HistoryAISearchDetailViewModel.class);

        setupWebView();
        observeHistory();

        binding.btnBack.setOnClickListener(v -> {
            finish();
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

    private void observeHistory(){
        viewModel.setHistory(getIntent());
        viewModel.getHistoryLiveData().observe(this, history -> {

            if (history.getImageUrl() != null && !history.getImageUrl().isEmpty()) {
                Log.e(TAG, "Image url: " + history.getImageUrl());
                Glide.with(this)
                        .load(SystemConstant.BASE_URL + history.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_img_error)
                        .fitCenter()
                        .into(binding.img);
            }

            String answer = history.getAnswer() == null ? "Hiện tại chưa có lời giải" : history.getAnswer();
            String htmlContent = createResponsiveHtmlContent(answer);
            binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
            binding.webView.setVisibility(View.VISIBLE);
            binding.loadingContainer.setVisibility(View.GONE);
        });
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

}
