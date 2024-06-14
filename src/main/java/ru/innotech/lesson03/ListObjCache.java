package ru.innotech.lesson03;

import java.util.ArrayList;
import java.util.List;

public class ListObjCache {
    public static List<CacheClass> cacheObj= new ArrayList<>();
    public static void addListCache(CacheClass cacheClass) {
        cacheObj.add(cacheClass);
    }
    public static void cleanCache() {
        for (int i = 0; i < cacheObj.size(); i++)
            cacheObj.get(i).cleanCache();
    }
}
