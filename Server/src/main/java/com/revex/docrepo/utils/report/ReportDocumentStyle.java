package com.revex.docrepo.utils.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.Locale;

@Data
public class ReportDocumentStyle {
	private Locale locale;
	private BigInteger top;
	private BigInteger bottom;
	private BigInteger left;
	private BigInteger right;

	@Builder
	private ReportDocumentStyle(Locale locale, int top, int right, int bottom, int left) {
		this.locale = locale;
		this.top = BigInteger.valueOf(top);
		this.bottom = BigInteger.valueOf(bottom);
		this.left = BigInteger.valueOf(left);
		this.right = BigInteger.valueOf(right);
	}
}
