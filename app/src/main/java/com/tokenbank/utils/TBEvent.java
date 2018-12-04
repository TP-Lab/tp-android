package com.tokenbank.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.CopyOnWriteArraySet;

public class TBEvent extends BroadcastReceiver {
    private static final String ACTION_EVENT = "com.tokenbank.ACTION_EVENT";
    private static TBEvent mInst;

    private CopyOnWriteArraySet<OnEventListener> mOnRxEventListener;
    private Context mContext;

    public interface OnEventListener {
        boolean onEvent(String event, Bundle data);
    }

    public static synchronized TBEvent getInstance(Context context) {
        if (mInst == null) {
            mInst = new TBEvent(context);
        }

        return mInst;
    }

    private TBEvent(Context context) {
        mContext = context.getApplicationContext();
    }

    public TBEvent subscribe(OnEventListener listener) {
        if (listener == null) {
            return this;
        }

        if (mOnRxEventListener == null) {
            mOnRxEventListener = new CopyOnWriteArraySet<>();
            LocalBroadcastManager.getInstance(mContext).registerReceiver(this, new IntentFilter(ACTION_EVENT));
        }

        mOnRxEventListener.add(listener);
        return this;
    }

    public TBEvent unsubscribe(OnEventListener listener) {
        if (listener != null && mOnRxEventListener != null) {
            mOnRxEventListener.remove(listener);

            if (mOnRxEventListener.isEmpty()) {
                mOnRxEventListener = null;
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(this);
            }
        }

        return this;
    }

    public TBEvent broadcast(String event, Bundle data) {
        if (event == null) {
            return this;
        }

        Intent intent = new Intent(ACTION_EVENT);
        intent.putExtra("event", event);
        intent.putExtra("data", data);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        return this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_EVENT.equals(intent.getAction()) && mOnRxEventListener != null) {
            for (OnEventListener listener : mOnRxEventListener) {
                if (listener.onEvent(intent.getStringExtra("event"), intent.getBundleExtra("data"))) {
                    break;
                }
            }
        }
    }
}
