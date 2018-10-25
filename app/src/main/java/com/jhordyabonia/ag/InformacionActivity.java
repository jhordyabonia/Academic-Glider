package com.jhordyabonia.ag;

import util.InformacionFragment;
import util.Style;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class InformacionActivity extends FragmentActivity
{
	int last=0;
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
	}
}
