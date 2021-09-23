#include "set.h"
#include <iostream>
#include <cstdlib>

using namespace std;

/*===========================
	Set Constructor
===========================*/
Set::Set()
{
 	Head = 0;
 	Current = 0;
	Temp = 0;
	Null = 0;
}

Set::~Set() 
{
	Current = Head;				// Sets Current to the Head of the list
	while(Current != 0)			// While Current doesn't equal the end of the list...
	{
		Temp = Current -> Next;	// Apply Temp to the next node of Current
		delete Current;			// Delete the node Current is at
		Current = Temp;			// Move Current to Temps Node
	}
}

/*===========================
	Set Operators
===========================*/
ostream& operator<< (ostream& Outp, Set& Node)
{
	int i = 0;										// Initalize counter int i
	Node.Temp = Node.Head;							// Sets Temp to the Head of the list
	while (Node.Temp != 0)							// While Temp doesn't equal the end of list...
		{			
			cout << "\n\t\t";
			for(i = 0; i <= 3; i++)					// for loop to loop 4 times
			{		
				Outp << Node.Temp -> Num << "\t";	// Output the value found in the Temp node and add a space
				Node.Temp = Node.Temp -> Next;		// Step Temp to the next node
				if(Node.Temp == 0)					// If Temp = 0 after last step
					break;							// Break out of for loop
			}
			cout << "\n";							// Create a new line for a 4 column output
		}
	Node.Temp = 0;									// Resets temp to zero
	return Outp;									// Returns the output
}

void Set::InsertAfter(int NewNum)
{	
	if (Head == 0) 							// if... to check if Head is NULL
	{
		Head = new Node; 					// creates head node
		Current = Head;						// sets current node to head
		Current -> Num = NewNum;			// Inserts data to node
		cout << "Head node has been set." << endl;
		Current -> Next = 0;
	}
	else
	{	
		Temp = new Node;					// Create new temp node					
		Temp -> Next = Current -> Next;		// Temp's next points to Current's next				
		Current -> Next = Temp; 			// Current's next is set to Temp
		Temp -> Num = NewNum;				// Temp points to num and inserts NewNum
		Current = Temp;						// Change current position to temp
		cout << "Node created!" << endl;
		//Current -> Next = 0;
	}
	
	return;
}

bool Set::Search(int UserNum)
{
	bool Search = false;					// bool tracking variable
    Temp = Head;							// Sets temps location to the head of the list
    while(Temp != 0)
		{
        	if (Temp -> Num == UserNum)	// compares user input to temp location nodes value
        	{
            	Search = true;				// sets a true value to search when a matching value is found
            	break;						// exits the while loop
        	}
        	else
            	Temp = Temp -> Next;	// search remains false and temp moves to the next number in the list     	
 		}
 	if(Search == true)						// checks to see if search is true after the checks
 	{
 		cout << "True with the value: " << Temp -> Num;	// prompt good return if true
 		Current = Temp;				// sets location of current if number is found
 		return true;
	}
	else
		cout << "False value not found!" << endl;	// prompts false return if not found
		return false;
}

int Set::Remove()
{	
	if (Head == 0)									// if head is empty
		{											
			cout << "The set is empty!";			// prompt for empty set
			return -1;								// returns -1 to signal empty
		}
	else if (Head!= 0)								// if head isn't equal to 0
		{   
			if (Head == Current)					// and head is equal to current
				{
					cout << "The head value removed was: " << Current -> Num << endl;	// prompt head removal
					Null = Head; // set null to head
					Head = Head -> Next; // move head to next
					delete Null; // delete node null
					Null = 0; // reset null
				}
			else if (Current -> Next != 0 )
				{
					cout << "For Current -> Next != 0: " << Current -> Num << endl;	// prompt for middle removal
					Current -> Num = Current -> Next -> Num; // set current num to currents next num
					Null = Current -> Next;	// set null to currents next
					Current -> Next = Null -> Next; // currents next is set to nulls next
					delete Null; // delete node null
					Null = 0; // reset null
				}
			else
				{
					cout << "For Tail deletion: " << Current -> Num << endl;	// prompt for tail removal
					Null = Head; // set null to head
					while (Null -> Next -> Next != 0) // while loop to move through the list
							Null = Null -> Next; // null is set to nulls next
					Current = Null; // current becomes null
					Null = Null -> Next; // null is set to nulls next
					Current -> Next = 0; // currents next is set to 0
    				delete Null; // delete node null
    				Null = 0; // reset null
				}
		}
	Current = Head; // set current back to head
}
