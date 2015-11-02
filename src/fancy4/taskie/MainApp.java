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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	/* public ObservableList<String> taskData = FXCollections.observableArrayList();
    public ObservableList<String> dTaskData = FXCollections.observableArrayList();
    public ObservableList<String> fTaskData = FXCollections.observableArrayList();
	 */
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

	/* public static String[] mdata = {};
    public static String[] ddata = {};
    public static String[] fdata = {};
	 */
	public MainApp() {
		//data = TaskieLogic.execute("add order a pizza")[0];
		//TaskieLogic.initialise();
		TaskieLogic.logic().initialise();
	

	}


	/* public static void refresh(String cmd) {

    	String[] data = TaskieLogic.execute(cmd);
    	taskData.addAll(data);
    	System.out.println("refreshed");
    }*/

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Taskie");
		Image icon = new Image(getClass().getResourceAsStream("view/TaskieIcon.png"));
		this.primaryStage.getIcons().add(icon);
		initRootLayout();

		showTaskieOverview();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add("JMetroLightTheme.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showTaskieOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/TaskieOverview.fxml"));
			AnchorPane taskieOverview = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(taskieOverview);

			TaskieOverviewController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the main stage.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}