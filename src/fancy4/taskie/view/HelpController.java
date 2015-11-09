//@author A0130221H
package fancy4.taskie.view;

import fancy4.taskie.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

public class HelpController {
	@FXML
	private Label helpLabel;
	
	@FXML 
	private Button backButton;
	private MainApp mainApp;
	@FXML
	private void initialize() {
		helpLabel.setText("help!!!!!!");
		backButton.setOnAction(e-> ButtonClicked(e));
	}
	
	private void ButtonClicked(ActionEvent e) {
		// TODO Auto-generated method stub
		helpLabel.setText("run!!!!!!!!!!");
		mainApp.switchToOverview();
	}

	@FXML
	private void handleKey (KeyEvent event) {
		if (event.getCode().equals("ESCAPE")) {
			helpLabel.setText("run!!!!");
		}
	}

	public void setMainApp(MainApp mp) {
		// TODO Auto-generated method stub
		this.mainApp = mp;
	}
}
