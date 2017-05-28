package com.rahul.seriousx.buyhatkeassignment.ui.activities;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rahul.seriousx.buyhatkeassignment.R;
import com.rahul.seriousx.buyhatkeassignment.constants.Constants;
import com.rahul.seriousx.buyhatkeassignment.model.ContentModel;
import com.rahul.seriousx.buyhatkeassignment.ui.fragments.CouponStatusDialogFragment;
import com.rahul.seriousx.buyhatkeassignment.utility.GetBuyHatkeCoupons;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.rahul.seriousx.buyhatkeassignment.utility.Utility.isNull;

public class CustomJabongWebView extends AppCompatActivity {

    private static final String JS_RECEIVED_VALUE = "js_return_value";
    private static final int DISCOUNT_PRICE = 100;
    private static final int ORIGINAL_PRICE = 150;
    private static final int COUPON_ERROR = 200;
    private static final int CHECK_FOR_COUPON_ERROR = 250;
    private static final int REMOVE_COUPON = 300;
    private static final int EMPTY_MESSAGE = 0;
    private static final int START_PROCESS = 400;
    private static final int CART_EMPTY = 401;
    private Handler handler;
    private ProgressDialog progressDialog;
    private ProgressBar webviewProgressBar;
    boolean isCheckForCouponErrorFinished, isCouponApplied, isCartPage,
            isCouponPage, isProcessStart, isSuccess;
    ImageView tryCouponsImageVIew;
    protected WebView webView;
    protected WebViewClient webViewClient;
    private float originalPrice = 0;
    private List<ContentModel> couponList;
    private int couponCount = 0;
    private Thread checkForErrorMsgThread;
    private String currentCoupon;
    private ContentModel budgetCoupon;
    private CouponStatusInterface couponStatusInterface;

    public interface CouponStatusInterface {
        void onStatusChange(int position);
        void couponApplied(String coupon, float offAmount);
        void onFinish(ContentModel model);
    }

    public class JSInterface {
        @JavascriptInterface
        public void checkForErrorCoupon(String element) {
            if (!isNull(element)) {
                // Log.d("coupon", "presenr");
                stopCheckingForError();
                handler.sendEmptyMessage(COUPON_ERROR);
            }
        }

        @JavascriptInterface
        public void isApplyCouponExists(String element) {
            if (element == null) {
                handler.sendEmptyMessage(START_PROCESS);
            } else {
                handler.sendEmptyMessage(CART_EMPTY);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_web_view);
        bindViews();
        setupWebviewClient();
        setupWebviewSetting();
        setupHandler();
        loadUrl("https://m.jabong.com");
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupWebviewClient() {
        webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Log.d("finished", url);
                tryCouponsImageVIew.setVisibility(View.GONE);
                if (isProcessStart) {
                    if (url.contains(Constants.JabongConstants.COUPON_PAGE_IDENTIFIER)) {
                        processCouponPage();
                    } else if (url.contains(Constants.JabongConstants.CART_PAGE_IDENTIFIER)) {
                        if (!isCheckForCouponErrorFinished) {
                            // Log.d("visible", "stop checking");
                            isCouponApplied = true;
                            stopCheckingForError();
                        }
                        processCartPage();
                    }
                }
                else if (url.contains(Constants.JabongConstants.CART_PAGE_IDENTIFIER)
                        && !url.contains(Constants.JabongConstants.COUPON_PAGE_IDENTIFIER)) {
                    tryCouponsImageVIew.setVisibility(View.VISIBLE);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                if (!isProcessStart && url.contains(Constants.JabongConstants.CART_PAGE_IDENTIFIER)
                        && !url.contains(Constants.JabongConstants.COUPON_PAGE_IDENTIFIER)) {
                    tryCouponsImageVIew.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (isProcessStart) {
                    if (url.contains(Constants.JabongConstants.COUPON_APPLIED_IDENTIFIER)) {
                        startCheckingForCouponErrorResponse();
                    }//else if (url.contains(Constants.JabongConstants.REMOVE_COUPON_IDENTIFIER)) {}
                }
                return super.shouldInterceptRequest(view, url);
            }
        };
    }

    private void setupWebviewSetting() {
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.addJavascriptInterface(new JSInterface(), "JSInterface");
        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress)
            {
                if(progress < 100 && webviewProgressBar.getVisibility() == ProgressBar.GONE){
                    webviewProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
                webviewProgressBar.setProgress(progress);
                if(progress == 100) {
                    webviewProgressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        webView.setWebViewClient(webViewClient);
    }

    private void startProcess() {
        resetLocalData();
        isProcessStart = true;
        isCartPage = true;
        setOriginalPrice();
        showLoadingDialog("Fetching coupons...");
        new GetBuyHatkeCoupons(new GetBuyHatkeCoupons.BuyHatkeAPI(){
            @Override
            public void onSuccess(List<ContentModel> couponList) {
                hideLoadingDialog();
                CustomJabongWebView.this.couponList = couponList;
                showCouponFragment();
                removeCoupon(); // IF ANY COUPON APPLIED THEN REMOVE IT
            }

            @Override
            public void onFailure(Throwable t) {
                hideLoadingDialog();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }).fetchList();
    }

    private void showCouponFragment() {
        EventBus.getDefault().postSticky(couponList);
        FragmentManager fm = getSupportFragmentManager();
        CouponStatusDialogFragment fragment = new CouponStatusDialogFragment();
        fragment.setCancelable(false);
        fragment.show(fm, "Show Fragment");
        couponStatusInterface = fragment;
    }

    @Subscribe
    public void onCouponDialogDismiss(Boolean b) {
        resetLocalData();
    }

    private void resetLocalData() {
        couponCount = 0;
        originalPrice = 0;
        isCartPage = false;
        budgetCoupon = null;
        currentCoupon = null;
        isProcessStart = false;
        isCouponPage = false;
        stopCheckingForError();
        isCouponApplied = false;
        couponList = new ArrayList<>();
    }

    private void processCouponPage() {
        isCartPage = false;
        isCouponPage = true;
        tryForOtherCoupons();
    }

    private void processCartPage() {
        isCartPage = true;
        isCouponPage = false;
        if (isCouponApplied) {
            setDiscountedPrice();
        } else {
            tryForOtherCoupons();
        }
    }

    private void setupHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (!isProcessStart)
                    return true;
                switch (msg.what) {
                    case CART_EMPTY:
                        Toast.makeText(getApplicationContext(), "Please add atleast one product to cart", Toast.LENGTH_SHORT).show();
                        isProcessStart = false;
                        break;

                    case START_PROCESS:
                        startProcess();
                        break;

                    case ORIGINAL_PRICE:
                        originalPrice = Float.valueOf(msg.getData().getString(JS_RECEIVED_VALUE).replace("\"", ""));
                        break;

                    case DISCOUNT_PRICE:
                        isSuccess = true;
                        addCouponStateToMap(true, Float.valueOf(msg.getData().getString(JS_RECEIVED_VALUE).replace("\"", "")));
                        tryForOtherCoupons();
                        break;

                    case COUPON_ERROR:
                        addCouponStateToMap(false, originalPrice);
                        tryForOtherCoupons();
                        break;

                    case CHECK_FOR_COUPON_ERROR:
                        executeJavascript(Constants.JabongConstants.ERROR_MESSAGE_JS, EMPTY_MESSAGE);
                        break;

                    case REMOVE_COUPON:
                        isCouponApplied = false;
                        tryForOtherCoupons();
                }
                return true;
            }
        });
    }

    private void addCouponStateToMap(boolean isValid, float discountedPrice) {
        ContentModel currentModel = couponList.get(couponCount - 1);
        currentModel.setOriginalPrice(originalPrice);
        currentModel.setDiscountedPrice(discountedPrice);
        currentModel.setValid(isValid);
        currentModel.setInProgress(false);
        couponStatusInterface.onStatusChange(couponCount - 1);
        if (isValid && (null == budgetCoupon || discountedPrice < budgetCoupon.getDiscountedPrice())) {
            budgetCoupon = currentModel;
            couponStatusInterface.couponApplied(currentModel.getCoupon(), originalPrice - discountedPrice);
        }
    }

    private void startCheckingForCouponErrorResponse() {
        isCheckForCouponErrorFinished = false;
        checkForErrorMsgThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        handler.sendEmptyMessage(CHECK_FOR_COUPON_ERROR);
                        if (isCheckForCouponErrorFinished)
                            break;
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        checkForErrorMsgThread.start();
    }

    private void stopCheckingForError() {
        isCheckForCouponErrorFinished = true;
        if (checkForErrorMsgThread != null && checkForErrorMsgThread.isAlive() && couponCount == couponList.size())
            checkForErrorMsgThread.interrupt();
    }

    protected void loadUrl(String url) {
        webView.loadUrl(url);
    }

    private void bindViews() {
        webView = (WebView) findViewById(R.id.webview1);
        webviewProgressBar = (ProgressBar) findViewById(R.id.webview_progressbar);
        tryCouponsImageVIew = (ImageView) findViewById(R.id.try_coupons);
        tryCouponsImageVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfProductAdded();
            }
        });
    }

    private void checkIfProductAdded() {
        isProcessStart = true;
        executeJavascript(Constants.JabongConstants.EMPTY_CART_CHECK_JS, EMPTY_MESSAGE);
    }

    private void showLoadingDialog(String message) {
        progressDialog = isNull(progressDialog) ? new ProgressDialog(this) : progressDialog;
        progressDialog.setTitle("Buyhatke!");
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void tryForOtherCoupons() {
        // Log.d("try", "Try Coupon "+isCouponApplied);
        if (couponCount == couponList.size()) {
            checkForBudgetCouponApplied();
        }
        boolean isAllCouponsTried = couponCount == couponList.size();
        if (isAllCouponsTried) {
            if (isCouponPage && isNull(budgetCoupon)) {
                webView.goBack();
            } else {
                publishResult();
            }
        } else if (isCouponApplied) {
            // Log.d("try", "couponapplied");
            removeCoupon();
        } else if (isCartPage) {
            // Log.d("try", "cart page");
            executeJavascriptWithDelay(Constants.JabongConstants.HAVE_COUPON_JS, EMPTY_MESSAGE, 1000);
        } else if (isCouponPage) {
            // Log.d("try", "coupon page");
            applyCoupon(couponList.get(couponCount++).getCoupon());
        }
    }

    private void checkForBudgetCouponApplied() {
        // Log.d("budget", "enters");
        if (!isNull(budgetCoupon) && !currentCoupon.equals(budgetCoupon.getCoupon())) {
            // Log.d("budget", "enters "+isCartPage+"  "+budgetCoupon.getCoupon()+"  "+currentCoupon);
            if (isCartPage) {
                removeCoupon();
            } else {
                applyCoupon(budgetCoupon.getCoupon());
            }
        }
    }

    private void publishResult() {
        couponStatusInterface.onFinish(budgetCoupon);
    }

    private void applyCoupon(String coupon) {
        currentCoupon = coupon;
        executeJavascript(String.format(Constants.JabongConstants.FILL_COUPON_JS, coupon), EMPTY_MESSAGE);
        executeJavascriptWithDelay(Constants.JabongConstants.APPLY_COUPON_JS, EMPTY_MESSAGE, 500);
    }

    private void removeCoupon() {
        executeJavascript(Constants.JabongConstants.REMOVE_COUPON_JS, REMOVE_COUPON);
    }

    private void setOriginalPrice() {
        if (originalPrice == 0) {
            executeJavascript(Constants.JabongConstants.GET_ORIGINAL_PRICE_JS, ORIGINAL_PRICE);
        }
    }

    private void setDiscountedPrice() {
        executeJavascript(Constants.JabongConstants.GET_DISCOUNT_PRICE_JS, DISCOUNT_PRICE);
    }

    private void executeJavascript(String js, final int what) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if (what == EMPTY_MESSAGE) {
                        handler.sendEmptyMessage(what);
                    } else {
                        // Log.d("Execute", "Value "+value);
                        Message message = new Message();
                        message.what = what;
                        Bundle bundle = new Bundle();
                        bundle.putString(JS_RECEIVED_VALUE, value);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            });
        } else {
            webView.loadUrl("javascript:" + js);
        }
        // Log.d("Execute", js);
    }

    private void executeJavascriptWithDelay(final String script, final int what, final int timeInMs) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeInMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executeJavascript(script, what);
            }
        });
    }
}