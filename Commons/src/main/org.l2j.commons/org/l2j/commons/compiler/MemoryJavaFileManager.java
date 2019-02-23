package org.l2j.commons.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MemoryJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

	private Set<String> loadedClasses = new HashSet<>();

	public MemoryJavaFileManager(StandardJavaFileManager sjfm) {
		super(sjfm);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		loadedClasses.add(className);
		return super.getJavaFileForOutput(location, className, kind, sibling);
	}

	public Set<String> getLoadedClasses() {
		return loadedClasses;
	}
}
