package org.wso2.mobile.idp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.wso2.mobile.idp.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class WebViewActivity extends Activity {
	private static final String TAG = "WebViewActivity";
	private WebView webView;
	private String clientID = null;
	private String redirectURL = null;
	private String selfLogin = null;
	private OauthEndPoints oauthEndPoints = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		oauthEndPoints = OauthEndPoints.getInstance();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String host = sharedPreferences.getString("edittext_preference_host", "10.100.5.3");
		Log.d("Host", host);
		String port = sharedPreferences.getString("edittext_preference_port", "9763");
		Log.d("Port", port);

		oauthEndPoints.setEndPointURLs(host, port);
		oauthEndPoints.setRedirectURL("http://www.wso2.com");

		clientID = getIntent().getStringExtra("client_id");
		redirectURL = getIntent().getStringExtra("redirect_url");
		selfLogin = getIntent().getStringExtra("self_login");

		if (clientID != null && redirectURL != null) {
			Log.d(TAG, "Recived Client ID, Redirect URL from Third Party Application");
			Log.d(TAG, clientID);
			Log.d(TAG, redirectURL);
			showImage();
			loadWebView();
		} else if (selfLogin != null) {
			Log.d(OauthCostants.INFO, "Recived Client ID, Redirect URL from IDP Proxy Application");
			clientID = OauthCostants.CLIENT_ID;
			redirectURL = oauthEndPoints.getRedirectURL();
			showImage();
			loadWebView();
		} else {
			Intent entry = new Intent(this, MainActivity.class);
			startActivity(entry);
			finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_settings:
				Log.v("Menu Clicked", "Menu Setting Clicked");
				Intent intent = new Intent(this, Configuration.class);
				startActivity(intent);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private class LoginWebViewClient extends WebViewClient {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit
		 * .WebView, java.lang.String)
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("Redirect URL", url);
			if (url.contains(redirectURL)) {
				String code = Uri.parse(url).getQueryParameter("code");
				Log.d("Obtained Authorization Code", code);
				if (selfLogin != null) {
					selfLogin = null;
					Intent entry = new Intent(WebViewActivity.this, MainActivity.class);
					startActivity(entry);
					finish();
				} else {
					Intent intent = new Intent();
					intent.putExtra("code", code);
					intent.putExtra("authorize_url", oauthEndPoints.getAuthorizeURL());
					intent.putExtra("access_token_url", oauthEndPoints.getAccessTokenURL());
					setResult(RESULT_OK, intent);
					finish();
				}
			}
			return false;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed(); // Ignore SSL certificate errors
		}
	}

	/*
	 * 
	 */
	void showImage() {
		FileInputStream readFile;
		try {
			readFile = openFileInput("profile_pic.png");
			byte[] fileInputArray = new byte[MainActivity.imageFileSize];
			readFile.read(fileInputArray);
			readFile.close();
			Bitmap myBitmap =
			                  BitmapFactory.decodeByteArray(fileInputArray, 0,
			                                                fileInputArray.length);

			ImageView iv = (ImageView) findViewById(R.id.imageView1);
			iv.setImageBitmap(myBitmap);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/*
	 * 
	 */
	@SuppressLint("SetJavaScriptEnabled")
	void loadWebView() {
		String authrizeRequest =
		                         oauthEndPoints.getAuthorizeURL() +
		                                 "?response_type=code&client_id=" + clientID +
		                                 "&redirect_uri=" + redirectURL + "&scope=openid";
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(authrizeRequest);
		webView.setWebViewClient(new LoginWebViewClient());
	}

}