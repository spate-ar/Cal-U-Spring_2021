// Andrew Bissell, Andrew Spate, Nicholas Spudich, and Zachary Wright

/*

//--------------------Constructor--------------------\\
FUNCTION:        Set();
General            : Initializes the values for the constructor
Preconditions    : Private data needs to be set: Top, Temp
PostConditions    : Constructor initializes private data with a value of 0.
/

//--------------------Deconstructor------------------\\
FUNCTION:        ~Set();
General            : Deconstructor made to destroy the list once return 0 is given in main.
Preconditions    : Needs a list created and uses private data Top, Temp
PostConditions    : Cycles through the list and deletes the linked list
/

//--------------------Class Functions----------------\\
FUNCTION:        Push
General            : Pushes a new node onto the stack.
Preconditions    : Use of the Private data Top and Temp
PostConditions    : Adds a node to the linked list and puts the data inputed by the user into the node.
/

/
FUNCTION:        Search
General            : Searches Node Number values to determine if Num is in Linked List values
Preconditions    : Uses list values from private data, and requires user entered number to search for
PostConditions    : returns a boolean True or False value if the number is within the Linked list
/

/
FUNCTION:        Pop
General            : Pops a node from the top of the stack.
Preconditions    : Use of the Private data Top and Temp.
PostConditions    : Deletes the top data in the stack.
/

/
FUNCTION:        Peek
General            : Shows the top of the stack
Preconditions    : Use of the Private data Top and Temp.
PostConditions    : Returns the value of the top of the stack with the use of a temp variable.
*/

#ifndef STACK_H
#define STACK_H
#include <iostream>
#include <iomanip> 

struct Node
{
	int Num;
	struct Node* Next;
};

class Stack
{
	public:
		Stack();
		~Stack();
		void Push(int num);
		int Pop();
		// void Display();		// For Testing Purposes
		int Peek();
		void Clear();
		
	private:
		Node* Top;
		Node* Temp;
};

#endif
