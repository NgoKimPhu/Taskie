package fancy4.taskie.view;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
//import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

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
	private TreeView<String> AllTree;
	@FXML
	private TextField textInput;
	//@FXML
	//private TextArea textOutput;

	private MainApp mainApp;

	private String textOutputResponse = "";

	private ArrayList<ArrayList<String>> testList;
	TreeItem<String> dummyRoot;
	TreeItem<String> overdueNode;

	TreeItem<String> todayNode;

	TreeItem<String> tomorrowNode;

	TreeItem<String> everythingElseNode;

	private PseudoClass titileCell = PseudoClass.getPseudoClass("titileCell");
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

		createTree(new ArrayList<String>());
		setupTestList();
		populateTree();
	}
	private void setupCell() {
		AllTree.setCellFactory(p -> {
			return new TreeCell<String>() {

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);  
					if (empty) {
						setText(null);
						pseudoClassStateChanged(titileCell, false);
					} else {
						setText(item);
						boolean bool = (item.equals("Overdue") || item.equals("Today") || item.equals("Tomorrow")
								|| item.equals("Everything Else"));
						pseudoClassStateChanged(titileCell, bool);
					}
				}
			};
		});
	}


	private void setupTestList() {
		testList = new ArrayList<ArrayList<String>> ();
		ArrayList<String> overdue = new ArrayList<String>();
		ArrayList<String> today = new ArrayList<String>();
		ArrayList<String> tomorrow = new ArrayList<String>();

		ArrayList<String> everythingElse = new ArrayList<String>();
		overdue.add("overdue1");overdue.add("overdue2");
		today.add("today1");today.add("today2");
		tomorrow.add("tmr1");tomorrow.add("tmr2");

		everythingElse.add("else1");everythingElse.add("else2");
		testList.add(overdue);testList.add(today);testList.add(tomorrow);testList.add(everythingElse);

	}
	private void populateTree() {

		for (String str : testList.get(0)) {
			TreeItem<String> overdueLeaf = new TreeItem<String>(str);
			overdueNode.getChildren().add(overdueLeaf);
		}
		for (String str : testList.get(1)) {
			TreeItem<String> todayLeaf = new TreeItem<String>(str);
			todayNode.getChildren().add(todayLeaf);
		}
		for (String str : testList.get(2)) {
			TreeItem<String> tomorrowLeaf = new TreeItem<String>(str);
			tomorrowNode.getChildren().add(tomorrowLeaf);
		}
		for (String str : testList.get(3)) {
			TreeItem<String> everythingElseLeaf = new TreeItem<String>(str);
			everythingElseNode.getChildren().add(everythingElseLeaf);
		}
	}

	private void initColumn(TableColumn<String, String> column) {
		//TaskieLogic.initialise();
		column.setCellValueFactory(new Callback<CellDataFeatures<String, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<String, String> p) {
				return new SimpleStringProperty(p.getValue());
			}
		});
	}

	private void createTree(ArrayList<String> allTask) {
		dummyRoot = new TreeItem<>("root");
		overdueNode = new TreeItem<>("Overdue");
		overdueNode.setExpanded(true);
		/*for (String str : testList.get(0)) {
			TreeItem<String> overdueLeaf = new TreeItem<String>(str);
			overdueNode.getChildren().add(overdueLeaf);
		}*/
		//root.setExpanded(true);
		//create child
		todayNode = new TreeItem<>("Today");
		todayNode.setExpanded(true);
		tomorrowNode = new TreeItem<>("Tomorrow");
		tomorrowNode.setExpanded(true);
		everythingElseNode = new TreeItem<>("Everything Else");
		everythingElseNode.setExpanded(true);


		//root is the parent of itemChild
		dummyRoot.getChildren().add(overdueNode);
		dummyRoot.getChildren().add(todayNode);
		dummyRoot.getChildren().add(tomorrowNode);
		dummyRoot.getChildren().add(everythingElseNode);
		AllTree.setRoot(dummyRoot);
		AllTree.setShowRoot(false);
		//root.setExpanded(true);
	}
	public void inputEnter(KeyEvent event) {
		String input;
		String response;
		LogicOutput fromLogic;
		ArrayList<String> mainData, allData;
		if (event.getCode() == KeyCode.ENTER) { 
			input = textInput.getText();  


			/*try {
				fromLogic = TaskieLogic.logic().execute(input);
				mainData = fromLogic.getMain();
				allData = fromLogic.getAll();
				response = fromLogic.getFeedback();
				mainApp.updateDisplay(mainData, allData);
				textOutput.setText(response);
				textInput.clear();
			} catch (UnrecognisedCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

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

			/*  mainData = fromLogic[1];
      dData = fromLogic[2];
      fData = fromLogic[3];
      response =  fromLogic[0][0] ;

      updateMainTable(mainData);
      updateDTable(dData);
      updateFTable(fData);
			 */
			//  textOutputResponse += "> " + input + "\n" + response + "\n";





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
		this.mainApp = mainApp;

		// Add observable list data to the table
		MainList.setItems(mainApp.getMainDisplay());
		AllList.setItems(mainApp.getAllDisplay());
		//fTaskTable.setItems(mainApp.getFTaskData());

	}

	public static class Task {
		private final SimpleStringProperty content;

		private Task(SimpleStringProperty logicOutput) {
			this.content = logicOutput;
		}

		private String getContent() {
			return this.content.get();
		}
	}
}

