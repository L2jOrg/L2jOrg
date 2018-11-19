package l2s.commons.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * @author G1ta0
 */
public class ExProperties extends Properties
{
	private static final long serialVersionUID = 1L;

	public static final String defaultDelimiter = "[\\s,;]+";
	
	public void load(String fileName) throws IOException
	{
		load(new File(fileName));
	}

	public void load(File file) throws IOException
	{
		InputStream is = null;
		try
		{
			load(is = new FileInputStream(file));
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
	}
	
	public static boolean parseBoolean(String s) 
	{
		switch (s.length()) {
			case 1: {
				char ch0 = s.charAt(0);
				if (ch0 == 'y' || ch0 == 'Y' || ch0 == '1')
				{
					return true;
				}
				if (ch0 == 'n' || ch0 == 'N' || ch0 == '0')
				{
					return false;
				}
				break;
			}
			case 2: {
				char ch0 = s.charAt(0);
				char ch1 = s.charAt(1);
				if ((ch0 == 'o' || ch0 == 'O') && 
						(ch1 == 'n' || ch1 == 'N') ) 
				{
					return true;
				}
				if ((ch0 == 'n' || ch0 == 'N') && 
						(ch1 == 'o' || ch1 == 'O') ) 
				{
					return false;
				}
				break;
			}
			case 3: {
				char ch0 = s.charAt(0);
				char ch1 = s.charAt(1);
				char ch2 = s.charAt(2);
				if ((ch0 == 'y' || ch0 == 'Y') &&
						(ch1 == 'e' || ch1 == 'E') &&
						(ch2 == 's' || ch2 == 'S') ) 
				{
					return true;
				}
				if ((ch0 == 'o' || ch0 == 'O') &&
						(ch1 == 'f' || ch1 == 'F') &&
						(ch2 == 'f' || ch2 == 'F') ) 
				{
					return false;
				}
				break;
			}
			case 4: {
				char ch0 = s.charAt(0);
				char ch1 = s.charAt(1);
				char ch2 = s.charAt(2);
				char ch3 = s.charAt(3);
				if ((ch0 == 't' || ch0 == 'T') &&
						(ch1 == 'r' || ch1 == 'R') &&
						(ch2 == 'u' || ch2 == 'U') &&
						(ch3 == 'e' || ch3 == 'E') ) 
				{
					return true;
				}
				break;
			}
			case 5: {
				char ch0 = s.charAt(0);
				char ch1 = s.charAt(1);
				char ch2 = s.charAt(2);
				char ch3 = s.charAt(3);
				char ch4 = s.charAt(4);
				if ((ch0 == 'f' || ch0 == 'F') &&
						(ch1 == 'a' || ch1 == 'A') &&
						(ch2 == 'l' || ch2 == 'L') &&
						(ch3 == 's' || ch3 == 'S') &&
						(ch4 == 'e' || ch4 == 'E') ) 
				{
					return false;
				}
				break;
			}
		}

		throw new IllegalArgumentException("For input string: \"" + s + "\"");
	}
	
	public boolean getProperty(String name, boolean defaultValue)
	{
		boolean val = defaultValue;

		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
			val = parseBoolean(value);

		return val;
	}

	public int getProperty(String name, int defaultValue)
	{
		int val = defaultValue;

		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
			val = Integer.parseInt(value);		

		return val;
	}

	public long getProperty(String name, long defaultValue)
	{
		long val = defaultValue;

		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
			val = Long.parseLong(value);

		return val;
	}

	public double getProperty(String name, double defaultValue)
	{
		double val = defaultValue;

		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
			val = Double.parseDouble(value);

		return val;
	}

	public String[] getProperty(String name, String[] defaultValue)
	{
		return getProperty(name, defaultValue, defaultDelimiter);
	}
	
	public String[] getProperty(String name, String[] defaultValue, String delimiter)
	{
		String[] val = defaultValue;
		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
			val = value.split(delimiter);

		return val;
	}

	public boolean[] getProperty(String name, boolean[] defaultValue)
	{
		return getProperty(name, defaultValue, defaultDelimiter);
	}
	
	public boolean[] getProperty(String name, boolean[] defaultValue, String delimiter)
	{
		boolean[] val = defaultValue;
		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
		{
			String[] values = value.split(delimiter);
			val = new boolean[values.length];
			for(int i = 0; i < val.length; i++)
				val[i] = parseBoolean(values[i]);
		}

		return val;
	}
	
	public int[] getProperty(String name, int[] defaultValue)
	{
		return getProperty(name, defaultValue, defaultDelimiter);
	}
	
	public int[] getProperty(String name, int[] defaultValue, String delimiter)
	{
		int[] val = defaultValue;
		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
		{
			String[] values = value.split(delimiter);
			val = new int[values.length];
			for(int i = 0; i < val.length; i++)
				val[i] = Integer.parseInt(values[i]);
		}

		return val;
	}

	public long[] getProperty(String name, long[] defaultValue)
	{
		return getProperty(name, defaultValue, defaultDelimiter);
	}
	
	public long[] getProperty(String name, long[] defaultValue, String delimiter)
	{
		long[] val = defaultValue;
		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
		{
			String[] values = value.split(delimiter);
			val = new long[values.length];
			for(int i = 0; i < val.length; i++)
				val[i] = Long.parseLong(values[i]);
		}

		return val;
	}

	public double[] getProperty(String name, double[] defaultValue)
	{
		return getProperty(name, defaultValue, defaultDelimiter);
	}
	
	public double[] getProperty(String name, double[] defaultValue, String delimiter)
	{
		double[] val = defaultValue;
		String value;

		if((value = super.getProperty(name, null)) != null && !value.isEmpty())
		{
			String[] values = value.split(delimiter);
			val = new double[values.length];
			for(int i = 0; i < val.length; i++)
				val[i] = Double.parseDouble(values[i]);
		}

		return val;
	}
}