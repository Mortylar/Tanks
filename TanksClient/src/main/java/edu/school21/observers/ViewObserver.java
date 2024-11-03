package com.example.observers;

import com.example.view.Viewable;

public class ViewObserver implements Observable {

    private Viewable view;

    public ViewObserver(Viewable view) { this.view = view; }

    @Override
    public void notifyView() {
        view.catchEvent();
    }
}
