package com.megaminds.govisaviya.util;

public class RestURIs {

    // Auth controller
    public final static String AUTH = "/api/v1/auth";
    public final static String LOGIN = "/login";
    public final static String REGISTER = "/register";
    public final static String LOGOUT = "/logout";

    // Disease controller
    public final static String DISEASE = "/api/v1/disease";
    public final static String IDENTIFY = "/identify";
    public final static String HISTORY = "/history";

    // Plantnet controller
    public final static String PLANTNET = "https://my-api.plantnet.org";
    public final static String DISEASE_IDENTIFY = "/v2/diseases/identify";

    // Monitor controller
    public final static String MONITOR = "/api/v1/monitor";
    public final static String POLYGON = "/polygon";
    public final static String SOIL    = "/soil";
    public final static String WEATHER = "/weather";

    // AgroMonitoring API paths
    public final static String AGRO_POLYGONS = "/polygons";
    public final static String AGRO_SOIL     = "/soil";
    public final static String AGRO_WEATHER  = "/weather";

    // Fertilizer Approval controller
    public final static String FERTILIZER              = "/api/v1/fertilizer";
    public final static String FERTILIZER_SUBMIT       = "/submit";
    public final static String FERTILIZER_MY_REQUESTS  = "/my-requests";
    public final static String FERTILIZER_ADMIN_ALL    = "/admin/all";
    public final static String FERTILIZER_ADMIN_BY_ID  = "/admin/{id}";
    public final static String FERTILIZER_ADMIN_REVIEW = "/admin/{id}/review";
}
