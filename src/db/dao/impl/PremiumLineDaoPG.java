package db.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import db.dao.DBConnection;
import db.dao.PremiumLineDao;
import exceptions.AddFailException;
import exceptions.DBConnectionException;
import exceptions.DeleteFailException;
import exceptions.ModifyFailException;
import javafx.scene.paint.Color;
import models.busline.BusLine;
import models.busline.PremiumLine;
import models.busline.PremiumLine.PremiumLineService;

public class PremiumLineDaoPG implements PremiumLineDao {
	private static final String INSERT_SQL =
			"INSERT INTO PremiumLine " + 
			"(name) " +
			"VALUES (?);";
	private static final String SELECT_SQL_PREMIUM_LINES_NO_SERVICES =
			"SELECT BusLine.name, color, seating_capacity " +
			"FROM BusLine, PremiumLine " +
			"WHERE BusLine.name = PremiumLine.name;";
	private static final String SELECT_SQL_PREMIUM_LINE_SERVICES = 
			"SELECT name_service " +
			"FROM PremiumLineServices " +
			"WHERE name_line = ?;";
	
	@Override
	public void addData(PremiumLine premiumLine) throws DBConnectionException, AddFailException {
		BusLineDaoPG busLineDaoPG = new BusLineDaoPG();
		busLineDaoPG.addData(premiumLine);
		try(Connection connection = DBConnection.getConnection()){
			try(PreparedStatement ps = connection.prepareStatement(INSERT_SQL)){
				ps.setString(1, premiumLine.getName());
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			throw new AddFailException("Error inesperado. Contacte con el administrador.");
		}
		PremiumLineServiceDaoPG premiumLineServiceDaoPG = new PremiumLineServiceDaoPG();
		premiumLineServiceDaoPG.AddData(premiumLine);
	}

	@Override
	//Los servicios son inmutables
	public void modifyData(PremiumLine premiumLine) throws DBConnectionException, ModifyFailException {
		BusLineDaoPG busLineDaoPG = new BusLineDaoPG();
		busLineDaoPG.modifyData(premiumLine);
		PremiumLineServiceDaoPG premiumLineServiceDaoPG = new PremiumLineServiceDaoPG();
		premiumLineServiceDaoPG.ModifyData(premiumLine);
	}

	@Override
	public void deleteData(PremiumLine premiumLine) throws DBConnectionException, DeleteFailException {
		BusLineDaoPG busLineDaoPG = new BusLineDaoPG();
		busLineDaoPG.deleteData(premiumLine);
	}
	
	@Override
	public List<PremiumLine> getAllPremiumLines() throws DBConnectionException{
		List<PremiumLine> ret = new ArrayList<>();
		try(Connection connection = DBConnection.getConnection()) {
			try(PreparedStatement ps = connection.prepareStatement(SELECT_SQL_PREMIUM_LINES_NO_SERVICES)){
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					String name = rs.getString(1);
					Color color = Color.valueOf(rs.getString(2));
					Integer seating_capacity = rs.getInt(3);
					
					HashSet<PremiumLineService> services = new HashSet<PremiumLineService>();
					try(PreparedStatement ps2 = connection.prepareStatement(SELECT_SQL_PREMIUM_LINE_SERVICES)) {
						ps2.setString(1, name);
						ResultSet rs2 = ps2.executeQuery();
						while(rs2.next()) {
							String service = rs2.getString(1);
							if (service.equals(PremiumLineService.WIFI.toString())) {
								services.add(PremiumLineService.WIFI);
							}
							else {
								services.add(PremiumLineService.AIR_CONDITIONING);
							}
						}
					}
					PremiumLine premiumLine = new PremiumLine(name, color, seating_capacity, services);
					ret.add(premiumLine);
				}
			}
		}
		catch(SQLException | DBConnectionException  e) {
			throw new DBConnectionException("Error inesperado. Contacte con el administrador.");
		}
		return ret;
	}

	private class PremiumLineServiceDaoPG {
		private static final String INSERT_SQL =
				"INSERT INTO PremiumLineServices " + 
				"(name_line, name_service) " +
				"VALUES (?, CAST(? as PremiumLineService));";
		private static final String DELETE_SQL = 
				"DELETE FROM PremiumLineServices " +
				"WHERE name_line=?;";
		
		private void AddData(PremiumLine premiumLine) throws DBConnectionException, AddFailException{
			try(Connection connection = DBConnection.getConnection()) {
				for(PremiumLine.PremiumLineService premiumLineService : premiumLine.getServices()) {
					try(PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
						ps.setString(1,premiumLine.getName());
						ps.setString(2, premiumLineService.toString());
						ps.executeUpdate();
					}
				}
			}
			catch (SQLException e) {
				throw new AddFailException("Error inesperado. Contacte con el administrador.");
			}
		}

		public void ModifyData(PremiumLine premiumLine) throws DBConnectionException, ModifyFailException {
			try {
				DeleteData(premiumLine);
				AddData(premiumLine);
			}catch(AddFailException|DeleteFailException e) {
				throw new ModifyFailException("Error inesperado. Contacte con el administrador.");
			}
		}

		private void DeleteData(PremiumLine premiumLine) throws DBConnectionException, DeleteFailException{
			try(Connection connection = DBConnection.getConnection()) {
				try(PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
					ps.setString(1, premiumLine.getName());
					ps.executeUpdate();
				}
			}
			catch(SQLException e) {
				throw new DeleteFailException("Error inesperado. Contacte con el administrador.");
			}
		}
	}

}
