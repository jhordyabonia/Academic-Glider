package webservice;

import models.DB;

public class LOG 
{
	public static boolean ACTIVE=false;
	public static String rest="",history="";
	public static void save(String data)
	{rest+=data;save(rest,"LOG.json");}
	public static void history(String data)
	{history+=data;save(history,"HISTORY.json");}
	public static void save(String data,String file)
	{DB.save(null,data,file);}
}
