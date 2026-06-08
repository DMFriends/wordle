package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

	// public static String getWord(Stage primaryStage)
	// {
	// 	TextField word = new TextField();
	// 	word.setPromptText("Enter a 5-letter word: ");

	// 	BorderPane enterWord = new BorderPane();
	// 	enterWord.setCenter(word);

	// 	Button randomWordButton = new Button("Random Word");
	// 	randomWordButton.setOnAction(_ -> {
	// 		word.setText(chooseRandomWord());
	// 	});

	// 	primaryStage.setScene(new Scene(enterWord, 400, 200));
	// 	primaryStage.show();
	// 	return word.getText();
	// }

	public static String getWord(Stage owner)
	{
		Stage dialog = new Stage();
		dialog.initOwner(owner);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("Wordle " + Main.APP_VERSION + " - Choose a Word");
		dialog.getIcons().add(new Image(Objects.requireNonNull(Wordle.class.getResourceAsStream("/resources/wordle.png"))));
		dialog.setOnCloseRequest(_ -> {
			System.exit(0);
		});

		TextField word = new TextField();
		word.setPromptText("Enter a 5-letter word: ");
		word.setPrefWidth(160);
		word.setMinWidth(160);
		word.setMaxWidth(160);

		Button randomWordButton = new Button("Random Word");
		Button okButton = new Button("OK");
		final String[] result = new String[1];

		randomWordButton.setOnAction(_ -> 
					{
						result[0] = chooseRandomWord();
						word.setText(result[0]);
						Main.isRandomWord = true;
						dialog.close();
					});
		
		okButton.setOnAction(_ -> {
			result[0] = word.getText().trim().toLowerCase();
			if(result[0].length() > 10 || result[0].length() < 3)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Invalid word length");
				alert.setHeaderText(result[0].length() > 10 ? "Word too long" : "Word too short");
				alert.setContentText("Please enter a word with 3-10 letters.");
				alert.initOwner(dialog);
				alert.showAndWait();
				return;
			}
			dialog.close();
		});

		VBox layout = new VBox(10, word, randomWordButton, okButton);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(10));
		layout.setPrefWidth(220);
		layout.setPrefHeight(140);
		dialog.setResizable(false);
		dialog.setScene(new Scene(layout, 400, 200));

		dialog.showAndWait();
		return result[0];
	}

	// Check if the guess is valid
	public static boolean checkGuess(String guess)
	{
		if(Main.isRandomWord)
		{
			if (guess.length() >= 5 && Main.possibleWords.contains(guess.toLowerCase().substring(0,5)))
			{
				return true;
			}
			
			return false;
		}
		else
		{
			if(guess.length() >= Main.selectedWord.length())
			{
				return true;
			}
			
			return false;
		}
		
		
	}

	// Check each letter of a given guess to determine which letters are correctly
	// and incorrectly guessed
	public static void checkEachLetter(ArrayList<String> guess, Rectangle[] row)
	{
		ArrayList<String> realWord = stringToArrayList(Main.selectedWord);
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
		ArrayList<String> realWord = stringToArrayList(Main.selectedWord);
		int counter = 0;

		for (int i = 0; i < guess.size(); i++)
		{
			if (guess.get(i).equals(realWord.get(i)))
			{
				counter++;
			}
		}

		if (counter == realWord.size())
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
