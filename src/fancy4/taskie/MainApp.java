package src.fancy4.taskie;

import java.io.IOException;
import java.util.Date;

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
    public static ObservableList<String> taskData = FXCollections.observableArrayList();
    
    public static String[] data = {};
    public MainApp() {
    	//data = TaskieLogic.execute("add order a pizza")[0];
    	TaskieLogic.initialise();
    	String command = "add dummy line for testing";
    	String[][] screen = TaskieLogic.execute(command);
    	data = screen[0];
    	System.out.println(data.length);
    	taskData.addAll(data);
   
    }
    
   /* public static void refresh(String cmd) {
 
    	String[] data = TaskieLogic.execute(cmd);
    	taskData.addAll(data);
    	System.out.println("refreshed");
    }*/
    
    public ObservableList<String> getTaskData() {
    	return taskData;
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");

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