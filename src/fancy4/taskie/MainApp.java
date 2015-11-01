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

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
   /* public ObservableList<String> taskData = FXCollections.observableArrayList();
    public ObservableList<String> dTaskData = FXCollections.observableArrayList();
    public ObservableList<String> fTaskData = FXCollections.observableArrayList();
    */
    public static ObservableList<String> mainDisplay = FXCollections.observableArrayList();
    public static ObservableList<String> allDisplay = FXCollections.observableArrayList();
    public static ArrayList<String> mainData = new ArrayList<String>();
    public static ArrayList<String> allData = new ArrayList<String>();
    
   /* public static String[] mdata = {};
    public static String[] ddata = {};
    public static String[] fdata = {};
    */
    public MainApp() {
    	//data = TaskieLogic.execute("add order a pizza")[0];
    	//TaskieLogic.initialise();
    	Logic.logic().initialise();
    	iniAllTable();

    }
    private void iniAllTable() {
    	
    	LogicOutput iniDisplay;
		try {
			iniDisplay = Logic.logic().execute("search");
			mainData = iniDisplay.getMain();
			mainDisplay.addAll(mainData);
			
			allData = iniDisplay.getAll();
			allDisplay.addAll(allData);
		} catch (UnrecognisedCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*ArrayList<String> l1 = new ArrayList<String>();
		l1.add("all1");
		l1.add("all2");
		ArrayList<String> l2 = new ArrayList<String>();
		l2.add("main1");
		l2.add("main2");*/
		//iniDisplay = new LogicOutput("test", l1, l2);
		//iniDisplay = TaskieLogic.logic().execute("search");
		
		/*mainData = iniDisplay[1];
		taskData.addAll(mdata);
		ddata = iniDisplay[2];
		dTaskData.addAll(ddata);
		fdata = iniDisplay[3];
		fTaskData.addAll(fdata);
		*/
    	
    }
    public static void updateDisplay(ArrayList<String> main, ArrayList<String> all) {
    	
    	mainDisplay.removeAll(mainDisplay);
    	mainDisplay.addAll(main);
    	allDisplay.removeAll(allDisplay);
    	allDisplay.addAll(all);
    }
    
   /* public static void refresh(String cmd) {
 
    	String[] data = TaskieLogic.execute(cmd);
    	taskData.addAll(data);
    	System.out.println("refreshed");
    }*/
    
  /*  public ObservableList<String> getTaskData() {
    	return taskData;
    }
    public ObservableList<String> getDTaskData() {
    	return dTaskData;
    }
    public ObservableList<String> getFTaskData() {
    	return fTaskData;
    }
    */
    public ObservableList<String> getMainDisplay() {
    	return mainDisplay;
    }
    public ObservableList<String> getAllDisplay() {
    	return allDisplay;
    }
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