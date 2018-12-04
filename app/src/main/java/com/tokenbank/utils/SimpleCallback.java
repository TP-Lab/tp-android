package com.tokenbank.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class SimpleCallback<T> {
    public interface ICallback<T> {
        void onFireEvent(T listener, Object... objects);
    }

    private HashMap<T, Boolean> mListener;
    private HashSet<T> mRemoveSet;

    public synchronized int size() {
        return mListener == null ? 0 : mListener.size();
    }

    public synchronized void clear() {
        if (mListener != null) {
            mListener.clear();
        }
    }

    public synchronized void addCallback(T listener) {
        addCallback(listener, false);
    }

    public synchronized void addCallback(T listener, boolean autoDetach) {
        if (listener == null) {
            return;
        }

        if (mListener == null) {
            mListener = new HashMap<>();
        }

        mListener.put(listener, autoDetach);
    }

    public synchronized void removeCallback(T listener) {
        if (mListener != null && listener != null) {
            if (mRemoveSet != null) {
                mRemoveSet.add(listener);
            } else {
                mListener.remove(listener);
            }
        }
    }

    public synchronized void firEvent(ICallback<T> callback, Object...objects) {
        if (mListener == null || callback == null) {
            return;
        }

        mRemoveSet = new HashSet<>();

        for (Map.Entry<T, Boolean> entry : mListener.entrySet()) {
            T listener = entry.getKey();
            callback.onFireEvent(listener, objects);

            if (entry.getValue()) {
                mRemoveSet.add(listener);
            }
        }

        for (T listener : mRemoveSet) {
            mListener.remove(listener);
        }

        mRemoveSet.clear();
        mRemoveSet = null;
    }
}
