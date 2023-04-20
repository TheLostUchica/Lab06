package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Date d = rs.getDate("Data");
				LocalDate l = d.toLocalDate();

				Rilevamento r = new Rilevamento(rs.getString("Localita"), l, rs.getInt("Umidita"));
				
				//LocalDate <------- Date
				
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		List<Rilevamento> rilevamenti = new LinkedList<>();
		
		String sql = "SELECT Localita, Data, Umidita FROM situazione s WHERE MONTH(s.data) = ? AND s.Localita = ? ORDER BY data ASC";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			st.setString(2, localita);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				Date d = res.getDate("Data");
				LocalDate l = d.toLocalDate();

				Rilevamento r = new Rilevamento(res.getString("Localita"), l, res.getInt("Umidita"));
				
				rilevamenti.add(r);
			}
			
			st.close();
			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Double getUmiditaMedia(int mese, String localita) {
		
		String sql = "SELECT AVG(s.umidita) AS lei FROM situazione s WHERE s.localita=? AND MONTH(s.data) = ?";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(2, mese);
			st.setString(1, localita);
			ResultSet res = st.executeQuery();
			
			res.first();
			
			Double d = res.getDouble("lei");
			
			st.close();
			conn.close();
			
			return d;
		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


}
