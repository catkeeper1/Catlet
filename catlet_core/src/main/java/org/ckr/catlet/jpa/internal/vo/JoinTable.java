package org.ckr.catlet.jpa.internal.vo;

import java.util.ArrayList;
import java.util.List;

public class JoinTable {
    private String tableName = null;

    private String joinFullClassName = null;

    private String inverseFullClassName = null;

    private List<Index> indexList = new ArrayList<>();

    private List<Column> joinColumnList = new ArrayList<>();

    private List<Column> inverseColumnList = new ArrayList<>();

}
