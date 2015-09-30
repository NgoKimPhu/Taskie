package fancy4.taskie.view;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
//import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import fancy4.taskie.MainApp;
import fancy4.taskie.model.TaskieLogic;


public class TaskieOverviewController {
	@FXML
	private TableView<String> mainTaskTable;
	@FXML
	private TableColumn<String, String> indexColumn;
	@FXML
	private TableColumn<String, String> taskColumn;
	@FXML
	private TableColumn<String, String> priorityColumn;

	@FXML
	private TextField textInput;
	@FXML
	private TextArea textOutput;

	private MainApp mainApp;

    private String textOutputResponse = "";
    
	public TaskieOverviewController() {

	}

	@FXML
	private void initialize() {
		TaskieLogic.initialise();
		taskColumn.setCellValueFactory(new Callback<CellDataFeatures<String, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<String, String> p) {
				return new SimpleStringProperty(p.getValue());
			}
		});
	}


	public void inputEnter(KeyEvent event) {
		String input;
		if (event.getCode() == KeyCode.ENTER) {
			ObservableList<String> taskData = FXCollections.observableArrayList();

			input = textInput.getText();
			String[][] fromLogic = TaskieLogic.execute(input);
			String[] data;
			data = fromLogic[0];
			String response =  fromLogic[1][0];
			taskData.addAll(data);
			mainTaskTable.getItems().removeAll(MainApp.taskData);
			mainTaskTable.getItems().addAll(data);
			textOutputResponse += "input: " + input + "\n" + "response: " + response + "\n";
			textOutput.setText(textOutputResponse);
			//System.out.println(d.length);

			textInput.clear();
		}

	}




	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		mainTaskTable.setItems(mainApp.getTaskData());
	}
}
