package com.revex.docrepo.database.entities;

import com.revex.docrepo.database.utils.SemesterType;
import com.revex.docrepo.database.views.GroupView;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Student {
    private final long id;
    private String fullName;
    private int beginYear;
    private int endYear;
    private SemesterType semesterType;
    private GroupView group;
    private int courseNumber;
}
