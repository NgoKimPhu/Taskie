
package fancy4.taskie;

import java.io.IOException;
import java.util.ArrayList;

import fancy4.taskie.model.*;
import fancy4.taskie.view.TaskieOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//@@author A0130221H

public class MainApp extends Application {

	// ================================================================
    // Constants
    // ================================================================
	private static final int MIN_WIDTH = 900;
	private static final int MIN_HEIGHT = 600;
	private static final String WINDOW_TITLE = "Taskie";
	private static final String ICON_PATH = "view/TaskieIcon.png";
	private static final String ROOT_FXML_PATH = "view/RootLayout.fxml";
	private static final String OVERVIEW_FXML_PATH = "view/TaskieOverview.fxml";
	private static final String CSS_PATH = "fancy4/taskie/view/Theme.css";
	
	
	// ================================================================
    // Fields
    // ================================================================
	private Stage primaryStage;
	private BorderPane rootLayout;
	public static ObservableList<String> mainDisplay = FXCollections.observableArrayList();
	public static ObservableList<String> overdueDisplay = FXCollections.observableArrayList();
	public static ObservableList<String> todayDisplay = FXCollections.observableArrayList();
	public static ObservableList<String> tmrDisplay = FXCollections.observableArrayList();
	public static ObservableList<String> elseDisplay = FXCollections.observableArrayList();
	public static ArrayList<String> mainData = new ArrayList<String>();
	public static ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> overdueData = new ArrayList<String>();
	public static ArrayList<String> todayData = new ArrayList<String>();
	public static ArrayList<String> tmrData = new ArrayList<String>();
	public static ArrayList<String> elseData = new ArrayList<String>();

	// ================================================================
    // Methods
    // ================================================================
	public MainApp() {
		TaskieLogic.getInstance().initialise();
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(WINDOW_TITLE);
		Image icon = new Image(getClass().getResourceAsStream(ICON_PATH));
		this.primaryStage.getIcons().add(icon);
		
		initRootLayout();
		showTaskieOverview();
	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(ROOT_FXML_PATH));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(CSS_PATH);
			primaryStage.setScene(scene);

			primaryStage.show();
			primaryStage.setMinHeight(MIN_HEIGHT);
			primaryStage.setMinWidth(MIN_WIDTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showTaskieOverview() {
		try {
			// Load overview from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(OVERVIEW_FXML_PATH));
			AnchorPane taskieOverview = (AnchorPane) loader.load();

			rootLayout.setCenter(taskieOverview);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}