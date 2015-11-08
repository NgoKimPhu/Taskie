package fancy4.taskie.view;

import fancy4.taskie.MainApp;
import fancy4.taskie.model.LogicOutput;
import fancy4.taskie.model.TaskieCommandHistory;
import fancy4.taskie.model.TaskieLogic;
import fancy4.taskie.model.UnrecognisedCommandException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import java.util.*;
import java.io.IOException;


//@@author A0130221H
public class TaskieOverviewController {





	// ================================================================
    // FXML Fields
    // ================================================================
	
	private static final String SEARCH_CMD = "search";

	@FXML
	private ListView<String> MainList;
	
	@FXML
	private Label mainLabel;
	
	@FXML
	private Label allLabel;
	
	@FXML
	private Label textOutput;
	
	@FXML
	private Label mainListFeedback;
	
	@FXML
	private TreeView<String> AllTree;
	
	@FXML
	private TextField textInput;
	
	// ================================================================
    // other Fields
    // ================================================================
	private ObservableList<String> mainDisplay;
	private MainApp mainApp;
	private LogicOutput logicOut;
	private TreeItem<String> dummyRoot;
	private TreeItem<String> overdueNode;
	private TreeItem<String> todayNode;
	private TreeItem<String> tomorrowNode;
	private TreeItem<String> everythingElseNode;
	private Stack<String> undo_command;
	private Stack<String> redo_command;
	private boolean upPressed = false;
	private TaskieCommandHistory cmdHistory;
	// ================================================================
    // Constants
    // ================================================================
	private static final String EMPTY_STRING = "";
	private static final String DEFAULT_INPUT_COMMAND = "Please input a command here";
	private static final String TREE_ROOT = "root";
	private static final String TREE_OVERDUE = "-title Overdue";
	private static final String TREE_TODAY = "-title Today";
	private static final String TREE_TOMORROW = "-title Tomorrow";
	private static final String TREE_EVERYTHING_ELSE = "-title Everything Else";

	public TaskieOverviewController() {

	}

	@FXML
	private void initialize() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				textInput.requestFocus();
				textInput.setText(DEFAULT_INPUT_COMMAND);
				textInput.selectAll();
			}
		});
		
		cmdHistory = new TaskieCommandHistory();
		undo_command = new Stack<String>();
		redo_command = new Stack<String>();
		mainDisplay = FXCollections.observableArrayList();
		createTree(new ArrayList<String>());


		setupListCell();
	
		setupTreeCell();
		
		try {
			logicOut = TaskieLogic.logic().execute(SEARCH_CMD);
			populate(logicOut.getMain(), logicOut.getAll());
		} catch (UnrecognisedCommandException e) {
			// TODO Auto-generated catch block
			textOutput.setText("Invalid command!");
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



	public void populate(ArrayList<String> mainList, ArrayList<ArrayList<String>> allList) {
		
		
		overdueNode.getChildren().removeAll(overdueNode.getChildren());
		todayNode.getChildren().removeAll(todayNode.getChildren());
		tomorrowNode.getChildren().removeAll(tomorrowNode.getChildren());
		everythingElseNode.getChildren().removeAll(everythingElseNode.getChildren());
		for (String str : allList.get(0)) {
			TreeItem<String> overdueLeaf = new TreeItem<String>(str);
			overdueNode.getChildren().add(overdueLeaf);
		}
		for (String str : allList.get(1)) {
			TreeItem<String> todayLeaf = new TreeItem<String>(str);
			todayNode.getChildren().add(todayLeaf);
		}
		for (String str : allList.get(2)) {
			TreeItem<String> tomorrowLeaf = new TreeItem<String>(str);
			tomorrowNode.getChildren().add(tomorrowLeaf);
		}
		for (String str : allList.get(3)) {
			TreeItem<String> everythingElseLeaf = new TreeItem<String>(str);
			everythingElseNode.getChildren().add(everythingElseLeaf);
		}
		String mainFeedback = mainList.get(0);
		ArrayList<String> mainRemovedFirst = mainList;
		
		mainRemovedFirst.remove(0);
		mainDisplay.removeAll(mainDisplay);
		mainDisplay.addAll(mainRemovedFirst);
		mainListFeedback.setText(mainFeedback);
		setupListCell();
		MainList.setItems(mainDisplay);

	}



	private void createTree(ArrayList<String> allTask) {
		dummyRoot = new TreeItem<>(TREE_ROOT);
		overdueNode = new TreeItem<>(TREE_OVERDUE);
		overdueNode.setExpanded(true);

		todayNode = new TreeItem<>(TREE_TODAY);
		todayNode.setExpanded(true);
		tomorrowNode = new TreeItem<>(TREE_TOMORROW);
		tomorrowNode.setExpanded(true);
		everythingElseNode = new TreeItem<>(TREE_EVERYTHING_ELSE);
		everythingElseNode.setExpanded(true);


		//root is the parent of itemChild
		dummyRoot.getChildren().add(overdueNode);
		dummyRoot.getChildren().add(todayNode);
		dummyRoot.getChildren().add(tomorrowNode);
		dummyRoot.getChildren().add(everythingElseNode);
		AllTree.setRoot(dummyRoot);
		AllTree.setShowRoot(false);

	}
	
	private void handleInput() {
		String input;
		String response;
		input = textInput.getText();  
		cmdHistory.addCommand(input);
		cmdHistory.setPointer(cmdHistory.getSize());
		
		try {
			logicOut = TaskieLogic.logic().execute(input);
			populate(logicOut.getMain(), logicOut.getAll());
			response = logicOut.getFeedback();
			textOutput.setText(response);
			textInput.clear();
		} catch (UnrecognisedCommandException e) {
			textOutput.setText("Invalid command!");
		}

	}
	private void handleUp() {
		if (cmdHistory.isEmpty()) {
			return;
		} else {
			cmdHistory.decrementPointer();
			textInput.setText(cmdHistory.getCommand());
		}
	}
	
	private void handleDown() {
		if (cmdHistory.getPointer() == cmdHistory.getSize() - 1) {
			textInput.clear();
			return;
				
			
		} else {
			cmdHistory.incrementPointer();
			textInput.setText(cmdHistory.getCommand());
		}
	}
	@FXML
	private void handleKeyPress(KeyEvent event){
		
		switch (event.getCode()) {
		case ENTER:
			handleInput();
			break;
		case UP:
			event.consume();
			handleUp();
			break;
		case DOWN:
			event.consume();
			handleDown();
			break;
		default:
			break;
		}
	}


	public void setMainApp(MainApp ma) {
		mainApp = ma;
	}


}

