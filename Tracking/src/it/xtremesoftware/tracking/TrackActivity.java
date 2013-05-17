package it.xtremesoftware.tracking;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import it.xtremesoftware.tracking.R;
import it.xtremesoftware.tracking.Entities.Gara;
import it.xtremesoftware.tracking.Entities.Tracking;
import it.xtremesoftware.tracking.Util.CustomDigitalClock;
import it.xtremesoftware.tracking.Util.GPSTracker;
import it.xtremesoftware.tracking.Util.MyApplication;
//import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//extends Activity
public class TrackActivity extends FragmentActivity  {
	private static final String LOG_TITLE= "MappaActivity";
	
	private String mIdRuoliInGaraDaFiltrare="";	//Id Ruoli (separati da , di cui richiedere le posizioni), se "" allora avrò le posizioni di tutti
	public String getIdRuoliInGaraDaFiltrare() {
		return mIdRuoliInGaraDaFiltrare;
	}

	//private float mCurrentZoom = -1;			//Mi accorgo se lo Zoom della mappa è cambiato
	private boolean mMapAutoAdapt=false;		//se la mappa si sta adattando (Zoom e posizione) per suoi scopi o se la ha spostata l'utente
	private Boolean mIsInPrimoPiano=true;		//se la mappa è in primo piano, oppure il Thread sta continuando a girare, ma l'activity è chiusa
	
	private static Handler mHandler;

	
	private GoogleMap map;
	private TextView txtNomeGara;
	private ImageView imgOnMap;
	private CustomDigitalClock dc;
	
	private HashMap<Marker, Tracking> markers = new HashMap<Marker, Tracking>();
	
	final int RQS_GooglePlayServices = 1;
	private Boolean mPlayServiceOK=false;
	
	//*****************************************************
	//HANDLER CASE
	//*****************************************************
	public static final int FINISH_LOAD = 0 ;
	public static final int START_LOAD = 1 ;
	public static final int ABORT_LOAD = 2 ;
    //*****************************************************
  	//FINE HANDLER CASE
	//*****************************************************
	
	
	
	//*****************************************************
	//STATI DELLA MAPPA
	//*****************************************************
	private int MAP_STATE;						//STATO CORRENTE DELLA MAPPA
	private final int MAP_FREE = 100 ;			//LA mappa è libera e si aggiorna con tutti i punti ricevuti
	private final int MAP_WAITING = 101 ;		//La Mappa è stata spostata dall'utente (alla prossima richiesta di punti, chiederò info solo sui punti visualizzati)
	private final int MAP_REQUEST_WAITING = 103 ;	//Inizializzo la richiesta di WAITING, che non è immadiata, ma sarà soggetto ad un Timer
	private final int MAP_LOCKED = 102 ;		//la Mappa è loKKata, ossia mostra solo i marker dello zoom precedentemente fatto 
    //*****************************************************
  	//FINE STATI DELLA MAPPA
	//*****************************************************
	//private final int SEC_BEFORE_WAITING=10;			//numero di secondi per il quale l'utente può operare prima che la mappa vada in stato waiting
	//private int mSec_Counted_Before_Waiting=-1;		//numero di secondi trascorsi per intercettare il waiting //-1 per resettare
	
	
	//*****************************************************
	//NOTIFICHE APP
	//*****************************************************
	NotificationManager notificationManager;
	int mNotificationId=0;
	//*****************************************************
	//FINE NOTIFICHE APP
	//*****************************************************
	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Progress BAR sul titolo
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_track);
		
		//SCHERMO SEMPRE ACCESO
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//DEFINISCO IL MIO HANDLER
		setMyHandler();

		//CONTROLLO se ho i requisiti per utilizzare la mappa (Google Play Service)
		if (playServiceCheck())
		{
			/*
			//TIMER PER LA GESTIONE DEL MAP_REQUEST_WAITING
			mTimerWaiting = new Timer();
			//Set the schedule function and rate
			mTimerWaiting.scheduleAtFixedRate(new TimerTask() {
			    @Override
			    public void run() {
			        OnTimer();
			    }
			         
			},0,1000);*/
			 
			 
			//fornisco al Session Manager il mio Handler e l'istanza corrente della classe
			MyApplication.getSessionManager().setTrackingHandler(mHandler);
			MyApplication.getSessionManager().setTrackingActivityIstance(this);
			
			//Riferimenti agli oggetti della activity
			txtNomeGara=(TextView) findViewById(R.id.txtNomeGara);
	        imgOnMap=(ImageView) findViewById(R.id.imgOnMap);
	        dc=(CustomDigitalClock) findViewById(R.id.dgtOrario);
	        
			//map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapTrack)).getMap();
	        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTrack)).getMap();
			
	        //Settaggi iniziali della Mappa
	        mMapAutoAdapt=true;
	        setMap();
	        
	        //quando la App nasce, la Mappa è in stato FREE
			changeMapState(MAP_FREE);
			
			
			
			//*****************************************************
			//NOTIFICHE APP
			//*****************************************************
			Intent intent = new Intent(this, TrackActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
			
			
			
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle("Moto Tracking")
			        .setContentText("Applicazione in esecuzione!")
			        .setDefaults(Notification.FLAG_NO_CLEAR)
			        .setContentIntent(pIntent);

			notificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(mNotificationId, mBuilder.build());
			//*****************************************************
			//FINE NOTIFICHE APP
			//*****************************************************
		}
		else
		{
			Toast.makeText(MyApplication.getAppContext(), getResources().getString(R.string.playServiceError), Toast.LENGTH_LONG).show();
		}
	}
	
	/*
	protected void OnTimer() {
		
		mSec_Counted_Before_Waiting--;
		if (mSec_Counted_Before_Waiting==0)
		{
			changeMapState(MAP_WAITING);
		}
		
		Log.d(LOG_TITLE,"TIMER:" + mSec_Counted_Before_Waiting);
		
	}
	
	private void resetTimerWaiting()
	{
		mSec_Counted_Before_Waiting=-1;
	}
	
	private void startTimerWaiting()
	{
		mSec_Counted_Before_Waiting=SEC_BEFORE_WAITING;
		Log.d(LOG_TITLE, "startTimerWaiting");
	}
	*/


	//Setto la Mappa
    private void setMap()
    {
    	if (map!=null)
    	{
	    	map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.setMyLocationEnabled(true);
			
			map.getUiSettings().setZoomControlsEnabled(false);
			map.getUiSettings().setCompassEnabled(true);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			
			
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
	            @Override
	            public void onCameraChange(CameraPosition position) {
	                //Log.d("onCameraChange","CAMERA CHANGE");
	        		//Toast.makeText(MyApplication.getAppContext(), "onCameraChange", Toast.LENGTH_SHORT).show();
	            	Log.d(LOG_TITLE, "onCameraChange");
	        		
	        		/*if (position.zoom != mCurrentZoom){
	                    mCurrentZoom = position.zoom;
	                    Log.d("onCameraChange","ZOOM CAMBIATO");
	            		//Toast.makeText(MyApplication.getAppContext(), "ZOOM CAMBIATO", Toast.LENGTH_SHORT).show();
	                }*/
	        		
	        		//ho spostato oppure Zoommato sulla Mappa
	        		//deve andare in stato Waiting
	        		changeMapState(MAP_WAITING);
	        		//changeMapState(MAP_REQUEST_WAITING);
	            }
	        });
			
			map.setOnMapClickListener(new OnMapClickListener() {
		        @Override
				public void onMapClick(LatLng arg0) {
					//Toast.makeText(MyApplication.getAppContext(), "onMapClick", Toast.LENGTH_SHORT).show();
		        	Log.d(LOG_TITLE, "onMapClick");
					
					//Alla prossima richiesta voglio tutti i Marker
					mIdRuoliInGaraDaFiltrare="";
					
					//resetTimerWaiting();
					
					//faccio subito la richiesta perche al Tap non voglio attendere la temporizzazione del Thread
					//mHandler.sendEmptyMessage(GET_TRACKING);
					MyApplication.getSessionManager().Track();
					
				}
		    }); 
			
    	}
    	else
    	{
    		Toast.makeText(MyApplication.getAppContext(), "Per utilizzare l'applicazione devi avere i Google Play Service installati", Toast.LENGTH_LONG).show();
    		finish();
    	}
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
	                	
	                	//se ho filtrato i luoghi nella mappa, questa andrà in stato Locked
	                	if (!mIdRuoliInGaraDaFiltrare.equals(""))
	                	{
	                		changeMapState(MAP_LOCKED);
	                	}
	                	else
	                	{
	                		changeMapState(MAP_FREE);
	                	}
	                	
	                	CheckGaraStatus();
	                	DisegnaMarker();
	                	MostraLoading(false);
	                    break;
	                case START_LOAD:
	                	//Log.d(LOG_TITLE,"HANDLE: START LOAD");
	                	MostraLoading(true);
	                    break;
	                case ABORT_LOAD:
	                	//Log.d(LOG_TITLE,"HANDLE: ABORT LOAD");
	                	MostraLoading(false);
                        break;
                }
            }
	    };
	}

	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_LogOut:
        	LogOut();
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
        
    }
	
	@Override
    protected void onStart() {
    	super.onStart();
    	
    	 //RilevaCoordinateUtente(true);
    }
	
	
	 @Override
	 protected void onResume() {
	    super.onResume();
	    
	    if (playServiceCheck())
	    {
		    //L'activity viene Riesumata, è in primo piano
		    mIsInPrimoPiano=true;
		    
		    //NOME DELLA GARA sull'apposita Text
		    txtNomeGara.setText(MyApplication.getSessionManager().getNomeGara());
	
		    if (MyApplication.VerificaConnessione())
	        {
	    		MyApplication.getSessionManager().StartTrack();
	        }
	    	else
	    	{
	    		AlertDialog.Builder dialog = new AlertDialog.Builder(MyApplication.getAppContext());
	    		dialog.setTitle(R.string.Avviso);
	    		dialog.setMessage(R.string.MsgAvvisoConnessione);
	    		dialog.setCancelable(true);
	     		
	    		 // On pressing Settings button
	    		/*dialog.setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog,int which) {
	                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
	                    mContext.startActivity(intent);
	                }
	            });*/
	    		
	     		// on pressing cancel button
	    		dialog.setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.cancel();
	                	finish();
	                }
	            });
	     		
	     		dialog.show();
	    	}
	    }
	    else
	    {
	    	
	    }
	 }
	 
	 
	 @Override
	 protected void onPause() {
	    super.onPause();
	    
	    //L'activity viene chiusa, non è iun primo piano
	    mIsInPrimoPiano=false;
	    
	    //***************************************************************
	    //SE IL THREAD SI DEVE STOPPARE QUANDO L'ACTIVITY VA IN PAUSE
	    //decommentare mContinueThread=false;
	    //***************************************************************
	    //variabile a false per stoppare il thread
	    //mContinueThread=false;
	    //***************************************************************
	    //FINE SE IL THREAD SI DEVE STOPPARE QUANDO L'ACTIVITY VA IN PAUSE
	    //***************************************************************
	 }
	 
	 @Override
	 public void onBackPressed() {
	    //alla pressione del tasto back devo fare il Logout
		//Log.d(LOG_TITLE, "onBackPressed Called");
	    LogOut();
	 }
	
	 
	 private void LogOut()
	 {
		notificationManager.cancel(mNotificationId);
     	
		MyApplication.getSessionManager().LogOut();
		
     	//STOPPO IL THREAD
     	//variabile a false
 	    //mContinueThread=false;
 	    
 	    //mTimerWaiting.cancel();
     	
 	    //RESETTO L'applicazione
     	//MyApplication.getSessionManager().resetApplication();
     	
     	//Torno al Login
     	if (mIsInPrimoPiano)
     	{
     		GoToLoginActivity();
     	}
	 }
	 
	 private void changeMapState(int Stato)
    {
    	switch (Stato)
    	{
    		case MAP_FREE:
    			MAP_STATE=MAP_FREE;
    			
    			//metto la apposita iconcina (trasparente)
    			imgOnMap.setImageDrawable(getResources().getDrawable(R.drawable.transparent));
    			break;
    		case MAP_REQUEST_WAITING:
    			MAP_STATE=MAP_REQUEST_WAITING;
    			/*
    			//la mappa va in stato di waiting, quando viene spostata dall'utente
    			//se si sposta per suoi scopi non ci va'
    			Log.d(LOG_TITLE, "changeMapState-MAP_REQUEST_WAITING: mMapAutoAdapt:" + mMapAutoAdapt);
    			if (!mMapAutoAdapt)
    			{
    				startTimerWaiting();
    			}
    			mMapAutoAdapt=false;*/
    			break;
    		case MAP_WAITING:
    			//la mappa va in stato di waiting, quando viene spostata dall'utente
    			//se si sposta per suoi scopi non ci va'
    			if (!mMapAutoAdapt)
    			{
    				MAP_STATE=MAP_WAITING;
    				//metto la apposita iconcina
    				imgOnMap.setImageDrawable(getResources().getDrawable(R.drawable.clock));
    			
    				//CERCO I PUNTI MOSTRATI SULLA MAPPA IN QUESTO MOMENTO
    				GetMarkerShowedOnMap();
    			}
    			mMapAutoAdapt=false;
    			break;
    		case MAP_LOCKED:
    			MAP_STATE=MAP_LOCKED;
    			//metto la apposita iconcina
    			imgOnMap.setImageDrawable(getResources().getDrawable(R.drawable.lock));
    			break;
    	}
    }
	 
	 protected void CheckGaraStatus()
	{
		//se dopo aver stoppato il thread ho ancora richieste pendenti, viene ancora fatto
		//questo controllo dopo che il Logout è stato fatto.
		//controllo quindi che il Logout non sia già avvenuto
		if (MyApplication.getSessionManager().IsLogged())
	  	{
			if (!MyApplication.getSessionManager().getTracking().getStatus().equals(Gara.STATE_RUNNING))
		  	{
		  		Toast.makeText(MyApplication.getAppContext(), R.string.GaraTerminata, Toast.LENGTH_LONG).show();
		  		LogOut();
		  	}
	  	}
	}
	 
	private void GoToLoginActivity()
  	{
  		Intent intent=new Intent(MyApplication.getAppContext(),LoginActivity.class);
  		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
  	}
	 
	public void MostraLoading(Boolean Visualizza)
  	{
  		//MOSTRA O NASCONDE L'ICONA DI LOADING DALL'APPLICAZIONE
  		setProgressBarIndeterminateVisibility(Visualizza);
  	}
	
	@SuppressLint("NewApi")
	private void DisegnaMarker()
  	{
		//se l'activity è in primo piano
		if (mIsInPrimoPiano)
		{
			//Log.d(LOG_TITLE,"    Disegno i Marker");
			
			//pulisco la mappa dai marker attuali
			map.clear();
			//pulisco l'HashMap con i marker visualizzati sulla mappa
			markers.clear();
			
			if (((MyApplication.getSessionManager().getLatitudine()!=0)&&(MyApplication.getSessionManager().getLatitudine()!=0))||(MyApplication.getSessionManager().getTracking().getTrackingList().size()>0))
	        {
	
		        //DISEGNO I TRACK
		  		if (MyApplication.getSessionManager().getTracking().getTrackingList().size()>0)
		  		{
			        for(Tracking track : MyApplication.getSessionManager().getTracking().getTrackingList())
			        {
			        	int idGraphicResources = getResources().getIdentifier("it.xtremesoftware.tracking:drawable/" + track.getCodRuolo().toLowerCase(Locale.ITALY),null,null);
			        	final LatLng Coordinate = new LatLng(track.getLatitudine(), track.getLongitudine());
			        	//Alpha
			        	int alpha=GetAlphaForTracking(track);
			        	
			        	//CREO UNA VISTA RUNTIME DA USARE COME MARKER
			        	View MioMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
			    		TextView txtMarker = (TextView) MioMarkerView.findViewById(R.id.txtMarker);
			    		String progressivo;
			    		if (track.getProgressivo()==0)
			    		{
			    			progressivo="";
			    		}
			    		else
			    		{
			    			progressivo=track.getProgressivo().toString();
			    		}
			    		txtMarker.setText(progressivo);
			    		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
			    		{
			    			txtMarker.setAlpha(alpha);
			    		}
			    		ImageView imgMarker=(ImageView) MioMarkerView.findViewById(R.id.imgMarker);
			    		imgMarker.setImageResource(idGraphicResources);
			    		imgMarker.getDrawable().setAlpha(alpha);
			    		//FINE CREO UNA VISTA RUNTIME DA USARE COME MARKER
			    		
			    		//Log.d(LOG_TITLE,"DISEGNO MARKER: " + track.getCodRuolo() + track.getProgressivo() + " - ALPHA:" + alpha);
			    		
			    		
			    		
			    		Marker mioMarker = map.addMarker(new MarkerOptions()
			    		.position(Coordinate)
			    		.title(track.getCodRuolo())
				        .snippet(track.getLatitudine() + " " + track.getLongitudine())
			    		.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this,MioMarkerView))));
			        	
			        	
			  	        //builder.include(Coordinate);
			  	    	markers.put(mioMarker, track);
			        }
			    }
		  		
		        fitZoomAndPositionToMapByMarkers();
	        }
			
		}
  	}
	
	private int GetAlphaForTracking(Tracking track)
	{
		int alpha=255;
    	switch(track.getSecReliability())
    	{
    		case(0):
    			alpha=255;
    			break;
    		case(1):
    			alpha=153;
    			break;
    		case(2):
    			alpha=51;
    			break;
    	}
    	
    	return alpha;
	}
	
	
	// Convert a view to bitmap (per la generazione dei Marker)
	public static Bitmap createDrawableFromView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.buildDrawingCache();
		
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
 
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
 
		return bitmap;
	}
	
	
	protected void GetMarkerShowedOnMap() {
		mIdRuoliInGaraDaFiltrare="";
	    LatLngBounds bounds2 = map.getProjection().getVisibleRegion().latLngBounds;
	    for (Map.Entry<Marker, Tracking> ma : markers.entrySet())
	    {
	        //Log.d(LOG_TITLE,"Ciclo Marker: " + ma.getKey() + "/" + ma.getValue() + " - " + ma.getKey().getTitle());
	        if (bounds2.contains(ma.getKey().getPosition()))
        	{
        		//Log.d("BBBBB",ma.getKey().getTitle() + " NELLA MAPPA");
        		if (mIdRuoliInGaraDaFiltrare.equals(""))
        		{
        			mIdRuoliInGaraDaFiltrare="(" + ma.getValue().getIdRuoloGara().toString();
        		}
        		else
        		{
        			mIdRuoliInGaraDaFiltrare+= "," + ma.getValue().getIdRuoloGara();
        		}
            }
        	else
        	{
        		//Log.d("BBBBB",ma.getKey().getTitle() + " FUORI MAPPA");
        	}
	    }
	    if (!mIdRuoliInGaraDaFiltrare.equals(""))
	    {
	    	mIdRuoliInGaraDaFiltrare+= ")";
	    }
	}
	
	private Boolean playServiceCheck()
	{
		if (!mPlayServiceOK)
		{
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
	  	  
			if (resultCode == ConnectionResult.SUCCESS){
				//Toast.makeText(MyApplication.getAppContext(), "isGooglePlayServicesAvailable SUCCESS", Toast.LENGTH_SHORT).show();
				mPlayServiceOK=true;
			}
			else
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
				mPlayServiceOK=false;
			}
		}
		else
		{
			mPlayServiceOK=true;
		}
		return mPlayServiceOK;
	}
	
	
	
	public void fitZoomAndPositionToMapByMarkers() {
		//SE L'UTENTE STA MUOVENDO LA MAPPA. ALLORA NON RIADATTO LO ZOOM AI MARKER
		//if (MAP_STATE!=MAP_REQUEST_WAITING) 
		//{
		    int minLat = Integer.MAX_VALUE;
		    int maxLat = Integer.MIN_VALUE;
		    int minLon = Integer.MAX_VALUE;
		    int maxLon = Integer.MIN_VALUE;
		    
		    Log.d(LOG_TITLE, "-------------------");
		    Log.d(LOG_TITLE, "MAP_STATE:" + MAP_STATE);
		    
		    //CICLO TUTTI I MARKER PRESENTI SULLA MAPPA
		    for (Map.Entry<Marker, Tracking> ma : markers.entrySet())
		    {
		        int lat = (int) (ma.getValue().getLatitudine() * 1E6);
		        int lon = (int) (ma.getValue().getLongitudine() * 1E6);
		        
		        //I MArker con rielability 2 non partecipano alla definizione dello Zoom sulla Mappa
		        //a meno che la mappa non sia in stato MAP_LOCKED o MAP_WAITING
		        //Log.d(LOG_TITLE, ma.getValue().getCodRuolo() + ma.getValue().getProgressivo() + ": " + ma.getValue().getSecReliability());
		        if ((ma.getValue().getSecReliability()!=2)||((MAP_STATE==MAP_LOCKED)||(MAP_STATE==MAP_WAITING)))
		        {
			        maxLat = Math.max(lat, maxLat);
			        minLat = Math.min(lat, minLat);
			        maxLon = Math.max(lon, maxLon);
			        minLon = Math.min(lon, minLon);
			        //Log.d(LOG_TITLE,"S");
			    }
		        else
		        {
		        	//Log.d(LOG_TITLE,"N");
		        }
		    }
		    
		    
		    //Aggiungo i valori della mia posizione
		    if ((MyApplication.getSessionManager().getLatitudine()!=0)&&(MyApplication.getSessionManager().getLongitudine()!=0))
		    {
			    int lat = (int) (MyApplication.getSessionManager().getLatitudine() * 1E6);
			    int lon = (int) (MyApplication.getSessionManager().getLongitudine() * 1E6);
			    maxLat = Math.max(lat, maxLat);
		        minLat = Math.min(lat, minLat);
		        maxLon = Math.max(lon, maxLon);
		        minLon = Math.min(lon, minLon);
		    }
	
		    LatLng southWestLatLon = new LatLng(minLat / 1E6, minLon / 1E6);
		    LatLng northEastLatLon = new LatLng(maxLat / 1E6, maxLon / 1E6);
		    
		    int padding=80;
		    //map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southWestLatLon, northEastLatLon), 50));
		    mMapAutoAdapt=true;
		    map.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southWestLatLon, northEastLatLon), padding));
		//}
	}



}

