
package wd.android.example.apis.fragment;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import wd.android.app.ui.activity.MyBaseActivity;
import wd.android.framework.ui.fragment.WebViewFragment;
import wd.android.util.util.MyLog;
import wd.android.wdcommondapi.R;

public class FragmentWebView extends MyBaseActivity {

    private WebViewFragment newFragment;

    @Override
    public void onBeforeContentView(Bundle savedInstanceState) {
        //super.onBeforeContentView(savedInstanceState);
    }

    @Override
    public void initView(View rootView, Bundle savedInstanceState) {
        newFragment = new WebViewFragment();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.add(R.id.simple_fragment, newFragment);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.addToBackStack(null);
//        ft.commit();
        mFragmentHelper.replace(null, R.id.simple_fragment, newFragment);

        delayHandler.sendEmptyMessageAtTime(0, 1000);
    }

    private void setUpWebView(String mUrl) {
        WebView webView = newFragment.getWebView();
        if (webView == null) {
            return;
        }
        // ����ʹ�û��棺
        // webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // ��ʹ�û���
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webView.getSettings().setLoadWithOverviewMode(false);

        webView.setWebViewClient(new TencentAuthWebViewClient());
        webView.setInitialScale(10);// Ϊ25%����С���ŵȼ�
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl(mUrl);
        webView.requestFocus();
    }

    private class TencentAuthWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            MyLog.d("===onPageStarted=====url=======" + url);
            super.onPageStarted(view, url, favicon);
            showLoadingHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            MyLog.d("===onPageFinished=====url=======" + url);
            super.onPageFinished(view, url);
            showLoadingHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            MyLog.d("===shouldOverrideUrlLoading=====url=======" + url);
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            showLoadingHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS);
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
            showLoadingHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS);
        }
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        //delayHandler.sendEmptyMessageAtTime(0, 1000);
    }

    private Handler delayHandler = new Handler() {
        public void handleMessage(Message msg) {
            setUpWebView("www.baidu.com");
        }
    };

    @Override
    public int getRootViewId() {
        return R.layout.fragment_webview;
    }


}
