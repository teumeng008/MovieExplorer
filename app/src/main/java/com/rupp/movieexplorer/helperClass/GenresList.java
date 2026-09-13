package com.rupp.movieexplorer.helperClass;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenresList {
    private static final Map<String, Integer> TV_GENRES_LIST;
    private static final Map<String, Integer> MOVIE_GENRES_LIST;

    static {
        Map<String, Integer> TvMap = new LinkedHashMap<>();
        TvMap.put("Action & Adventure", 10759);
        TvMap.put("Animation", 16);
        TvMap.put("Comedy", 35);
        TvMap.put("Crime", 80);
        TvMap.put("Documentary", 99);
        TvMap.put("Drama", 18);
        TvMap.put("Family", 10751);
        TvMap.put("Kids", 10762);
        TvMap.put("Mystery", 9648);
        TvMap.put("News", 10763);
        TvMap.put("Reality", 10764);
        TvMap.put("Sci-Fi & Fantasy", 10765);
        TvMap.put("Soap", 10766);
        TvMap.put("Talk", 10767);
        TvMap.put("War & Politics", 10768);
        TvMap.put("Western", 37);
        TV_GENRES_LIST = Collections.unmodifiableMap(TvMap);

        Map<String, Integer> MovieMap = new LinkedHashMap<>();
        MovieMap.put("Action", 28);
        MovieMap.put("Adventure", 12);
        MovieMap.put("Animation", 16);
        MovieMap.put("Comedy", 35);
        MovieMap.put("Crime", 80);
        MovieMap.put("Documentary", 99);
        MovieMap.put("Drama", 18);
        MovieMap.put("Family", 10751);
        MovieMap.put("Fantasy", 14);
        MovieMap.put("History", 36);
        MovieMap.put("Horror", 27);
        MovieMap.put("Music", 10402);
        MovieMap.put("Mystery", 9648);
        MovieMap.put("Romance", 10749);
        MovieMap.put("Science Fiction", 878);
        MovieMap.put("TV Movie", 10770);
        MovieMap.put("Thriller", 53);
        MovieMap.put("War", 10752);
        MovieMap.put("Western", 37);
        MOVIE_GENRES_LIST = Collections.unmodifiableMap(MovieMap);
    }
    public static Map<String, Integer> getGenres(String mediaType) {
        if("tv".equals(mediaType)){
            return TV_GENRES_LIST;
        }
        return MOVIE_GENRES_LIST;
    }

    public static Map<String, Integer> getMovieGenresList() {
        return MOVIE_GENRES_LIST;
    }

    public static Map<String, Integer> getTvGenresList() {
        return TV_GENRES_LIST;
    }
}



