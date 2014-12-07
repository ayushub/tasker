package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import model.Board;
import model.Card;
import model.Label;
import model.List;
import model.Member;
import dao.BoardDB;
import dao.CardDB;
import dao.LabelDB;
import dao.ListDB;
import dao.MemberDB;

public class BoardController {

	public static final int ASSIGN_MEMBER = 5;
	public static final int ASSIGN_LABEL = 4;
	public static final int EDIT_TITLE = 1;
	public static final int EDIT_ORDER = 2;
	public static final int RELOCATE_CARD = 3;
	private static String ip, username, password;

	//constructor
	public BoardController(String ip, String username, String password){
		BoardController.ip = ip;
		BoardController.username = username;
		BoardController.password = password;
		MemberDB user = new MemberDB(ip, username, password);
		try {
			if(user.getAllMembers().isEmpty()) {
				user.member.setName("Yourself");
				user.addMember();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//display a board 
	public String displayBoard(int id) {
		StringBuilder boardDisplay = new StringBuilder();
		ListDB getLists = new ListDB(ip, username, password);
		CardDB getCards = new CardDB(ip, username, password);
		ArrayList<List> myLists;
		try {
			myLists = getLists.getAllListsForBoard(id);
			ArrayList<Card> myCards;
			for(List l: myLists) {
				boardDisplay.append("|--- " + l.getTitle() +"\n");
				myCards = getCards.getAllCardsForList(l.getId());
				for (Card c: myCards) {
					boardDisplay.append("  -|--" + c.getTitle() +"\n");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return boardDisplay.toString();

	}

	//clear and reset tasker
	public void resetTasker() {
		BoardDB boardVanisher = new BoardDB(ip, username, password);
		ListDB listVanisher = new ListDB(ip, username, password);
		CardDB cardVanisher = new CardDB(ip, username, password);
		MemberDB memberVanisher = new MemberDB(ip, username, password);
		LabelDB labelVanisher = new LabelDB(ip, username, password);
		try {
			labelVanisher.resetLabel();
			memberVanisher.resetMember();
			cardVanisher.resetCards();
			listVanisher.resetList();
			boardVanisher.resetBoard();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//list all boards
	public String listAllBoards() {
		BoardDB boardFetch = new BoardDB(ip, username, password);
		ArrayList<Board> myBoards;
		StringBuilder listofBoards = new StringBuilder();
		try {
			myBoards = boardFetch.getAllBoards();
			for(Board b: myBoards) {
				listofBoards.append(b.getId()+". " + b.getTitle()+ "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listofBoards.toString();
	}

	//add a new board
	public int createBoard(String title) {
		System.out.println("creating board");
		BoardDB newBoard = new BoardDB(ip, username, password);
		LabelDB newLabelSet = new LabelDB(ip, username, password);
		System.out.println(title);
		newBoard.board.setTitle(title);
		int boardId = 0;
		try {

			boardId = newBoard.addBoard();
			if(boardId != 0) {
				newLabelSet.label.setBoardId(boardId);
				for (int i=1; i<7; i++) {
					newLabelSet.label.setName("Label " + i );
					newLabelSet.addLabel();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return boardId;

	}
	//edit board rename
	public String editBoard(int id, String name) {

		BoardDB editor = new BoardDB(ip, username, password);
		editor.board.setId(id);
		editor.board.setTitle(name);
		try {
			editor.updateBoard();
		} catch (SQLException e) {
			e.printStackTrace();
			return ("Problem Creating Board: " );
		}
		return ("Edited Board: "+ name);
	}

	//archive board
	public void archiveBoard(int id) {
		BoardDB archiver = new BoardDB(ip, username, password);
		archiver.board.setArchived(1);
		archiver.board.setId(id);
		try {
			archiver.archiveUnarchiveBoard();
			archiveList(0, id, 2);
			archiveLabels(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//add new list to board
	public String createList(String title, int boardId) {
		System.out.println("creating list "+ title);
		ListDB newList = new ListDB(ip, username, password);
		System.out.println(title);
		newList.list.setTitle(title);
		newList.list.setBoardId(boardId);
		try {

			newList.addList();
		} catch (SQLException e) {
			e.printStackTrace();
			return ("Problem Creating List" );
		}
		return ("Created List: "+ title);

	}
	//list all lists of a board
	public String listAllListsForBoard(int boardId) {
		ListDB listFetch = new ListDB(ip, username, password);
		ArrayList<List> myLists;
		StringBuilder listofLists = new StringBuilder();
		try {
			myLists = listFetch.getAllListsForBoard(boardId);
			Collections.sort(myLists);
			for(List l: myLists) {
				listofLists.append(l.getId()+". " + l.getTitle()+ "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listofLists.toString();
	}

	//edit list on board -rename/reorder
	public String editListOnBoard(int id, String name, int order, int boardId) {

		ListDB editor = new ListDB(ip, username, password);
		editor.list.setId(id);
		editor.list.setBoardId(boardId);
		try {
			if(order != -1) { 
				editor.list.setOrder(order);
				editor.updateListOrder();
			}
			else {
				editor.list.setTitle(name);
				editor.updateListTitle();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ("Problem updating List: " );
		}
		return ("Edited List: "+ name);
	}

	//archive lists on a board
	public void archiveList(int id, int boardId, int level) {
		ListDB archiver = new ListDB(ip, username, password);
		archiver.list.setArchived(1);
		archiver.list.setId(id);
		archiver.list.setBoardId(boardId);
		try {
			archiver.archiveUnarchiveList(level);

			//check if list is to archived
			if(level == 1) {
				level++;
				archiveCard(0, id, level);
			} else { //if board is to be archived
				//gather all lists
				ArrayList<List> myLists = archiver.getAllListsForBoard(boardId);
				for(List l: myLists) {
					//archive cards assoc with each list on board
					archiveCard(0, l.getId(), level);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//add new card to a list of a board
	public String createCard(String title, int listId) {
		System.out.println("creating card "+ title);
		CardDB newCard = new CardDB(ip, username, password);
		newCard.card.setTitle(title);
		newCard.card.setListId(listId);
		try {

			newCard.addCard();
		} catch (SQLException e) {
			e.printStackTrace();
			return ("Problem Creating Card" );
		}
		return ("Created Card: "+ title);

	}

	//edit card on a list - rename/reorder
	public String editCardOnList(int id, String strValue, int value, int listId, int choice) {

		CardDB editor = new CardDB(ip, username, password);
		editor.card.setId(id);
		editor.card.setListId(listId);
		try {
			switch(choice) {
			case EDIT_TITLE: 
				editor.card.setTitle(strValue);
				editor.updateCardTitle();
				break;
			case EDIT_ORDER:
				editor.card.setOrder(value);
				editor.updateCardOrder();
				break;
			case RELOCATE_CARD:
				editor.updateCardLocation();
				break;
			case ASSIGN_LABEL:
				editor.card.setLabel_id(value);
				editor.updateCardLabel();
				break;
			case ASSIGN_MEMBER:
				editor.card.setMember_id(value);
				editor.updateCardMember();
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ("Problem updating List: " );
		}
		return ("Edited Card #"+ id);
	}

	//list all cards
	public String listAllCardsForList(int listId) {
		CardDB cardFetch = new CardDB(ip, username, password);
		ArrayList<Card> myCards;
		StringBuilder listofCards = new StringBuilder();
		try {
			myCards = cardFetch.getAllCardsForList(listId);
			Collections.sort(myCards);
			for(Card c: myCards) {
				listofCards.append(c.getId()+". " + c.getTitle()+ "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listofCards.toString();
	}

	//edit card due date

	//archive cards
	public void archiveCard(int id, int listId, int level) {
		CardDB archiver = new CardDB(ip, username, password);
		archiver.card.setArchived(1);
		archiver.card.setId(id);
		archiver.card.setListId(listId);
		try {
			archiver.archiveUnarchiveCard(level);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//move card from list1 to list2

	//add member to a card in a list of a board

	//list all members
	public String listAllMembers() {
		MemberDB memberFetch = new MemberDB(ip, username, password);
		ArrayList<Member> myMembers;
		StringBuilder listofMembers = new StringBuilder();
		try {
			myMembers = memberFetch.getAllMembers();
			for(Member m: myMembers) {
				listofMembers.append(m.getId()+". " + m.getName() + "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listofMembers.toString();
	}

	//add a new member
	public String createMember(String name) {
		System.out.println("creating member");
		MemberDB newMember = new MemberDB(ip, username, password);
		newMember.member.setName(name);
		try {

			newMember.addMember();
		} catch (SQLException e) {
			e.printStackTrace();
			return ("Problem Creating Member: " );
		}
		return ("Created Member: "+ name);

	}

	//archive members
	public void archiveMember(int id) {
		MemberDB archiver = new MemberDB(ip, username, password);
		archiver.member.setArchived(1);
		archiver.member.setId(id);
		try {
			archiver.archiveUnarchiveMember();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	//list all labels
	public String listAllLabels(int boardId) {
		LabelDB labelFetch = new LabelDB(ip, username, password);
		labelFetch.label.setBoardId(boardId);
		ArrayList<Label> myLabels;
		StringBuilder listofLabels = new StringBuilder();
		try {
			myLabels = labelFetch.getAllLabels();
			for(Label l: myLabels) {
				listofLabels.append(l.getId()+". " + l.getName() + "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listofLabels.toString();
	}


	//edit members - rename
	public String editMember(int id, String name) {

		MemberDB editor = new MemberDB(ip, username, password);
		editor.member.setId(id);
		editor.member.setName(name);
		try {
			editor.updateMember();
		} catch (SQLException e) {
			e.printStackTrace();
			return ("Problem editing Member" );
		}
		return ("Edited Member: "+ name);
	}


	//assign member to a card

	//rename label
	public String editLabel(int id, String name, int boardId) {

		LabelDB editor = new LabelDB(ip, username, password);
		editor.label.setId(id);
		editor.label.setName(name);
		editor.label.setBoardId(boardId);
		try {
			editor.updateLabel();
		} catch (SQLException e) {
			e.printStackTrace();
			return ("Problem editing Label" );
		}
		return ("Edited Label: "+ name);
	}

	//archive labels
	public void archiveLabels(int boardId) {
		LabelDB archiver = new LabelDB(ip, username, password);
		archiver.label.setArchived(1);
		archiver.label.setBoardId(boardId);
		try {
			archiver.archiveUnarchiveLabel();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//assign label to card
}
