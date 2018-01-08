package com.suhel.photosphere.screens.comments.presenter;

import android.support.annotation.NonNull;

import com.suhel.photosphere.screens.comments.contract.CommentsContract;
import com.suhel.photosphere.service.realtime.WS;
import com.suhel.photosphere.service.rest.RestService;
import com.suhel.photosphere.service.storage.Store;

public abstract class CommentsPresenter implements CommentsContract.Presenter {

    protected CommentsContract.View view;
    protected RestService restService;
    protected Store store;
    protected WS ws;

    CommentsPresenter(@NonNull CommentsContract.View view, RestService restService, Store store, WS ws) {
        this.view = view;
        this.restService = restService;
        this.store = store;
        this.ws = ws;
    }

}
