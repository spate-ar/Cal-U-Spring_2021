#include "queue.h"	// includes information from header file

using namespace std;
const int SIZE = 100;

/*====== Function Prototypes =====*/
void RadixSort(int arr[]); // sorts based off Queue formula
void ArrDisplay(int arr[]); // For displaying the queues
bool isDigit(string n); // checks for good data, integer

int main() 
{
	int s = 0;
	int arr[SIZE] = {0}; // for all queue data sorting
	ifstream f; // input file handle
	string inbuffer; // line file storage
	string n; // used for integer conversion from input, atoi
	int m = 0; // keeps track of largest number
	f.open("input.txt"); // opens file for reading
	assert(!f.fail()); // make sure file is open/available
	while(!f.eof())
	{	
		getline(f, inbuffer, '\n'); // pushes line to string inbuffer
		if(isDigit(inbuffer) == true) // checks string for digits
		{
			int num = atoi(inbuffer.c_str()); // convert to integer
			if(num <= 32000 && num > 0)
			{
				arr[s] = num;
				s++;
			}
		}
	}
	f.close(); // close input file after processing 
	RadixSort(arr);	
	ArrDisplay(arr);
	assert(!f.fail());
	return 0;
}

/*====== Functions =====*/
void RadixSort(int arr[]) // sorts based off Queue formula
{	int x, x1, v, i = 0, o = 0;
	int a1 = 0;
	Queue a[10];
	do
	{	
		if(i == 0)
		{
			x = 1;
		}
		else if(i == 1)
		{
			x = 10;
		}
		else if(i == 2)
		{
			x = 100;
		}
		else if(i == 3)
		{
			x = 1000;
		}
		else
		{
			x = 10000;
		}
 		x1 = arr[o]/x;
		v = x1%10;
		a[v].Enqueue(arr[o]); 
		o++; // increments array Enqueue
		if(arr[o] == 0)
		{
			for(int w = 0; w <= 9; w++)
			{	
				while(a[w].QueueFront() != -1)
				{
					arr[a1] = a[w].Dequeue();
					a1++; // increments array Dequeue
				}
			}
			i++; // increments for digit check
			a1 = 0; // reset dequeue passes
			o = 0; // reset enqueue passes
		}	
	}while(i <= 5);
}

void ArrDisplay(int arr[]) // For displaying the queues
{
	int i = 0;
	for(int q = 0; arr[q] != 0; q++)
	{	
		cout << setw(7) << right << arr[q];
		i++;
		if(i == 5)
		{
			cout << endl;
			i = 0;
		}
	}
}
bool isDigit(string n) // checks for good data, integer
{
	bool digit = false;
	
	for(int k = 0; n[k] != 0; k++)
	{
		if(n[k] <= 57 && n[k] >= 48) // checks ascii code for digits
		{
			digit = true;
		}
		else
		{
			digit = false;
			break;
		}
	}
		return digit;
}
