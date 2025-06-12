package com.bank.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class RequestParser {
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public static <T> T parseRequest(HttpServletRequest req, Class<T> clazz) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            return gson.fromJson(reader, clazz);
        }
    }
}
