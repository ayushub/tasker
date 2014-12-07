package model;

public class Label {

	private int id;
	private String name;
	private int boardId;

	private int archived;

	public int getArchived() {
		return archived;
	}
	public void setArchived(int archived) {
		this.archived = archived;
	}
	public String getMyLabel() {
		return name;
	}

	public void setMyLabel(String myLabel) {
		this.name = myLabel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


}
