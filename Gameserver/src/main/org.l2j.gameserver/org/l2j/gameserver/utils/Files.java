package org.l2j.gameserver.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Files {

	private static final Logger _log = LoggerFactory.getLogger(Files.class);

	public static void writeFile(String path, String string) {
		try {
			java.nio.file.Files.writeString(Path.of(path), string, StandardCharsets.UTF_8);
		} catch(IOException e) {
			_log.error("Error while saving file {} : {}", path, e);
		}
	}

	public static boolean copyFile(String srcFile, String destFile) {
		try {
			java.nio.file.Files.copy(Path.of(srcFile), Path.of(destFile));
			return true;
		} catch(IOException e) {
			_log.error("Error while copying file {} to {} : {}", srcFile, destFile, e);
		}
		return false;
	}
}