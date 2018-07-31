package com.jhordyabonia.ag;

import java.util.HashMap;

import models.DB;

import webservice.Asynchtask;
import webservice.Client;

import android.app.Activity;

public final class Server {
	public static String URL_SERVER = "http://123seller.azurewebsites.net/pu/";
	static
	{
		String url=DB.load("server.js");
		if(!url.isEmpty())
			Server.URL_SERVER=url;
	}
	private static Client ws;
	private static HashMap<String, String> data_toSend = new HashMap<String, String>();

	public static final void setDataToSend(HashMap<String, String> toSend) {
		data_toSend = toSend;
	}

	public static void send(String url, Activity a, Asynchtask t) {
		ws = new Client();
		ws.setUrl(URL_SERVER + url);
		ws.setDatos(data_toSend);
		ws.setActividad(a);
		ws.setCallback(t);
		ws.execute("");
	}

}
