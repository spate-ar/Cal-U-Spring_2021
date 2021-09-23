#include <iostream>	// std::cout, endl
#include <cstdlib>	// string conversion
#include <fstream> 	// file processing
#include <string>	// string processing
#include <iomanip> 	// std::setw
#include "stack.h"	// includes information from header file
#include <assert.h> // includes assert


using namespace std;

/*===== Prototypes =====*/
bool isDigit(char n);
char isExpression(char n);

int main() 
{
	ifstream f; // input file handle
	string inbuffer; // saves lines to string for processing
	Stack s;	// object declaration of stack class
	string n; // used for integer conversion from input, atoi
	int r; // var for integer conversion from input, atoi
	int k = 1;
	int n1, n2; // operands
	char Operator; // handles case statement
	bool EndProc = false;
	
	f.open("input.txt"); // opens file for reading
	assert(!f.fail()); 	 // looks to see if there is a file to process
	while(!f.eof())
	{
		f >> inbuffer; // pushes first line to string inbuffer
		cout << setw(30) << "Expression " << k << endl;
		cout << "----------------------------------------------------" << endl;
		for(int i = 0; i < inbuffer.length(); i++)
		{  
			if(isDigit(inbuffer[i]) == true && EndProc == false) // check for digit
			{
				n = inbuffer[i]; // loads one string character to char n
				r = atoi(n.c_str()); // convert to int
				s.Push(r); // add it to the stack
				cout << r << " was added to the stack." << endl;
			}
			else
			{
				Operator = isExpression(inbuffer[i]); // check for expression
				if(Operator == '+' || Operator == '-' || Operator == '/' || Operator == '*')
				{
					switch (Operator)
					{
						case '*':
							n1 = s.Pop();
							n2 = s.Pop();
							if(n1 != 0 && n2 != 0) // check if stack can process the expression
							{
								cout << n1 << " * " << n2 << " = ";
								n1 = n1 * n2; // process multiply
								cout << n1 << endl;
								cout << n1 << " was added to the stack." << endl;
								s.Push(n1);	// pushes it back to the stack
							}
							else // stack doesn't contain enough digits
							{
								cout << "Expression: Multiplication can't be completed... \nStack does not contain at least two digits!" << endl;
							}
							break;
						case '/':
							n1 = s.Pop();
							n2 = s.Pop();
							if(n1 != 0 && n2 != 0) // check if stack can process the expression
							{
								n1 = n1 / n2; // process division
								s.Push(n1);	// pushes it back to the stack
							}
							else // stack doesn't contain enough digits
							{
								cout << "Expression: Division can't be completed... \nStack does not contain at least two digits!" << endl;
							}
							break;
						case '+':
							n1 = s.Pop();
							n2 = s.Pop();
							if(n1 != 0 && n2 != 0) // check if stack can process the expression
							{
								cout << n1 << " + " << n2 << " = ";
								n1 = n1 + n2; // process addition
								cout << n1 << endl;
								cout << n1 << " was added to the stack." << endl;
								s.Push(n1); // pushes it back to the stack
							}
							else // stack doesn't contain enough digits
							{
								cout << "Expression: Addition can't be completed... \nStack does not contain at least two digits!" << endl;
							}
							break;
						case '-':
							n1 = s.Pop();
							n2 = s.Pop();
							if(n1 != 0 && n2 != 0) // check if stack can process the expression
							{
								cout << n1 << " - " << n2 << " = ";
								n1 = n1 - n2; // process subtraction
								cout << n1 << endl;
								cout << n1 << " was added to the stack." << endl;
								s.Push(n1);	// pushes it back to the stack
							}
							else // stack doesn't contain enough digits
							{
								cout << "Expression: Subtraction can't be completed... \nStack does not contain at least two digits!" << endl;
							}
							break;
						default:
							cout << "Error: " << inbuffer[i] << " is not a valid Operand or Operator." << endl;
							break;
					} // end switch...case
				} // end if operator
				else
				{
					cout << "Error: " << inbuffer[i] << " is not a valid Operand or Operator." << endl;
					cout << "INVALID EXPRESSION: The postfix expression: " << inbuffer << " can't be processed.\n" << endl; // prints the postfix expression
					inbuffer = "";
					EndProc = true; // set end processing flag to true
					s.Clear();
				}
			} // end else not operator
		} // end for loop
		if(s.Peek() != -1)
		{
			cout << "The postfix expression is: " << inbuffer << endl; // prints the postfix expression
			cout << "The postfix evaluation is: " << s.Peek() << endl << endl; // writes postfix eval or top of stack to console
		}	
		k++; // increment for expression number
		EndProc = false; // reset processing flag
	} // eof
	f.close(); // closes input file
	assert(!f.fail()); 
	return 0;	// Destructs the Stack as well as returns 0.
}

bool isDigit(char n)
{
	bool digit = false;
	if(n <= 57 && n >= 48) // checks ascii code for digits
	{
		digit = true;
	}
	return digit;
}		

char isExpression(char n)
{ // checks for order of operations and performs operation if available
	int Op;
	if(n == '*')
	{
		Op = '*';
	}
	else if(n == '/')
	{
		Op = '/';
	}
	else if(n == '-')
	{	
		Op = '-';
	}
	else if(n == '+')
	{	
		Op = '+';
	}
	else
	{
		cout << "Not a valid expression!" << endl; // error message for invalid expression
	}
	return Op;
}
