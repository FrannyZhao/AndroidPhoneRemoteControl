package com.frannyzhao.mqttlib.utils;

import android.util.SparseArray;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaofengyi on 3/6/18.
 */

public final class CollectionUtils {
    private CollectionUtils() {

    }

    public static boolean valid(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(SparseArray array) {
        return array == null || array.size() == 0;
    }

    public static <T> boolean isNullOrEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNullOrEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean equalSize(Collection<?> collection, int size) {
        if (collection == null || size < 0) {
            return false;
        }
        return size == collection.size();
    }

    public static boolean moreThanSize(Collection<?> collection, int size) {
        if (collection == null || size < 0) {
            return false;
        }
        return size < collection.size();
    }

    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations supported
     * by this list.
     *
     * @return 条件不符合，返回原来的collection
     *
     * <p>
     */
    public static <E> List<E> subList(List<E> collection, int fromIndex, int toIndex) {
        if (isNullOrEmpty(collection)) {
            return collection;
        }
        int size = collection.size();
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            return collection;
        } else {
            if (fromIndex == 0 && toIndex == size) {
                return collection;
            }
            return collection.subList(fromIndex, toIndex);
        }
    }

    public static boolean equalOrMoreThanSize(Collection<?> collection, int size) {
        if (collection == null || size < 0) {
            return false;
        }
        return size <= collection.size();
    }

    public static int size(Collection<?> collection) {
        if (collection == null) {
            return 0;
        }

        return collection.size();
    }

    public static <T> int size(T[] ts) {
        if (ts == null) {
            return 0;
        }

        return ts.length;
    }

    public static int size(Map map) {
        if (map == null) {
            return 0;
        }

        return map.size();
    }
}
