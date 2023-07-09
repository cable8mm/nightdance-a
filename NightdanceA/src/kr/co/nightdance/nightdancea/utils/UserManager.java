package kr.co.nightdance.nightdancea.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserManager {
	public static final String TAG = "UserManager";
	private SharedPreferences _sharedPrefs;
	private static String UM_PREF_NAME	= "user_default";
	private boolean _isLogin	= false;
	private int _userId	= -1;
	private int _userNcash	= -1;
	private String _nickname	= null;
	private Date _expiryDate	= null;
	public static final String kUM_USER_ID	= "kUM_USER_ID";
	public static final String kUM_USER_NCASH	= "kUM_USER_NCASH";
	public static final String kUM_NICKNAME	= "kUM_NICKNAME";
	public static final String kUM_EXPIRY_DATE	= "kUM_EXPIRY_DATE";
	public static final String kUM_SCRAPED_CLIPS	= "kUM_SCRAPED_CLIPS";
	public static final String kUM_VIEWED_SCRAPING_CLIP	= "kUM_VIEWED_SCRAPING_CLIP";
	
	// http://iilii.egloos.com/viewer/3807664
	private static UserManager userManager;
	private Context _context;
	public static synchronized UserManager getInstance(Context ctx) {
		if(userManager == null) {
			userManager	= new UserManager(ctx);
		}
		return userManager;
	}
	public static synchronized UserManager getInstance() {
		return userManager;
	}
	private UserManager(Context ctx){
		_context	= ctx;
    	_sharedPrefs	= _context.getSharedPreferences(UserManager.UM_PREF_NAME, Context.MODE_PRIVATE);
	}
	
	public boolean isLogin() {
		return _isLogin;
	}
	
	public void login(int userId, int userNcash, String nickname, String ticketExpired) {
		Log.i(TAG, "userId = "+userId+", userNcash="+userNcash+", nickname="+nickname+", ticketExpired="+ticketExpired);
		_isLogin	= true;
		this.setUserId(userId);
		this.setUserNcash(userNcash);
		this.setNickname(nickname);
		this.setExpiryDate(ticketExpired);
		ObservingService.getInstance().postNotification("LOG_UPDATED", true);
	}
	
	public void logout() {
		this.setUserId(-1);
		this.setUserNcash(-1);
		this.setNickname(null);
		this.setExpiryDate((Date)null);
		_isLogin	= false;
		Log.i(TAG, "logout / userId = "+getUserid()+", userNcash = "+getUserNcash()+", nickname = "+getNickname());
		ObservingService.getInstance().postNotification("LOG_UPDATED", false);
	}
	
	public int getUserid() {
		if(_userId == -1) {
			return _sharedPrefs.getInt(UserManager.kUM_USER_ID, -1);
		}
		return _userId;
	}

	public int getUserNcash() {
		if(_userNcash == -1) {
			return _sharedPrefs.getInt(UserManager.kUM_USER_NCASH, -1);
		}
		return _userNcash;
	}

	public String getCommaUserNcash() {
		int userNcash	= getUserNcash();
		NumberFormat formatter = new DecimalFormat("###,###,###");
		return formatter.format(userNcash) + " 코인";
	}
	
	public String getNickname() {
		if(_nickname == null) {
			return _sharedPrefs.getString(UserManager.kUM_NICKNAME, null);
		}
		return _nickname;
	}

	public Date getExpiryDate() {
		if(_expiryDate == null) {
			return null;
		}
		long longDate	= _sharedPrefs.getLong(UserManager.kUM_EXPIRY_DATE, 0);
		_expiryDate = new Date(longDate);
		return _expiryDate;
	}
	
	public String getExpiryDateString() {
		Date expiryDate	= getExpiryDate();
		Locale current = _context.getResources().getConfiguration().locale;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", current);
		// Using DateFormat format method we can create a string 
		// representation of a date with the defined format.
		String expiryDateString = df.format(expiryDate);
		float remainingOnSubscription	= daysRemainingOnSubscription();
		
		return "유효 기간 : " + expiryDateString + "\n(약 " + remainingOnSubscription + " 일 남음)";
	}
	
	public float daysRemainingOnSubscription() {
		float days	= 0.f;
		Date expiryDate	= this.getExpiryDate();
		Date now	= new Date();
		
		if(expiryDate == null) {
			return days;
		}
		
		long diffInMin = expiryDate.getTime() - now.getTime();
	    days = (float)(diffInMin / 1000 / 60 / 60 / 24);
	    
	    return days >= 0.f? days : 0.f;
	}

	public String getScrapedClipIdsString() {
		return _sharedPrefs.getString(kUM_SCRAPED_CLIPS, null);
	}
	
	public int[] getScrapedClipIds() {
	    String scrapedClips = _sharedPrefs.getString(kUM_SCRAPED_CLIPS, null);
	    Log.i(TAG, "getScrapedClipIds = " + scrapedClips);
	    if(scrapedClips == null) {
	    	return null;
	    }
	    
	    String[] scrapedClipArray	= scrapedClips.split(",");
	    
	    if(scrapedClipArray.length == 0) {
	    	return null;
	    }
	    
	    int[] intScrapedClipArray	= new int[scrapedClipArray.length];
	    for(int i=0; i < scrapedClipArray.length; i++) {
	    	intScrapedClipArray[i]	= Integer.parseInt(scrapedClipArray[i]);
	    }
	    
	    return intScrapedClipArray;
	}

	public boolean isScrapedClip(int clipId) {
	    String scrapedClips = _sharedPrefs.getString(kUM_SCRAPED_CLIPS, null);

	    if(scrapedClips == null) {
	    	return false;
	    }
	    
	    String[] scrapedClipArray	= scrapedClips.split(",");
	    
	    for(int i=0;i<scrapedClipArray.length;i++) {
	    	if(scrapedClipArray[i].equals(clipId+"") == true) {
	    		return true;
	    	}
	    }

	    return false;
	}
	
	public void removeScrapedClip(int clipId) {
	    String scrapedClips = _sharedPrefs.getString(kUM_SCRAPED_CLIPS, null);
	    SharedPreferences.Editor editor = _sharedPrefs.edit();  

	    if(scrapedClips == null) {
	    	return;
	    }
	    
	    String[] scrapedClipArray	= scrapedClips.split(",");
	    String newScrapedClips	= "";
	    
	    for(int i=0;i<scrapedClipArray.length;i++) {
	    	if(scrapedClipArray[i].equals(clipId+"") == false) {
	    		if(newScrapedClips.equals("") == false) {
	    			newScrapedClips	= newScrapedClips + ",";
	    		}
	    		newScrapedClips	= newScrapedClips + scrapedClipArray[i];
	    	}
	    }
	    
	    editor.putString(kUM_SCRAPED_CLIPS, newScrapedClips);  
	    editor.commit();
	    Log.i(TAG, "removeScrapedClip = " + newScrapedClips);

	    return;
	}
	
	public void addScrapedClipId(int clipId) {
	    String scrapedClips = _sharedPrefs.getString(kUM_SCRAPED_CLIPS, null);
	    SharedPreferences.Editor editor = _sharedPrefs.edit();
	    
	    if(scrapedClips == null) {
		    editor.putString(kUM_SCRAPED_CLIPS, clipId+"");
		    editor.commit();
	    	return;
	    }
	    
	    String[] scrapedClipArray	= scrapedClips.split(",");
	    
	    for(int i=0;i<scrapedClipArray.length;i++) {
	    	if(scrapedClipArray[i].equals(clipId+"")) {
	    		return;
	    	}
	    }
	    
	    String addScrapedClips	= scrapedClips + "," + clipId;
	    editor.putString(kUM_SCRAPED_CLIPS, addScrapedClips);  
	    editor.commit();
	    Log.i(TAG, "addScrapedClipId = " + addScrapedClips);
//	    [UserManager setViewedScrapingClip:YES];
	    setViewedScrapingClip(true);
	    return;
	}
	
	public void setViewedScrapingClip(boolean b) {
	    SharedPreferences.Editor editor = _sharedPrefs.edit();
	    editor.putBoolean(kUM_VIEWED_SCRAPING_CLIP, b);
	    editor.commit();
	}
	
	public boolean isViewedScrapingClip() {
		return _sharedPrefs.getBoolean(kUM_VIEWED_SCRAPING_CLIP, false);
	}
	
	public void setUserId(int v) {
	    SharedPreferences.Editor editor = _sharedPrefs.edit();
		_userId	= v;
		editor.putInt(UserManager.kUM_USER_ID, v);
		editor.commit();
	}

	public void setUserNcash(int v) {
	    SharedPreferences.Editor editor = _sharedPrefs.edit();
		_userNcash	= v;
		editor.putInt(UserManager.kUM_USER_NCASH, v);
		editor.commit();
	}

	public void setNickname(String v) {
	    SharedPreferences.Editor editor = _sharedPrefs.edit();
		_nickname	= v;
		if(v == null) {
			editor.remove(UserManager.kUM_NICKNAME);
		} else {
			editor.putString(UserManager.kUM_NICKNAME, v);
		}
		editor.commit();
	}
	
	public void setExpiryDate(Date v) {
	    SharedPreferences.Editor editor = _sharedPrefs.edit();
		_expiryDate	= v;
		if(v == null) {
			editor.remove(UserManager.kUM_EXPIRY_DATE);
		} else {
			editor.putLong(UserManager.kUM_EXPIRY_DATE, v.getTime());
		}
		editor.commit();
	}
	
	public void setExpiryDate(String expiryDateString) {
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		try {
			Date expiryDate	= new Date();
			expiryDate	= dateFormat.parse(expiryDateString);
			this.setExpiryDate(expiryDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
