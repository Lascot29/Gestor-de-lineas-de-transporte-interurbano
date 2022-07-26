package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import managers.CityMapManager;
import managers.StageManager;

public class mainScreenController implements Initializable{
	@FXML
	private AnchorPane pane;
	@FXML
	private Button cityMapButton;
	@FXML
	private Button lineScreenButton;
	@FXML
	private Button travelScreenButton;
	@FXML
	private ImageView imageView;
	
	private Stage mainStage = Main.mainStage;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		imageView.setImage(new Image("MainScreenImage.png"));
	}
	@FXML
	public void setCityMap(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/cityMapScreen.fxml"));
			BorderPane cityMapScreen = loader.load();
			cityMapScreenController controller = loader.getController();
			controller.setPrevScene(cityMapButton.getScene());
			Scene scene = new Scene(cityMapScreen);
			mainStage.setTitle("Mapa de la ciudad");
			mainStage.setScene(scene);
			StageManager.updateMainStage();
			CityMapManager cityMapManager = CityMapManager.getInstance();
        	cityMapManager.initView();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	// Event Listener on Button[#lineScreenButton].onAction
	@FXML
	public void setLineScreen(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/busLineScreen.fxml"));
			GridPane busLineScreen = loader.load();
			busLineScreenController controller = loader.getController();
			controller.setPrevScene(lineScreenButton.getScene());
			Scene scene = new Scene(busLineScreen);
			mainStage.setTitle("Men� l�neas de colectivos");
			mainStage.setScene(scene);
			StageManager.updateMainStage();
			controller.getLineMapManager().initView();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Event Listener on Button[#travelScreenButton].onAction
	@FXML
	public void setTravelScreen(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/busPathScreen.fxml"));
			BorderPane busPathScreen = loader.load();
			busPathScreenController controller = loader.getController();
			Scene scene = new Scene(busPathScreen);
			controller.setPrevScene(travelScreenButton.getScene());
			mainStage.setTitle("Realizar viaje");
			mainStage.setScene(scene);
			StageManager.updateMainStage();
			controller.getPathManager().initView();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

