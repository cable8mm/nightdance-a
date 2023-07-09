package kr.co.nightdance.nightdancea.utils;

import java.util.Observable;

class CustomObservable extends Observable {
	// To force notifications to be sent
	public void notifyObservers(Object data) {
		setChanged();
		super.notifyObservers(data);
	}
}