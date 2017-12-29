package com.example.wozart.aura.sqlLite.device;

import android.provider.BaseColumns;

/**
 * Created by wozart on 29/12/17.
 */

public class DeviceContract {
    public static final class DeviceEntry implements BaseColumns {
        public static final String TABLE_NAME = "device";
        public static final String DEVICE_NAME = "name";
        public static final String LOAD_1 = "load_1";
        public static final String LOAD_2 = "load_2";
        public static final String LOAD_3 = "load_3";
        public static final String LOAD_4 = "load_4";
        public static final String HOME_NAME = "home";
        public static final String ROOM_NAME = "room";
        public static final String THING_NAME = "thing";
    }
}
