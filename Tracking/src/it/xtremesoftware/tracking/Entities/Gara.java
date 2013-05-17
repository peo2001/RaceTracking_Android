package it.xtremesoftware.tracking.Entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import it.xtremesoftware.tracking.LoginActivity;
import it.xtremesoftware.tracking.Util.DeviceInfo;
import it.xtremesoftware.tracking.Util.MyParser;
import it.xtremesoftware.tracking.Util.RemoteConnector;
import it.xtremesoftware.tracking.Util.SessionManager;

public class Gara extends GaraT {
	private static final String LOG_TITLE= "Gara";

	//PAGINA DEL WEBSERVICE
	private final String remoteCallPage="Accesso";
	
	//STATI DELLA GARA
	public static final String STATE_RUNNING="R";
	public static final String STATE_TOSTART="S";
	public static final String STATE_ENDED="E";
	public static final String STATE_LOGINFAILED="F";
	
	//VARIABILE CHE CONTERRA' L'esito del Login
	private Boolean mEsitoLogin=false;
	public Boolean getEsitoLogin() {
		return mEsitoLogin;
	}
	public void setEsitoLogin(Boolean mEsitoLogin) {
		this.mEsitoLogin = mEsitoLogin;
	}



	//handler del Chiamante
    private Handler mainHandler;
    
    //mio Handler
	private Handler mHandler;
	
	//Istanza della classe connector
	private RemoteConnector remoteConnector;
	public RemoteConnector getRemoteConnector() {
		if (remoteConnector==null)
		{
			remoteConnector=new RemoteConnector();
		}
    	return remoteConnector;
	}
	
	
	
	//*****************************************************
	//HANDLER CASE
	//*****************************************************
	protected static final int FINISH_LOAD = 0 ;
    protected static final int START_LOAD = 1 ;
    protected static final int ABORT_LOAD = 2 ;
    //*****************************************************
  	//FINE HANDLER CASE
  	//*****************************************************
    
    
    public Gara()
    {
    	setMyHandler();
    }
	
	
	
	//Setto il mio Handler e ne definisco le azioni
    private void setMyHandler()
	{
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) 
            {
            	//Log.d(LOG_TITLE," HandleMessage Ricevuto: " + msg.what);
                switch (msg.what) 
                {
	                case FINISH_LOAD:
	                	//Log.d(LOG_TITLE,"HANDLE: FINISH LOAD");
	                	parse(remoteConnector.getXmlResponse());
	                    break;
	                case START_LOAD:
	                	//Log.d(LOG_TITLE,"HANDLE: START LOAD");
	                    break;
	                case ABORT_LOAD:
	                	//Log.d(LOG_TITLE,"HANDLE: ABORT LOAD");
                        break;
	               
                }
            }
	    };
	}
	
	
	/*public void RC_()
	{
		//DO AL CONNECTOR IL MIO HANDLER
    	getRemoteConnector().setMainHandler(mHandler);
    	
		//getRemoteConnector().setDataMode("data");
		getRemoteConnector().AddParameter("IdGara", MyApplication.getIdGara().toString());
		
		getRemoteConnector().RC_(remoteCallPage);
	}*/
	
	
	public void Login(String Codice)
	{
		getRemoteConnector().setMainHandler(mHandler);
		getRemoteConnector().AddParameter("CodiceAttivazione", Codice);
		DeviceInfo devInf=new DeviceInfo();
		getRemoteConnector().AddParameter("DeviceId", devInf.getPhoneID().toUpperCase());
		
		getRemoteConnector().RC_(remoteCallPage);
	}
	
	
	//parse XML ricevuto
	private void parse(String xml)
	{
		Log.d(LOG_TITLE,"xml da parsare: " + xml);
		
		NodeList nl;

		if (xml.equals(""))
		{
			mainHandler.sendEmptyMessage(SessionManager.ABORT_LOGIN);
		}
		else
		{
			SimpleDateFormat dfh = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss",Locale.ITALY);
			MyParser myParser=new MyParser(xml);
			
			//RECUPERO LO STATO DAL NODO PADRE
			nl=myParser.GetNodeList("Login");
			Element currentElement=myParser.GetElement(nl, 0);
			setStatus(myParser.GetAttributaValue(currentElement,"St"));
			
			
			
			//SE NON SONO IN LOGIN_FAILED, ALLORA POSSO LEGGERE I DATI DELLA GARA
			if (!getStatus().equals(STATE_LOGINFAILED))
			{
				nl=myParser.GetNodeList("Gara");
				
				setIdGara(Long.parseLong(myParser.getValue("IdGara")));
				setIdRuoloINGara(Long.parseLong(myParser.getValue("IdRuoloInGara")));
				setCodRuolo(myParser.getValue("CodRuolo"));
				setGara(myParser.getValue("Gara"));
				setCodiceAttivazione(myParser.getValue("CodiceAttivazione"));
				
				//DATA INIZIO E FINE
				Date d=null;
	        	try {
					d=dfh.parse(myParser.getValue("DataInizio"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
	        	setInizio(d);
	        	
	        	d=null;
	        	try {
					d=dfh.parse(myParser.getValue("DataFine"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
	        	setFine(d);
			}
			
			//SE NON SONO IN RUNNING IL LOGIN FALLISCE
			if (!getStatus().equals(STATE_RUNNING)) //R è lo stato di Running (La gara è attiva)
			{
				setEsitoLogin(false);
			}
			else
			{
				setEsitoLogin(true);
			}
			
			
			mainHandler.sendEmptyMessage(SessionManager.FINISH_LOGIN);
		}
		
	}



	public void setMainHandler(Handler h) {
     	mainHandler = h ;
	}
	
}
