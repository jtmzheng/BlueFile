package com.example.bluefile;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;

public class BTFile implements Serializable {
	private static final long serialVersionUID = 1L;
	public String fileName;
	public Timestamp lastModified;
	public byte[] contents;

	public BTFile (File file, byte[] data){
		fileName = file.getName();
		lastModified = new Timestamp(file.lastModified());
		contents = data;
	}
}
