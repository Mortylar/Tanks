package edu.school21.observers;

import edu.school21.view.Viewable;

public class ViewObserver implements Observable {

    private Viewable view;

    public ViewObserver(Viewable view) { this.view = view; }

    @Override
    public void notifyView() {
        view.catchEvent();
    }
}
