#include <iostream>	// std::cout, endl
#include <cstdlib>	// string conversion
#include <fstream> 	// file processing -- probably not needed
#include <string>	// string processing
#include <iomanip> 	// std::setw
#include "set.h"	// includes information from header file

int main() 
{
	Set Node; // for handling node data
	int UserNum; // User entered data
	int i; // declare integer i for menu option navigation 
	
	/*===========================
	 	   Main Menu
	 ===========================*/
	cout << "Welcome to the main menu! Please select from one of the following available menu options...\n" << endl; // prompt for main menu
	cout << "\n\n\t\t\tMain Menu" << endl;	//cont'd
	
	while (i != 5)
	{	
		while (i != 5) // handles checks for case
		{
			cout << "\n\t\t1: Insert \n" << "\t\t2: Search \n" << "\t\t3: Remove current node\n" << "\t\t4: Display list \n" << "\t\t5: Quit" << endl;	// menu prompt
			while(!(cin >> i) || cin.peek() != '\n')	// check next character of i for end of line
   			{
     			cout << "Please enter an INTEGER: ";	// prompts user if integer is not entered
     			cin.clear();							// clears the input buffer
     			cin.ignore(30000, '\n');				// ignore inputs up to 30000 characters and new line char
   			}
			if(i >= 1 && i <= 5)						// compares input to menu option values
				break;									// if within range it breaks the while loop
			else
				cout << "\nERROR: Please enter a number based on the list below!" << endl; // prompts user if integer value is not within menu options parameters
		}
		switch (i) 	// switch statement for menu options
		{
			case 1: //insert, pass value to be inserted as a parameter, new node is inserted after the current node, new node becomes the current node
					cout << "\t\tOption 1: Insert\n" << endl; 		// prompt for validation of entry
					cout << "Enter a value to insert: ";			// prompts user for input
					while(!(cin >> UserNum) || cin.peek() != '\n')	// checks input stream and checks characters until new line
   					{
     					cout << "Please enter an INTEGER: "; // prompt if integer is not entered
     					cin.clear(); // clears the input buffer
     					cin.ignore(30000, '\n'); // ignore inputs up to 30000 characters and new line char
   					}
					Node.InsertAfter(UserNum);	// call function insert passing user defined number
					break; // exits case statement
			case 2: //search, pass value as a parameter, if value is found, return true and make the node the current node, otherwise return false and do not change the current node
					cout << "\t\tOption 2: Search\n" << endl; // prompt for validation of entry
					cout << "Enter a value to search for: "; // prompt for search
					while(!(cin >> UserNum) || cin.peek() != '\n') // check next character of user input for end of line
   					{
     					cout << "Please enter an INTEGER: "; // prompt if integer is not entered
     					cin.clear(); // clears the input buffer
     					cin.ignore(30000, '\n'); // ignore inputs up to 30000 characters and new line char
   					}
					Node.Search(UserNum); // node search function passing user input
					break;	// exits case statement
			case 3: //removeCurrentNode, remove current node and return value removed, return -1 to signal the Set was empty, make the head node the new current node
					cout << "\t\tOption 3: Remove\n" << endl; // prompt for validation of entry
					Node.Remove();	// calls the function to remove current node
					break;	// exits case statement
			case 4: //display list, displays the linked list
					cout << "\t\tOption 4: Displaying List\n" << endl; // prompt for validation of entry
					cout << Node; // displays the set
					cout << endl << endl;
					break;	// exits case statement
			case 5: //quit, exits the menu/program
					cout << "\t\tOption 5: Quit!\n" << endl; // prompt for validation of entry
					break;	// exits case statement
		
		}
	}
	return 0; // returns 0 when no errors
}
