package dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Card;

public class CardDB {

	// Data Members
	public Card card;

	private java.sql.Connection connection;
	private java.sql.PreparedStatement resetRecords;
	private java.sql.PreparedStatement addRecord;
	private java.sql.PreparedStatement getRecords; 
	private java.sql.PreparedStatement getRecordById;
	private java.sql.PreparedStatement getCountOfCardForList; 
	private java.sql.PreparedStatement updateRecordTitle;
	private java.sql.PreparedStatement updateRecordLabel;
	private java.sql.PreparedStatement updateRecordMember;
	private java.sql.PreparedStatement updateRecordOrder;
	private java.sql.PreparedStatement updateRecordDesc;
	private java.sql.PreparedStatement updateRecordDueDate;
	private java.sql.PreparedStatement archiveRecordById;
	private java.sql.PreparedStatement archiveRecordByListId;
	private java.sql.PreparedStatement removeRecord;
	private java.sql.PreparedStatement removeAllRecords;

	public CardDB (String ip, String username, String password) {
		card = new Card();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://"+ ip +":3306/tasker",
					username, password);
			getRecords = connection.prepareStatement("SELECT * FROM Cards");

			getRecordById = connection.prepareStatement("SELECT * FROM Cards WHERE id=?");

			getCountOfCardForList = connection.prepareStatement("SELECT COUNT(*) AS cardcount FROM Cards WHERE list_id=? ");

			resetRecords = connection.prepareStatement("ALTER TABLE `Cards` auto_increment = 1");

			addRecord = connection.prepareStatement("INSERT INTO Cards(" 
					+ " title, c_order, list_id)"
					+ "VALUES (?, ?, ?)");

			updateRecordTitle = connection.prepareStatement("UPDATE `Cards` SET"
					+ "`title`= ?"
					+ " WHERE "
					+ "`id` = ?"); 

			updateRecordLabel = connection.prepareStatement("UPDATE `Cards` SET"
					+ "`label_id`= ?"
					+ " WHERE "
					+ "`id` = ?"); 

			updateRecordMember = connection.prepareStatement("UPDATE `Cards` SET"
					+ "`member_id`= ?"
					+ " WHERE "
					+ "`id` = ?"); 

			updateRecordOrder = connection.prepareStatement("UPDATE `Cards` SET"
					+ "`c_order`= ?"
					+ " WHERE "
					+ " `id` = ?"); 

			updateRecordDesc = connection.prepareStatement("UPDATE `Cards` SET"
					+ "`description`= ?"
					+ " WHERE "
					+ " `id` = ?"); 

			updateRecordDueDate = connection.prepareStatement("UPDATE `Cards` SET"
					+ "`due_date`= ?"
					+ " WHERE "
					+ " `id` = ?"); 

			archiveRecordById = connection.prepareStatement("UPDATE `Cards` SET"
					+ " `archived`= ?"
					+ " WHERE "
					+ " `id` = ?"); 

			archiveRecordByListId = connection.prepareStatement("UPDATE `Cards` SET"
					+ " `archived`= ?"
					+ " WHERE "
					+ " `list_id` = ?"); 

			removeRecord = connection.prepareStatement("DELETE FROM `tasker`.`Cards` " +
					"WHERE `id`=?" );

			removeAllRecords = connection.prepareStatement("DELETE FROM `tasker`.`Cards` " +
					"WHERE 1" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//reset card
	public void resetCards() throws SQLException{
		removeAllRecords.execute();
		resetRecords.execute();
	}

	//add card
	public void addCard () throws SQLException{
		getCountOfCardForList.setInt(1, card.getListId());
		ResultSet r = getCountOfCardForList.executeQuery();
		r.next();
		card.setOrder(r.getInt("cardcount") + 1);
		r.close();

		addRecord.setString(1, this.card.getTitle());
		addRecord.setInt(2, this.card.getOrder());
		addRecord.setInt(3, this.card.getListId());
		addRecord.executeUpdate();
	}

	//update card title
	public void updateCardTitle () throws SQLException{
		updateRecordTitle.setString(1, this.card.getTitle());
		updateRecordTitle.setInt(2, this.card.getId());
		updateRecordTitle.executeUpdate();
	}

	//update card label
	public void updateCardLabel () throws SQLException{
		updateRecordLabel.setInt(1, this.card.getLabel_id());
		updateRecordLabel.setInt(2, this.card.getId());
		updateRecordLabel.executeUpdate();
	}

	//update card member
	public void updateCardMember () throws SQLException{
		updateRecordMember.setInt(1, this.card.getMember_id());
		updateRecordMember.setInt(2, this.card.getId());
		updateRecordMember.executeUpdate();
	}

	//update card - move to other list
	public void updateCardLocation () throws SQLException{
		getRecordById.setInt(1, card.getId());
		ResultSet getDetails = getRecordById.executeQuery();
		if(getDetails.next()) {
			this.card.setDueDate(getDetails.getDate("due_date"));
			this.card.setDescription(getDetails.getString("description"));
			this.card.setLabel_id(getDetails.getInt("label_id"));
			this.card.setMember_id(getDetails.getInt("member_id"));
			this.card.setTitle(getDetails.getString("title"));
			removeCard();
			addCard();
		}

	}

	//update card order
	public void updateCardOrder () throws SQLException{
		getRecordById.setInt(1, card.getId());
		ResultSet findOrder = getRecordById.executeQuery();
		if(findOrder.next()) {
			int origOrder = findOrder.getInt("c_order");
			findOrder.close();
			if(origOrder == card.getOrder())
			{
				return;
			}
			ResultSet results = getRecords.executeQuery();
			if(origOrder > card.getOrder()) { 
				while (results.next()) {
					Card tmp = new Card();
					tmp.setId(results.getInt("id"));
					tmp.setListId(results.getInt("list_id"));
					tmp.setOrder(results.getInt("c_order"));
					//Check if the list is in the same board
					//Check if the list lies between requiredOrder and origOrder 
					if((tmp.getListId() == card.getListId()) && (tmp.getOrder() < origOrder) && (tmp.getOrder() >= card.getOrder()) ){

						tmp.setOrder(tmp.getOrder() + 1);
						updateRecordOrder.setInt(1, tmp.getOrder());
						updateRecordOrder.setInt(2, tmp.getId());
						updateRecordOrder.executeUpdate();
					}
				}
			} else {
				while (results.next()) {
					Card tmp = new Card();
					tmp.setId(results.getInt("id"));
					tmp.setListId(results.getInt("list_id"));
					tmp.setOrder(results.getInt("c_order"));
					//Check if the list is in the same board
					//Check if the list lies between origOrder and requiredOrder
					if((tmp.getListId() == card.getListId()) && (tmp.getOrder() > origOrder) && (tmp.getOrder() <= card.getOrder()) ){
						tmp.setOrder(tmp.getOrder() - 1);
						updateRecordOrder.setInt(1, tmp.getOrder());
						updateRecordOrder.setInt(2, tmp.getId());
						updateRecordOrder.executeUpdate();

					}
				}
			}
			updateRecordOrder.setInt(1, this.card.getOrder());
			updateRecordOrder.setInt(2, this.card.getId());
			updateRecordOrder.executeUpdate();
			results.close();
		} else {
			findOrder.close();
		}

	}

	//update description
	public void updateDesc() throws SQLException {
		updateRecordDesc.setString(1, card.getDescription());
		updateRecordDesc.setInt(2, card.getId());
		updateRecordDesc.executeUpdate();
	}

	//update due date
	public void updateDueDate () throws SQLException {
		updateRecordDueDate.setDate(1, card.getDueDate());
		updateRecordDueDate.setInt(2, card.getId());
		updateRecordDueDate.executeUpdate();
	}

	//archive or unarchive record
	public void archiveUnarchiveCard (int level) throws SQLException{
		if( level == 1) {
			archiveRecordById.setInt(1, this.card.getArchived());
			archiveRecordById.setInt(2, this.card.getId());
			archiveRecordById.executeUpdate();
		} else {
			archiveRecordByListId.setInt(1, this.card.getArchived());
			archiveRecordByListId.setInt(2, this.card.getListId());
			archiveRecordByListId.executeUpdate();
		}
	}

	//get a Card
	public Card getCardById() throws SQLException {
		Card myCard = new Card();
		myCard.setId(card.getId());
		getRecordById.setInt(1, card.getId());
		ResultSet getDetails = getRecordById.executeQuery();
		if(getDetails.next()) {
			myCard.setDueDate(getDetails.getDate("due_date"));
			myCard.setDescription(getDetails.getString("description"));
			myCard.setLabel_id(getDetails.getInt("label_id"));
			myCard.setMember_id(getDetails.getInt("member_id"));
			myCard.setTitle(getDetails.getString("title"));
		}
		return myCard;
	}

	//get all cards of a list
	public ArrayList<Card> getAllCardsForList (int listId) throws SQLException{
		ArrayList<Card> myCards = new ArrayList<Card>();
		ResultSet results = getRecords.executeQuery();

		while (results.next()) {
			Card tmp = new Card();
			tmp.setListId(results.getInt("list_id"));
			tmp.setArchived(results.getInt("archived"));
			if((tmp.getListId() == listId) 
					&& (tmp.getArchived() == 0)){
				tmp.setId(results.getInt("id"));
				tmp.setTitle(results.getString("title"));
				tmp.setOrder(results.getInt("c_order"));

				myCards.add(tmp);
			}
		}
		results.close();
		return myCards;
	}
	//remove Card
	public void removeCard () throws SQLException{
		removeRecord.setInt(1, this.card.getId());
		removeRecord.executeUpdate();
	}

	//	close statements
	protected void finalize(){
		//		attempt to close connection
		try {
			getRecords.close();
			getRecordById.close();
			getCountOfCardForList.close();
			addRecord.close();
			updateRecordTitle.close();
			updateRecordLabel.close();
			updateRecordMember.close();
			updateRecordOrder.close();
			updateRecordDesc.close();
			updateRecordDueDate.close();
			archiveRecordById.close();
			archiveRecordByListId.close();
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
