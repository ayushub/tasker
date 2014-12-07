package dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Member;


public class MemberDB {

	// Data Members
	public Member member;

	private java.sql.Connection connection;
	private java.sql.PreparedStatement resetRecords;
	private java.sql.PreparedStatement addRecord;
	private java.sql.PreparedStatement getRecords;
	private java.sql.PreparedStatement updateRecord;
	private java.sql.PreparedStatement archiveRecord;
	private java.sql.PreparedStatement removeRecord;
	private java.sql.PreparedStatement removeAllRecords;
	
	public MemberDB (String ip, String username, String password) {
		member = new Member();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://"+ ip +":3306/tasker",
					username, password);
			getRecords = connection.prepareStatement("SELECT * FROM Members");
			
			resetRecords = connection.prepareStatement("ALTER TABLE `Members` auto_increment = 1");

			addRecord = connection.prepareStatement("INSERT INTO Members(" 
					+ " name)"
					+ "VALUES ( ?)");
			
			updateRecord = connection.prepareStatement("UPDATE `Members` SET"
					+ "`name`= ?"
					+ "WHERE "
					+ "`id` = ?"); 
			
			archiveRecord = connection.prepareStatement("UPDATE `Members` SET"
					+ "`archived`= ?"
					+ " WHERE "
					+ " `id` = ?"); 

			
			removeRecord = connection.prepareStatement("DELETE FROM `tasker`.`Members` " +
					"WHERE `id`=?" );
			
			removeAllRecords = connection.prepareStatement("DELETE FROM `tasker`.`Members` " +
					"WHERE 1" );
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	//reset Members
	public void resetMember() throws SQLException{
		removeAllRecords.execute();
		resetRecords.execute();
	}

	//add Member
	public void addMember () throws SQLException{
		addRecord.setString(1, this.member.getName());
		addRecord.executeUpdate();
	}

	//update Member
	public void updateMember () throws SQLException{
		updateRecord.setString(1, this.member.getName());
		updateRecord.setInt(2, this.member.getId());
		updateRecord.executeUpdate();
	}
	
	//archive or unarchive record
	public void archiveUnarchiveMember () throws SQLException{
		archiveRecord.setInt(1, this.member.getArchived());
		archiveRecord.setInt(2, this.member.getId());
		archiveRecord.executeUpdate();
	}
	
	//get Members
	public ArrayList<Member> getAllMembers () throws SQLException{
		ArrayList<Member> myMembers = new ArrayList<Member>();
		ResultSet results = getRecords.executeQuery();
		
		while (results.next()) {
			Member tmp = new Member();
			tmp.setArchived(results.getInt("archived"));
			if(tmp.getArchived() == 0) {
				tmp.setId(results.getInt("id"));
				tmp.setName(results.getString("name"));
				
				myMembers.add(tmp);
			}
		}
		
		return myMembers;
	}
	
	//remove Member
	public void removeMember () throws SQLException{
		removeRecord.setInt(1, this.member.getId());
		removeRecord.executeUpdate();
	}
	
//	close statements
	protected void finalize(){
//		attempt to close connection
		try {
			getRecords.close();
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
