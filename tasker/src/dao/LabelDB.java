package dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Label;


public class LabelDB {

	// Data Members
	public Label label;

	private java.sql.Connection connection;
	private java.sql.PreparedStatement resetRecords;
	private java.sql.PreparedStatement addRecord;
	private java.sql.PreparedStatement getRecords;
	private java.sql.PreparedStatement getCountOfLabels; 
	private java.sql.PreparedStatement updateRecord;
	private java.sql.PreparedStatement archiveRecord;
	private java.sql.PreparedStatement removeRecord;
	private java.sql.PreparedStatement removeAllRecords;

	public LabelDB (String ip, String username, String password) {
		label = new Label();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://"+ ip +":3306/tasker",
					username, password);
			getRecords = connection.prepareStatement("SELECT * FROM Labels");

			getCountOfLabels = connection.prepareStatement("SELECT COUNT(*) AS labelcount FROM Labels WHERE board_id = ?");

			resetRecords = connection.prepareStatement("ALTER TABLE `Labels` auto_increment = 1");

			addRecord = connection.prepareStatement("INSERT INTO Labels(" 
					+ " name, board_id)"
					+ "VALUES ( ?, ?)");

			updateRecord = connection.prepareStatement("UPDATE `Labels` SET"
					+ "`name`= ?"
					+ "WHERE "
					+ "`id` = ?"); 

			archiveRecord = connection.prepareStatement("UPDATE `Labels` SET"
					+ " `archived` = ?"
					+ " WHERE "
					+ " `board_id` = ?"); 


			removeRecord = connection.prepareStatement("DELETE FROM `tasker`.`Labels` " +
					"WHERE `id`=?" );

			removeAllRecords = connection.prepareStatement("DELETE FROM `tasker`.`Labels` " +
					"WHERE 1" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//reset Labels
	public void resetLabel() throws SQLException{
		removeAllRecords.execute();
		resetRecords.execute();
	}

	//add Label
	public void addLabel() throws SQLException{
		getCountOfLabels.setInt(1, label.getBoardId());
		ResultSet labelCountQuery = getCountOfLabels.executeQuery();
		if(labelCountQuery.next())
		{
			int labelCount =  labelCountQuery.getInt("labelcount");
			if(labelCount < 6) {
				addRecord.setString(1, this.label.getName());
				addRecord.setInt(2, label.getBoardId());
				addRecord.executeUpdate();
			}
		}
	}

	//update Label
	public void updateLabel () throws SQLException{
		updateRecord.setString(1, this.label.getName());
		updateRecord.setInt(2, this.label.getId());
		updateRecord.executeUpdate();
	}

	//archive or unarchive record
	public void archiveUnarchiveLabel () throws SQLException{
		archiveRecord.setInt(1, this.label.getArchived());
		archiveRecord.setInt(2, this.label.getBoardId());
		archiveRecord.executeUpdate();
	}

	//get Labels
	public ArrayList<Label> getAllLabels () throws SQLException{
		ArrayList<Label> myLabels = new ArrayList<Label>();
		ResultSet results = getRecords.executeQuery();

		while (results.next()) {
			Label tmp = new Label();
			tmp.setBoardId(results.getInt("board_id"));
			tmp.setArchived(results.getInt("archived"));
			if((tmp.getBoardId() == label.getBoardId())
					&& (tmp.getArchived() == 0)) {
				tmp.setId(results.getInt("id"));
				tmp.setName(results.getString("name"));

				myLabels.add(tmp);

			} 
		}

		return myLabels;
	}

	//remove Label
	public void removeLabel () throws SQLException{
		removeRecord.setInt(1, this.label.getId());
		removeRecord.executeUpdate();
	}

	//	close statements
	protected void finalize(){
		//		attempt to close connection
		try {
			getRecords.close();
			getCountOfLabels.close(); 
			addRecord.close();
			archiveRecord.close();
			updateRecord.close();
			removeRecord.close();
			connection.close();
		}
		// process SQLException on close operation
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}

}
