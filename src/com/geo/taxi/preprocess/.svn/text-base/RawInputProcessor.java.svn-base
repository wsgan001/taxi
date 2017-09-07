package com.geo.taxi.preprocess;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import javax.naming.spi.DirectoryManager;

public class RawInputProcessor {
	private String path;
	private String extension;

	public RawInputProcessor(String p, String e) {
		setPath(p);
		setExtension(e);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public void processFiles(byte fromStatus, byte toStatus) throws FileNotFoundException {
		File dir = new File(path);
		if (dir.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("." + getExtension());
				}
			};
			File[] files = dir.listFiles(filter);
			int cnt=1;
			for (File f : files){
				new FileRawInputProcessor ( f , fromStatus, toStatus );
				System.out.println( "processed " + cnt++ + " out of " + files.length );
			}
		}
	}
}
