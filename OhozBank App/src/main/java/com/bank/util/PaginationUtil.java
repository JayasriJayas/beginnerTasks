package com.bank.util;

public class PaginationUtil {

    // Calculate the total number of pages based on total records and page size
    public static int calculateTotalPages(int totalRecords, int pageSize) {
        if (pageSize == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    // Validate page number, ensure it's not less than 1
    public static int validatePageNumber(int pageNumber) {
        return pageNumber > 0 ? pageNumber : 1;
    }

    // Validate page size, ensure it's a positive number
    public static int validatePageSize(int pageSize) {
        return pageSize > 0 ? pageSize : 10; // Default to 10 if pageSize is invalid
    }

    // Calculate the offset for the SQL query
    public static int calculateOffset(int pageNumber, int pageSize) {
        return (pageNumber - 1) * pageSize;
    }
}
