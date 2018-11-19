package l2s.commons.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils
{
	private LogUtils()
	{}

	/**
	 * This method works the same as Thread.dumpStack(), but the only difference is that String with
	 * Stack Trace is being returned and nothing is printed into console.
	 * 
	 * @return Stack Trace
	 */
	public static String dumpStack()
	{
		return dumpStack(new Throwable());
	}

	public static String dumpStack(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		pw.close();
		return sw.toString();
	}
}
