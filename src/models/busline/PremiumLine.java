package models.busline;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.paint.Color;
import models.BusLineRoute;
import models.BusLineStop;

public class PremiumLine extends BusLine {
	public enum PremiumLineService{
		WIFI,AIR_CONDITIONING
	}
	private Set<PremiumLineService> services;
	private static final Double ticketPercentagePerUse = 0.1;
	private static final Double ticketPercentagePerService = 0.05;

	public PremiumLine() {
		super();
		services = new HashSet<PremiumLineService>();
	}
	public PremiumLine(String name, Color color, Integer seatingCapacity, Set<PremiumLineService> services) {
		super(name, color, seatingCapacity);
		this.services = services;
	}
	protected PremiumLine(String name,Color color) {
		super(name,color);
		services = new HashSet<PremiumLineService>();
	}
	public PremiumLine(List<BusLineStop> busStops,List<BusLineRoute> routes) {
		super(busStops,routes);
		services = new HashSet<PremiumLineService>();
	}
	public String getType() {
		return "Superior";
	}
    public Set<PremiumLineService> getServices() {
        return services;
    }
    public void setServices(Set<PremiumLineService> services) {
    	this.services = services;
    }
    public Boolean isService(PremiumLineService service) {
    	return this.services.contains(service);
    }
    public Boolean validateChanges(PremiumLine premiumLine) {
    	return this.services.equals(premiumLine.getServices()) && super.validateChanges(premiumLine);
    }
	@Override
	public Double calculateCost(BusLineRoute route) {
		Double extraPercentage = ticketPercentagePerUse + services.size() * ticketPercentagePerService;
		return route.getDistanceInKM()*super.ticketCostPerKM*(1+extraPercentage);		
	}
}
