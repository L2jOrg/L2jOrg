package l2s.commons.string;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.UniversalDetector;

public class CharsetEncodingDetector
{
	private static final UniversalDetector DETECTOR = new UniversalDetector(null);
	private static final String DEFAULT_ENCODING = "UTF-8";

	public static String detectEncoding(File file) throws IOException
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			byte[] buf = new byte[4096];

			int nread;
			while((nread = fis.read(buf)) > 0 && !DETECTOR.isDone())
				DETECTOR.handleData(buf, 0, nread);

			DETECTOR.dataEnd();

			String encoding = DETECTOR.getDetectedCharset();

			DETECTOR.reset();
			return encoding;
		}
		catch(Exception e)
		{
			return DEFAULT_ENCODING;
		}
		finally
		{
			IOUtils.closeQuietly(fis);
		}
	}
}