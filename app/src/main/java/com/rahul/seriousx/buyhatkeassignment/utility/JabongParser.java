package com.rahul.seriousx.buyhatkeassignment.utility;

/**
 * Created by root on 5/25/17.
 */

public class JabongParser {

    final String CART_PAGE = "jabong.com/cart/";
    final String ENTER_COUPON_PAGE = "jabong.com/cart/coupon";
    final String HAVE_COUPON_BUTTON_CLASS = "have-a-coupon";
    final String APPLY_COUPON_BUTTON_ID = "jbApplyCoupon";

    boolean isCartPageOpen;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setListeners();
//        super.loadUrl("https://m.jabong.com");
//    }
//
    private void setListeners() {
//        webViewClient = new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                return super.shouldOverrideUrlLoading(view, request);
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                Log.d("started", url);
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                Log.d("finished", url);
//                if (url.contains("jabong.com/cart/")) {
//                    isCartPageOpen = true;
//                }
//                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//                super.onPageFinished(view, url);
//            }
//
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                Log.d("intercept", url);
//                return super.shouldInterceptRequest(view, url);
//            }
//
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                return super.shouldInterceptRequest(view, request);
//            }
//        };
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.setWebViewClient(webViewClient);
    }

    public void setCoupon(String coupon) {
        String jsFunction = "document.getElementById('applyCoupon').value = " + coupon;
    }

    private void haveCoupon() {
        String jsFunction = "document.querySelector('."+HAVE_COUPON_BUTTON_CLASS+"').click()";
    }

    private void applyCoupon() {
        String jsFunction = "document.getElementById('"+APPLY_COUPON_BUTTON_ID+"').click()";
    }

    private void validateCoupon(String coupon) {

    }

    private void removeCoupon() {

    }
}
