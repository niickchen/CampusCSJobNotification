
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobDB {
	private String db_pass;
	private String db_ip;
	private String db_name;
	private String db_user;
	private int db_port;

	public JobDB(String db_ip, String db_name, String db_user, String db_pass,
			int db_port) {
		this.db_pass = db_pass;
		this.db_name = db_name;
		this.db_user = db_user;
		this.db_ip = db_ip;
		this.db_port = db_port;
	}

	// default port 3306
	public JobDB(String db_ip, String db_name, String db_user, String db_pass) {
		this(db_ip, db_name, db_user, db_pass, 3306);
	}

	public String getDbIp() {
		return this.db_ip;
	}

	public String getDbPass() {
		return this.db_pass;
	}

	public String getDbUser() {
		return this.db_user;
	}

	public String getDbName() {
		return this.db_name;
	}

	public int getDbPort() {
		return this.db_port;
	}

	private Connection conn = null;

	public int connect() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		// connect:

		try {
			Class.forName ("com.mysql.jdbc.Driver").newInstance (); // driver
			conn = DriverManager.getConnection(
					"jdbc:mysql://" + db_ip + ":" + db_port + "/" + db_name,
					db_user, db_pass);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		if (conn != null)
			return 1; // success
		else
			return 0; // failure
	}

	//TD
	public List<String> selectOneColumn(String table, String col) throws SQLException {
		
		
		String selectQuery = "SELECT " + col + " FROM " + table;
		
		Statement st = conn.createStatement();
		// execute the query
	    ResultSet rs = st.executeQuery(selectQuery);
	    
	    int i = 1;
	    List<String> arr = new ArrayList<String>();
	    
	    while(rs.next()) {
	    	arr.add(rs.getString(i++));
	    	
	    }
	    
	    return arr;
	}
	
	// check if connect successfully beforehand
	public void insert(String table, String id, String title, String type, String pay,
			String description) throws SQLException {

		String insertString = "INSERT IGNORE INTO " + table + " (`ID`, `Title`, `Type`, `Pay`, `Description(HTML)`) VALUES (?,?,?,?,?)"; // item will be skipped if id exists
		// TD: will update and compare edit time to decide if skip or replace older posts!
		// TD: also save text version description later
		
		// creates the statement
		PreparedStatement insertStatement = conn.prepareStatement(insertString);

		insertStatement.setString(1, id);
		insertStatement.setString(2, title);
		insertStatement.setString(3, type);
		insertStatement.setString(4, pay);
		insertStatement.setString(5, description);
		
		//System.out.println(insertStatement); // test
		insertStatement.execute();
		insertStatement.close();
		System.out.println("Inserted successfully.");
	}

	public void updateInt(String table, String id, String updateColumn, int updateValue)
			throws SQLException {
		String updateString = "UPDATE " + table + " SET ? = ? WHERE ID = ?";
		PreparedStatement updateStatement = conn.prepareStatement(updateString);
		updateStatement.setString(1, updateColumn);
		updateStatement.setInt(2, updateValue);
		updateStatement.setString(3, id);
		updateStatement.execute();
		updateStatement.close();
		System.out.println("Updated successfully.");
	}

	public void updateString(String table, String id, String updateColumn, String updateValue)
			throws SQLException {
		String updatestring = "UPDATE " + table + " SET ? = ? WHERE ID = ?";
		PreparedStatement updateStatement = conn.prepareStatement(updatestring);
		updateStatement.setString(1, updateColumn);
		updateStatement.setString(2, updateValue);
		updateStatement.setString(3, id);
		updateStatement.execute();
		updateStatement.close();
		System.out.println("Updated successfully.");
	}

	// close the connection
	public void close() throws SQLException {
		conn.close();
	}

}
