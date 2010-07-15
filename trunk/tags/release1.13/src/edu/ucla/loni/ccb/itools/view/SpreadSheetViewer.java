/**
 * 
 */
package edu.ucla.loni.ccb.itools.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.ucla.loni.ccb.itools.DataBank;
import edu.ucla.loni.ccb.itools.dao.Security;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.RUser;

/**
 * This class shows the value of a NcbcResource, can save the modification
 * 
 * @author Qunfei Ma
 * 
 */
public class SpreadSheetViewer extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static String UPDATE = "Update";
	public static String CANCEL = "Cancel";
	public static String PROMOTION = "Move Out of SandBox";
	public static String DEMOTION = "Move Into SandBox";

	private NcbcResource rsc;
	private ResourceSpreadSheet resourceSheet = new ResourceSpreadSheet(false);
	private JButton updateB = new JButton(UPDATE);
	private JButton cancelB = new JButton(CANCEL);
	private JButton promotionB = new JButton(PROMOTION);
	private ResourceAndViewNode data;

	// private ButtonPane buttons = new ButtonPane(new String[]{UPDATE,
	// CANCEL});

	public SpreadSheetViewer() {
		setLayout(new BorderLayout());
		add(resourceSheet, BorderLayout.CENTER);
		Box buttons = new Box(BoxLayout.X_AXIS);
		add(buttons, BorderLayout.SOUTH);
		buttons.add(updateB);
		buttons.add(cancelB);
		buttons.add(promotionB);

		updateB.addActionListener(this);
		cancelB.addActionListener(this);
		promotionB.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(UPDATE)) {
			rsc.getAllProperties().putAll(
					resourceSheet.getResource().getAllProperties());
			DataBank.updateResource(rsc);
		} else if (cmd.equals(CANCEL)) {
			resourceSheet.displayResource(rsc);
		} else if (e.getSource() == promotionB) {
			if (!Security.hasPermission(RUser.EXPERTUSER)) {
				GuiUtil
						.showMsg("It requires an Expert account. You don't have permission.");
				return;
			}

			if (cmd.equals(PROMOTION)) {
				rsc.setInSandBox(false);
				MainFrame.getContent().getRegularViewerTabs().addResource(rsc);
			} else if (cmd.equals(DEMOTION)) {
				rsc.setInSandBox(true);
				MainFrame.getContent().getSandboxViewerTabs().addResource(rsc);
			}
			
			data.getTree().displayer.getResourceViewer().setData(
					new ArrayList<ResourceAndViewNode>(0));

			data.removeNodeFromTree();
			DataBank.updateResource(rsc);
		}

	}

	public void setData(ResourceAndViewNode data) {
		this.data = data;
		rsc = data.getRsc();
		promotionB.setText(rsc.isInSandBox() ? PROMOTION : DEMOTION);
		resourceSheet.displayResource(rsc);
	}
}