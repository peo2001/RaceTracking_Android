package it.xtremesoftware.tracking.Entities;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.xtremesoftware.tracking.TrackActivity;
import it.xtremesoftware.tracking.Util.DeviceInfo;
import it.xtremesoftware.tracking.Util.MyApplication;
import it.xtremesoftware.tracking.Util.MyParser;
import it.xtremesoftware.tracking.Util.RemoteConnector;
import it.xtremesoftware.tracking.Util.SessionManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Tracking extends TrackingT {
	private static final String LOG_TITLE= "Tracking";
	private final String remoteCallPage="Tracking";
	
	private List<Tracking> trackingList;
	public List<Tracking> getTrackingList() {
		return trackingList;
	}
	/*
	public void setTrackingList(List<Tracking> trackingList) {
		this.trackingList = trackingList;
	}*/
	
	
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
    
    
    public Tracking()
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
	                	//Toast.makeText(MyApplication.getAppContext(), R.string.abortGetTracking, Toast.LENGTH_LONG).show();
                        break;
	               
                }
            }
	    };
	}
    
    
    public void Track(Double latitudine, Double longitudine, Float Speed, Float Bearing, Float Accuracy, String AnnotationFilter)
	{
    	getRemoteConnector().setMainHandler(mHandler);
		
		getRemoteConnector().AddParameter("Lat", latitudine.toString());
		getRemoteConnector().AddParameter("Long", longitudine.toString());
		getRemoteConnector().AddParameter("IdRuoloInGara", MyApplication.getSessionManager().getIdRuoloInGara().toString());
		getRemoteConnector().AddParameter("IdGara", MyApplication.getSessionManager().getIdGara().toString());
		getRemoteConnector().AddParameter("AnnotationFilter", AnnotationFilter);
		getRemoteConnector().AddParameter("Course", Bearing.toString());
		getRemoteConnector().AddParameter("Speed", Speed.toString());
		getRemoteConnector().AddParameter("Accuracy", Accuracy.toString());
		
		DeviceInfo devInf=new DeviceInfo();
		getRemoteConnector().AddParameter("DeviceId", devInf.getPhoneID().toUpperCase());

		//Log.d(LOG_TITLE,"POST PARAMETER: Lat=" + latitudine.toString());
		//Log.d(LOG_TITLE,"POST PARAMETER: Long=" + longitudine.toString());
		//Log.d(LOG_TITLE,"POST PARAMETER: IdRuoloInGara=" + MyApplication.getIdRuoloInGara().toString());
		//Log.d(LOG_TITLE,"POST PARAMETER: IdGara=" + MyApplication.getIdGara().toString());
		//Log.d(LOG_TITLE,"POST PARAMETER: AnnotationFilter=" + AnnotationFilter);
		//Log.d(LOG_TITLE,"POST PARAMETER: Course=" + Bearing.toString());
		//Log.d(LOG_TITLE,"POST PARAMETER: Speed=" + Speed.toString());
		//Log.d(LOG_TITLE,"POST PARAMETER: Accuracy=" + Accuracy.toString());
		//Log.d(LOG_TITLE,"POST PARAMETER: DeviceId=" + devInf.getPhoneID());
		
		getRemoteConnector().RC_(remoteCallPage);
	}
    
    //parse XML ricevuto
  	private void parse(String xml)
  	{
  		Log.d(LOG_TITLE,"xml da parsare: " + xml);
  		
  		NodeList nl;
  		Element currentElement;
  		
  		if (xml.equals(""))
		{
  			mainHandler.sendEmptyMessage(SessionManager.ABORT_LOAD_TRACKING);
		}
		else
		{
  		
	  		trackingList = new ArrayList<Tracking>();
	  		//SimpleDateFormat dfh = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  		
	  		MyParser myParser=new MyParser(xml);
	  		
	  		//RECUPERO LO STATO DAL NODO PADRE
			nl=myParser.GetNodeList("Tracking");
			currentElement=myParser.GetElement(nl, 0);
			setStatus(myParser.GetAttributaValue(currentElement,"St"));
	
			nl=myParser.GetNodeList("Track");
			for (int i = 0; i < nl.getLength(); i++) {
				Tracking nuovoTracking=new Tracking();
				
				currentElement=myParser.GetElement(nl, i);
				
				nuovoTracking.setCodRuolo(myParser.GetAttributaValue(currentElement,"cr"));
				nuovoTracking.setIdGara(MyApplication.getSessionManager().getIdGara());
				nuovoTracking.setIdRuoloGara(Integer.parseInt(myParser.GetAttributaValue(currentElement,"idRG")));
				nuovoTracking.setLongitudine(Float.parseFloat(myParser.GetAttributaValue(currentElement,"x")));
				nuovoTracking.setLatitudine(Float.parseFloat(myParser.GetAttributaValue(currentElement,"y")));
				nuovoTracking.setProgressivo(Integer.parseInt(myParser.GetAttributaValue(currentElement,"Pr")));
				nuovoTracking.setsecReliability(Integer.parseInt(myParser.GetAttributaValue(currentElement,"Rel")));
				nuovoTracking.setBearing(Float.parseFloat(myParser.GetAttributaValue(currentElement,"Crs")));
				nuovoTracking.setSpeed(Float.parseFloat(myParser.GetAttributaValue(currentElement,"Sp")));
	        	
	        	
	        	trackingList.add(nuovoTracking);
			}
	
	  		mainHandler.sendEmptyMessage(SessionManager.FINISH_LOAD_TRACKING);
		}
  	}



  	



	public void setMainHandler(Handler h) {
       	mainHandler = h ;
  	}
}
