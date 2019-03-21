package com.berberic.android.fishinglog.database;

/**
 * Created by berberic on 8/28/2017.
 */

public class FishlogDbSchema {

    public static final class FishlogTable {
        public static final String NAME = "fishlogs";

        public static final class Cols {
            public static final String UUID = "uuid";  // unique id
            public static final String TITLE = "title";  // Stream Name
            public static final String DATEIT = "date"; //date
            public static final String QSF = "qsf"; //fish caught
            public static final String PARTNERS = "partners"; //pSEASON
            public static final String LOCDESC = "locdesc"; //locdesc
            public static final String NOTES = "notes"; //notes
            public static final String HIDELOC = "hideloc";
            public static final String WATERTEMP ="watertemp"; //watertemp
            public static final String RECEIPIENT = "receipient"; //month
        }
    }

    public static final class FishlogIDTable {
        public static final String NAME = "fishlogsuserid";

        public static final class Cols {
            public static final String USERUUID = "useruuid";
            public static final String LOCK = "lockid";
            public static final String ENCRYPTID = "encryptid";
        }

    }
}
