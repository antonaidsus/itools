/**
 * created on August 24, 2005, $ID$
 */
package edu.ucla.loni.ccb.itools.action;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Helper class. The default action will invoked at a new thread.
 * Subclass should override the doAction(). If it needs be run at
 * the same thread, invoke doAction().
 * 
 * @author <A HREF="mailto:qma@loni.ucla.edu">Jeff Ma </A>
 *
 * @version August 24, 2005
 */
public abstract class MyAction extends AbstractAction {
    /** Logger for this class*/
    public static final Logger LOGGER = Logger.getLogger(MyAction.class);
    public static final String SOURCENAME = "MyAction.sourcename";

    /**
     * Constructor
     * @param name the name of this action.
     */
    public MyAction(String name){
        super(name);        
    }
    
    public MyAction(){        
    }
    
    public String getName() {
        return (String)getValue(AbstractAction.NAME);
    }
    
    /** 
     * Do the actual work, subclass should be override this method.
     */
    public void doAction(){};

    public void actionPerformed(ActionEvent e) {
        actionAtNewThread();
    }
    
    /** 
     * run the action on a new thread.
     */
    public void actionAtNewThread() {
        new Thread() {
            public void run() {
                doAction();
            }
        }.start();
    }
}
