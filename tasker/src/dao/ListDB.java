package dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.List;


public class ListDB {

	// Data Members
	public List list;

	private java.sql.Connection connection;
	private java.sql.PreparedStatement resetRecords;
	private java.sql.PreparedStatement addRecord;
	private java.sql.PreparedStatement getRecords; 
	private java.sql.PreparedStatement getRecordById;
	private java.sql.PreparedStatement getCountOfListForBoard; 
	private java.sql.PreparedStatement updateRecordTitle;
	private java.sql.PreparedStatement updateRecordOrder;
	private java.sql.PreparedStatement archiveRecordById;
	private java.sql.PreparedStatement archiveRecordByBoardId;
	private java.sql.PreparedStatement removeRecord;
	private java.sql.PreparedStatement removeAllRecords;

	public ListDB (String ip, String username, String password) {
		list = new List();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://"+ ip +":3306/tasker",
					username, password);
			getRecords = connection.prepareStatement("SELECT * FROM Lists");

			getRecordById = connection.prepareStatement("SELECT * FROM Lists WHERE id=?");

			getCountOfListForBoard = connection.prepareStatement("SELECT COUNT(*) AS listcount FROM Lists WHERE board_id=?");

			resetRecords = connection.prepareStatement("ALTER TABLE `Lists` auto_increment = 1");

			addRecord = connection.prepareStatement("INSERT INTO Lists(" 
					+ " title, l_order, board_id)"
					+ "VALUES (?, ?, ?)");

			updateRecordTitle = connection.prepareStatement("UPDATE `Lists` SET"
					+ "`title`= ?"
					+ "WHERE "
					+ "`id` = ?"); 

			updateRecordOrder = connection.prepareStatement("UPDATE `Lists` SET"
					+ "`l_order`= ?"
					+ " WHERE "
					+ " `id` = ?"); 
			
			archiveRecordById = connection.prepareStatement("UPDATE `Lists` SET"
					+ " `archived`= ?"
					+ " WHERE "
					+ " `id` = ?"); 
			
			archiveRecordByBoardId = connection.prepareStatement("UPDATE `Lists` SET"
					+ " `archived`= ?"
					+ " WHERE "
					+ " `board_id` = ?"); 

			removeRecord = connection.prepareStatement("DELETE FROM `tasker`.`Lists` " +
					"WHERE `id`=?" );

			removeAllRecords = connection.prepareStatement("DELETE FROM `tasker`.`Lists` " +
					"WHERE 1" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//reset List
	public void resetList() throws SQLException{
		removeAllRecords.execute();
		resetRecords.execute();
	}

	//add List
	public void addList () throws SQLException{
		getCountOfListForBoard.setInt(1, list.getBoardId());
		ResultSet r = getCountOfListForBoard.executeQuery();
		r.next();
		list.setOrder(r.getInt("listcount") + 1);
		r.close();

		addRecord.setString(1, this.list.getTitle());
		addRecord.setInt(2, this.list.getOrder());
		addRecord.setInt(3, this.list.getBoardId());
		addRecord.executeUpdate();
	}

	//update list
	public void updateListTitle () throws SQLException{
		updateRecordTitle.setString(1, this.list.getTitle());
		updateRecordTitle.setInt(2, this.list.getId());
		updateRecordTitle.executeUpdate();
	}

	//update list order
	public void updateListOrder () throws SQLException{
		getRecordById.setInt(1, list.getId());
		ResultSet findOrder = getRecordById.executeQuery();
		if(findOrder.next()) {
			int origOrder = findOrder.getInt("l_order");
			findOrder.close();
			if(origOrder == list.getOrder())
			{
				return;
			}

			ResultSet results = getRecords.executeQuery();
			if(origOrder > list.getOrder()) {
				while (results.next()) {
					List tmp = new List();
					tmp.setId(results.getInt("id"));
					tmp.setBoardId(results.getInt("board_id"));
					tmp.setOrder(results.getInt("l_order"));
					//Check if the list is in the same board
					//Check if the list lies between requiredOrder and origOrder 
					if((tmp.getBoardId() == list.getBoardId()) && (tmp.getOrder() < origOrder) && (tmp.getOrder() >= list.getOrder()) ){
						tmp.setOrder(tmp.getOrder() + 1);
						updateRecordOrder.setInt(1, tmp.getOrder());
						updateRecordOrder.setInt(2, tmp.getId());
						updateRecordOrder.executeUpdate();
					}
				}
			} else {
				while (results.next()) {
					List tmp = new List();
					tmp.setId(results.getInt("id"));
					tmp.setBoardId(results.getInt("board_id"));
					tmp.setOrder(results.getInt("l_order"));
					//Check if the list is in the same board
					//Check if the list lies between origOrder and requiredOrder
					if((tmp.getBoardId() == list.getBoardId()) && (tmp.getOrder() > origOrder) && (tmp.getOrder() <= list.getOrder()) ){
						tmp.setOrder(tmp.getOrder() - 1);
						updateRecordOrder.setInt(1, tmp.getOrder());
						updateRecordOrder.setInt(2, tmp.getId());
						updateRecordOrder.executeUpdate();

					}
				}
			}
			updateRecordOrder.setInt(1, this.list.getOrder());
			updateRecordOrder.setInt(2, this.list.getId());
			updateRecordOrder.executeUpdate();
			results.close();
		} else {
			findOrder.close();
		}

	}

	//archive or unarchive record
	public void archiveUnarchiveList (int level) throws SQLException{
		if( level == 1) {
			archiveRecordById.setInt(1, this.list.getArchived());
			archiveRecordById.setInt(2, this.list.getId());
			archiveRecordById.executeUpdate();
		} else {
			archiveRecordByBoardId.setInt(1, this.list.getArchived());
			archiveRecordByBoardId.setInt(2, this.list.getBoardId());
			archiveRecordByBoardId.executeUpdate();
		}
	}


	//get Lists
	public ArrayList<List> getAllListsForBoard (int boardId) throws SQLException{
		ArrayList<List> myLists = new ArrayList<List>();
		ResultSet results = getRecords.executeQuery();

		while (results.next()) {
			List tmp = new List();
			tmp.setBoardId(results.getInt("board_id"));
			tmp.setArchived(results.getInt("archived"));
			if((tmp.getBoardId() == boardId)
					&& (tmp.getArchived() == 0)) {
				tmp.setId(results.getInt("id"));
				tmp.setTitle(results.getString("title"));
				tmp.setOrder(results.getInt("l_order"));

				myLists.add(tmp);
			}
		}
		results.close();
		return myLists;
	}
	//remove Board
	public void removeBoard () throws SQLException{
		removeRecord.setInt(1, this.list.getId());
		addRecord.executeUpdate();
	}

	//	close statements
	protected void finalize(){
		//		attempt to close connection
		try {
			getRecords.close();
			getRecordById.close();
			getCountOfListForBoard.close();
			addRecord.close();
			updateRecordTitle.close();
			updateRecordOrder.close();
			archiveRecordById.close();
			archiveRecordByBoardId.close();
			removeRecord.close();
			removeAllRecords.close();
			connection.close();
		}
		// process SQLException on close operation
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}

}
