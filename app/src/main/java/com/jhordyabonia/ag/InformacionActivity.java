package com.jhordyabonia.ag;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import chat.ChatService;
import chat.DBChat;

import models.DB;
import webservice.Asynchtask;
import webservice.LOG;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.jhordyabonia.ag.PlaceholderFragment.newInstance;

public class InformacionActivity extends FragmentActivity
{
	int last=0;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.informacion_main);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, new InformacionFragment())
				.commit();
	}
}
