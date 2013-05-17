package it.xtremesoftware.tracking.Util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;

public class DeviceInfo {
	
	
	
	private String PhoneID="";
	private String SoVersion="";
	private String VersionName="";
	private int VersionCode=0;
	
	public String getPhoneID() {
		return PhoneID;
	}


	public String getSoVersion() {
		return SoVersion;
	}


	public String getVersionName() {
		return VersionName;
	}


	public int getVersionCode() {
		return VersionCode;
	}


	
	
	public DeviceInfo() {
		//VALORIZZO INFO SUL DISPOSITIVO
        PhoneID=Secure.getString(MyApplication.getAppContext().getContentResolver(), Secure.ANDROID_ID);
        SoVersion="Android" + android.os.Build.VERSION.SDK_INT;
        
        PackageInfo pinfo = null;
		try {
			pinfo = MyApplication.getAppContext().getPackageManager().getPackageInfo(MyApplication.getAppContext().getPackageName(), 0);
			VersionCode = pinfo.versionCode;
		    VersionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
}
