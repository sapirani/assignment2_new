package bgu.spl.mics.application.passiveObjects; // The package

// Imports:
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * The Class is used to read a json file and convert it's data to Input object
 */
public class JsonInputReader
{
    /**
     * Convert data from json file to Input object
     * Input:
     *      @param filePath is the path to the input.json file
     * Output:
     *      @return Input object
     * @throws IOException exception
     */
    public static Input getInputFromJson(String filePath) throws IOException
    {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath))
        {
            return gson.fromJson(reader, Input.class);
        }
    }
}