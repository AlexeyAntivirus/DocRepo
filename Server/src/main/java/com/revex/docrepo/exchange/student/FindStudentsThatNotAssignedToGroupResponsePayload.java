package com.revex.docrepo.exchange.student;

import com.revex.docrepo.database.entities.Student;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FindStudentsThatNotAssignedToGroupResponsePayload {
	private List<Student> students;
}
