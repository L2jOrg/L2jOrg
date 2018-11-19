package l2s.gameserver.utils;

public class SqlBatch
{
	private String _header;
	private String _tail;
	private StringBuilder _sb;
	private StringBuilder _result;
	private long _limit = Long.MAX_VALUE;
	private boolean isEmpty = true;

	public SqlBatch(String header, String tail)
	{
		_header = header + "\n";
		_tail = tail != null && tail.length() > 0 ? " " + tail + ";\n" : ";\n";
		_sb = new StringBuilder(_header);
		_result = new StringBuilder();
	}

	public SqlBatch(String header)
	{
		this(header, null);
	}

	public void writeStructure(String str)
	{
		_result.append(str);
	}

	public void write(String str)
	{
		isEmpty = false;
		if(_sb.length() + str.length() < _limit - _tail.length())
			_sb.append(str + ",\n");
		else
		{
			_sb.append(str + _tail);
			_result.append(_sb.toString());
			_sb = new StringBuilder(_header);
		}
	}

	public void writeBuffer()
	{
		String last = _sb.toString();
		if(last.length() > 0)
			_result.append(last.substring(0, last.length() - 2) + _tail);
		_sb = new StringBuilder(_header);
	}

	public String close()
	{
		if(_sb.length() > _header.length())
			writeBuffer();
		return _result.toString();
	}

	public void setLimit(long l)
	{
		_limit = l;
	}

	public boolean isEmpty()
	{
		return isEmpty;
	}
}