package edu.ucla.loni.ccb.itools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Controller {
	private static boolean centerCategorized = false;

    private final PropertyChangeSupport support = new PropertyChangeSupport(Main.class);;

    private static final Controller regularInst = new Controller();
    private static final Controller sandboxInst = new Controller();
    
    public static final Controller INST = new Controller();
	public static final String DATA_OF = "DataOf";
	public static final String SELECTED_PATH = "SelectPath";
	public static final String CENTER_CATEGORIZED = "CenterCategorized";
	
	private Controller() {
	}
	
	public static Controller getInstance(boolean sandbox) {
		return sandbox ? sandboxInst : regularInst;
	}
	public boolean isResourceCenterCategorized() {
		return centerCategorized;
	}
	
	public void toggleResourceCenterCategorized() {
		centerCategorized = !centerCategorized;
		firePropertyChange(CENTER_CATEGORIZED, !centerCategorized, centerCategorized);
	}


	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	public boolean equals(Object obj) {
		return support.equals(obj);
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			boolean oldValue, boolean newValue) {
		support.fireIndexedPropertyChange(propertyName, index, oldValue,
				newValue);
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			int oldValue, int newValue) {
		support.fireIndexedPropertyChange(propertyName, index, oldValue,
				newValue);
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			Object oldValue, Object newValue) {
		support.fireIndexedPropertyChange(propertyName, index, oldValue,
				newValue);
	}

	public void firePropertyChange(PropertyChangeEvent evt) {
		support.firePropertyChange(evt);
	}

	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return support.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		return support.getPropertyChangeListeners(propertyName);
	}

	public int hashCode() {
		return support.hashCode();
	}

	public boolean hasListeners(String propertyName) {
		return support.hasListeners(propertyName);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		support.removePropertyChangeListener(propertyName, listener);
	}

	public String toString() {
		return support.toString();
	}
    
}
