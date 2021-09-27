/*
 Average (again) A Getting Started Program
 CET - 350 Technical Computing using Java
 Group 2
 Andrew Spate & Nicholas Spudich
 spa3195@calu.edu & spu8504@calu.edu 
*/

import java.io.*;	// include .class files, input & output from user to java application
public class Average // public file access for class Average
{
	public static void main (String[] args) // indicates particular member type main taking string argument
	{
		BufferedReader stdin= new BufferedReader(new InputStreamReader(System.in)); // initialize keyboard for reading
		String grade;
		double temp=0.0, totalGrade=0.0, avg = 0; // declares temp value for conversion purposes, variables for storing totalGrade and avg
		int i=0; // declare incrementer i
		while (temp>=0.0 && temp<=100.0) // while loop for single or multiple grade entries
		{
			try
				{
					try
					{
						System.out.println("Enter a numeric grade from [0-100] (<0 or >100 to exit):  "); // prints line to direct user
						System.out.flush(); // clears IO stream
						grade = stdin.readLine(); // reads user grade entry as string
						temp = Float.valueOf(grade).floatValue(); // converts string grade to float temp
						if (temp>=0.0 && temp<=100.0) // tests temp within bounds from 0-100 before processing occurs
							{
								totalGrade=totalGrade+temp; // creates running total of points
								i++; // increments i per grade entry
							}
					}
					catch(NumberFormatException e)
					{
						System.out.println("The input was not an integer "+ e); // error message for non-int types
					}
				}
			catch(IOException e)
			{
				System.out.println("Error occured while reading from the keyboard: "+e); // error message for IO Exception
			}
		}
		avg = totalGrade/i; // calculates average on given data
		Double AVG = new Double(avg); // creates wrapper object for average
		System.out.println("\nInfinite test: \t\t" + AVG.isInfinite()); // uses wrapper object to conduct infinite test
		System.out.println("NaN test: \t\t" + AVG.isNaN()); // uses wrapper object to conduct NaN test
		System.out.println("Total: \t\t\t"+ totalGrade);	// prints totalGrade to console
		System.out.println("Count:  \t\t"+ i); // prints count
		System.out.println("Average:  \t\t"+ avg); // prints average of grades
	}
}
		
