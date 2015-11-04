/**
 * @author Lu Yu
 *
 */
package fancy4.taskie.view;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.LinkedList;
import java.util.Stack;

import fancy4.taskie.MainApp;
import fancy4.taskie.model.LogicOutput;
import fancy4.taskie.model.TaskieLogic;
import fancy4.taskie.model.UnrecognisedCommandException;


public class TaskieOverviewController {
	ObservableList<String> mainDisplay;
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


	private ArrayList<ArrayList<String>> testList;
	private LogicOutput logicOut;
	private ArrayList<String> testMain;
	private TreeItem<String> dummyRoot;
	private TreeItem<String> overdueNode;

	private TreeItem<String> todayNode;

	private TreeItem<String> tomorrowNode;

	private TreeItem<String> everythingElseNode;

	private PseudoClass titileCell = PseudoClass.getPseudoClass("titileCell");

	private Stack<String> undo_command;
	private Stack<String> redo_command;
	private boolean upPressed = false;
	public TaskieOverviewController() {

	}

	@FXML
	private void initialize() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				textInput.requestFocus();
			}
		});
		undo_command = new Stack<String>();
		redo_command = new Stack<String>();
		mainDisplay = FXCollections.observableArrayList();
		createTree(new ArrayList<String>());
		setupTestList();
		//populateTree();
		setupCell();

		try {
			logicOut = TaskieLogic.logic().execute("search");
			populate(logicOut.getMain(), logicOut.getAll());
		} catch (UnrecognisedCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
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
	public void populate(ArrayList<String> main, ArrayList<ArrayList<String>> list) {
		overdueNode.getChildren().removeAll(overdueNode.getChildren());
		todayNode.getChildren().removeAll(todayNode.getChildren());
		tomorrowNode.getChildren().removeAll(tomorrowNode.getChildren());
		everythingElseNode.getChildren().removeAll(everythingElseNode.getChildren());
		for (String str : list.get(0)) {
			TreeItem<String> overdueLeaf = new TreeItem<String>(str);
			overdueNode.getChildren().add(overdueLeaf);
		}
		for (String str : list.get(1)) {
			TreeItem<String> todayLeaf = new TreeItem<String>(str);
			todayNode.getChildren().add(todayLeaf);
		}
		for (String str : list.get(2)) {
			TreeItem<String> tomorrowLeaf = new TreeItem<String>(str);
			tomorrowNode.getChildren().add(tomorrowLeaf);
		}
		for (String str : list.get(3)) {
			TreeItem<String> everythingElseLeaf = new TreeItem<String>(str);
			everythingElseNode.getChildren().add(everythingElseLeaf);
		}
		mainDisplay.removeAll(mainDisplay);
		mainDisplay.addAll(main);
		MainList.setItems(mainDisplay);
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
		ArrayList<String> mainData;
		ArrayList<ArrayList<String>> allData; 
		if (event.getCode() == KeyCode.ENTER) { 
			input = textInput.getText();  
			undo_command.push(input);
			/*ArrayList<String> l1 = new ArrayList<String>();
			l1.add("enter all1");
			l1.add("enter all2");
			ArrayList<String> ovd = new ArrayList<String>();
			ovd.add("enter main1");
			ovd.add("enter main2");
			ArrayList<String> td = new ArrayList<String>();
			td.add("todaasdfasdfy");
			ArrayList<String> tmr = new ArrayList<String>();
			tmr.add("tmr");
			ArrayList<String> allelse = new ArrayList<String>();
			allelse.add("everything else");
			ArrayList<ArrayList<String>> all = new ArrayList<ArrayList<String>>();
			all.add(ovd); all.add(td); all.add(tmr);all.add(allelse);
			fromLogic = new LogicOutput("test", l1, all);
			response = fromLogic.getFeedback();
			mainData = fromLogic.getMain();
			allData = fromLogic.getAll();
			 */
			try {
				logicOut = TaskieLogic.logic().execute(input);
				populate(logicOut.getMain(), logicOut.getAll());
				response = logicOut.getFeedback();
				textOutput.setText(response);
				textInput.clear();
			} catch (UnrecognisedCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	
		if (event.getCode() == KeyCode.UP) {
			
			if (!undo_command.isEmpty()) {
				textInput.clear();
				String poped = undo_command.pop();
				redo_command.push(poped);
				textInput.setText(poped);
				upPressed = true;
			}
		}
		if (event.getCode() == KeyCode.DOWN) {
		
			if (upPressed == true) {
				if (!redo_command.isEmpty()) {
					if (!undo_command.isEmpty()) {
						textInput.clear();
						String poped = redo_command.pop();
						undo_command.push(poped);
						textInput.setText(poped);
					} else {
						textInput.clear();
						String poped = redo_command.pop();
						undo_command.push(poped);
					
						textInput.clear();
						poped = redo_command.pop();
						undo_command.push(poped);
						textInput.setText(poped);
					}
				}
			} else {
				textInput.clear();
			}
		}


	}


	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}


}

