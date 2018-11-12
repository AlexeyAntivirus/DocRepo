package com.revex.docrepo.exchange.teacher;

import lombok.Data;

@Data
public class UpdateTeacherByParamRequestPayload {
    private long id;
    private String fullName;
    private String cathedra;
    private String position;
    private String degree;
    private String rank;
    private boolean isWorking;
}
