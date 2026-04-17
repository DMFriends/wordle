package application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LoadTextFile
{
	// The name of the file containing the words
	private static final String FILE_NAME = "words.txt";

	/**
	 * Loads words from the specified text file into a HashSet. Each word is trimmed
	 * of whitespace and converted to lowercase for case-insensitive lookups.
	 *
	 * @return A Set of lowercase words from the file, or an empty set if an error
	 *         occurs.
	 */
	public static Set<String> loadWords()
	{
		// Construct the Path to the file.
		// Assuming the file is in the same directory as the application, adjust the
		// path as necessary.
		Path filePath = Paths.get(FILE_NAME);

		// Check if the file exists
		if (!Files.exists(filePath))
		{
			System.err.println("Error: Word file not found at " + filePath.toAbsolutePath());
			return Collections.emptySet(); // Return an empty set
		}

		try
		{
			// Use Files.lines() for efficient line-by-line reading as a Stream
			Set<String> words = Files.lines(filePath).map(String::trim) // Remove leading/trailing whitespace
					.filter(s -> !s.isEmpty()) // Filter out empty lines
					.map(String::toLowerCase) // Convert to lowercase for case-insensitive matching
					.collect(Collectors.toCollection(HashSet::new)); // Collect into a HashSet

			System.out.println("Successfully loaded " + words.size() + " words from " + FILE_NAME);
			return words;

		} catch (IOException e)
		{
			System.err.println("Error reading words file: " + e.getMessage());
			e.printStackTrace();
			return Collections.emptySet(); // Return an empty set on error
		}
	}
}