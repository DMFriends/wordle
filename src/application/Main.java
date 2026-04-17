package application;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.shape.*;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.input.*;
import javafx.scene.image.Image;
import javafx.scene.text.*;
import javafx.scene.paint.*;
import java.util.ArrayList;
import java.util.Set;

public class Main extends Application
{
	public static Set<String> possibleWords = LoadTextFile.loadWords();
	public static String correctWord = Wordle.chooseRandomWord();
	public static Text[] letterTexts = new Text[26];

	public int counter = 0;

	@SuppressWarnings("unused")
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			Rectangle[][] letterBoxes = new Rectangle[6][5];
			Text[][] letters = new Text[6][5];

			TextFlow alphabet = new TextFlow();
			alphabet.setLineSpacing(5);
			alphabet.setPrefWidth(720);

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
				if (currentText.length() > 5)
				{
					currentText = currentText.substring(0, 5);
					// Optionally, you can set the text back to the truncated version
					// This prevents the user from typing more, but can feel a bit abrupt
					guess.setText(currentText);
				}

				if (counter < 6)
				{
					// Update the display boxes
					for (int i = 0; i < 5; i++)
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

			ArrayList<String> guessArr = new ArrayList<String>();

			HBox topBox = new HBox(alphabet);
			topBox.setAlignment(Pos.CENTER);

			BorderPane root = new BorderPane();
			root.setPrefSize(800, 700);
			root.setStyle("-fx-background-color: lightgray;");
			root.setTop(topBox);

			Button restart = new Button("Restart");
			restart.relocate(650, 40);
			restart.setScaleX(1.5);
			restart.setScaleY(1.5);
			restart.setOnAction(event ->
			{
				root.getChildren().remove(restart);
				counter = 0;
				correctWord = Wordle.chooseRandomWord();
				restart(primaryStage);
			});

			Pane guesses = new Pane();

			for (int i = 0; i < letterBoxes.length; i++)
			{
				for (int j = 0; j < letterBoxes[i].length; j++)
				{
					Rectangle rect = new Rectangle(80, 80, Color.WHITE);
					rect.setY(90 * i + 100);
					rect.setX(90 * j + 170);
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

			Scene scene = new Scene(root);

			scene.setOnKeyPressed(event ->
			{
				// When the user presses enter, we will check the input
				if (event.getCode() == KeyCode.ENTER)
				{
					String input = guess.getText().toLowerCase();
					guesses.getChildren().remove(error);

					// Check if the input is a valid 5 letter word
					if (Wordle.checkGuess(input))
					{
						// If the input is valid, we will add it to the guessArr
						guessArr.clear();
						input = input.substring(0, 5);
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
							result.setText("You won!\nWould you like to play again?");
							result.setFont(new Font(30));
							result.setFill(Color.GREEN);
							result.setX(200);
							result.setY(40);
							result.setTextAlignment(TextAlignment.CENTER);
							guess.setDisable(true);
							guess.clear();
							guesses.getChildren().add(restart);
							guesses.getChildren().add(result);
						}
						else if (!Wordle.result(guessArr) && counter == 6)
						{
							result.setText("You lost! The word was " + correctWord + ".\nWould you like to try again?");
							result.setFont(new Font(25));
							result.setFill(Color.RED);
							result.setX(230);
							result.setY(40);
							result.setTextAlignment(TextAlignment.CENTER);
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
						error.setText("Invalid Guess!!\nPlease enter a valid 5-letter word.");
						error.setFont(new Font(30));
						error.setFill(Color.RED);
						error.setX(170);
						error.setY(40);
						error.setTextAlignment(TextAlignment.CENTER);
						guesses.getChildren().add(error);
					}
				}
			});

			primaryStage.setTitle("Wordle");

			Image icon = new Image("assets/wordle.png");
			primaryStage.getIcons().add(icon);
			
			primaryStage.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth());
			primaryStage.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());

			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
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
