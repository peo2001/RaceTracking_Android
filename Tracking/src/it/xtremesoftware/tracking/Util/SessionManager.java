package it.xtremesoftware.tracking.Util;


import it.xtremesoftware.tracking.LoginActivity;
import it.xtremesoftware.tracking.TrackActivity;
import it.xtremesoftware.tracking.Entities.Gara;
import it.xtremesoftware.tracking.Entities.Tracking;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class SessionManager {
	private static final String LOG_TITLE="SessionManager";
	
	//*****************************************************
	//Variabili di Oggetto (GET e SET)
	//*****************************************************
	private String SessionId="";
	private Handler mHandler;
	private Handler loginHandler;
	private Handler trackingHandler;
	private TrackActivity trackActivityIstance;
	private Gara mGara;
	private Tracking mTracking;
	
	//ID GARA A CUI SONO LOGGATO
    private Long IdGara=0L;
    //ID RUOLO IN GARA A CUI SONO LOGGATO
    private Long IdRuoloInGara=0L;
    //NOME DELLA GARA
    private String NomeGara="";
	
	
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	
	public Gara getGara() {
		if (mGara==null)
		{
			return null;
		}
		return mGara;
	}
	
	public Tracking getTracking() {
		if (mTracking==null)
		{
			return null;
		}
		return mTracking;
	}
	
	
	public Long getIdGara() {
		return IdGara;
	}

	public void setIdGara(Long idGara) {
		IdGara = idGara;
	}
	
	public String getNomeGara() {
		return NomeGara;
	}

	public void setNomeGara(String nomeGara) {
		NomeGara = nomeGara;
	}
	
	public Long getIdRuoloInGara() {
		return IdRuoloInGara;
	}

	public void setIdRuoloInGara(Long idRuoloInGara) {
		IdRuoloInGara = idRuoloInGara;
	}

	
	
	private final Long MILLISEC_BEFORE_RELOAD=5000L;	//millisecondi tra una richiesta di Tracking e la successiva
	private Runnable r;
	private Thread t;
	private boolean mContinueThread;
	private boolean mThreadIsStarted=false;
	
	
	
	
	private GPSTracker mGps;
	private double mLatitudine;
	private double mLongitudine;
	private float mBearing;
	private float mSpeed;
	private float mAccuracy;
	
	
	
	
	public double getLatitudine() {
		return mLatitudine;
	}
	public void setLatitudine(double Latitudine) {
		this.mLatitudine = Latitudine;
	}
	public double getLongitudine() {
		return mLongitudine;
	}
	public void setLongitudine(double Longitudine) {
		this.mLongitudine = Longitudine;
	}
	//*****************************************************
	//Fine Variabili di Oggetto (GET e SET)
	//*****************************************************


	//*****************************************************
	//HANDLER CASE
	//*****************************************************
	public static final int FINISH_LOGIN = 0 ;
	public static final int START_LOGIN = 1 ;
	public static final int ABORT_LOGIN = 2 ;
	
	public static final int FINISH_LOAD_TRACKING = 3 ;
	public static final int START_LOAD_TRACKING = 4 ;
	public static final int ABORT_LOAD_TRACKING = 5 ;
	
	public static final int GET_TRACKING = 6 ;
    //*****************************************************
  	//FINE HANDLER CASE
  	//*****************************************************
	
    
    //*****************************************************
  	//COSTRUTTORE
  	//*****************************************************
	public SessionManager()
	{
		setMyHandler();
		
		mGara=new Gara();
        mGara.setMainHandler(mHandler);
        
        mTracking=new Tracking();
        mTracking.setMainHandler(mHandler);
        
        
        //definisco un oggetto runnable per temporizzare il thread delle richieste di Track
		r = new Runnable() { 
			 public void run() { 
				 while (mContinueThread) { 
					 try { 
						 Thread.sleep(MILLISEC_BEFORE_RELOAD); 
						 mHandler.sendEmptyMessage(GET_TRACKING);
					 }
					 catch (Exception e)
					 {
						 
					 } 
	             } 
          } 
		 }; 
		 
		//CREO OGGETTO DELLA CLASSE DI LOCALIZZAZIONE
		 mGps = new GPSTracker(MyApplication.getAppContext());
	}
	//*****************************************************
  	//FINE COSTRUTTORE
  	//*****************************************************
	
	
	
	
	
	
	//*****************************************************
  	//METODI OGGETTO
  	//*****************************************************
	
	//Setto il mio Handler e ne definisco le azioni
	private void setMyHandler()
	{
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) 
            {
                switch (msg.what) 
                {
	                case FINISH_LOGIN:
	                	Log.d(LOG_TITLE,"HANDLE: FINISH LOGIN");
	                	
	                	IdGara=mGara.getIdGara();
	                	IdRuoloInGara=mGara.getIdRuoloINGara();
	                	NomeGara=mGara.getGara();
	                	
	                	loginHandler.sendEmptyMessage(LoginActivity.FINISH_LOGIN);
	                    break;
	                case START_LOGIN:
	                	Log.d(LOG_TITLE,"HANDLE: START LOGIN");
	                	loginHandler.sendEmptyMessage(LoginActivity.START_LOGIN);
	                    break;
	                case ABORT_LOGIN:
	                	Log.d(LOG_TITLE,"HANDLE: ABORT LOGIN");
	                	loginHandler.sendEmptyMessage(LoginActivity.ABORT_LOGIN);
                        break;
                        
	                case FINISH_LOAD_TRACKING:
	                	Log.d(LOG_TITLE,"HANDLE: FINISH LOAD TRACKING");
	                	trackingHandler.sendEmptyMessage(TrackActivity.FINISH_LOAD);
	                    break;
	                case START_LOAD_TRACKING:
	                	Log.d(LOG_TITLE,"HANDLE: START LOAD TRACKING");
	                	trackingHandler.sendEmptyMessage(TrackActivity.START_LOAD);
	                    break;
	                case ABORT_LOAD_TRACKING:
	                	Log.d(LOG_TITLE,"HANDLE: ABORT LOAD TRACKING");
	                	trackingHandler.sendEmptyMessage(TrackActivity.ABORT_LOAD);
                        break;

	                case GET_TRACKING:
	                	Log.d(LOG_TITLE,"HANDLE: GET_TRACKING");
	                	
	                	trackingHandler.sendEmptyMessage(TrackActivity.START_LOAD);
	                	if (mContinueThread)
	                	{
	                		RilevaCoordinateUtente(false);
	                		Track(mLatitudine, mLongitudine, mSpeed, mBearing, mAccuracy,trackActivityIstance.getIdRuoliInGaraDaFiltrare());
	                		//mTracking.Track(mLatitudine, mLongitudine, mSpeed, mBearing, mAccuracy,mIdRuoliInGaraDaFiltrare);
	                	}
	                	
	                	break;
                }
            }
	    };
	}
	
	
	
	
	
	public void resetApplication()
	{
		setIdGara(0L);
		setIdRuoloInGara(0L);
	}
	
	public boolean IsLogged()
	{
		return ((IdGara!=0)&&(IdRuoloInGara!=0));
	}
	
	
	
	
	
	public void Login(String CodiceGara) {
		mGara.Login(CodiceGara);
	}
	
	public void StartTrack()
	{
		//variabile che mi dice che il thread può girare
    	//se sto riesumando l'applicazione il thread deve ripartire
    	//Ammesso che sia stato stoppato
		mContinueThread=true;
		
		//INTANTO FACCIO PARTIRE LA PRIMA RICHIESTA
		mHandler.sendEmptyMessage(GET_TRACKING);
		
		//se il thread non è mai partito (avevo già fatto il LOgout oppure e la prima volta che chiamo questo metodo) lo starto
		if (!mThreadIsStarted)
		{
			mThreadIsStarted=true;
			//definisco un nuovo thread e lo starto con il Runnable prima definito
			t = new Thread(r);
			t.start();
		}
	}
	
	public void Track()
	{
		mHandler.sendEmptyMessage(GET_TRACKING);
	}
	
	private void Track(double Latitudine, double Longitudine, float Speed, float Bearing, float Accuracy, String IdRuoliInGaraDaFiltrare) {
		mTracking.Track(Latitudine, Longitudine, Speed, Bearing, Accuracy, IdRuoliInGaraDaFiltrare);
	}
	
	private void RilevaCoordinateUtente(boolean RichiediAccessoImpostazioni)
	{
		mGps.getLocation();
		
		// check if GPS enabled
		if(mGps.canGetLocation())
		{
			mLatitudine = mGps.getLatitude();
			mLongitudine = mGps.getLongitude();
			mBearing = mGps.getBearing();
			mSpeed = mGps.getSpeed();
			mAccuracy = mGps.getAccuracy();
		}
		else
		{
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			if ((GPSTracker.UserWantSettingGps)&&(RichiediAccessoImpostazioni))
			{
				mGps.showSettingsAlert();
			}
		}
	}
	
	public void LogOut() {
		//SE IL THREAD E IN ESECUZIONE
 	    mContinueThread=false;
 	    if (mThreadIsStarted)
 	    {
 	    	//STOPPO IL THREAD
 	     	//variabile a false
 	    	mContinueThread=false;
 	    	mThreadIsStarted=false;
 	    	
 	    	//METTO IL THREAD A NULL IN MODO DA POTERLO RICREARE SUCCESSIVAMENTE
 	    	t=null;
 	    }
 	    
 	    resetApplication();
	}
	
	
	
	
	//HANDLER DEI CONTROLLER CHE INTERAGISCONO CON ME
	public void setLoginHandler(Handler h) {
		loginHandler = h ;
	}
	
	public void setTrackingHandler(Handler h) {
		trackingHandler = h ;
		
	}
	public void setTrackingActivityIstance(TrackActivity trackActivity) {
		trackActivityIstance=trackActivity;
		
	}
	
	
}