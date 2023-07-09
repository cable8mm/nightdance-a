package kr.co.nightdance.nightdancea.utils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Observer;

/*
http://stackoverflow.com/questions/10327200/equivalent-of-ios-nsnotificationcenter-in-android
*/
public class ObservingService {
	private static ObservingService observingService;

	public static synchronized ObservingService getInstance() {
		if(observingService == null) {
			observingService	= new ObservingService();
		}
		return observingService;
	}

    public HashMap<String, CustomObservable> observables;

    private ObservingService() {
        observables = new HashMap<String, CustomObservable>();
    }

    public void addObserver(String notification, Observer observer) {
    	CustomObservable observable = observables.get(notification);
        if (observable==null) {
            observable = new CustomObservable();
            observables.put(notification, observable);
        }
        observable.addObserver(observer);
    }

    public void removeObserver(String notification, Observer observer) {
    	CustomObservable observable = observables.get(notification);
        if (observable!=null) {         
            observable.deleteObserver(observer);
        }
    }       

    public void postNotification(String notification, Object object) {
    	CustomObservable observable = observables.get(notification);
        if (observable!=null) {
        	Hashtable<String,Object> dic	= new Hashtable<String,Object>();
        	dic.put("key", notification);
        	dic.put("userInfo", object);
            observable.notifyObservers(dic);
        }
    }
}