package edu.ucla.loni.ccb.itools.view;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * @author jeff.ma
 * This is a JTabbedPane which contains all kinds of Viewer.
 * New Viewer class should be added as a tab to this class.
 * 
 * For iTools, two instances will be created, one for sandbox and the other is for
 * normal resources. 
 * 
 */
public class ViewerTabbedPane extends JTabbedPane {
	private static final Logger logger = Logger.getLogger(ViewerTabbedPane.class);
	private static final long serialVersionUID = 1L;
	
	public static final String STAND_VIEWER = "Standard Viewer";
	
	private StandardResourceDisplayer standardViewer;	
	private ContentPane contentPane;
	private boolean forSandBox;	
	private String namePre;

	private ArrayList<Object> optionalViewers = new ArrayList<Object>();
	
	ViewerTabbedPane(ContentPane cPane, boolean sandBox) {
		this.contentPane = cPane;
		this.forSandBox = sandBox;
		
		namePre = forSandBox ? "SandBox " :"Regular: ";
		setName(namePre);
		
		standardViewer = new StandardResourceDisplayer(forSandBox);		
		standardViewer.setName(namePre + STAND_VIEWER);
		
		addTab(STAND_VIEWER, standardViewer);
		addOptionalViewers();
		
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				contentPane.setStatus(getSelectedComponent().getName());
			}
		});
	}
	
	private void addOptionalViewers() {
		InputStream viewesInput = ViewerTabbedPane.class.getResourceAsStream("/viewer.txt");
		Properties props = new Properties();
		try {
			props.load(viewesInput);
			String[] viewers = props.getProperty("viewers").split(",");
			
			for (String v : viewers) {
				try {
					Class<?> c =  Class.forName(v);
					Constructor<?> constructor = c.getConstructor(boolean.class);
					Object viewer = constructor.newInstance(forSandBox);
					
					Method type = c.getMethod("getViewType", new Class[] {});
					String str = (String) type.invoke(viewer, new Object[] {});
					addTab(str, (JComponent)viewer);
					optionalViewers .add(viewer);
					logger.debug(namePre + " viewer::" + v + ", was added");
				} catch (Exception e) {
					logger.error(e);
				}
			}			
		} catch (Exception e) {
			logger.warn("Error in load viewer.txt file", e);
		}

	}

	public void init() {
		logger.debug(getName() + "init();");
		standardViewer.init();
		try {
			for (Object viewer : optionalViewers) {
				try {
					Method init = viewer.getClass().getMethod("init", new Class[] {});
					init.invoke(viewer, new Object[] {});
				} catch (Exception e) {
					logger.error("Error in init() of " + viewer.getClass(), e);
				}
			}			
		} catch (Exception e) {
			logger.warn(e);
		}
	}

	public void addResource(NcbcResource rsc) {
		standardViewer.addResource(rsc);
	}
}
