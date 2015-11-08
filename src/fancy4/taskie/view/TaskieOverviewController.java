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

	@FXML
	private ListView<String> mainListView;

	@FXML
	private TreeView<String> allTreeView;

	@FXML
	private Label mainLabel;

	@FXML
	private Label allLabel;

	@FXML
	private Label feedbackLabel;

	@FXML
	private Label mainListFeedbackLabel;

	@FXML
	private TextField textInput;

	// ================================================================
	// other Fields
	// ================================================================
	private ObservableList<String> obeservableMainList;
	private MainApp mainApp;
	private LogicOutput logicOut;
	private TreeItem<String> dummyRoot;
	private TreeItem<String> overdueNode;
	private TreeItem<String> todayNode;
	private TreeItem<String> tomorrowNode;
	private TreeItem<String> everythingElseNode;
	private TaskieCommandHistory cmdHistory;

	// ================================================================
	// Constants
	// ================================================================
	private static final String INVALID_COMMAND_MESSAGE = "Invalid command!";
	private static final String SEARCH_CMD = "search";
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
		// Request focus for text field input, and select all default text
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				textInput.requestFocus();
				textInput.setText(DEFAULT_INPUT_COMMAND);
				textInput.selectAll();
			}
		});

		// Construct the TaskieCommandHistory to keep track of commands typed in by user
		cmdHistory = new TaskieCommandHistory();

		obeservableMainList = FXCollections.observableArrayList();
		createTree(new ArrayList<String>());

		setupListCell();
		setupTreeCell();

		try {
			logicOut = TaskieLogic.logic().execute(SEARCH_CMD);
			populate(logicOut.getMain(), logicOut.getAll());
		} catch (UnrecognisedCommandException e) {
			// catch the exception thrown by logic
			// displays the warning message to feedbackLabel.
			feedbackLabel.setText(INVALID_COMMAND_MESSAGE);
		}	
	}

	/*
	 * Initialize the TreeView by adding in children nodes to a dummy root.
	 * Dummy root is not shown.
	 */
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

		dummyRoot.getChildren().add(overdueNode);
		dummyRoot.getChildren().add(todayNode);
		dummyRoot.getChildren().add(tomorrowNode);
		dummyRoot.getChildren().add(everythingElseNode);
		allTreeView.setRoot(dummyRoot);
		allTreeView.setShowRoot(false);
	}

	/*
	 * Initialize the ListView by populating it with custom ListCell class
	 */
	private void setupListCell() {
		mainListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override 
			public ListCell<String> call(ListView<String> listView) {
				return new TaskListCell();
			}
		});
	}

	/*
	 * Initialize the TreeView by populating it with custom TreeCell class
	 */
	private void setupTreeCell() {
		allTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override 
			public TreeCell<String> call(TreeView<String> treeView) {
				return new TaskTreeCell();
			}
		});
	}

	/*
	 * Populate the ListView and TreeView (left and right window respectively)
	 * with content returned by TaskieLogic.
	 * 
	 * @param mainList	 ArrayList to be displayed on left window wrapped in LogicOutput class returned by Logic 
	 * @param allList 	 2D ArrayList to be displayed on right window wrapped in LogicOutput class returned by Logic 
	 */
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

		// Extract the first element of mainList and display it on the mainListFeedbackLabel
		// Display the rest of the list on mainListView
		mainRemovedFirst.remove(0);
		obeservableMainList.removeAll(obeservableMainList);
		obeservableMainList.addAll(mainRemovedFirst);
		mainListFeedbackLabel.setText(mainFeedback);
		setupListCell();
		mainListView.setItems(obeservableMainList);
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
			feedbackLabel.setText(response);
			textInput.clear();
		} catch (UnrecognisedCommandException e) {
			feedbackLabel.setText(INVALID_COMMAND_MESSAGE);
		}

	}
	private void handleUp() {
		if (cmdHistory.isEmpty()) {
			return;
		} else {
			cmdHistory.decrementPointer();
			textInput.setText(cmdHistory.getCommand());
			textInput.positionCaret(Integer.MAX_VALUE);
		}
	}

	private void handleDown() {
		if (cmdHistory.getPointer() == cmdHistory.getSize() - 1) {
			return;

		} else {
			cmdHistory.incrementPointer();
			textInput.setText(cmdHistory.getCommand());
			textInput.positionCaret(Integer.MAX_VALUE);
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

