package it.xtremesoftware.tracking.Util;
import it.xtremesoftware.tracking.Util.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class RemoteConnector {
	private static final String LOG_TITLE="RemoteConnector";
	
	//*********************************************************************************
	//PARAMETRI CONNESSIONE
	//*********************************************************************************
	//private static String remoteServerUrl="http://hq.xtremesoftware.it/mototracking/rest/";
	private static String remoteServerUrl="http://hq1.xtremesoftware.it/mototracking/rest/";
	//private final String remoteServerUrl="http://10.10.10.243/mototracking/rest/"; //KISS
	//private final String remoteServerUrl="http://kiss/mototracking/rest/"; //KISS
	
	//private final String webServiceExtension = "";
	private final String webServiceExtension = ".asp";
	//private final String userName = "xtreme";
	//private final String password = "XtremeSoftware";
	//*********************************************************************************
	//FINE PARAMETRI CONNESSIONE
	//*********************************************************************************
	
	
	
	//*****************************************************
	//Variabili di Oggetto (GET e SET)
	//*****************************************************
	//HASHMAP PER I PARAMETRI POST
	private Map<String, String> parameter = new HashMap<String, String>();
	//private String dataMode="msg";
	private boolean standardBodyPrepared = false;
	private String xmlResponse;
	//handler del Chiamante
    private Handler mainHandler;
/*
	public String getDataMode() {
		return dataMode;
	}
	public void setDataMode(String dataMode) {
		this.dataMode = dataMode;
	}
*/	
	public String getXmlResponse() {
		return xmlResponse;
	}
	//*****************************************************
	//FINE Variabili di Oggetto (GET e SET)
	//*****************************************************
	
	
	
	
	
	//*****************************************************
	//HANDLER CASE
	//*****************************************************
	protected static final int FINISH_LOAD = 0 ;
    protected static final int START_LOAD = 1 ;
    protected static final int ABORT_LOAD = 2 ;
    //*****************************************************
  	//FINE HANDLER CASE
  	//*****************************************************
	
    
	



    //*****************************************************
  	//COSTRUTTORE
  	//*****************************************************
	public RemoteConnector()
	{
		prepareStandardBody();
	}
	//*****************************************************
  	//FINE COSTRUTTORE
  	//*****************************************************
	 
	
	
	
	
	
	//*****************************************************
  	//METODI OGGETTO
  	//*****************************************************
	 private void prepareStandardBody()
	 {
	     if (!standardBodyPrepared)
	     {
			 //AddParameter("ActivationKey", "1234567890");
		     //AddParameter("ClientType", "mobile/ios/xtreme");
		     //AddParameter("ClientId", "UNIQUE_ID");
		     
		     /*if (MyApplication.getSessionManager()!=null)
		     {
			     Log.d(LOG_TITLE,MyApplication.getSessionManager().toString());
			     if ((MyApplication.getSessionManager().getSessionId()!="")&&(MyApplication.getSessionManager().getSessionId()!=null))
			     {
			    	 AddParameter("SessionId", MyApplication.getSessionManager().getSessionId());
			     }
		     }*/
		     standardBodyPrepared=true;
	     }
	 }
	 
	 //aggiunta Parametri per il POST
	 public void AddParameter(String chiave, String valore)
	 {
		 parameter.put(chiave, valore);
	 }
	 
	 
	 
	 public void RC_(String pagina)
	 {
		 //DEFINISCO LA MIA CLASSE PER LE RICHIESTE ASINCRONE
	     Rest task=new Rest();
	     Log.d(LOG_TITLE,"RC_ su URL:" + remoteServerUrl + pagina + webServiceExtension);
	  	 task.execute(new String[] {remoteServerUrl + pagina + webServiceExtension});
	 }


    public void setMainHandler(Handler h){
     	mainHandler = h ;
     }
    
  	//*****************************************************
  	//FINE METODI OGGETTO
  	//*****************************************************
    
    
    
    
    
    
    //*****************************************************
  	//CLASSE ASINCRONA PER LE RICHIESTE HTTP
  	//*****************************************************
    //The parameters are the following AsyncTask <TypeOfVarArgParams , ProgressValue , ResultValue>
  	//TypeOfVarArgParams is passed into the doInBackground() method as input, ProgressValue is used for progress information
  	//and ResultValue must be returned from doInBackground() method and is passed to onPostExecute() as parameter.
    class Rest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
        	//The doInBackground() method contains the coding instruction which should be performed in a background thread. 
    		//This method runs automatically in a separate Thread.
    			
        	if(mainHandler != null)
        	{
        		mainHandler.sendEmptyMessage(START_LOAD);
        		//Log.d(LOG_TITLE,"doInBackground START_LOAD");
        	}
        	
    		  //Log.d(LOG_TITLE,"doInBackground Inizio");
	          String response = "";
	          for (String url : urls) {
	        	
	        	//Log.d(LOG_TITLE,"doInBackground url:" + url);
	        	  
	            
	            //DEFINISCO PARAMETRI PER I TIMEOUT DELLA CONNESSIONE
	            HttpParams httpParameters = new BasicHttpParams();
	            // Set the timeout in milliseconds until a connection is established.
	            // The default value is zero, that means the timeout is not used. 
	            int timeoutConnection = 3000;
	            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	            
	            // Set the default socket timeout (SO_TIMEOUT) 
	            // in milliseconds which is the timeout for waiting for data.
	            int timeoutSocket = 5000;
	            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	            
	            //Oggetto HTTP con gestione dei Timeout sopra definiti
	            //DefaultHttpClient client = new DefaultHttpClient();
	            DefaultHttpClient client = new DefaultHttpClient(httpParameters);
	            
	            //Oggetto che conterrà il risultato della chiamata HTTP
	            InputStream content = null;
	            
	            //Oggetto che conterrà i Dati POST
	            HttpPost httppost = new HttpPost(url);
	            try {
	            	
	            	//Creazione dei DATI POST
	            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            	for (Entry<String, String> currentElement : parameter.entrySet()) {
	            	    nameValuePairs.add(new BasicNameValuePair(currentElement.getKey(), currentElement.getValue()));
	            	    Log.d(LOG_TITLE,"Post Parater Aggiunto --- Key:" + currentElement.getKey() + " Value:" + currentElement.getValue());
	            	}
	                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));	
	            	
	                
	                //richiesta HTTP e ne ottengo il contenuto
	                HttpResponse execute = client.execute(httppost);
	                content = execute.getEntity().getContent();
	
		            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		            String s = "";
		            while ((s = buffer.readLine()) != null) {
		                response += s;
		            }
	
	            } catch (Exception e) {
	            	if(mainHandler != null)
	            	{
	            		mainHandler.sendEmptyMessage(ABORT_LOAD);
	            		//Log.d(LOG_TITLE,"doInBackground ABORT_LOAD");
	            	}
	            	e.printStackTrace();
	            }
	            finally {
	            	//Chiudere uno stream quando non è più necessario è molto
	            	//importante, così importante che il nostro programma usa un blocco
	            	//finally per garantire che lo stream sia chiuso se si
	            	//presenta un errore

	            	if (content != null) 
	            	{
	            		try {
							content.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
	            	}
	            }

	          }
	          
	          //Log.d(LOG_TITLE,"doInBackground Finito:" + response);
	          return response;
        }

        @Override
        protected void onPostExecute(String result) {
        	//The onPostExecute() method synchronize itself again with the user interface thread and allows to update it. 
        	//This method is called by the framework once the doInBackground() method finishes.
        	if(mainHandler != null)
        	{
        		mainHandler.sendEmptyMessage(FINISH_LOAD);
        	}
      	  	
        	xmlResponse=result;

        	//Log.d(LOG_TITLE,"onPostExecute Risultato:" + result);
        }
   }
   //*****************************************************
   //CLASSE ASINCRONA PER LE RICHIESTE HTTP
   //*****************************************************
}
