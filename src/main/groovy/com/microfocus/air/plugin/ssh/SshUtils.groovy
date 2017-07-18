package com.microfocus.air.plugin.ssh


class SshUtils {

    /**
     * Check if a string is null, empty, or all whitespace
     * @param str The string whose value to check
     */
    static boolean isEmpty(String str) {
        return (str == null) || str.trim().isEmpty()
    }

    /**
     *  @param list The list to trim whitespaces and remove null entries
     *  @param delimiter The string that separates each entry
     */
    static List<String> toTrimmedList(def list, String delimiter) {
        return list.split(delimiter).findAll{ it?.trim() }.collect{ it.trim() }
    }

    static debug(String message) {
        println("[DEBUG] ${message}")
    }

    static info(String message) {
        println("[INFO] ${message}")
    }

    static error(String message) {
        println("[ERROR] ${message}")
    }
}