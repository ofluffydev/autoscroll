package com.kadenfrisk.autoscroll.custom;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;

public class BetterScrollPane extends ScrollPane {
    private static final double SCROLL_MULTIPLIER = 0.01; // Adjust scrolling sensitivity

    public BetterScrollPane() {
        super();
        initializeScroll();
    }

    private void initializeScroll() {
        setOnScroll(this::handleScroll);
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY() * SCROLL_MULTIPLIER;
        double currentVvalue = getVvalue();
        double currentHvalue = getHvalue();

        setVvalue(currentVvalue - delta);

        if (getVvalue() < 0) {
            setVvalue(0);
        } else if (getVvalue() > 1) {
            setVvalue(1);
        }

        setHvalue(currentHvalue - (event.getDeltaX() * SCROLL_MULTIPLIER));
    }
}
