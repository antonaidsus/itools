TO START:

1)   copy build.template.property to build.properties 
     and modify the values corresponding to your setting
     
2)   ant deploy to deploy the tomcat server
     ant package-web to create the war file
     ant run   to run the iTools client program and read/write data to the server
     ant localrun run the iTools client program read/write data locally.
     
3)   if you set value of dao.type a different value than filesystem, then you need modify
     mysql.properties, or hsql.properties, or derby.properties depending on which database
     you use. Please be noted however, that since it was not tested for a while, it may have some
     bugs.
     
     
     
 new 
 09/20/2009 1.11
    1) ontology parser for new format
    2) build number will not be updated every deployment
        "ant package-web"
    3) remove toggle center button from Ht/Hw Viewer
 
 07/16/2009
 
 1) it now supports new viewers by plugin.
 
    0) add the class name of the viewer in the file 'resources/viewer.txt'
    
    The requirements of the plugin viewer class are:
    
    1) It subclasses  JComponent.
    2) It has a Constructor with a boolean as input, which specify the instance used
       to display sandBox resources or regular resources.
       SandBox resources  are resources which is in testing stage and not finalized.
    3) It has a method "public String getViewType()". The returned value will be 
       used as the tab name when it is added to the JTabbedPane.
    4) It has a method "public void init()", which will be called when the viewer
       was added to the pane, and it should be implemented as get and display the 
       data.
       
       The viewer can consider use the 'edu.ucla.loni.ccb.itools.parser.HtViewerDataHelper"
       to get the necessary data. For example, it can call 
       'public static List<Node> getOntology()' to get the Ontology tree.
       'public static List<NcbcResource> getAllResources()' to get all the resources.
       'public List<NcbcResource> getResourcesFromDao(String centerName)' to get resources
        of the center
    5) If it wants synchronize with other viewers, you can do it with class 
       'edu.ucla.loni.ccb.gui.htviewer.Controller'. 
        Using the following to add your viewer as listener:

        Controller.getInstance(isSandbox).addPropertyChangeListener(this);

        And using the following code to notify the interested listeners

        Controller.getInstance(isSandbox).firePropertyChange(
        new PropertyChangeEvent(this, propertyName, oldValue, newValue));
        
        Most used propertName is Controller.SELECTED_PATH, whose value is an array
        of string, which represents the path to the selected Node.

Design issue
1) This is how the iTools starts
   A) ContentPane.init()  
        1) loads properties form property file.
        2) creates and adds two viewers tabs, one for regular resources and the other
           is for sandbox resources. Each viewers tab loads viewers from viewer.txt,
           which is iTools plugin platform.
        3) loads data to viewers.