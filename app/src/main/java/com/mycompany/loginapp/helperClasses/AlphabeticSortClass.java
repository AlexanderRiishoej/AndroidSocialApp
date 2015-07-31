package com.mycompany.loginapp.helperClasses;

import com.google.common.collect.ComparisonChain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Alexander on 29-07-2015.
 */
public class AlphabeticSortClass {

    public AlphabeticSortClass(){
    }

    /**
     * Creates a unique id with the current user id and the receiver id
     */
    public static String addUniqueId(String currentUserId, String receiverUserId) {
        List<String> userIds = new ArrayList<>();
        userIds.add(currentUserId);
        userIds.add(receiverUserId);

        Collections.sort(userIds, stringAlphabeticalComparator);
        StringBuilder comparedId = new StringBuilder();
        for (String s : userIds) {
            comparedId.append(s);
        }

        return comparedId.toString();
    }

    /** Compares two strings alphabetically */
    private static Comparator<String> stringAlphabeticalComparator = new Comparator<String>() {
        public int compare(String str1, String str2) {
            return ComparisonChain.start().
                    compare(str1, str2, String.CASE_INSENSITIVE_ORDER).
                    result();
        }
    };
}
