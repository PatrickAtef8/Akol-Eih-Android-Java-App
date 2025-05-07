package com.example.akoleih.utils;

import java.util.HashMap;

public class CountryFlagUtil {
    private static final HashMap<String, String> COUNTRY_CODES = new HashMap<String, String>() {{
        put("american", "US");
        put("british", "GB");
        put("canadian", "CA");
        put("chinese", "CN");
        put("croatian", "HR");
        put("dutch", "NL");
        put("egyptian", "EG");
        put("filipino", "PH");
        put("french", "FR");
        put("greek", "GR");
        put("indian", "IN");
        put("irish", "IE");
        put("italian", "IT");
        put("jamaican", "JM");
        put("japanese", "JP");
        put("kenyan", "KE");
        put("malaysian", "MY");
        put("mexican", "MX");
        put("moroccan", "MA");
        put("polish", "PL");
        put("portuguese", "PT");
        put("russian", "RU");
        put("spanish", "ES");
        put("thai", "TH");
        put("tunisian", "TN");
        put("turkish", "TR");
        put("ukrainian", "UA");
        put("uruguayan", "UY");
        put("vietnamese", "VN");
    }};

    public static String getCountryCode(String area) {
        return COUNTRY_CODES.getOrDefault(area.toLowerCase(), null);
    }
}