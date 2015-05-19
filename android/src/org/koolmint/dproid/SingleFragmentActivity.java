package org.koolmint.dproid;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public abstract class SingleFragmentActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		
		FragmentManager fm=this.getFragmentManager();
		Fragment fragment=fm.findFragmentById(R.layout.activity_fragment);
		
		if(fragment == null) {
			fragment=createFragment();
			
			fm.beginTransaction()
				.add(R.id.fragment_container, fragment)
				.commit();
		}
	}
	
	public abstract Fragment createFragment();
	
}
