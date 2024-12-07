package com.kadenfrisk.autoscroll.custom;

import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;

public class BetterPoint2D extends Point2D {
    public BetterPoint2D(double v, double v1) {
        super(v, v1);
    }

    public boolean insideOf(ScrollPane scrollPane) {
        // Get the dimensions of scroll pane
        double topLeft = 0;
        double topRight = topLeft + scrollPane.getViewportBounds().getWidth();
        double bottomLeft = 0 + scrollPane.getViewportBounds().getHeight();
        double bottomRight = topRight + bottomLeft;
        System.out.printf("topLeft: %f, topRight: %f, bottomLeft: %f, bottomRight: %f\n", topLeft, topRight, bottomLeft, bottomRight);

        // Get the dimensions of the point
        double x = getX();
        double y = getY();
        System.out.printf("x: %f, y: %f\n", x, y);

        // Check if the point is within the calculated values
        // topLeft <= x <= topRight
        // bottomLeft <= y <= bottomRight
        return topLeft <= x && x <= topRight && bottomLeft <= y && y <= bottomRight;
    }
}
