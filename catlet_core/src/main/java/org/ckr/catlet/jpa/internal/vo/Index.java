package org.ckr.catlet.jpa.internal.vo;

import java.util.ArrayList;
import java.util.List;

public class Index {
    private String name = null;

    private List<IndexColumn> columnList = new ArrayList<>();

    private Boolean unique = null;

    public static class IndexColumn {

        private String name;

        private String order;

        public String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        public String getOrder() {
            return order;
        }

        private void setOrder(String order) {
            this.order = order;
        }

        @Override
        public String toString() {
            return "IndexColumn{"
                    + "name='" + name + '\''
                    + ", order='" + order + '\''
                    + '}';
        }
    }
}
