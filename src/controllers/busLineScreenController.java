package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import controllers.line.addLineController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import managers.LineMapManager;

public class busLineScreenController implements Initializable,returnScene{
	@FXML
	private BorderPane borderPane;
    @FXML
    private Button addBusLineButton;
    @FXML
    private Button deleteBusLineButton;
    @FXML
    private Button modifyBusLineButton;
    @FXML
    private Button goToPrevSceneButton;
    
    private Scene prevScene;
    private LineMapManager lineMapManager;
    
    public busLineScreenController() {
    	lineMapManager = new LineMapManager();
    }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		borderPane.setCenter(lineMapManager.getMapView());		
	}
    public LineMapManager getLineMapManager() {
    	return this.lineMapManager;
    }

    @FXML
    void addBusLine(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/line/addLine.fxml"));
            AnchorPane root = loader.load();
            addLineController controller = loader.getController();
            controller.setManager(this.lineMapManager);
            Scene scene = new Scene(root);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteBusLine(ActionEvent event) {
      	Stage stage = new Stage();
    	FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/line/showLine.fxml"));
    	try {
    		AnchorPane root = loader.load();
    		Scene scene = new Scene(root);
    		stage.initModality(Modality.APPLICATION_MODAL);
    		stage.setTitle("Eliminar Linea");
    		stage.setScene(scene);
    		stage.setMaximized(false);
    		stage.setResizable(false);
    		stage.showAndWait();
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    }

    @FXML
    void modifyBusLine(ActionEvent event) {
      	Stage stage = new Stage();
    	FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/line/showLine.fxml"));
    	try {
    		AnchorPane root = loader.load();
    		Scene scene = new Scene(root);
    		stage.initModality(Modality.APPLICATION_MODAL);
    		stage.setTitle("Modificar Linea");
    		stage.setScene(scene);
    		stage.setMaximized(false);
    		stage.setResizable(false);
    		stage.showAndWait();
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    }

	@Override
	public void setPrevScene(Scene scene) {
		this.prevScene=scene;
	}
	
	@FXML
	@Override
	public void goToPrevScene(ActionEvent event) {
			((Stage) goToPrevSceneButton.getScene().getWindow()).setScene(prevScene);
	}





}