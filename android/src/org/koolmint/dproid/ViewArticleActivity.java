package org.koolmint.dproid;

import android.app.Fragment;

public class ViewArticleActivity extends SingleFragmentActivity {
	@Override
	public Fragment createFragment() {
		// temp value
		int wr_id=9345575;
		return ViewArticleFragment.newInstance(wr_id);
	}
}
