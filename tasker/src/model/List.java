package model;

public class List implements Comparable<List>{

	// Data Members
	private int id;
	private String title;
	private int order;
	private int boardId;
	private int archived;

	// Getters and Setters
	public int getArchived() {
		return archived;
	}
	public void setArchived(int archived) {
		this.archived = archived;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getBoardId() {
		return boardId;
	}
	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	//List Sorter
	public int compareTo(List l)
	{
		return(order - l.getOrder());
	}



}
