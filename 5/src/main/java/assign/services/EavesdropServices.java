package assign.services;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class EavesdropServices {
	private DataSource dataSource;
	private static EavesdropServices singleton;
		
	public static EavesdropServices sharedServices () {
		return singleton == null ? (singleton = new EavesdropServices ()) : singleton;
	}
	
	public void setDataSource (DataSource dataSource) {this.dataSource = dataSource; }
	public DataSource getDataSource () { return dataSource; }
	
	
	public PreparedStatement prepareStatement (String query, List<String> params) {
		PreparedStatement statement = null;
		try {
			 Connection conn = dataSource.getConnection();
			 statement = conn.prepareStatement(query);
			 int i = 1;
			 for (String param : params) {
				 statement.setString(i++, param);
			 }
		} catch (SQLException e) {
		}
		return statement;
	}
		
	public ResultSet executeQuery(PreparedStatement ps) {
		ResultSet results = null;
		try {
			results = ps.executeQuery();
		} catch (SQLException e) {
		}
		return results;
	}
	
	public void executeUpdate (PreparedStatement statement) {
		try {
			statement.executeUpdate();
		} catch (SQLException e) {
		}
	}
	
	private EavesdropServices () {}
}