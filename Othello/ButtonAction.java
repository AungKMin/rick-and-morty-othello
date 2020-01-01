import javax.swing.*;
import java.awt.event.*;

class ButtonAction extends AbstractAction { 

	public ButtonAction (ImageIcon icon) { 
		super(icon);
	}

	public void actionPerformed(ActionEvent event) { 
		JButton aiPlayerButton = (JButton) event.getSource();

	}
}