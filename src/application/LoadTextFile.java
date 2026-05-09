package application;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
		InputStream resourceStream = LoadTextFile.class.getResourceAsStream("/resources/words.txt");
		
	    if (resourceStream == null)
	    {
	        System.err.println("Could not find bundled words.txt");
	        return Collections.emptySet();
	    }
		
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8)))
		{
			Set<String> words = reader.lines().map(String::trim).filter(s -> !s.isEmpty()).map(String::toLowerCase)
					.collect(Collectors.toCollection(HashSet::new));

			System.out.println("Successfully loaded " + words.size() + " words from bundled " + FILE_NAME);
			return words;
		}
		catch (IOException e)
		{
			System.err.println("Error reading bundled words file: " + e.getMessage());
			e.printStackTrace();
			return Collections.emptySet(); // Return an empty set on error
		}
	}
}
