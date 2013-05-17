package it.xtremesoftware.tracking.Util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class MyApplication extends Application{

    private static Context context;
    
    private static MyApplication sInstance;
    private SessionManager sessionManager;
    
    /*
    //ID GARA A CUI SONO LOGGATO
    private static Long IdGara=0L;
    //ID RUOLO IN GARA A CUI SONO LOGGATO
    private static Long IdRuoloInGara=0L;
    //NOME DELLA GARA
    private static String NomeGara="";
    */


    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
        
        sInstance = this;
        sInstance.initializeInstance();
    }
    
    protected void initializeInstance() {
        // do all your initialization here
    	 sInstance.sessionManager = new SessionManager();
    }
    
    public static SessionManager getSessionManager() {
		if (sInstance.sessionManager==null)
		{
			return null;
		}
		return sInstance.sessionManager;
	}

    public static Context getAppContext() {
    	return MyApplication.context;
    }
    /*
    public static Long getIdGara() {
		return IdGara;
	}

	public static void setIdGara(Long idGara) {
		IdGara = idGara;
	}
	
	public static String getNomeGara() {
		return NomeGara;
	}

	public static void setNomeGara(String nomeGara) {
		NomeGara = nomeGara;
	}
	
	public static Long getIdRuoloInGara() {
		return IdRuoloInGara;
	}

	public static void setIdRuoloInGara(Long idRuoloInGara) {
		IdRuoloInGara = idRuoloInGara;
	}
	*/
	public static Boolean VerificaConnessione()
  	{
  		//Log.d(LOG_TITLE,"VerificaConnessione Inizio");
  		ConnectivityManager cm = (ConnectivityManager)getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
          
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        
        if (activeNetwork==null)
        {
        	return false;
        }
        //return activeNetwork.isConnectedOrConnecting();
        return activeNetwork.isConnected();
  		
  	}

	
	/*
	public static void resetApplication()
	{
		setIdGara(0L);
		setIdRuoloInGara(0L);
	}
	
	public static boolean IsLogged()
	{
		return ((IdGara!=0)&&(IdRuoloInGara!=0));
	}
	*/
}