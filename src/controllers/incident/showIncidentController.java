package controllers.incident;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import application.Main;
import controllers.returnScene;
import db.dao.IncidentDao;
import db.dao.impl.IncidentDaoPG;
import exceptions.DBConnectionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import managers.AlertManager;
import managers.StageManager;
import models.BusStop;
import models.Incident;
import models.utils.MyHeap;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

public class showIncidentController implements Initializable,returnScene {
	@FXML
	private TableView<Incident> incidentTable;
	@FXML
	private TableColumn<Incident,BusStop> stopNumberColumn;
	@FXML
	private TableColumn<Incident,LocalDate> beginDateColumn;
	@FXML
	private TableColumn<Incident,LocalDate> endDateColumn;
	@FXML
	private TableColumn<Incident,String> descriptionColumn;
	@FXML
	private Button goToPrevSceneButton;
	
	private Scene previousScene;
	private ObservableList<Incident> incidentRow;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		incidentTable.setRowFactory( tv -> {
		    TableRow<Incident> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && !row.isEmpty() ) {
		        	Stage stage = new Stage(StageStyle.UTILITY);
		    		stage.initModality(Modality.APPLICATION_MODAL);
		    		try {
		    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/incident/modIncident.fxml"));
		    			Parent root = loader.load();
		    			modIncidentController controller = loader.getController();
		    			Incident incident = row.getItem();
		    			controller.setIncident(incident);
		    			Scene scene =  new Scene(root);
		    			stage.setTitle("Modificaci�n de incidencia");
		    	        stage.setScene(scene);
		    	        stage.showAndWait();
		    	        if(incident.getConcluded())
		    	        	this.incidentRow.remove(incident);
		    	        else
		    	        	this.incidentRow.sort(Incident::compareTo);
		    	        this.incidentTable.refresh();
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		        }
		    });
		    return row ;
		});
		incidentRow = FXCollections.observableArrayList();
		incidentTable.setItems(incidentRow);
		stopNumberColumn.setCellValueFactory(new PropertyValueFactory<>("busStopDisabled"));
		beginDateColumn.setCellValueFactory(new PropertyValueFactory<>("beginDate"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		incidentTable.setPlaceholder(new Label("No se encuentran incidencias activas en el sistema."));
		incidentTable.setStyle("-fx-font: normal 14pt verdana;");
		try {
			IncidentDao incidentDao = new IncidentDaoPG();
			MyHeap<Incident> heap = new MyHeap<>(Incident::compareTo);
			for(Incident incident: incidentDao.getAllInconcludedIncident()) {
				heap.push(incident);
			}
			while(!heap.empty()) {
				incidentRow.add(heap.top());
				heap.pop();
			}
		} catch (DBConnectionException e) {
			AlertManager.createAlert(AlertType.ERROR, "ERROR", e.getMessage()).showAndWait();
		}
	}
	@Override
	public void setPrevScene(Scene scene) {
		this.previousScene = scene;
	}
	@FXML
	@Override
	public void goToPrevScene(ActionEvent event) {
		Stage stage = (Stage) incidentTable.getScene().getWindow();
		stage.setTitle("Men� mapa de la ciudad");
		stage.setScene(previousScene);
		StageManager.updateMainStage();
	}
	
}
