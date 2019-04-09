package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import pt.it.porto.mydiabetes.R;

public class MyDiabetesWebViewActivity extends Activity {

    private String site = "https://mydiabetes.dcc.fc.up.pt/register_mobile.php";
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        setMainView();
    }

    private void setMainView(){

        if(isNetworkStatusAvialable(this)){
            MyWebViewClient webC = new MyWebViewClient();

            findViewById(R.id.backSync).setVisibility(View.GONE);
            findViewById(R.id.reSync).setVisibility(View.GONE);
            findViewById(R.id.errorConnection).setVisibility(View.GONE);
            findViewById(R.id.myDiabetesWebView).setVisibility(View.VISIBLE);
            findViewById(R.id.loading).setVisibility(View.GONE);

            webView = findViewById(R.id.myDiabetesWebView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(webC);
            webView.loadUrl(site);


        }else{
            findViewById(R.id.errorConnection).setVisibility(View.VISIBLE);
            findViewById(R.id.myDiabetesWebView).setVisibility(View.GONE);
            findViewById(R.id.loading).setVisibility(View.GONE);
            findViewById(R.id.reSync).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMainView();
                }
            });
            findViewById(R.id.backSync).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });


        }
    }
    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            findViewById(R.id.errorConnection).setVisibility(View.GONE);
            findViewById(R.id.myDiabetesWebView).setVisibility(View.GONE);
            findViewById(R.id.loading).setVisibility(View.VISIBLE);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            findViewById(R.id.errorConnection).setVisibility(View.GONE);
            findViewById(R.id.myDiabetesWebView).setVisibility(View.VISIBLE);
            findViewById(R.id.loading).setVisibility(View.GONE);

            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {

            if(Build.VERSION.SDK_INT >= 19){

                final String js = "javascript:document.getElementById('user').value + ' ' + document.getElementById('pass').value;";


                view.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                        s=s.trim();
                        s=s.substring(1, s.length()-1);
                        String[] namePass = s.split(" ");
                        String name = namePass[0];
                        String pass = namePass[1];

                        if(name !=null && pass != null){
                            pt.it.porto.mydiabetes.database.Preferences.saveCloudSyncCredentials(view.getContext(), name, pass);
                            setResult(1);
                            //Log.i("cenas", "onReceiveValue: -> "+name+" "+pass);
                        }

                        //arranjar o loading
                          //      tnetar dar erro...

                    }
                });
            }




            if(url.contains("exit")){
                //try {
                    //this.finalize();
                    setResult(1);
                    finish();
                //} catch (Throwable throwable) {
                //    throwable.printStackTrace();
               // }
            }else{
                if (Uri.parse(url).getHost().contains("mydiabetes.dcc.fc.up.pt")) {
                    // This is my website, so do not override; let my WebView load the page
                    return false;
                }
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                return true;
            }

            return true;
        }
    }
}
