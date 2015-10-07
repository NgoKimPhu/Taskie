package fancy4.taskie;

import java.io.IOException;
import fancy4.taskie.model.*;
import fancy4.taskie.view.TaskieOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    public ObservableList<String> taskData = FXCollections.observableArrayList();
    public ObservableList<String> dTaskData = FXCollections.observableArrayList();
    public ObservableList<String> fTaskData = FXCollections.observableArrayList();
    
    public static String[] mdata = {};
    public static String[] ddata = {};
    public static String[] fdata = {};
    public MainApp() {
    	TaskieLogic.initialise();
    	mdata = TaskieLogic.execute("search")[1];
    	taskData.addAll(mdata);
    	ddata = TaskieLogic.execute("search")[2];
    	dTaskData.addAll(ddata);
    	fdata = TaskieLogic.execute("search")[3];
    	fTaskData.addAll(fdata);
    	
    }
    
   /* public static void refresh(String cmd) {
 
    	String[] data = TaskieLogic.execute(cmd);
    	taskData.addAll(data);
    	System.out.println("refreshed");
    }*/
    
    public ObservableList<String> getTaskData() {
    	return taskData;
    }
    public ObservableList<String> getDTaskData() {
    	return dTaskData;
    }
    public ObservableList<String> getFTaskData() {
    	return fTaskData;
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Taskie");

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