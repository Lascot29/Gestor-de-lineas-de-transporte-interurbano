package controllers.line;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import controllers.returnScene;
import db.dao.CheapLineDao;
import db.dao.PremiumLineDao;
import db.dao.impl.CheapLineDaoPG;
import db.dao.impl.PremiumLineDaoPG;
import exceptions.AddFailException;
import exceptions.DBConnectionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import managers.AlertManager;
import managers.LineMapManager;
import managers.LineMapSelectorManager;
import managers.StageManager;
import models.BusLineRoute;
import models.BusStop;
import models.Route;
import models.busline.BusLine;
import models.busline.CheapLine;
import models.busline.PremiumLine;
import src.com.brunomnsilva.smartgraph.graphview.SmartGraphVertex;
import src.com.brunomnsilva.smartgraph.graphview.SmartGraphEdge;

public class LineRouteSelectorController implements Initializable,returnScene{
	@FXML
	private BorderPane borderPane;
	@FXML
	private Button goToPrevSceneButton;
	@FXML
	private Button saveButton;
	@FXML 
	private Button cleanRoutesButton;
	
	private LineMapManager lineManager;
	private LineMapSelectorManager selectorManager;
	private Scene prevScene;

	public void init(BusLine busLine) {
		selectorManager.setBusline(busLine);
		selectorManager.initView();
	}
	public LineRouteSelectorController() {
		selectorManager = new LineMapSelectorManager();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		borderPane.setCenter(selectorManager.getMapView());
		selectorManager.setEdgeDoubleClickAction((SmartGraphEdge<Route,BusStop> ed) ->{
			if(ed.getUnderlyingEdge().element() instanceof BusLineRoute)
				return;
			BusLineRoute busLineRoute = new BusLineRoute(selectorManager.getBusLine(),ed.getUnderlyingEdge().element()); 
			if(!selectorManager.contains(busLineRoute)) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/line/EstimatedTimeRoute.fxml"));
					Parent root = loader.load();
					EstimatedTimeRouteController controller = loader.getController();
					controller.setBusLineRoute(busLineRoute);
					Scene scene = new Scene(root);
					Stage stage = new Stage();
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.setScene(scene);
					stage.showAndWait();
					if(controller.isNewRoute()) {
						System.out.println(busLineRoute);
						checkRoutePath(busLineRoute);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		selectorManager.setVertexDoubleClickAction((SmartGraphVertex<BusStop> b) -> 
			selectorManager.toggleStop(b.getUnderlyingVertex().element())
		);
	}
	public void checkRoutePath(BusLineRoute newRoute) {
		BusLine busLine = selectorManager.getBusLine();
		if(busLine.getRoutes().isEmpty()) {
			selectorManager.addStopLine(newRoute.getSourceStop());
			selectorManager.addStopLine(newRoute.getDestinationStop());
			selectorManager.addRouteLine(newRoute);
		}
		else {
			BusStop firstStop = busLine.getBeginStop();
			BusStop endStop = busLine.getEndStop();
			if(firstStop.equals(newRoute.getDestinationStop()) && !selectorManager.contains(newRoute.getSourceStop())) {
				selectorManager.addStopLine(newRoute.getSourceStop());
				selectorManager.addRouteLine(newRoute);
			}
			else if(endStop.equals(newRoute.getSourceStop()) && !selectorManager.contains(newRoute.getDestinationStop())) {
				selectorManager.addStopLine(newRoute.getDestinationStop());
				selectorManager.addRouteLine(newRoute);
			}
			else {
				AlertManager.createAlert(AlertType.ERROR,"ERROR","ERROR: Debe seleccionar una calle que contin�e el recorrido actual.");
			}
		}
	}
    public void setManager(LineMapManager lineManager) {
    	this.lineManager=lineManager;
    }
	@FXML
	public void cleanRoutes(ActionEvent event) {
		selectorManager.clear();
	}
	@FXML
	public void save(ActionEvent event) {
		BusLine busLine = selectorManager.getBusLine();
		if(busLine.getRoutes().isEmpty()) {
			AlertManager.createAlert(AlertType.ERROR, "ERROR", "Debe seleccionar al menos una calle para el recorrido.");
			return;
		}
		if(busLine.getType()=="Econ�mica") {
			CheapLineDao cheapLineDao = new CheapLineDaoPG();
			try {
				cheapLineDao.addData((CheapLine) busLine);
			} catch (AddFailException|DBConnectionException e) {
				AlertManager.createAlert(AlertType.ERROR, "ERROR", e.getMessage());
				return;
			}
		}
		else if(busLine.getType()=="Superior") {
			PremiumLineDao premiumLineDao = new PremiumLineDaoPG();
			try {
				premiumLineDao.addData((PremiumLine) busLine);
			} catch (AddFailException|DBConnectionException e) {
				AlertManager.createAlert(AlertType.ERROR, "ERROR", e.getMessage());
				return;
			}
		}
		lineManager.addLine(busLine);
		AlertManager.createAlert(AlertType.INFORMATION, "EXITO", "Se cre� la linea exitosamente.");
		((Stage) saveButton.getScene().getWindow()).close();
	}
	@Override
	public void setPrevScene(Scene scene) {
		this.prevScene=scene;
	}
	@FXML
	@Override
	public void goToPrevScene(ActionEvent event) {
		((Stage) goToPrevSceneButton.getScene().getWindow()).setScene(prevScene);
		StageManager.updateMainStage();
	}

}
