package org.wso2.mobile.idp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Configuration extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		getFragmentManager().beginTransaction().add(R.id.prefcontent, new PrefsFragment()).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configuration, menu);
		return true;
	}

}
