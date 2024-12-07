package com.kadenfrisk.autoscroll.custom;

import com.kadenfrisk.autoscroll.App;
import com.kadenfrisk.autoscroll.util.Geometry;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DraggableLabel extends Label {
    private static final Logger logger = Logger.getLogger(DraggableLabel.class.getName());
    private final ScrollPane scrollPane;
    private final Grid grid;
    private double x, y;

    public DraggableLabel(String text, ScrollPane scrollPane, Grid grid) {
        super(text);
        this.scrollPane = scrollPane;
        this.grid = grid;

        // Styles
        setStyle("""
                -fx-background-color: white;
                -fx-border-color: black;
                -fx-border-width: 1px;
                """);

        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
    }

    private void handleMousePressed(MouseEvent e) {
        x = e.getSceneX();
        y = e.getSceneY();
        logger.log(Level.INFO, "Mouse pressed at: ({0}, {1})", new Object[]{x, y});
        e.consume();
    }

    private void handleMouseDragged(MouseEvent e) {
        // Adjust drag deltas using the zoom factor
        double dx = (e.getSceneX() - x) / Geometry.getZoomFactor();
        double dy = (e.getSceneY() - y) / Geometry.getZoomFactor();

        double newX = getLayoutX() + dx;
        double newY = getLayoutY() + dy;

        Rectangle2D viewportBounds = App.getViewportBounds();

        // Get current horizontal and vertical scroll positions
        double hValue = scrollPane.getHvalue();
        double vValue = scrollPane.getVvalue();
        double hDistance = hValue * (grid.getWidth() - viewportBounds.getWidth());
        double vDistance = vValue * (grid.getHeight() - viewportBounds.getHeight());

        // Ensure the label stays within the parent's bounds
        if (newX < 0) {
            newX = 0;
        } else if (newX + getWidth() > grid.getWidth()) {
            newX = grid.getWidth() - getWidth();
        }
        // Check if the label is near the left or right edge of the viewport
        else if (newX < hDistance) {
            newX = hDistance;
        } else if (newX + getWidth() > hDistance + viewportBounds.getWidth()) {
            newX = hDistance + viewportBounds.getWidth() - getWidth();
        } else {
            // Auto-scroll horizontally if the label is near the left or right edge of the screen
            if (newX < hDistance + 10) { // 10 px from left edge
                scrollPane.setHvalue(hValue - 0.01);
            } else if (newX + getWidth() > hDistance + viewportBounds.getWidth() - 10) { // 10 px from right edge
                scrollPane.setHvalue(hValue + 0.01);
            }
        }

        // Ensure the label stays within vertical bounds
        if (newY < 0) {
            newY = 0;
        } else if (newY + getHeight() > grid.getHeight()) {
            newY = grid.getHeight() - getHeight();
        }
        // Check if the label is near the top or bottom edge of the viewport
        else if (newY < vDistance) {
            newY = vDistance;
        } else if (newY + getHeight() > vDistance + viewportBounds.getHeight()) {
            newY = vDistance + viewportBounds.getHeight() - getHeight();
        } else {
            // Auto-scroll vertically if the label is near the top or bottom edge of the screen
            if (newY < vDistance + 10) { // 10 px from top edge
                scrollPane.setVvalue(vValue - 0.01);
            } else if (newY + getHeight() > vDistance + viewportBounds.getHeight() - 10) { // 10 px from bottom edge
                scrollPane.setVvalue(vValue + 0.01);
            }
        }

        // Update the label position
        setLayoutX(newX);
        setLayoutY(newY);

        logger.log(Level.INFO, "Mouse dragged to: ({0}, {1})", new Object[]{newX, newY});

        // Update the last mouse position for the next drag event
        x = e.getSceneX();
        y = e.getSceneY();
    }

}