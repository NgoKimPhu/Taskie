package fancy4.taskie.view;
import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
//import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;

import fancy4.taskie.MainApp;
import fancy4.taskie.model.LogicOutput;
import fancy4.taskie.model.TaskieLogic;
import fancy4.taskie.model.UnrecognisedCommandException;


public class TaskieOverviewController {
	/*@FXML
	private TableView<String> mainTaskTable;
	@FXML
	private TableColumn<String, String> taskColumn;
	@FXML
	private TableView<String> dTaskTable;
	@FXML
	private TableColumn<String, String> dTaskColumn;
	@FXML
	private TableView<String> fTaskTable;
	@FXML
	private TableColumn<String, String> fTaskColumn;
	 */
	@FXML
	private ListView<String> MainList;
	@FXML
	private ListView<String> AllList;
	@FXML
	private Label textOutput;
	@FXML
	private Label allLabel;
	@FXML
	private Label mainLabel;

	@FXML
	private TextField textInput;
	//@FXML
	//private TextArea textOutput;

	public TaskieOverviewController() {

	}

	@FXML
	private void initialize() {
		//TaskieLogic.initialise();
		/*
		iniColumn(taskColumn);
		iniColumn(dTaskColumn);
		iniColumn(fTaskColumn);
		 */
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	    		textInput.requestFocus();
	        }
	    });
	}

	public void inputEnter(KeyEvent event) {
		String input;
		String response;
		LogicOutput fromLogic;
		ArrayList<String> mainData, allData;
		if (event.getCode() == KeyCode.ENTER) {	
			input = textInput.getText();	
		
			
			try {
				fromLogic = TaskieLogic.logic().execute(input);
				mainData = fromLogic.getMain();
				allData = fromLogic.getAll();
				response = fromLogic.getFeedback();
				MainApp.updateDisplay(mainData, allData);
				textOutput.setText(response);
				textInput.clear();
			} catch (UnrecognisedCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//String[] mainData, dData, fData;
		/*	
			ArrayList<String> l1 = new ArrayList<String>();
			l1.add("enter all1");
			l1.add("enter all2");
			ArrayList<String> l2 = new ArrayList<String>();
			l2.add("enter main1");
			l2.add("enter main2");
			fromLogic = new LogicOutput("test", l1, l2);
			
			*/
			
			/*	mainData = fromLogic[1];
			dData = fromLogic[2];
			fData = fromLogic[3];
			response =  fromLogic[0][0] ;

			updateMainTable(mainData);
			updateDTable(dData);
			updateFTable(fData);
			 */
			//	textOutputResponse += "> " + input + "\n" + response + "\n";
			



			
		}

	}
	/*
	public void updateMainTable(String[] data) {

		if (data == null) {	

		} else {
			mainTaskTable.getItems().removeAll(mainApp.getTaskData());
			mainTaskTable.getItems().addAll(data);
		}
	}
	public void updateDTable(String[] data) {

		if (data == null) {			
		} else {
			dTaskTable.getItems().removeAll(mainApp.getDTaskData());
			dTaskTable.getItems().addAll(data);
		}
	}

	public void updateFTable(String[] data) {
		if (data == null) {	
		} else {
			fTaskTable.getItems().removeAll(mainApp.getFTaskData());
			fTaskTable.getItems().addAll(data);
		}
	}
	 */


	public void setMainApp(MainApp mainApp) {
		
		// Add observable list data to the table
		MainList.setItems(mainApp.getMainDisplay());
		AllList.setItems(mainApp.getAllDisplay());
		//fTaskTable.setItems(mainApp.getFTaskData());

	}
}
