package controllers.route;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import managers.AlertManager;
import managers.MapManager;
import models.BusStop;
import models.Route;
import models.Route.distanceUnits;
import models.utils.SelectTwoStop;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import db.dao.RouteDao;
import db.dao.impl.RouteDaoPG;
import exceptions.AddFailException;
import exceptions.DBConnectionException;
import javafx.event.ActionEvent;

import javafx.scene.control.ChoiceBox;

public class addRouteController implements Initializable{
	@FXML
	private TextField sourceStopField;
	@FXML
	private TextField destinationStopField;
	@FXML
	private TextField distanceField;
	@FXML
	private ChoiceBox<distanceUnits> distanceUnitBox;
	@FXML
	private Button addRouteButton;
	
	private BusStop sourceStop;
	private BusStop destinationStop;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		distanceUnitBox.getItems().add(distanceUnits.Kil�metros);
		distanceUnitBox.getItems().add(distanceUnits.Metros);
		distanceUnitBox.getItems().add(distanceUnits.Millas);
		distanceUnitBox.getSelectionModel().selectFirst();
	}
	public void setSourceStop(BusStop busStop) {
		sourceStop = busStop;
		sourceStopField.setText(sourceStop.getStopStreetName()+" "+sourceStop.getStopStreetNumber());
	}
	public void setDestinationStop(BusStop busStop) {
		destinationStop = busStop;
		destinationStopField.setText(destinationStop.getStopStreetName()+" "+destinationStop.getStopStreetNumber());
	}

	// Event Listener on Button[#addRouteButton].onAction
	@FXML
	public void addRoute(ActionEvent event) {
		Double distance;
		try {
			distance = Double.parseDouble(distanceField.getText().trim());
		}catch(NumberFormatException|NullPointerException e) {
			AlertManager.createAlert(AlertType.ERROR, "Error", "Ingrese la distancia entre ambas paradas correctamente.");
			return;
		}
		switch(distanceUnitBox.getSelectionModel().getSelectedItem()) {
			case Kil�metros:
				break;
			case Metros:
				distance *= 0.001;
				break;
			case Millas:
				distance *= 1.609344;
				break;
		}
		Route route = new Route(sourceStop,destinationStop,distance);
		RouteDao routeDao = new RouteDaoPG();
		try {
			routeDao.addData(route);
			MapManager mapManager = MapManager.getInstance();
			mapManager.addRouteMap(route);
		} catch (AddFailException|DBConnectionException e) {
			AlertManager.createAlert(AlertType.ERROR, "Error", e.getMessage());
		    return;
		}
		AlertManager.createAlert(AlertType.INFORMATION, "Exito", "Se ha agregado la calle correctamente.");
	    ((Stage) (addRouteButton.getScene().getWindow())).close();
	}

}