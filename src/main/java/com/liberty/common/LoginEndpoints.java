package com.liberty.common;

/**
 * @author Dmytro_Kovalskyi.
 * @since 24.10.2016.
 */
public interface LoginEndpoints {

    String MAIN = "https://www.easports.com/fifa/ultimate-team/web-app";
    String NUCLEUS = "https://www.easports" +
            ".com/iframe/fut17/?locale=en_US&baseShowoffUrl=https%3A%2F%2Fwww.easports" +
            ".com%2Fde%2Ffifa%2Fultimate-team%2Fweb-app%2Fshow-off&guest_app_uri=http%3A%2F%2Fwww.easports.com%2Fde%2Ffifa%2Fultimate-team%2Fweb-app";
    String PERSONAS = "https://www.easports.com/fifa/api/personas";
    String SHARDS = "https://www.easports.com/iframe/fut17/p/ut/shards/v2";
    String ACCOUNTS = "https://www.easports" +
            ".com/iframe/fut17/p/ut/game/fifa17/user/accountinfo?filterConsoleLogin=true&sku=FUT17WEB" +
            "&returningUserGameYear=2016&_=";
    String SESSION = "https://www.easports.com/iframe/fut17/p/ut/auth";
    String QUESTION = "https://www.easports.com/iframe/fut17/p/ut/game/fifa17/phishing/question?_=";
    String VALIDATE = "https://www.easports.com/iframe/fut17/p/ut/game/fifa17/phishing/validate?_=";
}
