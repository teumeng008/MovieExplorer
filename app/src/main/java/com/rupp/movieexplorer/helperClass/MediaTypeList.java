package com.rupp.movieexplorer.helperClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaTypeList {
    private static final List<String> mediaTypeList;

    static {
        List<String> List = new ArrayList<>();
        List.add("tv");
        List.add("movie");
        List.add("person");
        mediaTypeList = Collections.unmodifiableList(List);
    }

    public static List<String> getMediaTypeList() {
        return mediaTypeList;
    }
}
