//@author A0130221H
package fancy4.taskie.view;

import java.util.ArrayList;

import fancy4.taskie.MainApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HelpController {
	@FXML
	private Label helpLabel;

	@FXML 
	private Button backButton;

	@FXML
	private AnchorPane anchorPane;

	@FXML 
	private VBox helpBox;

	@FXML 
	private Label backLabel;

	@FXML
	private VBox cmdBox;
	
	@FXML
	private ImageView imageView;

	private MainApp mainApp;

	private static final String HELP_ADD_FLOAT = "add a float task:";
	private static final String HELP_ADD_FLOAT_CMD = "add [title]";
	private static final String HELP_ADD_DEADLINE = "add a deadline task:";
	private static final String HELP_ADD_DEADLINE_CMD= "add [title] due/end/by/deadline [end time] [date]";
	private static final String HELP_ADD_EVENT = "add a deadline task";
	private static final String HELP_ADD_EVENT_CMD = "add [title] from/fr [start time] [date] (op)to/till/- [date]";
	private static final String HELP_DELETE = "delete a task:";
	private static final String HELP_DELETE_CMD = "delete/del l/r[index]";
	private static final String HELP_UPDATE = "update a task:";
	private static final String HELP_UPDATE_CMD = "update l/r[index] (task details/ new time or date)";
	private static final String HELP_MARK_DONE = "mark a task done:";
	private static final String HELP_MARK_DONE_CMD = "markdone/done/finish l/r[index]";
	private static final String HELP_UNDO = "undo a previous command:";
	private static final String HELP_UNDO_CMD = "undo";

	private ArrayList<Label> desLabels;
	private ArrayList<Label> cmdLabels;
	private ArrayList<String> descriptions;
	private ArrayList<String> commands;
	private Image image;
	@FXML
	private void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				helpBox.requestFocus();

			}
		});
		
		helpBox.setSpacing(20);
		cmdBox.setSpacing(20);
		image = new Image("fancy4/taskie/view/Help_Sign.jpg");
		imageView.setImage(image);
		desLabels = new ArrayList<Label>();
		cmdLabels = new	ArrayList<Label>();
		descriptions = new ArrayList<String>();
		commands = new ArrayList<String>();

		helpBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ESCAPE) {
					mainApp.switchToOverview();
				}
			}
		});
		helpLabel.setText("Need any help?");



		helpBox.setAlignment(Pos.TOP_LEFT);
		cmdBox.setAlignment(Pos.TOP_RIGHT);

		setupLists();
		setupLabels();
		populateBox();
	}

	private void setupLists() {
		descriptions.add(HELP_ADD_FLOAT);
		descriptions.add(HELP_ADD_DEADLINE);
		descriptions.add(HELP_ADD_EVENT);
		descriptions.add(HELP_DELETE);
		descriptions.add(HELP_UPDATE);
		descriptions.add(HELP_MARK_DONE);
		descriptions.add(HELP_UNDO);

		commands.add(HELP_ADD_FLOAT_CMD);
		commands.add(HELP_ADD_DEADLINE_CMD);
		commands.add(HELP_ADD_EVENT_CMD);
		commands.add(HELP_DELETE_CMD);
		commands.add(HELP_UPDATE_CMD);
		commands.add(HELP_MARK_DONE_CMD);
		commands.add(HELP_UNDO_CMD);
	}

	private void setupLabels() {
		for (String str: descriptions) {
			Label label = new Label(str);
			
			desLabels.add(label);
		}
		for (String str : commands) {
			Label label = new Label(str);
			cmdLabels.add(label);
		}
	}

	private void populateBox() {
		for (Label label: desLabels) {
			label.getStyleClass().addAll("label-blue");
			helpBox.getChildren().add(label);
		}
		for (Label label: cmdLabels) {
			label.getStyleClass().addAll("label-cmd");
			cmdBox.getChildren().add(label);
		}
	}


	public void setMainApp(MainApp mp) {
		// TODO Auto-generated method stub
		this.mainApp = mp;
	}
}
