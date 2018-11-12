package com.revex.docrepo.exchange.stat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatResponsePayload {
	private int wholeCount;
	private int aGradeWorksCount;
	private int bGradeWorksCount;
	private int cGradeWorksCount;
	private int fGradeWorksCount;
}
