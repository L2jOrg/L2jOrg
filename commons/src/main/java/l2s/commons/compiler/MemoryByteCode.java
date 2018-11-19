package l2s.commons.compiler;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class MemoryByteCode extends SimpleJavaFileObject
{
	private ByteArrayOutputStream oStream;
	private final String className;

	public MemoryByteCode(String className, URI uri)
	{
		super(uri, Kind.CLASS);
		this.className = className;
	}

	@Override
	public OutputStream openOutputStream()
	{
		oStream = new ByteArrayOutputStream();
		return oStream;
	}

	public byte[] getBytes()
	{
		return oStream.toByteArray();
	}

	@Override
	public String getName()
	{
		return className;
	}
}
