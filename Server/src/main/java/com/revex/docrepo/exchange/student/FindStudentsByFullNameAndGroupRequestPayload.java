package com.revex.docrepo.exchange.student;

import com.revex.docrepo.database.utils.SemesterType;
import com.revex.docrepo.database.views.GroupView;
import lombok.Data;

@Data
public class FindStudentsByFullNameAndGroupRequestPayload {
	private GroupView group;
	private int beginYear;
	private int endYear;
	private SemesterType semesterType;
}
