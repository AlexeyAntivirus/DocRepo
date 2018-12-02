package com.revex.docrepo.exchange.works;

import com.revex.docrepo.database.utils.QualificationWorkType;
import com.revex.docrepo.database.views.DisciplineView;
import com.revex.docrepo.database.views.GroupView;
import com.revex.docrepo.database.views.StudentView;
import com.revex.docrepo.database.views.TeacherView;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class InsertNewQualificationWorkRequestPayload {
	private long id;
	private int beginYear;
	private int endYear;
	private int semesterNumber;
	private DisciplineView discipline;
	private GroupView group;
	private StudentView student;
	private String title;
	private QualificationWorkType workType;
	private String workFilePath;
	private String studentFullName;
	private String faculty;
	private String groupName;
	private String specialty;
	private String branch;
	private String educationLevel;
	private String educationProgram;
	private String gradeECTS;
	private String gradeNational;
	private int grade;
	private int courseNumber;
	private String teacherNames;
	private List<Long> teachers;
	private boolean isExtramural;
	private boolean isShortened;
	private Integer documentNumber;
	private Integer slideNumber;
}
