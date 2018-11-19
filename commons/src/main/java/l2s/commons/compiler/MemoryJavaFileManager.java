package l2s.commons.compiler;

import java.io.IOException;
import java.net.URI;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class MemoryJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager>
{
	private MemoryClassLoader cl;

	public MemoryJavaFileManager(StandardJavaFileManager sjfm, MemoryClassLoader xcl)
	{
		super(sjfm);
		cl = xcl;
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException
	{
		MemoryByteCode mbc = new MemoryByteCode(className.replace('/', '.').replace('\\', '.'), URI.create("file:///" + className.replace('.', '/').replace('\\', '/') + kind.extension));
		cl.addClass(mbc);

		return mbc;
	}

	@Override
	public ClassLoader getClassLoader(Location location)
	{
		return cl;
	}
}
