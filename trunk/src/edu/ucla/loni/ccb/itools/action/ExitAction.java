package edu.ucla.loni.ccb.itools.action;


public class ExitAction extends MyAction {
	public ExitAction(String name) {
		super(name);
	}
	
	public void doAction() {
		System.exit(0);
	}
}
