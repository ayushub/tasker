package dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Board;


public class BoardDB {

	// Data Members
	public Board board;

	private java.sql.Connection connection;
	private java.sql.PreparedStatement resetRecords;
	private java.sql.PreparedStatement addRecord;
	private java.sql.PreparedStatement getRecords;
	private java.sql.PreparedStatement updateRecord;
	private java.sql.PreparedStatement archiveRecord;
	private java.sql.PreparedStatement removeRecord;
	private java.sql.PreparedStatement removeAllRecords;
	
	public BoardDB (String ip, String username, String password) {
		board = new Board();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://"+ ip +":3306/tasker",
					username, password);
			getRecords = connection.prepareStatement("SELECT * FROM Boards");
			
			resetRecords = connection.prepareStatement("ALTER TABLE `Boards` auto_increment = 3");

			addRecord = connection.prepareStatement("INSERT INTO Boards(" 
					+ " title)"
					+ "VALUES ( ?)", Statement.RETURN_GENERATED_KEYS);
			
			updateRecord = connection.prepareStatement("UPDATE `Boards` SET"
					+ "`title`= ?"
					+ "WHERE "
					+ "`id` = ?"); 
			
			archiveRecord = connection.prepareStatement("UPDATE `Boards` SET"
					+ "`archived`= ?"
					+ " WHERE "
					+ " `id` = ?"); 
			
			removeRecord = connection.prepareStatement("DELETE FROM `tasker`.`Boards` " +
					"WHERE `id`=?" );
			
			removeAllRecords = connection.prepareStatement("DELETE FROM `tasker`.`Boards` " +
					"WHERE 1" );
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	//reset Board
	public void resetBoard() throws SQLException{
		removeAllRecords.execute();
		resetRecords.execute();
	}

	//add Board
	public int addBoard () throws SQLException{
		addRecord.setString(1, this.board.getTitle());
		addRecord.executeUpdate();
		ResultSet getId = addRecord.getGeneratedKeys();
		int id = 0;
		if(getId.next()) {
			id = getId.getInt(1);
		}
		 return id;
	}

	//update Board
	public void updateBoard () throws SQLException{
		updateRecord.setString(1, this.board.getTitle());
		updateRecord.setInt(2, this.board.getId());
		updateRecord.executeUpdate();
	}

	//archive or unarchive record
	public void archiveUnarchiveBoard () throws SQLException{
		archiveRecord.setInt(1, this.board.getArchived());
		archiveRecord.setInt(2, this.board.getId());
		archiveRecord.executeUpdate();
	}

	//get Boards
	public ArrayList<Board> getAllBoards () throws SQLException{
		ArrayList<Board> myBoards = new ArrayList<Board>();
		ResultSet results = getRecords.executeQuery();
		
		while (results.next()) {
			Board tmp = new Board();
			tmp.setArchived(results.getInt("archived"));
			if(tmp.getArchived() == 0) {
				
				tmp.setId(results.getInt("id"));
				tmp.setTitle(results.getString("title"));

				myBoards.add(tmp);
			}
		}
		
		return myBoards;
	}
	
	//remove Board
	public void removeBoard () throws SQLException{
		removeRecord.setInt(1, this.board.getId());
		removeRecord.executeUpdate();
	}
	
//	close statements
	protected void finalize(){
//		attempt to close connection
		try {
			getRecords.close();
			addRecord.close();
			updateRecord.close();
			archiveRecord.close();
			removeRecord.close();
			connection.close();
		}
		// process SQLException on close operation
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}

}
