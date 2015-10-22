package com.sevenfloor.mtcsound;

public class Utils {

    public static Integer stringToInt(String value)
    {
        try{
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer stringToIntRange(String value, Integer min, Integer max){
        Integer result = stringToInt(value);
        if (result == null) return null;
        if (min != null && result < min) return null;
        if (max != null && result > max) return null;
        return result;
    }

    public static Boolean stringToBoolean(String value, String trueValue, String falseValue)
    {
        if (trueValue.equals(value)) return true;
        if (falseValue.equals(value)) return false;
        return null;
    }

    public static String booleanToString(boolean value, String trueValue, String falseValue)
    {
        return value ? trueValue : falseValue;
    }

    public static int adjustInt(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static <T extends Enum<T>> T stringToEnum(Class<T> enumType, String name){
        try {
            return Enum.valueOf(enumType, name);
        }
        catch(IllegalArgumentException e)
        {
            return null;
        }
    }

    public static String[] splitKeyValue(String keyValue) {
        if (keyValue == null) return null;
        int pos = keyValue.indexOf("=");
        if (pos < 0) return null;
        return new String[] {keyValue.substring(0, pos), keyValue.substring(pos+1)};
    }

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
