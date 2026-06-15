package application;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application
{
	public static Set<String> possibleWords = LoadTextFile.loadWords();
	public static Text[] letterTexts = new Text[26];
	public static String selectedWord;

	public static boolean isRandomWord = false;
	
	public static final String APP_VERSION = "v2.0";

	public int counter = 0;

	@SuppressWarnings({"unused", "CallToPrintStackTrace"})
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			selectedWord = Wordle.getWord(primaryStage);

			final double CELL_SIZE = 80;
			final double CELL_GAP = 10;
			final double MIN_SIDE_MARGIN = 100; // minimum margin on each side of the board
			final double ALPHABET_WIDTH = 720; // width of the alphabet display

			double boardWidth = selectedWord.length() * (CELL_SIZE + CELL_GAP) - CELL_GAP;
			double windowWidth = Math.max(boardWidth + 2 * MIN_SIDE_MARGIN, ALPHABET_WIDTH);
			double boardStartX = (windowWidth - boardWidth) / 2; // center the board
			
			// Calculate window height: alphabet + rows + margins
			double alphabetHeight = 80;
			double boardHeight = 6 * (CELL_SIZE + CELL_GAP) + 100; // 100 for top offset
			double bottomMargin = 30; // space for error/result messages
			double windowHeight = alphabetHeight + boardHeight + bottomMargin;

			Rectangle[][] letterBoxes = new Rectangle[6][selectedWord.length()];
			Text[][] letters = new Text[6][selectedWord.length()];

			TextFlow alphabet = new TextFlow();
			alphabet.setLineSpacing(5);
			alphabet.setPrefWidth(720);
			alphabet.setTextAlignment(TextAlignment.CENTER);

			for (int i = 0; i < 26; i++)
			{
				char letter = (char) ('A' + i);
				Text t = new Text(String.valueOf(letter));
				t.setFont(Font.font(34));
				t.setFill(Color.BLACK);

				letterTexts[i] = t;

				alphabet.getChildren().add(t);

				if (i < 25)
				{
					alphabet.getChildren().add(new Text("  ")); // space between letters
				}
			}

			TextField guess = new TextField();
			guess.setMaxWidth(200);
			guess.setMinWidth(200);
			guess.setScaleX(2);
			guess.setScaleY(2);
			guess.setLayoutX(300);
			guess.setLayoutY(800);
			guess.setManaged(false);
			
			guess.addEventFilter(KeyEvent.KEY_PRESSED, event ->
			{
				if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.V)
				{
					event.consume(); // Prevent Ctrl+V or Cmd+V
				}
			});

			guess.textProperty().addListener((observable, oldValue, newValue) ->
			{
				String currentText = newValue.toUpperCase(); // Convert to uppercase for display consistency

				// Truncate if longer than WORD_LENGTH
				if (currentText.length() > selectedWord.length())
				{
					currentText = currentText.substring(0, selectedWord.length());
					// Optionally, you can set the text back to the truncated version
					// This prevents the user from typing more, but can feel a bit abrupt
					guess.setText(currentText);
				}

				if (counter < 6)
				{
					// Update the display boxes
					for (int i = 0; i < selectedWord.length(); i++)
					{
						if (i < currentText.length())
						{
							letters[counter][i].setText(String.valueOf(currentText.charAt(i)));
							double rectCenterX = letterBoxes[counter][i].getX()
									+ letterBoxes[counter][i].getWidth() / 2;
							double rectCenterY = letterBoxes[counter][i].getY()
									+ letterBoxes[counter][i].getHeight() / 2;
							Bounds textBounds = letters[counter][i].getBoundsInLocal();
							double textWidth = textBounds.getWidth();
							double textHeight = textBounds.getHeight();
							letters[counter][i].setX(rectCenterX - textWidth / 2);
							letters[counter][i].setY(rectCenterY + textHeight / 4);
						}
						else if (!newValue.matches("[A-Za-z]*"))
						{
							guess.setText(newValue.replaceAll("[^A-Za-z]", "")); // Clear the box if user enters anything other than a letter
							letters[counter][i].setText("");
						}
						else
						{
							letters[counter][i].setText(""); // Clear the box if no character
						}
					}
				}
			});

			ArrayList<String> guessArr = new ArrayList<>();

			HBox topBox = new HBox(alphabet);
			topBox.setAlignment(Pos.CENTER);
			topBox.setMaxWidth(Double.MAX_VALUE);
			topBox.setPrefHeight(80);

			BorderPane root = new BorderPane();
			root.setPrefSize(windowWidth, windowHeight);
			root.setStyle("-fx-background-color: lightgray;");
			root.setTop(topBox);

			Button restart = new Button("Restart");
			restart.relocate(windowWidth - 130, 15);
			restart.setScaleX(1.5);
			restart.setScaleY(1.5);
			restart.setOnAction(event ->
			{
				root.getChildren().remove(restart);
				counter = 0;
				restart(primaryStage);
			});


			Pane guesses = new Pane();

			Button giveUp = new Button("Give Up");
			giveUp.relocate(75, 40);
			giveUp.setScaleX(1.5);
			giveUp.setScaleY(1.5);
			giveUp.setFocusTraversable(false);
			
			guesses.getChildren().add(giveUp);

			for (int i = 0; i < letterBoxes.length; i++)
			{
				for (int j = 0; j < letterBoxes[i].length; j++)
				{
					Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE, Color.WHITE);
					rect.setY((CELL_SIZE + CELL_GAP) * i + 100);
					rect.setX((CELL_SIZE + CELL_GAP) * j + boardStartX);
					rect.setStroke(Color.BLACK);
					letterBoxes[i][j] = rect;

					guesses.getChildren().add(rect);

					Text t = new Text();
					t.setFont(new Font(40));
					t.setFill(Color.BLACK);
					t.setStroke(Color.BLACK);
					t.setStrokeWidth(2);
					letters[i][j] = t;

					guesses.getChildren().add(t);
				}
			}

			Text result = new Text();

			Text error = new Text();

			guesses.getChildren().add(guess);

			root.setCenter(guesses);

			giveUp.setOnAction(event ->
			{
				guesses.getChildren().remove(giveUp);
				guesses.getChildren().remove(error);
				result.setText("You lost! The word was " + selectedWord + ".\nWould you like to try again?");
				result.setFont(new Font(24));
				result.setFill(Color.RED);
				result.setTextAlignment(TextAlignment.CENTER);
				centerTextX(result, windowWidth);
				result.setY(40);
				guess.setDisable(true);
				guess.clear();
				guesses.getChildren().add(restart);
				guesses.getChildren().add(result);
			});

			Scene scene = new Scene(root);

			scene.setOnKeyPressed(event ->
			{
				// When the user presses enter, we will check the input
				if (event.getCode() == KeyCode.ENTER)
				{
					String input = guess.getText().toLowerCase();
					guesses.getChildren().remove(error);

					// Check if the input is a valid word for the current game mode
					if (Wordle.checkGuess(input))
					{
						// If the input is valid, we will add it to the guessArr
						guessArr.clear();
						input = input.substring(0, input.length());
						for (int i = 0; i < input.length(); i++)
						{
							char c = input.charAt(i);
							guessArr.add(Character.toString(c));
						}

						Rectangle[] row = letterBoxes[counter];
						Wordle.checkEachLetter(guessArr, row);
						counter++;

						if (Wordle.result(guessArr))
						{
							guesses.getChildren().remove(giveUp);
							result.setText("You won!\nWould you like to play again?");
							result.setFont(new Font(24));
							result.setFill(Color.GREEN);
							result.setTextAlignment(TextAlignment.CENTER);
							centerTextX(result, windowWidth);
							result.setY(40);
							guess.setDisable(true);
							guess.clear();
							guesses.getChildren().add(restart);
							guesses.getChildren().add(result);
						}
						else if (!Wordle.result(guessArr) && counter == 6)
						{
							guesses.getChildren().remove(giveUp);
							result.setText("You lost! The word was " + selectedWord + ".\nWould you like to try again?");
							result.setFont(new Font(24));
							result.setFill(Color.RED);
							result.setTextAlignment(TextAlignment.CENTER);
							centerTextX(result, windowWidth);
							result.setY(40);
							guess.setDisable(true);
							guess.clear();
							guesses.getChildren().add(restart);
							guesses.getChildren().add(result);
						}
						else
						{
							guess.clear();
							guess.setPromptText("Enter your guess: ");
						}

					}
					else
					{
						error.setText("Invalid Guess!!\nPlease enter a valid " + Main.selectedWord.length() + "-letter word.");
						error.setFont(new Font(24));
						error.setFill(Color.RED);
						error.setTextAlignment(TextAlignment.CENTER);
						centerTextX(error, windowWidth);
						error.setY(40);
						guesses.getChildren().add(error);
					}
				}
			});

			primaryStage.setTitle("Wordle " + APP_VERSION);
			
			primaryStage.getIcons().add(
			    new Image(
			        Objects.requireNonNull(
			            getClass().getResourceAsStream("/resources/wordle.png")
			        )
			    )
			);
			
			primaryStage.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth());
			primaryStage.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());

			primaryStage.setResizable(false);

			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.centerOnScreen();
			guess.requestFocus();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Centers a (possibly multi-line) Text node horizontally within the given width
	private static void centerTextX(Text text, double containerWidth)
	{
		text.setX((containerWidth - text.getBoundsInLocal().getWidth()) / 2);
	}

	private void restart(Stage stage)
	{
		// Close current stage
		stage.close();

		// Create a new stage and start the app again
		Stage newStage = new Stage();
		start(newStage);
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
