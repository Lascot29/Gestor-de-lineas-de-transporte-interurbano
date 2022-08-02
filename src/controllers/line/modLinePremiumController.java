package controllers.line;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

import db.dao.PremiumLineDao;
import db.dao.impl.PremiumLineDaoPG;
import exceptions.DBConnectionException;
import exceptions.ModifyFailException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import managers.AlertManager;
import models.busline.PremiumLine;
import models.busline.PremiumLine.PremiumLineService;

public class modLinePremiumController extends modLineController{
	@FXML
    private Label servicesLabel;
	@FXML
    private CheckBox airService;
	@FXML
	private CheckBox wifiService;
	private BooleanProperty wifiServiceProperty;
	private BooleanProperty airServiceProperty;
	private PremiumLine premiumLineToModify;
	private PremiumLine modifiedPremiumLine;
	private class PremiumLineListener implements ChangeListener<Object> {
		@Override
		public void changed(ObservableValue<? extends Object> arg0, Object arg1, Object arg2) {
			HashSet<PremiumLineService> services = new HashSet<>();
			if (wifiService.isSelected()) {
				services.add(PremiumLineService.WIFI);
			}
			if (airService.isSelected()) {
				services.add(PremiumLineService.AIR_CONDITIONING);
			}
			modifiedPremiumLine.setServices(services);

			if (modifiedPremiumLine.validateChanges(premiumLineToModify)) {
				confirmChangesButton.setDisable(true);
			}
			else {
				confirmChangesButton.setDisable(false);
			}
		}
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		super.initialize(arg0, arg1);
		
		wifiServiceProperty = new SimpleBooleanProperty();
		wifiServiceProperty.bind(wifiService.selectedProperty());
		
		airServiceProperty = new SimpleBooleanProperty();
		airServiceProperty.bind(wifiService.selectedProperty());
	}
    @FXML
    void confirmChanges(ActionEvent event) {
    	super.confirmChanges();
    	premiumLineToModify.setServices(modifiedPremiumLine.getServices());
    	lineMapManager.chargeLine(premiumLineToModify);

    	PremiumLineDao cheapLineDao = new PremiumLineDaoPG();
    	try {
    		cheapLineDao.modifyData(premiumLineToModify);
    	}
    	catch(DBConnectionException | ModifyFailException e) {
			AlertManager.createAlert(AlertType.ERROR, "ERROR", e.getMessage()).showAndWait();
			return;
    	}

    	((Stage)servicesLabel.getScene().getWindow()).close();
    }
    @FXML
    void restoreChanges(ActionEvent event) {
    	super.restoreChanges();
    	wifiService.setSelected(premiumLineToModify.isService(PremiumLineService.WIFI));
    	airService.setSelected(premiumLineToModify.isService(PremiumLineService.AIR_CONDITIONING));
    }

    void setPremiumLine(PremiumLine premiumLineToModify) {
    	this.premiumLineToModify = premiumLineToModify;
    	this.modifiedPremiumLine = new PremiumLine();
    	setBusLine(premiumLineToModify, modifiedPremiumLine);
    	
    	wifiService.setSelected(premiumLineToModify.isService(PremiumLineService.WIFI));
    	airService.setSelected(premiumLineToModify.isService(PremiumLineService.AIR_CONDITIONING));
    	super.restoreChanges();
    	
		colorProperty.addListener(new PremiumLineListener());
		seatingCapacityProperty.addListener(new PremiumLineListener());
		wifiServiceProperty.addListener(new PremiumLineListener());
		airServiceProperty.addListener(new PremiumLineListener());
    }
}
