package com.revex.docrepo.utils.report;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.OutputStream;

public class Report {
	private XWPFDocument instance;

	Report(XWPFDocument instance) {
		this.instance = instance;
	}

	public void write(OutputStream stream) throws IOException {
		this.instance.write(stream);
		this.instance.close();
	}
}
