package com.wozart.aura.aura.model.AuraSwitch;

/**
 * Created by wozart on 14/03/18.
 */

public class AuraSwitchLoad {
    private AuraLoad load_1;
    private AuraLoad load_2;
    private AuraLoad load_3;
    private AuraLoad load_4;

    private static String INITIAL_LOAD = "{" +
            "  \"load_1\": {" +
            "    \"name\": \"LED\"," +
            "    \"dimmable\": true" +
            "  },\n" +
            "  \"load_2\": {" +
            "    \"name\": \"Lamp\"," +
            "    \"dimmable\": true" +
            "  }," +
            "  \"load_3\": {" +
            "    \"name\": \"Table Light\"," +
            "    \"dimmable\": true" +
            "  }," +
            "  \"load_4\": {" +
            "    \"name\": \"Switch\"," +
            "    \"dimmable\": false" +
            "  }" +
            "}";

    public void AuraSwitch(){

    }

    public AuraLoad getLoad_1(){return this.load_1;}

    public AuraLoad getLoad_2(){return this.load_2;}

    public AuraLoad getLoad_3(){return this.load_3;}

    public AuraLoad getLoad_4(){return this.load_4;}

    public void setLoad_1(AuraLoad load) {this.load_1 = load;}

    public void setLoad_2(AuraLoad load) {this.load_2 = load;}

    public void setLoad_3(AuraLoad load) {this.load_3 = load;}

    public void setLoad_4(AuraLoad load) {this.load_4 = load;}

    public String getDefaultLoads(){
        return INITIAL_LOAD;
    }

    public class AuraLoad{
        private String name;
        private boolean dimmable;

        public AuraLoad(){

        }

        public String getName(){return this.name;}

        public Boolean getDimmable(){return this.dimmable;}

        public void setName(String name){this.name = name;}

        public void setDimmable(boolean dim) {this.dimmable = dim;}

    }
}

