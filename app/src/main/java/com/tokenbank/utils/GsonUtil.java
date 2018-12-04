package com.tokenbank.utils;

import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class GsonUtil implements Serializable {

    private JSONObject obj;
    private JSONArray arrayobj;

    public String toString() {
        if (obj != null) {
            return obj.toString();
        } else if (arrayobj != null) {
            return arrayobj.toString();
        }

        return "";
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (obj != null) {
            out.write(obj.toString().getBytes());
        } else if (arrayobj != null) {
            out.write(arrayobj.toString().getBytes());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        char[] buffer = new char[256];
        int ret = -1;
        String content = "";

        while ((ret = br.read(buffer)) != -1) {
            content = content.concat(new String(buffer, 0, ret));
        }

        br.close();

        try {
            obj = new JSONObject(content);
        } catch (Throwable e) {
            try {
                arrayobj = new JSONArray(content);
            } catch (Throwable e1) {
                throw new IOException(e1.toString());
            }
        }
    }

    public GsonUtil(String jsonStr) {
        try {
            obj = new JSONObject(jsonStr);
        } catch (Throwable e) {
//            e.printStackTrace();
            try {
                arrayobj = new JSONArray(jsonStr);
            } catch (Throwable e1) {
            }
        }
    }

    public GsonUtil(JSONObject object) {
        obj = object;
    }

    public GsonUtil(JSONArray object) {
        arrayobj = object;
    }

    public GsonUtil(Object object) {
        this(new Gson().toJson(object));
    }

    public boolean isValid() {
        return arrayobj != null || obj != null;
    }

    public boolean isArray() {
        return arrayobj != null;
    }

    public int getLength() {
        if (isArray()) {
            return arrayobj.length();
        }

        return obj.length();
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (obj != null) {
            try {
                return obj.getBoolean(key);
            } catch (Throwable e) {
            }
        }

        return defValue;
    }

    public String getString(String key, String defValue) {
        if (obj != null) {
            try {
                String t = obj.getString(key);
                if (TextUtils.equals("null",t)) {
                    return "";
                }
                return t;
            } catch (Throwable e) {
            }
        }

        return defValue;
    }

    public String getString(int index, String defValue) {
        if (arrayobj != null) {
            try {
                String t = arrayobj.getString(index);
                if (TextUtils.equals("null",t)) {
                    return "";
                }
                return t;
            } catch (Throwable e) {
            }
        }

        return defValue;
    }

    public int getInt(String key, int defValue) {
        if (obj != null) {
            try {
                return obj.getInt(key);
            } catch (Throwable e) {
            }
        }

        return defValue;
    }

    public int getInt(int index, int defValue) {
        if (arrayobj != null) {
            try {
                return arrayobj.getInt(index);
            } catch (Throwable e) {
            }
        }

        return defValue;
    }

    public long getLong(String key, long defValue) {
        if (obj != null) {
            try {
                return obj.getLong(key);
            } catch (Throwable e) {
            }
        }

        return defValue;
    }

    public long getLong(int index, long defValue) {
        if (arrayobj != null) {
            try {
                return arrayobj.getLong(index);
            } catch (Throwable e) {
            }
        }

        return defValue;
    }


    public double getDouble(String key, double defValue) {
        if (obj != null) {
            try {
                return obj.getDouble(key);
            } catch (Throwable e) {
            }
        }

        return defValue;
    }

    public double getDouble(int index, double defValue) {
        if (arrayobj != null) {
            try {
                return arrayobj.getDouble(index);
            } catch (Throwable e) {
            }
        }

        return defValue;
    }

    public GsonUtil getArray(String key) {
        GsonUtil jw = null;

        if (obj != null) {
            try {
                jw = new GsonUtil(obj.getJSONArray(key));
            } catch (Throwable e) {
            }
        }

        return jw;
    }

    public GsonUtil getArray(String key, String def) {
        GsonUtil jw = getArray(key);
        if (jw == null) {
            jw = new GsonUtil(def);
        }

        return jw;
    }

    public GsonUtil getArray(int index) {
        GsonUtil jw = null;

        if (arrayobj != null) {
            try {
                jw = new GsonUtil(arrayobj.getJSONArray(index));
            } catch (Throwable e) {
            }
        }

        return jw;
    }

    public GsonUtil getArray(int index, String def) {
        GsonUtil jw = getArray(index);
        if (jw == null) {
            jw = new GsonUtil(def);
        }

        return jw;
    }

    public GsonUtil getObject(String key) {
        GsonUtil jw = null;

        if (obj != null) {
            try {
                jw = new GsonUtil(obj.getJSONObject(key));
            } catch (Throwable e) {
            }
        }

        return jw;
    }

    public GsonUtil getObject(String key, String def) {
        GsonUtil jw = getObject(key);

        if (jw == null) {
            jw = new GsonUtil(def);
        }

        return jw;
    }

    public GsonUtil getObject(int index) {
        GsonUtil jw = null;

        if (arrayobj != null) {
            try {
                jw = new GsonUtil(arrayobj.getJSONObject(index));
            } catch (Throwable e) {
                //Log.w("GsonUtil.getObject", "index: " + index + ", " + e.toString());
            }
        }

        return jw;
    }

    public GsonUtil getObject(int index, String def) {
        GsonUtil jw = getObject(index);

        if (jw == null) {
            jw = new GsonUtil(def);
        }

        return jw;
    }

    public GsonUtil remove(int index) {
        if (arrayobj != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    arrayobj.remove(index);
                } else {
                    Field field = JSONArray.class.getDeclaredField("values");
                    field.setAccessible(true);
                    List values = (List) field.get(arrayobj);
                    values.remove(index);
                }
            } catch (Throwable e) {
            }
        }

        return this;
    }

    public GsonUtil remove(String key) {
        if (obj != null) {
            try {
                obj.remove(key);
            } catch (Throwable e) {
            }
        }

        return this;
    }

    public GsonUtil add(GsonUtil j2) {
        if (arrayobj != null) {
            if (j2.isArray()) {
                for (int i = 0; i < j2.getLength(); ++i) {
                    try {
                        arrayobj.put(j2.arrayobj.get(i));
                    } catch (Throwable e) {
                    }
                }
            } else {
                arrayobj.put(j2.obj);
            }
        }

        return this;
    }


    public GsonUtil put(GsonUtil j2) {
        return put(-1, j2);
    }

    public GsonUtil put(int index, GsonUtil j2) {
        if (arrayobj != null) {
            try {
                if (index < 0) {
                    if (j2.isArray()) {
                        arrayobj.put(j2.arrayobj);
                    } else {
                        arrayobj.put(j2.obj);
                    }
                } else {
                    if (j2.isArray()) {
                        arrayobj.put(index, j2.arrayobj);
                    } else {
                        arrayobj.put(index, j2.obj);
                    }
                }
            } catch (Exception e) {
            }
        }

        return this;
    }

    public GsonUtil put(String key, GsonUtil j2) {
        if (obj != null && (j2.arrayobj != null || j2.obj != null)) {
            try {
                obj.put(key, j2.isArray() ? j2.arrayobj : j2.obj);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    public GsonUtil putString(String key, String value) {
        if (obj != null) {
            try {
                obj.put(key, value);
            } catch (Throwable e) {
            }
        }

        return this;
    }

    public GsonUtil putDouble(String key, double value) {
        if (obj != null) {
            try {
                obj.put(key, value);
            } catch (Throwable e) {
            }
        }

        return this;
    }

    public GsonUtil putString(int index, String value) {
        if (arrayobj != null) {
            try {
                if (index < 0) {
                    arrayobj.put(value);
                } else {
                    arrayobj.put(index, value);
                }
            } catch (Throwable e) {
            }
        }

        return this;
    }


    public GsonUtil putInt(String key, int value) {
        if (obj != null) {
            try {
                obj.put(key, value);
            } catch (Throwable e) {
            }
        }

        return this;
    }


    public GsonUtil putLong(String key, long value) {
        if (obj != null) {
            try {
                obj.put(key, value);
            } catch (Throwable e) {
            }
        }

        return this;
    }

}
