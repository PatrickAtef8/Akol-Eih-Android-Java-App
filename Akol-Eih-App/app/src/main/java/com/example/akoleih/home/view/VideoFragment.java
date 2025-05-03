package com.example.akoleih.home.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.akoleih.R;

public class VideoFragment extends DialogFragment {
    private static final String ARG_YOUTUBE_URL = "youtube_url";
    private static final String SAVED_WEBVIEW_STATE = "webview_state";
    private static final String SAVED_YOUTUBE_URL = "saved_youtube_url";

    private WebView webView;
    private Bundle webViewState;

    public static VideoFragment newInstance(String youtubeUrl) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_YOUTUBE_URL, youtubeUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        setRetainInstance(true); // Retain instance across configuration changes

        if (savedInstanceState != null) {
            webViewState = savedInstanceState.getBundle(SAVED_WEBVIEW_STATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        webView = view.findViewById(R.id.web_view);
        ImageButton btnClose = view.findViewById(R.id.btn_close);

        // Restore arguments if they were lost
        if (savedInstanceState != null && getArguments() == null) {
            String savedUrl = savedInstanceState.getString(SAVED_YOUTUBE_URL);
            Bundle args = new Bundle();
            args.putString(ARG_YOUTUBE_URL, savedUrl);
            setArguments(args);
        }

        setupWebView();
        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        // Restore WebView state if available
        if (webViewState != null) {
            webView.restoreState(webViewState);
        } else {
            loadYoutubeVideo();
        }
    }

    private void loadYoutubeVideo() {
        String youtubeUrl = getArguments() != null ?
                getArguments().getString(ARG_YOUTUBE_URL) : null;

        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            String videoId = youtubeUrl.contains("=") ?
                    youtubeUrl.substring(youtubeUrl.indexOf("=") + 1) : youtubeUrl;
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            webView.loadUrl(embedUrl);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save WebView state
        Bundle webViewBundle = new Bundle();
        webView.saveState(webViewBundle);
        outState.putBundle(SAVED_WEBVIEW_STATE, webViewBundle);

        // Save YouTube URL separately
        if (getArguments() != null) {
            outState.putString(SAVED_YOUTUBE_URL,
                    getArguments().getString(ARG_YOUTUBE_URL));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();

        // Save current WebView state
        webViewState = new Bundle();
        webView.saveState(webViewState);
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroyView();
    }
}