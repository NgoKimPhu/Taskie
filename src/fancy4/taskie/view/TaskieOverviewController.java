package fancy4.taskie.view;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
//import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import fancy4.taskie.MainApp;
import fancy4.taskie.model.TaskieLogic;
import fancy4.taskie.model.UnrecognisedCommandException;


public class TaskieOverviewController {
	@FXML
	private TableView<String> mainTaskTable;
	@FXML
	private TableColumn<String, String> taskColumn;
	@FXML
	private TableColumn<String, String> dTaskStartColumn;
	@FXML
	private TableColumn<String, String> dTaskEndColumn;
	@FXML
	private TableView<String> dTaskTable;
	@FXML
	private TableColumn<String, String> dTaskColumn;
	@FXML
	private TableView<String> fTaskTable;
	@FXML
	private TableColumn<String, String> fTaskColumn;
	@FXML
	private Label textOutput;

	@FXML
	private TextField textInput;
	//@FXML
	//private TextArea textOutput;

	private MainApp mainApp;

    private String textOutputResponse = "";
    
	public TaskieOverviewController() {

	}


	@FXML
	private void initialize() {
		iniColumn(taskColumn);
		iniColumn(dTaskColumn);
		iniColumn(fTaskColumn);
		iniColumn(dTaskStartColumn);
		iniColumn(dTaskEndColumn);
	}
	private void iniColumn(TableColumn<String, String> column) {
		//TaskieLogic.initialise();
		column.setCellValueFactory(new Callback<CellDataFeatures<String, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<String, String> p) {
				return new SimpleStringProperty(p.getValue());
			}
		});
	}


	public void inputEnter(KeyEvent event) {
		String input;
		if (event.getCode() == KeyCode.ENTER) {
			input = textInput.getText();	
			String[] mainData, dData, fData, dTaskStart;
			String response;
			//String[][] fromLogic = null;
			String[][] fromLogic;
			try {
				fromLogic = TaskieLogic.logic().execute(input);
				
				/*if (input.equals("test1")) {
					fromLogic[0][0] = "test response 1";
					fromLogic[1] = new String[]{"test main 1","test main 2"};
					fromLogic[2] = new String[]{"test deadline 1","test deadline 2"};
					fromLogic[3] = new String[]{"test float 1","test float 2"};
				}*/
				
				
			/*	
				mainData = new String[]{"m1","m2","m3","m4","m5",};
				dData = new String[]{"d1","d2","d3","d4","d5",};
				fData = new String[]{"f1","f2","f3","f4","f5",};
				String response = "response";*/

				mainData = fromLogic[1];
				dData = fromLogic[2];
				fData = fromLogic[3];
				response =  fromLogic[0][0];
				dTaskStart = new String[]{"123","456","789"};
				updateMainTable(mainData);
				updateDTable(dData);
				updateFTable(fData);
				
				textOutputResponse += "> " + input + "\n" + response + "\n";
				textOutput.setText(response);
				
				/*
				 * console print out for testing
				 
				System.out.println("main display:");
				for (String s:mainData) {
					System.out.print(s+" ");
				}
				System.out.println("\n  deadline display:");
				for (String s:dData) {
					System.out.print(s+" ");
				}
				System.out.println("\n  float display:");
				for (String s:fData) {
					System.out.print(s+" ");
				}*/
			} catch (UnrecognisedCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	

			textInput.clear();
		}

	}
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


	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		mainTaskTable.setItems(mainApp.getTaskData());
		dTaskTable.setItems(mainApp.getDTaskStart());
		dTaskTable.setItems(mainApp.getDTaskData());
		fTaskTable.setItems(mainApp.getFTaskData());
	}
}
