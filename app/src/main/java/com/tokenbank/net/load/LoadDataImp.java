package com.tokenbank.net.load;


import com.tokenbank.net.apirequest.ApiRequest;
import com.tokenbank.net.listener.LoadDataListener;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */
public class LoadDataImp implements ILoadData {

    @Override
    public void loadData(final ApiRequest request, boolean shouldCache, final LoadDataListener listener) {
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                listener.loadFinish();
            }

            @Override
            public void onError(Throwable throwable) {
                listener.loadFailed(throwable, request.getReqId());
            }

            @Override
            public void onNext(String response) {
                listener.loadSuccess(response);
            }
        };

        request.getObservableObj(shouldCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
