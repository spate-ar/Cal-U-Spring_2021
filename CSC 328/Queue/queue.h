// Andrew Bissell, Andrew Spate, Nicholas Spudich

/*

//--------------------Constructor--------------------\\
FUNCTION:        Queue();
General            : Initializes the value for the constructor.
Preconditions    : The private data Front, Back, Temp and Null need to be set.
PostConditions    : Loads the private data with zero
/

//--------------------Deconstructor------------------\\
FUNCTION:        ~Queue();
General            : Deconstructor made to destroy the list once return 0 is given in main.
Preconditions    : Needs a list created and uses private data Back, Temp
PostConditions    : Cycles through the list and deletes the linked list
/

//--------------------Class Functions----------------\\
FUNCTION:        Enqueue
General            : Adds a new node to the back of the queue
Preconditions    : The private data Temp, Front, and Back need to be initalized
PostConditions    : Creates either the first node with a pointer to the front, or creates a new node by itself
/

/
FUNCTION:        Dequeue
General            : Removes a node from the front of the queue
Preconditions    : The private data Null, Front and Back need to be initalized
PostConditions    : Removes a node from the front of the queue
/

/
FUNCTION:        QueueFront
General            : Displays what is at the front of the queue
Preconditions    : The private data Front needs to be initalized
PostConditions    : Shows the user what is at the front of the queue
/

/
FUNCTION:        QueueBack
General            : Displays what is at the back of the queue
Preconditions    : The private data Back needs to be initalized
PostConditions    : Shows the usere what is at the back of the queue
*/

#ifndef QUEUE_H
#define QUEUE_H
#include <iostream>
#include <iomanip> 

#include <iostream>	// std::cout, endl
#include <fstream> 	// file processing -- probably not needed
#include <string>	// string processing
#include <cassert>	// assert
#include <cstdlib>

struct Node
{
	int Num;
	struct Node* Next;
};

class Queue
{
	public:
		Queue();
		~Queue();
		void Enqueue(int x);
		int Dequeue();
		int QueueFront();
		int QueueBack();
		// void Display();			// For Testing Purposes
		int GetSize();
		void SetSize(int x);

	private:
		Node* Front;
		Node* Back;
		Node* Temp;
		Node* Null;
		int DQR;
		int Size;
		
};

#endif

