package com.tokenbank.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectUtil {
    static class Arg {
        Class<?> type;
        Object value;
    }

    public static Arg genArg(Class<?> type, Object value) {
        Arg arg = new Arg();
        arg.type = type;
        arg.value = value;
        return arg;
    }

    public static Object getFieldValue(Object obj, String name, Object defValue) {
        if (obj != null) {
            return getFieldValue(obj.getClass(), obj, name, defValue);
        }

        return defValue;
    }

    public static Object getFieldValue(Class<?> cls, Object obj, String name, Object defValue) {
        try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return defValue;
    }

    public static boolean getFieldValue(Object obj, String name, boolean defValue) {
        return (Boolean) getFieldValue(obj, name, Boolean.valueOf(defValue));
    }

    public static boolean getFieldValue(Class<?> cls, Object obj, String name, boolean defValue) {
        return (Boolean) getFieldValue(cls, obj, name, Boolean.valueOf(defValue));
    }

    public static int getFieldValue(Object obj, String name, int defValue) {
        return (Integer) getFieldValue(obj, name, Integer.valueOf(defValue));
    }

    public static int getFieldValue(Class<?> cls, Object obj, String name, int defValue) {
        return (Integer) getFieldValue(cls, obj, name, Integer.valueOf(defValue));
    }

    public static long getFieldValue(Object obj, String name, long defValue) {
        return (Long) getFieldValue(obj, name, Long.valueOf(defValue));
    }

    public static long getFieldValue(Class<?> cls, Object obj, String name, long defValue) {
        return (Long) getFieldValue(cls, obj, name, Long.valueOf(defValue));
    }

    public static String getFieldValue(Object obj, String name, String defValue) {
        return (String) getFieldValue(obj, name, defValue);
    }

    public static String getFieldValue(Class<?> cls, Object obj, String name, String defValue) {
        return (String) getFieldValue(cls, obj, name, defValue);
    }

    public static void setFieldVaule(Object obj, String name, Object value) {
        if (obj != null) {
            setFieldVaule(obj.getClass(), obj, name, value);
        }
    }

    public static void setFieldVaule(Class<?> cls, Object obj, String name, Object value) {
        try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Object callMethod(String cls, Object obj, String name, Object... args) {
        try {
            return callMethod(Class.forName(cls), obj, name, args);
        } catch (Throwable e) {}

        return null;
    }

    public static Object callMethod(Class<?> cls, Object obj, String name, Object... args) {
        try {
            if (args != null && args.length > 0) {
                Class<?> parameterTypes[] = new Class<?>[args.length];
                Object objs[] = new Object[args.length];

                for (int i = 0; i < args.length; ++i) {
                    if (args[i] instanceof Arg) {
                        Arg arg = (Arg) args[i];
                        parameterTypes[i] = arg.type;
                        objs[i] = arg.value;
                    } else {
                        if (args[i] instanceof Integer) {
                            parameterTypes[i] = int.class;
                        } else if (args[i] instanceof Long) {
                            parameterTypes[i] = long.class;
                        } else if (args[i] instanceof Boolean) {
                            parameterTypes[i] = boolean.class;
                        } else if (args[i] instanceof Byte) {
                            parameterTypes[i] = byte.class;
                        } else if (args[i] instanceof Double) {
                            parameterTypes[i] = double.class;
                        } else if (args[i] instanceof Float) {
                            parameterTypes[i] = float.class;
                        } else {
                            parameterTypes[i] = args[i].getClass();
                        }

                        objs[i] = args[i];
                    }
                }

                if (name == null || name.length() <= 0) {
                    Constructor method = cls.getConstructor(parameterTypes);
                    method.setAccessible(true);
                    return method.newInstance(objs);
                }

                try {
                    Method method = cls.getDeclaredMethod(name, parameterTypes);
                    method.setAccessible(true);
                    return method.invoke(obj, objs);
                } catch (NoSuchMethodException e) {
                    Method[] method = cls.getDeclaredMethods();
                    for (int i = 0; i < method.length; ++i) {
                        if (method[i].getName().equals(name)) {
                            method[i].setAccessible(true);
                            return method[i].invoke(obj, objs);
                        }
                    }
                }
            } else {
                if (name == null || name.length() <= 0) {
                    Constructor method = cls.getConstructor();
                    method.setAccessible(true);
                    return method.newInstance();
                }

                try {
                    Method method = cls.getDeclaredMethod(name);
                    method.setAccessible(true);
                    return method.invoke(obj);
                } catch (NoSuchMethodException e) {
                    Method[] method = cls.getDeclaredMethods();
                    for (int i = 0; i < method.length; ++i) {
                        if (method[i].getName().equals(name)) {
                            method[i].setAccessible(true);
                            return method[i].invoke(obj);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }
}
