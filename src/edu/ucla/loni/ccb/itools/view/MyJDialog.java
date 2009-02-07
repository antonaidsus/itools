package edu.ucla.loni.ccb.itools.view;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JOptionPane;

import com.l2fprod.common.swing.BaseDialog;



/**
 * A subclass of JDialog displaying in the middle of the main frame
 *
 * @author <A HREF="mailto:qma@loni.ucla.edu">Jeff Ma </A>
 **/

public class MyJDialog extends BaseDialog {
    private Window owner = null;
    /**
     * Constructs a dialog initialized with an owner, a  title and its modality.
     */
    public MyJDialog(Frame f, String title, boolean modal) {
        super(f,title,modal);
    }

    public MyJDialog(Dialog f, String title, boolean modal) {
        super(f,title,modal);
    }
    
    public MyJDialog(Component component) {
    	super(JOptionPane.getFrameForComponent(component));
        
    }
    public MyJDialog(Frame f) {
        super(f);
    }
    
    public MyJDialog(Dialog f) {
        super(f);
    }
            
    public void setOwner(Window owner) {
        this.owner = owner;
    }
    
    public MyJDialog getInstance(Component comp, boolean modal) {
        MyJDialog rv = null;
        if (comp instanceof Frame) {
            rv = new MyJDialog((Frame)comp, "", modal);
        } else if (comp instanceof Dialog) {
            rv = new MyJDialog((Dialog)comp, "", modal);
        } else {
            rv = new MyJDialog(JOptionPane.getFrameForComponent(comp),"", modal);
        }
        return rv;
    }

    public void setVisible(boolean visible) {
    	if (visible) {
    		Window _f = (owner == null) ? getOwner() : owner;
    		if ( _f != null && _f.isShowing()){
    			int x=_f.getLocationOnScreen().x+_f.getSize().width/2- getSize().width/2;
    			int y=_f.getLocationOnScreen().y+_f.getSize().height/2-getSize().height/2;
    			setLocation(x,y);
    		}
    		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    		
    		if (!new Rectangle(d).contains(this.getBounds())) {
    			setLocation(d.width / 2, d.height / 2);
    		}
    	}
    	super.setVisible(visible);
    }
}
