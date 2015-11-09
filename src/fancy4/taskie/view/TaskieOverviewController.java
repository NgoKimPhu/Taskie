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
	private MainApp mainApp;
	private ObservableList<String> obeservableMainList;
	private LogicOutput logicOut;
	private TreeItem<String> dummyRoot;
	private TreeItem<String> overdueNode;
	private TreeItem<String> todayNode;
	private TreeItem<String> tomorrowNode;
	private TreeItem<String> everythingElseNode;
	private TaskieCommandHistory cmdHistory;
	private boolean upPressed;

	// ================================================================
	// Constants
	// ================================================================
	private static final int MAX_INT = Integer.MAX_VALUE;
	private static final int FEEDBACK_LOGICOUT_INDEX = 0;
	private static final int OVERDUE_LOGICOUT_INDEX = 0;
	private static final int TODAY_LOGICOUT_INDEX = 1;
	private static final int TOMORROW_LOGICOUT_INDEX = 2;
	private static final int EVERYTHING_ELSE_LOGICOUT_INDEX = 3;
	private static final String INVALID_COMMAND_MESSAGE = "Invalid command!";
	private static final String VIEW_COMMAND = "view";
	private static final String DEFAULT_INPUT_COMMAND = "Please input a command here";
	private static final String TREE_ROOT = "root";
	private static final String TITLE_FLAG = "-title ";
	private static final String TREE_OVERDUE = "Overdue";
	private static final String TREE_TODAY = "Today";
	private static final String TREE_TOMORROW = "Tomorrow";
	private static final String TREE_EVERYTHING_ELSE = "Everything Else";
	


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
		upPressed = false;

		obeservableMainList = FXCollections.observableArrayList();
		createTree(new ArrayList<String>());

		setupListCell();
		setupTreeCell();

		try {
			logicOut = TaskieLogic.getInstance().execute(VIEW_COMMAND);
			populate(logicOut.getMain(), logicOut.getAll());
		} catch (UnrecognisedCommandException e) {
			// catch the exception thrown by logic
			// displays the warning message to feedbackLabel.
			feedbackLabel.setText(INVALID_COMMAND_MESSAGE);
		}	
	}

	/**
	 * Initialize the TreeView by adding in children nodes to a dummy root.
	 * Dummy root is hidden.
	 */
	private void createTree(ArrayList<String> allTask) {
		dummyRoot = new TreeItem<>(TREE_ROOT);

		overdueNode = setupNode(overdueNode, TITLE_FLAG + TREE_OVERDUE);
		todayNode = setupNode(todayNode, TITLE_FLAG + TREE_TODAY);
		tomorrowNode = setupNode(tomorrowNode, TITLE_FLAG + TREE_TOMORROW);
		everythingElseNode = setupNode(everythingElseNode, TITLE_FLAG + TREE_EVERYTHING_ELSE);


		allTreeView.setRoot(dummyRoot);
		allTreeView.setShowRoot(false);
	}

	private TreeItem<String> setupNode(TreeItem<String> node, String content) {
		node = new TreeItem<>(content);
		node.setExpanded(true);
		dummyRoot.getChildren().add(node);
		return node;
	}
	
	/**
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

	/**
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

	/**
	 * Populate the ListView and TreeView (left and right window respectively)
	 * with content returned by TaskieLogic.
	 * 
	 * @param mainList	 ArrayList to be displayed on left window wrapped in LogicOutput class returned by Logic 
	 * @param allList 	 2D ArrayList to be displayed on right window wrapped in LogicOutput class returned by Logic 
	 */
	private void populate(ArrayList<String> mainList, ArrayList<ArrayList<String>> allList) {
		clearNode();
		populateNode(overdueNode, allList.get(OVERDUE_LOGICOUT_INDEX));
		populateNode(todayNode, allList.get(TODAY_LOGICOUT_INDEX));
		populateNode(tomorrowNode, allList.get(TOMORROW_LOGICOUT_INDEX));
		populateNode(everythingElseNode, allList.get(EVERYTHING_ELSE_LOGICOUT_INDEX));

		String mainFeedback = mainList.get(FEEDBACK_LOGICOUT_INDEX);
		ArrayList<String> mainRemovedFirst = mainList;

		// Extract the first element of mainList and display it on the mainListFeedbackLabel
		// Display the rest of the list on mainListView
		mainRemovedFirst.remove(FEEDBACK_LOGICOUT_INDEX);
		obeservableMainList.removeAll(obeservableMainList);
		obeservableMainList.addAll(mainRemovedFirst);
		mainListFeedbackLabel.setText(mainFeedback);
		//setupListCell();
		mainListView.setItems(obeservableMainList);
	}

	/*
	 * Clear all nodes under the dummy root
	 */
	private void clearNode() {
		overdueNode.getChildren().removeAll(overdueNode.getChildren());
		todayNode.getChildren().removeAll(todayNode.getChildren());
		tomorrowNode.getChildren().removeAll(tomorrowNode.getChildren());
		everythingElseNode.getChildren().removeAll(everythingElseNode.getChildren());
	}

	/**
	 * populate a node with arraylist of String
	 * 
	 * @param node: Node under the dummy root node.
	 * @param list: ArrayList of String from LogicOut returned by Logic
	 */
	private void populateNode(TreeItem<String> node, ArrayList<String> list) {
		for (String str : list) {
			TreeItem<String> leaf = new TreeItem<String>(str);
			node.getChildren().add(leaf);
		}
	}

	/**
	 * Handle the KeyEvent when ENTER is pressed in TextInput
	 */
	private void handleInput() {
		upPressed = false;
		String input;
		String response;
		input = textInput.getText();  
		cmdHistory.addCommand(input);
		cmdHistory.setPointer(cmdHistory.getSize());

		try {
			if (input.equals("help")) {
				mainApp.switchToHelp();
				textInput.clear();
				return;
			}
			
			logicOut = TaskieLogic.getInstance().execute(input);
			populate(logicOut.getMain(), logicOut.getAll());
			response = logicOut.getFeedback();
			feedbackLabel.setText(response);
			textInput.clear();
		} catch (UnrecognisedCommandException e) {
			feedbackLabel.setText(INVALID_COMMAND_MESSAGE);
		}

	}

	/**
	 * Handle the KeyEvent when UP is pressed in TextInput
	 */
	private void handleUp() {
		upPressed = true;
		if (cmdHistory.getPointer() == 0) {
			return;
		} else {
			cmdHistory.decrementPointer();
			textInput.setText(cmdHistory.getCommand());
			textInput.positionCaret(MAX_INT);
		}
	}

	/**
	 * Handle the KeyEvent when DOWN is pressed in TextInput
	 */
	private void handleDown() {
		if (upPressed) {
			if (cmdHistory.getPointer() == cmdHistory.getSize() - 1) {
				return;
			} else {
				cmdHistory.incrementPointer();
				textInput.setText(cmdHistory.getCommand());
				textInput.positionCaret(Integer.MAX_VALUE);
			}
		}
	}
	
	/**
	 * FXML Event Handler of the textInput, handles 3 key events.
	 * 
	 * @param event: key event that needs to be handled
	 */
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
	 public void setMainApp(MainApp mainApp) {
	        this.mainApp = mainApp;
	 }

}

