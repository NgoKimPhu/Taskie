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
import javafx.scene.control.ListCell;
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

import java.io.IOException;
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


		setupListCell();
	
		setupTreeCell();
		
		try {
			logicOut = TaskieLogic.logic().execute("search");
			populate(logicOut.getMain(), logicOut.getAll());
			
			
		} catch (UnrecognisedCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private void setupListCell() {
		MainList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override public ListCell<String> call(ListView<String> listView) {
				return new TaskListCell();
			}
		});
	}
	private void setupTreeCell() {
		AllTree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override public TreeCell<String> call(TreeView<String> treeView) {
				return new TaskTreeCell();
			}
		});
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
		setupListCell();
		MainList.setItems(mainDisplay);

	}



	private void createTree(ArrayList<String> allTask) {
		dummyRoot = new TreeItem<>("root");
		overdueNode = new TreeItem<>("-title Overdue");
		overdueNode.setExpanded(true);

		todayNode = new TreeItem<>("-title Today");
		todayNode.setExpanded(true);
		tomorrowNode = new TreeItem<>("-title Tomorrow");
		tomorrowNode.setExpanded(true);
		everythingElseNode = new TreeItem<>("-title Everything Else");
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
	public void inputEnter(KeyEvent event) throws IOException {
		
		String input;
		String response;
		LogicOutput fromLogic;
		ArrayList<String> mainData;
		ArrayList<ArrayList<String>> allData; 
		if (event.getCode() == KeyCode.ENTER) { 
			input = textInput.getText();  
			undo_command.push(input);
			if (input.equals("help")) {
				mainApp.showHelp();
			}

			try {
				logicOut = TaskieLogic.logic().execute(input);
				populate(logicOut.getMain(), logicOut.getAll());
				response = logicOut.getFeedback();
				textOutput.setText(response);
				textInput.clear();
				for (String s: logicOut.getMain()) {
					System.out.println(s);
				}
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

