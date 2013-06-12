package wattsup.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public final class TextUtil
{
    /**
     * Private constructor to avoid instance of this class.
     */
    private TextUtil()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a {@link String} containing the contents lines of the {@code file}.
     * 
     * @param file
     *            The file to be read.
     * @return A String containing the contents of the lines, including line-termination characters, or <code>null</code> if the end of the stream has
     *         been reached.
     * @throws IOException
     *             If an I/O error occurs.
     * @throws NullPointerException
     *             If {@code file} is <code>null</code>.
     */
    public static String readLines(File file) throws IOException
    {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(Objects.requireNonNull(file))))
        {
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
