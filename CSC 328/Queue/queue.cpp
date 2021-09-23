#include "queue.h"

using namespace std;


/*===========================
	Set Constructor
===========================*/
Queue::Queue()
{
	Front = 0;								// Initialize the Front of the stack
	Back = 0;								// Initalize the Back of the stack
	Temp = 0;								// Initialize the Temp element
	Null = 0;								// Initialize the Null element
	Size = 0;
	DQR = 0;
}

Queue::~Queue()
{
	Temp = Front;							// Temp equals Front
	while(Front != 0)						// While Front doesn't equal to zero...
	{
		Temp = Front -> Next;				// Make the next from Front is temp
		delete Front;						// Delete Front
		Front = Temp;						// Front equals Temp
	}	
	SetSize(0);
}

void Queue::Enqueue(int x)
{
	Temp = new Node;						// Creates new node
	Temp -> Num = x;						// Sets passed value x to Temp's num
	Temp -> Next = 0;						// Creates a space for Temp's next
	if(Front == 0)							// Check to see if queue is empty
	{
		Back = Front = Temp;				// Place node in queue; where Back, Front, Temp are all the same node
	}
	else
	{
		Back -> Next = Temp;				// Places new node at Back of queue
		Back = Temp;						// Back set to Temp
	}
	Size++;									// increment size
}

int Queue::Dequeue()
{
	Null = Front;							// Create a temp front
	if(Null == 0)							// Check to see if it's empty
	{
		cout << "Empty queue!" << endl;
		return -1;
	}
	else
	{
		if(Null -> Next != 0)				// Peek at next node to see if its empty
		{
			Null = Null -> Next;			// Set temp front to the next node
			DQR = Front -> Num;				// Set return value to Front's number
 			delete Front;					// delete Front
			Front = Null;					// Set Front to the temp front
		}
		else
		{
			DQR = Null -> Num;				// Set return value to Front/temp Front's number
			delete Front;					// Delete Front
			Front = 0;						// Reinitialize Front/Back
			Back = 0;
		}
		Size--;								// Decrement Size
		return DQR;							// return the value
	}
}

int Queue::QueueFront()
{
	if(Front != 0)							// If Front is not empty
		return Front -> Num;				// Return the value of Front
	else
		return -1;
}

int Queue::QueueBack()
{
	if(Back != 0)							// If Back is not empty
		return Back -> Num;					// Return the value of Back
	else
		return -1;							// Exits function
}

int Queue::GetSize()
{
	return Size;
}

void Queue::SetSize(int x)
{
	Size = x;
}


void Queue::Display()						// For Testing Purposes
{	
	for(Temp = Front; Temp != 0; Temp = Temp -> Next)
	{
		cout << Temp -> Num << " ";
	}	
	cout << endl;
}

