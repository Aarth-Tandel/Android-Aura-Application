package com.wozart.aura.aura.sqlLite.device;

import android.provider.BaseColumns;

/**
 * Created by wozart on 29/12/17.
 */

public class DeviceContract {
    public static final class DeviceEntry implements BaseColumns {
        public static final String TABLE_NAME = "device";
        public static final String DEVICE_NAME = "name";
        public static final String LOAD = "load";
        public static final String HOME_NAME = "home";
        public static final String ROOM_NAME = "room";
        public static final String THING_NAME = "thing";
        public static final String UIUD = "uiud";
        public static final String ACCESS = "access";
    }
}
