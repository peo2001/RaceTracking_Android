package it.xtremesoftware.tracking.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Calendar;
import java.util.TimeZone;



public class DateHelper {
	
	// DEFINISCO PROPRIETA'
	private static String format = "dd/MM/yyyy HH:mm:ss";
	private static String simpleFormat = "dd/MM/yyyy";
	private static String bakFormat = "yyyy/MM/dd HH:mm:ss";


	// CONVERSIONE FORMATO DATA --> STRINGA
	public String dateToString(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	
	// FUNZIONE PER IL RECUPERO ORA FORMATO DATABASE
	public String timeToUsr(String time) {
		String returnValue = time.substring(0,2) + ":" + time.substring(2);
		return returnValue;
	}
	
	public String dateCompletaItaToString(Date date) {
		return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.ITALY).format(date);
	}
	

	// FUNZIONE PER IL RECUPERO ORA FORMATO DATABASE
	public String timeToDbf(String time) {
		String returnValue = time.substring(0,2) + time.substring(3);
		return returnValue;
	}	
	
	
	// FUNZIONE PER IL RECUPERO DELLA DATA ATTUALE FORMATO STRINGA UTENTE
	public String dateNowToUsr(){
		Date today = new Date();
		String formatoData = "dd/MM/yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(formatoData);
		String dataOdierna = formatter.format(today);
		return dataOdierna;
	}
	
	
	// FUNZIONE PER IL RECUPERO DELLA DATA ATTUALE FORMATO DATABASE
	public String dateNowToDbf(){
		Date today = new Date();
		String formatoData = "yyyyMMdd";
		SimpleDateFormat formatter = new SimpleDateFormat(formatoData);
		String dataOdierna = formatter.format(today);
		return dataOdierna;
	}
	
	
	// FUNZIONE PER IL RECUPERO DELL'ORA ATTUALE FORMATO STRINGA UTENTE
	public String timeNowToUsr(){
		Date now = new Date();
		String formatoOra = "HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(formatoOra);
		String oraAttuale = formatter.format(now);
		return oraAttuale;
	}
	
	
	// FUNZIONE PER IL RECUPERO DELL'ORA ATTUALE FORMATO DATABASE
	public String timeNowToDbf(){
		Date now = new Date();
		String formatoOra = "HHmmss";
		SimpleDateFormat formatter = new SimpleDateFormat(formatoOra);
		String oraAttuale = formatter.format(now);
		return oraAttuale;
	}	
	
	
	// FUNZIONE PER IL RECUPERO DELL'ORA ATTUALE FORMATO STRINGA UTENTE
	public String timeNowToUsr(String formatoOra){
		Date now = new Date();
		//String formatoOra = "HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(formatoOra);
		String oraAttuale = formatter.format(now);
		return oraAttuale;
	}
	
	
	// FUNZIONE PER IL RECUPERO DELL'ORA ATTUALE FORMATO DATABASE
	public String timeNowToDbf(String formatoOra){
		Date now = new Date();
		//String formatoOra = "HHmmss";
		SimpleDateFormat formatter = new SimpleDateFormat(formatoOra);
		String oraAttuale = formatter.format(now);
		return oraAttuale;
	}		
	

	// CONVERSIONE FORMATO STRINGA --> DATA
	public Date stringToDate(String date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date dataConvertita = null;
		try {
			dataConvertita = formatter.parse(date);
		}
		catch (ParseException e) {
			System.out.println("ERRORE stringToDate");
		}
		return dataConvertita;
	}
		
		
	// FUNZIONE PER CONVERSIONE DELLA DATA ( non usata )
	public String usrToDbf(String dataUsr, String divisore){
		int i;
		StringTokenizer st = new StringTokenizer(dataUsr,divisore);
		String [] stringheSplittate = new String[st.countTokens()];
		i = 0;
		while (st.hasMoreTokens()) {
		  stringheSplittate[i] = st.nextToken();
			i++;
		}		
		if (stringheSplittate[0].length() == 1){
			stringheSplittate[0] = "0" + stringheSplittate[0];
		}	
		if (stringheSplittate[1].length() == 1){
			stringheSplittate[1] = "0" + stringheSplittate[1];
		}	
		String giorno = stringheSplittate[0];
		String mese = stringheSplittate[1];
		String anno = stringheSplittate[2];
		return anno+mese+giorno;			
	}
	
	
	// FUNZIONE PER LA CONVERSIONE DELLA DATA	
	public String formatDate(String date, String from, String to) { 
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(from); 
			Date d = sdf.parse(date); 
			sdf = new SimpleDateFormat(to); 
			return sdf.format(d); 
		}
		catch(Exception e){
			return "";
		}
	} 
	
	
	// FUNZIONE PER CALCOLO DIFFERENZA TRA DUE DATE
	public int giorniDifferenza(String sdate1, String sdate2, String fmt, TimeZone tz){
		SimpleDateFormat df = new SimpleDateFormat(fmt);
		Date date1  = null;
		Date date2  = null;
		try {
			date1 = df.parse(sdate1); 
			date2 = df.parse(sdate2); 
		}catch (ParseException pe){
			pe.printStackTrace();
		}

		return giorniDifferenza(date1, date2, tz);
	}
	
	//FUNZIONE PER CALCOLO DIFFERENZA TRA DUE DATE
 	public int giorniDifferenza(Date date1, Date date2, TimeZone tz){
 		
 		Calendar cal1 = null; 
 		Calendar cal2 = null;
 		if (tz == null)
 		{
 			cal1=Calendar.getInstance(); 
 			cal2=Calendar.getInstance(); 
 		}
 		else
 		{
 			cal1=Calendar.getInstance(tz); 
 			cal2=Calendar.getInstance(tz); 
 		}
 		
 		// different date might have different offset
 		cal1.setTime(date1);          
 		long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);
 		cal2.setTime(date2);
 		long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET) + cal2.get(Calendar.DST_OFFSET);
 		
 		// Use integer calculation, truncate the decimals
 		int hr1   = (int)(ldate1/3600000); //60*60*1000
 		int hr2   = (int)(ldate2/3600000);
 		int days1 = (int)hr1/24;
 		int days2 = (int)hr2/24;
 		int dateDiff  = days2 - days1;
 		
 		/*
 		 * int weekOffset = (cal2.get(Calendar.DAY_OF_WEEK) - cal1.get(Calendar.DAY_OF_WEEK))<0 ? 1 : 0;
 		int weekDiff  = dateDiff/7 + weekOffset; 
 		int yearDiff  = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
 		int monthDiff = yearDiff * 12 + cal2.get(Calendar.MONTH) -
 		cal1.get(Calendar.MONTH);
 		*/

 		// RITORNA DIFFERENZA DATE
 		return dateDiff;
 	}
 	
 	
 	
 	//FUNZIONE PER CALCOLO DIFFERENZA TRA DUE DATE
 	 	public long millisecondiDifferenza(Date date1, Date date2, TimeZone tz){
 	 		
 	 		Calendar cal1 = null; 
 	 		Calendar cal2 = null;
 	 		if (tz == null)
 	 		{
 	 			cal1=Calendar.getInstance(); 
 	 			cal2=Calendar.getInstance(); 
 	 		}
 	 		else
 	 		{
 	 			cal1=Calendar.getInstance(tz); 
 	 			cal2=Calendar.getInstance(tz); 
 	 		}
 	 		
 	 		// different date might have different offset
 	 		cal1.setTime(date1);          
 	 		long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);
 	 		cal2.setTime(date2);
 	 		long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET) + cal2.get(Calendar.DST_OFFSET);
 	 		
 	 		long dateDiff  = (long)ldate1 - (long)ldate2;

 	 		// RITORNA DIFFERENZA in MILLISECONDI TRA LE DATE
 	 		return dateDiff;
 	 	}


	// FUNZIONE PER AGGIUNGERE / TOGLIERE DEI GIORNI AD UNA DATA 
	public String addDay(String dateDbf,int dayToAdd){
		
		String dataNew = "";
		String DATE_FORMAT = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try{
			Calendar c1 = Calendar.getInstance(); 
			int anno = Integer.parseInt(dateDbf.substring(0,4));
			int mese = Integer.parseInt(dateDbf.substring(4,6))-1;
			int giorno = Integer.parseInt(dateDbf.substring(6,8));
			c1.set(anno, mese, giorno);
			c1.add(Calendar.DATE,dayToAdd);
			dataNew = sdf.format(c1.getTime());
		}catch (Exception e){
			e.printStackTrace();
		}
		// RITORNA DIFFERENZA DATE
		return dataNew;
	}
	
	public String addDay(Date dateDbf,int dayToAdd){
		
		String Data;
		Data=dateToString(dateDbf,"yyyyMMdd");
		
		return addDay(Data,dayToAdd);
	}
	
	
	public Date dataDiIeri()
	{
		Calendar ieri = new GregorianCalendar();
		ieri.add(Calendar.DATE, -1);
	
		return ieri.getTime();
	}
	
	public Date dataDiOggi()
	{
		Calendar oggi = new GregorianCalendar();
		return oggi.getTime();
	}
	
	// FUNZIONE CHE RESTITUISCE LA DATA DA MESE, ANNO, NUMERO SETTIMANA, GIORNO SETTIMANA
	public String getDateFromWeek(String month_S, String year_S,  String week_of_month_S, int day_of_week) {
		String data = "";
		try{
			int month = Integer.parseInt(month_S);
			int year = Integer.parseInt(year_S);
			int week_of_month = Integer.parseInt(week_of_month_S);
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month-1);
			c.set(Calendar.WEEK_OF_MONTH, week_of_month);
			c.set(Calendar.DAY_OF_WEEK, day_of_week);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			data = sdf.format(c.getTime());
		}catch(Exception e){
			data = "";
		}	
	  return data;
	}	
	
	
	// FUNZIONE SOMMA DELLE ORE ( HH ) AD UNA DATA (AAAAMMDD) e UN'ORA (HHMM)
	//RESTITUENDO STRINGA (AAAAMMDD + HHMM)
	public String addHourToDateTime(String date, String hour, int hourToAdd){
		
		// DICHIARAZION VARIABILI
		String hh = "";
		String mm = "";
		int hh_total = 0;
		int hh_new = 0;
		String hh_new_S = "";
		String date_new = "";
		int dayToAdd = 0;
		String returnValue = "";
		
		try{
			// DIVIDO L'ORA IN ORE E MINUTI
			hh = hour.substring(0,2);
			mm = hour.substring(2);
			
			// AGGIUNGO LE ORE
			hh_total = Integer.parseInt(hh) + hourToAdd;
			
			// CALCOLO I GIORNI DA AGGIUUNGERE ALLA DATA
			dayToAdd = hh_total / 24;
			
			// SE HO DA AGGIUNGERE DEI GIORNI
			if(dayToAdd>0){
				// AGGIORNO LA DATA
				date_new = addDay(date,dayToAdd);
			}else{
				date_new = date;
			}
			
			// RECUPERO IL NUOVO ORARIO SE HO DIFFERENZA
			if(hh_total - (dayToAdd * 24)>=0){
				hh_new = hh_total - (dayToAdd * 24); 
				// PREPARO LA STRINGA ORA ( se lunghezza 1 aggiungo uno 0 )
				if(String.valueOf(hh_new).length()==1){
					hh_new_S = "0" + String.valueOf(hh_new);
				}else{
					hh_new_S = String.valueOf(hh_new);
				}
			}else{
				hh_new_S = hh;
			}
			
			// CALCOLO DEL VALORE FINALE DA RESTITUIRE
			returnValue = date_new + hh_new_S + mm;

			return returnValue;

		}catch (Exception e){
			return "";
		}

	}


	// FUNZIONE PER RESTITUIRE IL GIORNO DELLA SETTIMANA DA UNA DATA
	public int getDayWeek(String anno, String mese, String giorno){
		
		// IMPOSTO 0 PER EVENTUALI ERRORI
		int returnValue = 0;
		
		try{
			
			int year = Integer.parseInt(anno);
			// gennaio sarebbe 0, quindi per convenzione mia faccio -1
			int month = Integer.parseInt(mese) - 1;
			int day = Integer.parseInt(giorno);
			
			Calendar newCal = new GregorianCalendar();
			newCal.set(year, month, day);
			// BUG fix in Calendar class!
			newCal.setTime(newCal.getTime());    
			int dayOfWeek = newCal.get(Calendar.DAY_OF_WEEK);
			returnValue = dayOfWeek;
			
		}catch(Exception e){
			returnValue = 0;
		}
		
		
		// 1 = domenica, 2 = lunedì etc...
		return returnValue;
		
	}
	
	
	// FUNZIONE PER RESTITUIRE L'ANNO DA UNA DATA FORMATO STRINGA
	public String getYearFromDate(String data){
		String returnValue = "";
		try{
			returnValue = data.substring(0,4);
		}catch(Exception e){
			
		}
		return returnValue;
	}
	
	
	// FUNZIONE PER RESTITUIRE IL MESE DA UNA DATA FORMATO STRINGA
	public String getMonthFromDate(String data){
		String returnValue = "";
		try{
			returnValue = data.substring(4,6);
		}catch(Exception e){
			
		}
		return returnValue;
	}


	// FUNZIONE PER RESTITUIRE IL GIORNO DA UNA DATA FORMATO STRINGA
	public String getDayFromDate(String data){
		String returnValue = "";
		try{
			returnValue = data.substring(6,8);
		}catch(Exception e){
			
		}
		return returnValue;
	}
	
	
	// FUNZIONE PER CONTROLLARE LA VALIDITA DI UNA DATA IN FORMATO AAAAMMGG
	public boolean validateDateDbf(String dateDbf){
		boolean returnValue = false;
		
		String formatoData = "yyyyMMdd";

		try{
			SimpleDateFormat formatter = new SimpleDateFormat(formatoData);
			formatter.setLenient(false);
			Date dt2 = formatter.parse(dateDbf);
			returnValue = true;
		}
		catch (ParseException e) {
		}
		catch (IllegalArgumentException e){
		}

		return returnValue;
	}
	

	// FUNZIONE PER CONTROLLARE LA VALIDITA DI UNA DATA IN FORMATO AAAAMMGG
	public boolean validateDateUsr(String dateUsr){
		boolean returnValue = false;
		
		String formatoData = "dd/MM/yyyy";

		try {
			SimpleDateFormat formatter = new SimpleDateFormat(formatoData);
			formatter.setLenient(false);
			Date dt2 = formatter.parse(dateUsr);
			returnValue = true;
		}
		catch (ParseException e) {
		}
		catch (IllegalArgumentException e) {
		}

		return returnValue;
	}	
	
		
}
