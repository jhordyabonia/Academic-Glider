package com.jhordyabonia.ag;

import util.InformacionFragment;
import util.Style;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public class InformacionActivity extends FragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Style.bar(this);
		setContentView(R.layout.activity_empty);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, new InformacionFragment())
				.commit();

		getActionBar().setHomeButtonEnabled(true);
	}
	@Override
	public final boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
