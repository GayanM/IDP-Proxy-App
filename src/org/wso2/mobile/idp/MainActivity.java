package org.wso2.mobile.idp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.wso2.mobile.idp.filebrowser.FileDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity{
	private static final String TAG = "MainActivity";
	public static int imageFileSize = 0;
	private FileInputStream fis;
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button loginButton = (Button) findViewById(R.id.btnLogin);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainActivity.this, WebViewActivity.class);
				i.putExtra("self_login", "self_login");
				startActivity(i);
			}

		});
		Button imageUploadButton = (Button) findViewById(R.id.btnUploadImage);
		imageUploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getBaseContext(), FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, "/sdcard");
				intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
				intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "jpg" });
				try {
					startActivityForResult(intent, 23);
				} catch (NullPointerException e) {
					Log.d(TAG, e.toString());
				}
			}
		});
	}
	/*
	 * (non-Javadoc)
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
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				Log.d(TAG, "Menu Setting Clicked");
				Intent intent = new Intent(this, Configuration.class);
				startActivity(intent);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 23) {
			Log.d(TAG, "Saving...");
			try {
				String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
				File file = new File(filePath);
				Log.v("File Selected", filePath);
				fis = new FileInputStream(file);
				imageFileSize = (int) file.length();
				byte[] fileArray = new byte[imageFileSize];
				fis.read(fileArray);
				FileOutputStream outputStream =
						openFileOutput("profile_pic.png",
						               Context.MODE_PRIVATE);
				outputStream.write(fileArray);
				outputStream.flush();
				outputStream.close();

				FileInputStream readFile = openFileInput("profile_pic.jpg");
				byte[] fileInputArray = new byte[(int) file.length()];
				readFile.read(fileInputArray);
				readFile.close();
				Bitmap myBitmap =
						BitmapFactory.decodeByteArray(fileInputArray, 0,
						                              fileInputArray.length);

				ImageView iv = (ImageView) findViewById(R.id.imageView1);
				iv.setImageBitmap(myBitmap);
				super.onActivityResult(requestCode, resultCode, data);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, e.toString());
			} catch (Exception e) {
				Log.d(TAG, e.toString());
			}
		}
	}
}
