package com.codeoftheweb.salvo.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {
    public static boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    public static Map<String, Object> makeMap(String s, Object o) {
        Map<String, Object> aux = new LinkedHashMap<>();
        aux.put(s, o);
        return aux;
    }
}
