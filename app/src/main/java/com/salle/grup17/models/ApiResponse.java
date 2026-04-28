package com.salle.grup17.models;

import java.util.List;

public class ApiResponse<T> {
    private Info info;
    private List<T> results;

    public Info getInfo() {
        return info;
    }

    public List<T> getResults() {
        return results;
    }

    public static class Info {
        private int count;
        private int pages;
        private String next;
        private String prev;

        public int getCount() {
            return count;
        }

        public int getPages() {
            return pages;
        }

        public String getNext() {
            return next;
        }

        public String getPrev() {
            return prev;
        }
    }
}
