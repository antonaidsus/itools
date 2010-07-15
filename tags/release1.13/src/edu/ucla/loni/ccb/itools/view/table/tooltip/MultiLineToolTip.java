package edu.ucla.loni.ccb.itools.view.table.tooltip;

import javax.swing.JToolTip;

/**
 * @version 1.0 11/09/98
 */

public class MultiLineToolTip extends JToolTip {
  public MultiLineToolTip() {  
    setUI(new MultiLineToolTipUI());
  }
}
