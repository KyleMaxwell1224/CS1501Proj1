//Kyle Maxwell, Spring 2020 CS 1501
//This program is an autocomplete engine. It takes in the dictionary file and creates a De la Briandais trie
//It also takes into account previous user entries by creating another DLB trie
import java.util.*;
import java.io.*;

class DLB {
	private class Node
	{
		public char key;
		public Node rightSibling;
		public Node nextChild;
	}
	Node root = new Node();
	final char terminate = '^';
	private ArrayList<String> suffixList = new ArrayList<String>();
	public ArrayList<String> getSuffixList()
	{
		return suffixList;
	}
	public void clearList()
	{
		suffixList.clear();
	}
	
	public boolean add(String s)
	{
		Node terminatorNode = new Node();
		terminatorNode.key = terminate;		
		Node temp = root;
		char[] charArray = s.toCharArray();
	
		for(int i = 0; i < charArray.length; i++)
		{
			Node addedNode = new Node();			
			addedNode.key = charArray[i];
			if (linkedListSearch(temp.nextChild, charArray[i]) != null)
			{
				temp = linkedListSearch(temp.nextChild, charArray[i]);
			}
			else if(temp.nextChild != null)
			{
				Node lastLink = temp.nextChild;
				while(lastLink.rightSibling != null)
					lastLink = lastLink.rightSibling;
				lastLink.rightSibling = addedNode;
				temp = addedNode;	
			}
			else
			{
				temp.nextChild = addedNode;
				temp = addedNode;	
			}
		}
		if(temp.nextChild == null)
			temp.nextChild = terminatorNode;
		else
		{
			Node lastSibling = temp.nextChild;
			while(lastSibling.rightSibling != null)
				lastSibling = lastSibling.rightSibling;
			lastSibling.rightSibling = terminatorNode;
		}
		return true;
	}
	public Node linkedListSearch(Node node, char key)
	{
		while(node != null)
		{
			if (node.key == key)
				return node;
			else
				node = node.rightSibling;
		}
		return null;
	}
	 
	public void spiderNode(Node iterator, String word)
	{
		Node temp = iterator; //iterator = next list after searched word, gonna hold the verticallity
		while(temp != null)
		{
				if(temp.key == '^' && temp.rightSibling == null)
				{
					suffixList.add(word);
				}
				else if(temp.rightSibling != null)
				{
					if(temp.key =='^')
					{
						suffixList.add(word);
					}
					spiderNode(temp.rightSibling, (word));
				}
				if (temp.key != '^')
					word += temp.key; 
				if (temp.nextChild != null)
					temp = temp.nextChild;
				else
					return;
				
		}
	}
	public void search(DLB Trie, String key)
	{
		String[] words = new String[5];
		Node temp = root;
		char[] charArray = key.toCharArray();
		if (temp.nextChild == null)
			return; //This is for if no nodes exist after the root
		for(int i = 0; i < charArray.length; i++){
			temp = linkedListSearch(temp.nextChild, charArray[i]); 
			if (temp == null)
				return;
		}
		if (temp != null && temp.nextChild != null)
			temp = temp.nextChild;
		spiderNode(temp, key);
	}
}

public class ac_test
{
	public static void main(String args[]) throws Exception
	{
		DLB dictionary = new DLB();
		DLB userWords = new DLB();
		String dictionaryFile = "dictionary.txt";
		String userFile = "user_history.txt";
		FileWriter fw = new FileWriter(userFile, true);
		BufferedWriter writer = new BufferedWriter(fw);
		BufferedReader in = new BufferedReader(new FileReader(dictionaryFile));
		String dictLine;
		String userLine;
		while ((dictLine = in.readLine()) != null)
			dictionary.add(dictLine);
		in.close();
		BufferedReader userIn = new BufferedReader(new FileReader(userFile));
		while ((userLine = userIn.readLine()) != null)
			userWords.add(userLine);
		userIn.close();
		userInput(userWords, dictionary, writer);
		writer.close();
	}

	public static void userInput(DLB userWords, DLB dictionary, BufferedWriter writer) throws Exception
	{
		Scanner input = new Scanner(System.in);
		String userWord = "";
		ArrayList<Double> time = new ArrayList<Double>();
		String[] suggestions = new String[5];
		final long nanoToSeconds = 1000000000;
		char character;
		while(true)
		{	
			System.out.print("Please enter a character: ");
			character = input.next().charAt(0);
		
			if (character == '!') 
			{
				double totalTime = 0;
				for (int i = 0; i < time.size(); i++)
				{
					totalTime += time.get(i);					
				}
				System.out.println("AVERAGE TIME: " + (totalTime/time.size()));
				return;
			}
			else if (character =='$') 
			{
				userWords.add(userWord);
				writer.write(userWord);
				writer.newLine();
				System.out.println("WORD COMPLETED: " + userWord);
				userWord = "";
			}
			else if (character == '1')
			{
				userWords.add(suggestions[0]);
				writer.write(suggestions[0]);
				writer.newLine();
				System.out.println("WORD COMPLETED: " + suggestions[0]);
				userWord = "";
			}
			else if (character == '2')
			{
				userWords.add(suggestions[1]);
				writer.write(suggestions[1]);
				writer.newLine();
				System.out.println("WORD COMPLETED: " + suggestions[1]);
				userWord = "";
			}
			else if (character == '3')
			{
				userWords.add(suggestions[2]);
				writer.write(suggestions[2]);
				writer.newLine();	
				System.out.println("WORD COMPLETED: " + suggestions[2]);
				userWord = "";
			}
			else if (character == '4')
			{
				userWords.add(suggestions[3]);
				writer.write(suggestions[3]);
				writer.newLine();		
				System.out.println("WORD COMPLETED: " + suggestions[3]);
				userWord = "";
			}
			else if (character == '5')
			{
				userWords.add(suggestions[4]);
				writer.write(suggestions[4]);
				writer.newLine();
				System.out.println("WORD COMPLETED: " + suggestions[4]);
				userWord = "";
			}
			
			else
			{
				dictionary.clearList();
				userWords.clearList();
				Arrays.fill(suggestions, null);				
				userWord += character;	
				long start = System.nanoTime();		
				userWords.search(userWords, userWord);
				ArrayList<String> userList = userWords.getSuffixList();
				int count = 0;				
				if (userList != null)
				{
					for(int i = 0; i < userList.size(); i++)
					{
						if (count >= 5)
							break;
						suggestions[count] = userList.get(i);
						count++;
					}
				}  
				dictionary.search(dictionary, userWord);
				ArrayList<String> dictionaryList = dictionary.getSuffixList();
				for(int i = 0; i < dictionaryList.size(); i++)
				{
					if(count >= 5)
						break;
					else
					{
						if(!userList.contains(dictionaryList.get(i)))
						{
							suggestions[count] = dictionaryList.get(i);
							count++;
						}
					}
				}

				long estimatedTime = System.nanoTime() - start;
				double elapsedTimeInSecond = (double) estimatedTime /nanoToSeconds;
				time.add(elapsedTimeInSecond);
				System.out.println("(" + elapsedTimeInSecond + " seconds)");
				System.out.println("Suggestions:");
				for (int i = 0; i < suggestions.length; i++)
				{
					if(suggestions[0] == null)
					{
						System.out.print("NO SUGGESTIONS AVAILABLE");
						break;
					}
					if(suggestions[i] != null)
						System.out.print("(" + (i+1) + ") " + suggestions[i] + "\t");
				}
				System.out.println(); 

			}				
		}	
	}
}