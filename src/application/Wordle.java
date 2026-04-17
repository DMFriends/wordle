package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Wordle
{
	public static String chooseRandomWord()
	{
		ArrayList<String> wordList = new ArrayList<>(Main.possibleWords);
		Random random = new Random();
		int randomIndex = random.nextInt(wordList.size());
		String randomWord = wordList.get(randomIndex);
		//System.out.println("The randomly chosen word is: " + randomWord);
		return randomWord;
	}

	// Check if the guess is valid
	public static boolean checkGuess(String guess)
	{
		if (guess.length() >= 5 && Main.possibleWords.contains(guess.toLowerCase().substring(0,5)))
		{
			return true;
		}
		
		return false;
	}

	// Check each letter of a given guess to determine which letters are correctly
	// and incorrectly guessed
	public static void checkEachLetter(ArrayList<String> guess, Rectangle[] row)
	{
		ArrayList<String> realWord = stringToArrayList(Main.correctWord);
		char[] feedback = new char[guess.size()];
		boolean[] secretConsumed = new boolean[guess.size()];
		Map<String, Integer> secretCharCounts = new HashMap<>();

		Arrays.fill(feedback, 'h'); // fills the array with "gray"

		for (int i = 0; i < guess.size(); i++)
		{
			if (guess.get(i).equals(realWord.get(i)))
			{
				feedback[i] = 'g';
				secretConsumed[i] = true;
			}
			else
			{
				secretCharCounts.put(realWord.get(i), secretCharCounts.getOrDefault(realWord.get(i), 0) + 1);
			}
		}

		for (int i = 0; i < guess.size(); i++)
		{
			if (feedback[i] == 'g')
			{
				continue;
			}

			String currentGuessChar = guess.get(i);

			if (secretCharCounts.getOrDefault(currentGuessChar, 0) > 0)
			{
				feedback[i] = 'y';
				secretCharCounts.put(currentGuessChar, secretCharCounts.get(currentGuessChar) - 1);
			}

		}

		for (int i = 0; i < row.length; i++)
		{
			char c = guess.get(i).toUpperCase().charAt(0);
			
			int index = c - 'A';
		    
			if (feedback[i] == 'g')
			{
				row[i].setFill(Color.GREEN);
				Main.letterTexts[index].setFill(Color.GREEN);
			}
			else if (feedback[i] == 'y')
			{
				row[i].setFill(Color.YELLOW);
				if(!(Main.letterTexts[index].getFill() == Color.GREEN))
				{
					Main.letterTexts[index].setFill(Color.YELLOW);
				}
			}
			else
			{
				row[i].setFill(Color.GRAY);
				if(!(Main.letterTexts[index].getFill() == Color.GREEN) || !(Main.letterTexts[index].getFill() == Color.YELLOW))
				{
					Main.letterTexts[index].setFill(Color.WHITE);
				}
			}
		}
	}

	public static boolean result(ArrayList<String> guess)
	{
		ArrayList<String> realWord = stringToArrayList(Main.correctWord);
		int counter = 0;

		for (int i = 0; i < guess.size(); i++)
		{
			if (guess.get(i).equals(realWord.get(i)))
			{
				counter++;
			}
		}

		if (counter == 5)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// Algorithm to split a string into an ArrayList
	public static ArrayList<String> stringToArrayList(String word)
	{
		ArrayList<String> wordToAL = new ArrayList<String>();
		for (int i = 0; i < word.length(); i++)
		{
			char c = word.charAt(i);
			wordToAL.add(Character.toString(c));
		}
		return wordToAL;
	}

	public static int strToIndex(String letter)
	{
		letter = letter.toUpperCase();
		int index = letter.charAt(0);
		index -= 65;

		return index;
	}
}
