package com.nanda.inappbilling.utils

object JavaUtils {
    fun isNullOrEmpty(list: List<*>?): Boolean {
        return list == null || list.isEmpty()
    }
}