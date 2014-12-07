package view;

import java.util.Scanner;

import controller.BoardController;

public class Tasker {

	
	private static final int DISPLAY_BOARD = 1;
	private static final int ASSIGN_LABEL = 7;
	private static final int ASSIGN_MEMBER = 8;
	private static final int RENAME_LABEL = 1;
	private static final int CHANGE_BOARD = 8;
	private static final int ARCHIVE_MEMBER = 3;
	private static final int ARCHIVE_CARD = 4;
	private static final int ARCHIVE_LIST = 3;
	private static final int ARCHIVE_BOARD = 3;
	private static final int MANAGE_LABELS = 7;
	private static final int RENAME_MEMBER = 2;
	private static final int CREATE_MEMBER = 1;
	private static final int RELOCATE_CARD = 6;
	private static final int MANAGE_MEMBERS = 6;
	private static final int CREATE_CARD = 1;
	private static final int MANAGE_CARDS = 5;
	private static final int REORDER_CARD = 5;
	private static final int RENAME_CARD = 3;
	public static final int REORDER_LIST = 4;
	public static final int RENAME_LIST = 2;
	public static final int CREATE_LIST = 1;
	public static final int MANAGE_LISTS = 4;
	public static final int RENAME_BOARD = 2;
	public static final int CREATE_NEW_BOARD = 1;
	public static final int EXIT = 2;

	public static void main(String[] args){
		BoardController boardControl;
		Scanner userIntegerInput = new Scanner(System.in);
		Scanner userStringInput = new Scanner(System.in);
		int currentBoard = 0;
		int boardChoice = 0;
		int withinBoardChoice = 0;
		
		
		System.out.println("Welcome to Tasker\n Please enter the credentials provided");
		
		System.out.println("Enter ip address: ");
		String ip = userStringInput.next();
		System.out.println("Enter username: ");
		String user = userStringInput.next();
		System.out.println("Enter password: ");
		String pass = userStringInput.next();
		boardControl = new BoardController(ip, user, pass);
		
		//Reset the board for new user
		System.out.println("Would you like to reset the Tasker (Y/n)?");		
		System.out.println("(new user press Y) ");
		String resetTrue = userIntegerInput.next();
		if(resetTrue.equalsIgnoreCase("y"))
		{
			boardControl.resetTasker();
		}
		
		//Operations
		while(true) {
			
			String boards = boardControl.listAllBoards();
			if (boards.isEmpty()){ // No boards to work on
				
				System.out.println("Looks like you could use a board!\n");
				currentBoard = CREATE_NEW_BOARD;
				
			} else { //Display Boards to work on
				
				StringBuilder menu = new StringBuilder();
			
				menu.append("1. Create new Board\n");
				menu.append("2. Exit\n");
				menu.append(boards + "\n");
			
			
				System.out.println("What would you like to work on ?");
			
				System.out.println(menu);
				System.out.println("Enter your choice (board ID) :");
				//TODO check the input from user
				currentBoard = userIntegerInput.nextInt();
				System.out.println(currentBoard);
			}
			
			if(currentBoard == CREATE_NEW_BOARD) {
				System.out.println("Enter a name for the Board");
				String name = userIntegerInput.next();
				currentBoard = boardControl.createBoard(name);
				if(currentBoard == 0) {
					System.out.println("Error creating board. Please try again.");
					continue;
				}
				System.out.println(name + " created. Start working on it!!");
				
			}
			if(currentBoard != EXIT){
				boolean continueBoard = true; 
				//Work on the current board until 8
				while(continueBoard){
				
					System.out.println("What would you like to do?\n");
					System.out.println(" 1. Display the board"
						+ "\n 2. Rename the board"
						+ "\n 3. Archive the board"
						+ "\n 4. Manage Lists"
						+ "\n 5. Manage Cards"
						+ "\n 6. Manage Members"
						+ "\n 7. Manage Labels"
						+ "\n 8. Change Board");
					System.out.println("Enter your choice (number) :");
					boardChoice = userIntegerInput.nextInt();
					System.out.println(boardChoice);
					if(boardChoice == CHANGE_BOARD)
						break;
					switch(boardChoice) {
				
					case DISPLAY_BOARD:
						String disp = boardControl.displayBoard(currentBoard);
						if(disp.isEmpty()) {
							System.out.println("Nothing on the board!!");
						} else {
							System.out.println(disp);
						}
						break;
					case RENAME_BOARD: 
						System.out.println("Enter new name for the Board");
						String name = userStringInput.nextLine();
						if(name.isEmpty()) {
							System.out.println("shouldn't have pressed enter!");
							break;
						}
						boardControl.editBoard(currentBoard, name);
//						System.out.println("Renamed the board");
						break;
					case ARCHIVE_BOARD:
						boardControl.archiveBoard(currentBoard);
						System.out.println("Archived!");
						continueBoard = false;
						break;
					case MANAGE_LISTS:
						System.out.println("What would you like to do?\n");
						System.out.println(" 1. Create a new list"
							+ "\n 2. Rename a list"
							+ "\n 3. Archive a list"
							+ "\n 4. Reorder list"
							+ "\n 5. Return to Board");
						System.out.println("Enter your choice (number) :");
						withinBoardChoice = userIntegerInput.nextInt();
						System.out.println(withinBoardChoice);
						
						switch(withinBoardChoice) {
						
						case CREATE_LIST:
							System.out.println("Enter a title for the list");
							name = userStringInput.nextLine();
							if(name.isEmpty()) {
								System.out.println("shouldn't have pressed enter!");
								break;
							}
							System.out.println(boardControl.createList(name, currentBoard));
							break;
						case RENAME_LIST:
							String listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("No lists to edit");
								break;
							} 
							System.out.println(listOfLists);
							
							System.out.println("Enter list (ID) to be renamed :");
							int listId = userIntegerInput.nextInt();
							
							System.out.println("Enter new name for the List");
							name = userStringInput.nextLine();
							if(name.isEmpty()) {
								System.out.println("shouldn't have pressed enter!");
								break;
							}
							boardControl.editListOnBoard(listId, name, -1, currentBoard);
							break;
						case ARCHIVE_LIST:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("No lists to edit");
								break;
							} 
							System.out.println(listOfLists);
							
							System.out.println("Enter list (ID) to be archived :");
							listId = userIntegerInput.nextInt();
							
							boardControl.archiveList(listId, currentBoard, 1);
							System.out.println("Archived!");
							break;
						case REORDER_LIST:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("No lists to edit");
								break;
							} 
							System.out.println(listOfLists);
							
							System.out.println("Enter list (ID) to be reordered :");
							listId = userIntegerInput.nextInt();
							
							System.out.println("What order you want the list "+listId+", to come in ?");
							int listOrder = userIntegerInput.nextInt();
							boardControl.editListOnBoard(listId, "", listOrder, currentBoard);
							break;
						case 5:
							break;
						default:
							System.out.println("Invalid input");
						}
						break;
					case MANAGE_CARDS:
						System.out.println("What would you like to do?\n");
						//TODO: Display the cards with list
						
						//give choices
						System.out.println(" 1. Create a new card"
								+ "\n 2. Add other details for a card"
							+ "\n 3. Rename a card"
							+ "\n 4. Archive a card"
							+ "\n 5. Reorder card"
							+ "\n 6. Move to other list"
							+ "\n 7. Assign a label to a card"
							+ "\n 8. Assign a member to a card"
							+ "\n 9. Return to Board");
						System.out.println("Enter your choice (number) :");
						withinBoardChoice = userIntegerInput.nextInt();
						System.out.println(withinBoardChoice);
						switch(withinBoardChoice) {
						case CREATE_CARD:
							String listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("Please create a list first!");
								break;
							} 
							System.out.println(listOfLists);
							
							System.out.println("Enter list (ID) for which card is to be created :");
							int listId = userIntegerInput.nextInt();
							
							System.out.println("Enter a title for the card");
							name = userStringInput.nextLine();
							if(name.isEmpty()) {
								System.out.println("shouldn't have pressed enter!");
								break;
							}
							System.out.println(boardControl.createCard(name, listId));
							break;
						case 2:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("Please create a list first!");
								break;
							} 
							System.out.println(listOfLists);
							
							System.out.println("Enter list (ID) for which card is to be edited :");
							listId = userIntegerInput.nextInt();
							
							
							break;
						case RENAME_CARD:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("Please create a list first!");
								break;
							} 
							System.out.println(listOfLists);
							
							System.out.println("Enter list (ID) for which card is to be edited :");
							listId = userIntegerInput.nextInt();
							
							String listOfCards = boardControl.listAllCardsForList(listId);
							if(listOfCards.isEmpty()) {
								System.out.println("No cards to edit");
								break;
							} 
							System.out.println(listOfCards);
							System.out.println("Enter card (ID) to be edited :");
							int cardId = userIntegerInput.nextInt();
							
							System.out.println("Enter new name for the Card");
							name = userStringInput.nextLine();
							if(name.isEmpty()) {
								System.out.println("shouldn't have pressed enter!");
								break;
							}
							boardControl.editCardOnList(cardId, name, -1, listId, BoardController.EDIT_TITLE);
							break;
						case ARCHIVE_CARD:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("Please create a list first!");
								break;
							} 
							System.out.println("Select a list from the following \n"+listOfLists);
							System.out.println("Enter list (ID) for which card is to be archived :");
							listId = userIntegerInput.nextInt();
							
							listOfCards = boardControl.listAllCardsForList(listId);
							if(listOfCards.isEmpty()) {
								System.out.println("No cards to be archived!");
								break;
							} 
							System.out.println("The Cards in the list are: \n"+listOfCards);
							
							System.out.println("Enter Card (ID) to be archived:");
							cardId = userIntegerInput.nextInt();
							
							boardControl.archiveCard(cardId, listId, 1);
							System.out.println("Archived!");
							break;
						case REORDER_CARD:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("Please create a list first!");
								break;
							} 
							System.out.println(listOfLists);
							
							System.out.println("Enter list (ID) for which card is to be edited :");
							listId = userIntegerInput.nextInt();
							
							listOfCards = boardControl.listAllCardsForList(listId);
							if(listOfCards.isEmpty()) {
								System.out.println("No cards to reorder");
								break;
							} 
							System.out.println(listOfCards);
							System.out.println("Enter card (ID) to be reordered:");
							cardId = userIntegerInput.nextInt();
							
							System.out.println("What order you want the card #"+cardId+", to come in ?");
							int order= userIntegerInput.nextInt();
							//check order
							boardControl.editCardOnList(cardId, "", order, listId, BoardController.EDIT_ORDER);
							
							break;
						case RELOCATE_CARD:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("Please create a list first!");
								break;
							} 
							System.out.println("The list on the current board are: \n"+listOfLists);
							
							System.out.println("Enter list (ID) for which card is to be moved :");
							listId = userIntegerInput.nextInt();
							
							listOfCards = boardControl.listAllCardsForList(listId);
							if(listOfCards.isEmpty()) {
								System.out.println("No cards to relocate");
								break;
							} 
							System.out.println("Select the card from below \n"+listOfCards);
							System.out.println("Enter card (ID) to be moved :");
							cardId = userIntegerInput.nextInt();
							
							System.out.println("The list on the current board are: \n"+listOfLists);
							System.out.println("Enter list (ID) to which the card is to be moved :");
							listId = userIntegerInput.nextInt();
							
							boardControl.editCardOnList(cardId, "", 0, listId, BoardController.RELOCATE_CARD);
							System.out.println("Moved card #"+cardId + " to list #" + listId);
							break;
						case ASSIGN_LABEL:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("Please create a list first!");
								break;
							} 
							System.out.println("The lists on the current board are: \n"+listOfLists);
							
							System.out.println("Enter list (ID) for which card is to be labelled :");
							listId = userIntegerInput.nextInt();
							
							listOfCards = boardControl.listAllCardsForList(listId);
							if(listOfCards.isEmpty()) {
								System.out.println("No cards !!");
								break;
							} 
							System.out.println("Select the card from below \n"+listOfCards);
							System.out.println("Enter card (ID) to be labelled :");
							cardId = userIntegerInput.nextInt();
							
							String labels = boardControl.listAllLabels(currentBoard);
							System.out.println("The following are the  labels for this board: \n" + labels);
							
							System.out.println("Enter the ID of the label to be used:");
							int labelId = userIntegerInput.nextInt();
							
							boardControl.editCardOnList(cardId, "", labelId, listId, BoardController.ASSIGN_LABEL);
							System.out.println("Label #" + labelId + " assigned to card #" + cardId );
							break;
						case ASSIGN_MEMBER:
							listOfLists = boardControl.listAllListsForBoard(currentBoard);
							if(listOfLists.isEmpty()) {
								System.out.println("Please create a list first!");
								break;
							} 
							System.out.println("The lists on the current board are: \n"+listOfLists);
							
							System.out.println("Enter list (ID) of the card :");
							listId = userIntegerInput.nextInt();
							
							listOfCards = boardControl.listAllCardsForList(listId);
							if(listOfCards.isEmpty()) {
								System.out.println("No cards !!");
								break;
							} 
							System.out.println("Select the card from below \n"+listOfCards);
							System.out.println("Enter card (ID):");
							cardId = userIntegerInput.nextInt();
							
							String members = boardControl.listAllMembers();
							System.out.println("The Current Members are: \n" + members);
							
							System.out.println("Enter the ID of the member to be assigned:");
							int memberId = userIntegerInput.nextInt();
							
							boardControl.editCardOnList(cardId, "", memberId, listId, BoardController.ASSIGN_MEMBER);
							System.out.println("Member #" + memberId + " assigned to card #" + cardId);
						case 9: 
							break;
						default:
							System.out.println("Invalid input");
						}
						break;
					case MANAGE_MEMBERS:
						String members = boardControl.listAllMembers();
						System.out.println("The Current Members are: \n" + members);
						//give choices
						System.out.println(" 1. Create a new member"
							+ "\n 2. Rename a member"
							+ "\n 3. Archive a member"
							+ "\n 4. Return to Board");
						System.out.println("Enter your choice (number) :");
						withinBoardChoice = userIntegerInput.nextInt();
						System.out.println(withinBoardChoice);
						switch(withinBoardChoice) {
						case CREATE_MEMBER:
							System.out.println("Enter the name for the member");
							name = userStringInput.nextLine();
							if(name.isEmpty()) {
								System.out.println("shouldn't have pressed enter!");
								break;
							}
							System.out.println(boardControl.createMember(name));
							break;
						case RENAME_MEMBER:
							System.out.println("Enter the ID of the member to be renamed:");
							int memberId = userIntegerInput.nextInt();
							
							System.out.println("Enter new name for the member");
							name = userStringInput.nextLine();
							if(name.isEmpty()) {
								System.out.println("shouldn't have pressed enter!");
								break;
							}
							boardControl.editMember(memberId, name);
							break;
						case ARCHIVE_MEMBER:
							System.out.println("Enter the ID of the member to be archived:");
							memberId = userIntegerInput.nextInt();
							boardControl.archiveMember(memberId);
							System.out.println("Archived!");
							break;
						case 4:
							break;
						default:
							System.out.println("Invalid input");
						}
						
						break;
					case MANAGE_LABELS:
						String labels = boardControl.listAllLabels(currentBoard);
						System.out.println("The following are the  labels for this board: \n" + labels);
						//give choices
						System.out.println(" 1. Rename a label"
							+ "\n 2. Return to board");
						System.out.println("Enter your choice (number) :");
						withinBoardChoice = userIntegerInput.nextInt();
						System.out.println(withinBoardChoice);
						switch(withinBoardChoice) {
						case RENAME_LABEL:
							System.out.println("Enter the ID of the label to be renamed:");
							int labelId = userIntegerInput.nextInt();
							
							System.out.println("Enter new name for the label");
							name = userStringInput.nextLine();
							if(name.isEmpty()) {
								System.out.println("shouldn't have pressed enter!");
								break;
							}
							boardControl.editLabel(labelId, name, currentBoard);
							break;
						case 2:
							break;
						default:
							System.out.println("Invalid input");
						}
						
						break;
					case CHANGE_BOARD:
						break;
					default:
						System.out.println("Invalid input");
					}	
				}
			} else { //Option chosen: Exit
				System.out.println("Thank you!!");
				System.exit(0);
				
			}
			
		}
	}
	
}
