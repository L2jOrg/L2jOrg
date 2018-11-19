package l2s.commons.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для построения текстовый таблиц, пример работы:
 *	
 *	StrTable table = new StrTable("Test Table :)");
 *	table.set(0, "#", 1).set(0, "val", 23.5).set(0, "desc", " value #1");
 *	table.set(1, "#", 2).set(1, "v", 22.5).set(1, "desc", " value #2");
 *	table.set(3, "#", 3).set(3, "val", true).set(3, "desc", " bool #3 1334");
 *	table.set(2, "#", -1).set(2, "v", 22.5).set(2, "desc", "#######");
 *	System.out.print(table);
 *	
 * Вывод:
 * 
 *          Test Table :)
 *  ----------------------------------
 * | #  | val  |     desc      |  v   |
 * |----|------|---------------|------|
 * | 1  | 23.5 |    value #1   |  -   |
 * | 2  |  -   |    value #2   | 22.5 |
 * | -1 |  -   |    #######    | 22.5 |
 * | 3  | true |  bool #3 1334 |  -   |
 *  ----------------------------------
 *  
 *  
 * @Author: Drin
 * @Date: 27/04/2009
 * 
 * Переработан G1ta0 на использование StringBuilder.
 * 
 */
public class StrTable
{
	private final Map<Integer, Map<String, String>> rows = new HashMap<Integer, Map<String, String>>();
	private final Map<String, Integer> columns = new LinkedHashMap<String, Integer>();
	private final List<String> titles = new ArrayList<String>();

	public StrTable(String title)
	{
		if(title != null)
			titles.add(title);
	}

	public StrTable()
	{
		this(null);
	}

	public StrTable set(int rowIndex, String colName, boolean val)
	{
		return set(rowIndex, colName, Boolean.toString(val));
	}

	public StrTable set(int rowIndex, String colName, byte val)
	{
		return set(rowIndex, colName, Byte.toString(val));
	}
	
	public StrTable set(int rowIndex, String colName, char val)
	{
		return set(rowIndex, colName, String.valueOf(val));
	}
	
	public StrTable set(int rowIndex, String colName, short val)
	{
		return set(rowIndex, colName, Short.toString(val));
	}
	
	public StrTable set(int rowIndex, String colName, int val)
	{
		return set(rowIndex, colName, Integer.toString(val));
	}
	
	public StrTable set(int rowIndex, String colName, long val)
	{
		return set(rowIndex, colName, Long.toString(val));
	}
	
	public StrTable set(int rowIndex, String colName, float val)
	{
		return set(rowIndex, colName, Float.toString(val));
	}
	
	public StrTable set(int rowIndex, String colName, double val)
	{
		return set(rowIndex, colName, Double.toString(val));
	}
	
	public StrTable set(int rowIndex, String colName, Object val)
	{
		return set(rowIndex, colName, String.valueOf(val));
	}
	
	public StrTable set(int rowIndex, String colName, String val)
	{
		Map<String, String> row;

		if(rows.containsKey(rowIndex))
			row = rows.get(rowIndex);
		else
		{
			row = new HashMap<String, String>();
			rows.put(rowIndex, row);
		}

		row.put(colName, val);

		int columnSize;
		if(!columns.containsKey(colName))
			columnSize = Math.max(colName.length(), val.length());
		else if(columns.get(colName) >= (columnSize = val.length()))
			return this;
		columns.put(colName, columnSize);

		return this;
	}

	public StrTable addTitle(String s)
	{
		titles.add(s);
		return this;
	}

	private static StringBuilder right(StringBuilder result, String s, int sz)
	{
		result.append(s);
		if((sz -= s.length()) > 0)
			for(int i = 0; i < sz; i++)
				result.append(" ");
		return result;
	}

	private static StringBuilder center(StringBuilder result, String s, int sz)
	{
		int offset = result.length();
		result.append(s);
		int i;
		while((i = sz - (result.length() - offset)) > 0)
		{
			result.append(" ");
			if(i > 1)
				result.insert(offset, " ");
		}
		return result;
	}

	private static StringBuilder repeat(StringBuilder result, String s, int sz)
	{
		for(int i = 0; i < sz; i++)
			result.append(s);
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();

		if(columns.isEmpty())
			return result.toString();

		StringBuilder header = new StringBuilder("|");
		StringBuilder line = new StringBuilder("|");
		for(String c : columns.keySet())
		{
			center(header, c, columns.get(c) + 2).append("|");
			repeat(line, "-", columns.get(c) + 2).append("|");
		}

		if(!titles.isEmpty())
		{
			result.append(" ");
			repeat(result, "-", header.length() - 2).append(" ").append("\n");
			for(String title : titles)
			{
				result.append("| ");
				right(result, title, header.length() - 3).append("|").append("\n");
			}
		}

		result.append(" ");
		repeat(result, "-", header.length() - 2).append(" ").append("\n");

		result.append(header).append("\n");
		result.append(line).append("\n");

		for(Map<String, String> row : rows.values())
		{
			result.append("|");
			for(String c : columns.keySet())
			{
				center(result, row.containsKey(c) ? row.get(c) : "-", columns.get(c) + 2).append("|");
			}
			result.append("\n");
		}

		result.append(" ");
		repeat(result, "-", header.length() - 2).append(" ").append("\n");

		return result.toString();
	}
}