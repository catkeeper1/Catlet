package org.ckr.catlet.jpa.internal.vo;

import java.util.ArrayList;
import java.util.List;

public class ForeignKey {
    private String joinType;

    private String sourceTableName = "";

    private String targetTableName = "";

    private List<String> sourceColumnNames = new ArrayList<>();

    private List<String> targetColumnNames = new ArrayList<>();

    private Boolean optional = true;


}
