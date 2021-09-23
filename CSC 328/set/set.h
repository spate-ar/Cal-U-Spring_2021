/*

//--------------------Constructor--------------------\\
FUNCTION:        Set(); 
General            : Initializes the values for the constructor
Preconditions    : Private data needs to be set: Head, Current, Temp and Null
PostConditions    : Constructor initializes private data with a value of zero.
/

//--------------------Deconstructor------------------\\
FUNCTION:        ~Set();
General            : Deconstructor made to destroy the list once return 0 is given in main.
Preconditions    : Needs a list created and uses private data Current, Temp and Head.
PostConditions    : List is looped through and deleted.
/

//--------------------Class Functions----------------\\
FUNCTION:        InsertAfter
General            : Insert a new node as head, or after the current Node
Preconditions    : Requires the new number that will occupy the Node
PostConditions    : Will not return a value, but will create a new Node, with user defined Num value, pointing at the next Node or Null depending on the position
/

/
FUNCTION:        Search
General            : Searches Node Number values to determine if Num is in Linked List values
Preconditions    : Uses list values from private data, and requires user entered number to search for
PostConditions    : returns a boolean True or False value if the number is within the Linked list
/

/
FUNCTION:        Remove
General            : Will remove a node from within the list regardless of Node Position
Preconditions    : Uses linked list and current position
PostConditions    : Will update the linked list within class data
/

/
FUNCTION:        operator<<
General            : Overloads << operator to create output for the Num value in the Node
Preconditions    : Needs the Node location
PostConditions    : Will create for print to console
*/

#ifndef SET_H
#define SET_H
#include <iostream>
#include <iomanip> 

using namespace std;

struct Node
	{
		int Num;
		struct Node* Next;
	};

class Set
{
	public:
		Set();
		~Set();
		void InsertAfter(int NewNum); 
		bool Search(int UserNum);
		int Remove();		
		friend ostream& operator<< (ostream& Outp, Set& Node);	
	private:
		Node* Head;
		Node* Current;
		Node* Temp;
		Node* Null;
};
#endif
