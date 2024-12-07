package com.kadenfrisk.autoscroll.util;

import com.kadenfrisk.autoscroll.custom.BetterPoint2D;
import com.kadenfrisk.autoscroll.custom.BetterScrollPane;
import javafx.geometry.Rectangle2D;

import java.util.Objects;

import static java.lang.Math.abs;

public class Geometry {

    private static double zoomFactor = 1.0;

    public static double getZoomFactor() {
        return zoomFactor;
    }

    public static void setZoomFactor(double zoomFactor) {
        Geometry.zoomFactor = zoomFactor;
    }

    public static Rectangle2D scaleWithZoom(Rectangle2D rect, double zoomFactor) {
        if (zoomFactor <= 0) {
            throw new IllegalArgumentException("Zoom factor must be positive");
        }
        double reciprocal = 1.0 / zoomFactor;
        return new Rectangle2D(rect.getMinX() * reciprocal, rect.getMinY() * reciprocal, rect.getWidth() * reciprocal, rect.getHeight() * reciprocal);
    }


    public static Rectangle2D getViewPortBounds(BetterScrollPane betterScrollPane) {
        Objects.requireNonNull(betterScrollPane);

        double viewportWidth = betterScrollPane.getViewportBounds().getWidth();
        double viewportHeight = betterScrollPane.getViewportBounds().getHeight();

        // Scale the content dimensions by the zoom factor
        double contentWidth = betterScrollPane.getContent().getBoundsInLocal().getWidth() * zoomFactor;
        double contentHeight = betterScrollPane.getContent().getBoundsInLocal().getHeight() * zoomFactor;

        double hValue = betterScrollPane.getHvalue();
        double vValue = betterScrollPane.getVvalue();

        double x = hValue * (contentWidth - viewportWidth) / zoomFactor;
        double y = vValue * (contentHeight - viewportHeight) / zoomFactor;

        if (zoomFactor != 1.0) {
            x = x / zoomFactor;
            y = y / zoomFactor;
        }

        return new Rectangle2D(x, y, viewportWidth, viewportHeight);
    }


    public static BetterPoint2D getTopLeft(BetterScrollPane BetterScrollPane) {
        Rectangle2D viewport = getViewPortBounds(BetterScrollPane);
        return new BetterPoint2D(viewport.getMinX(), viewport.getMinY());
    }

    public static BetterPoint2D getBottomRight(BetterScrollPane BetterScrollPane) {
        Rectangle2D viewport = getViewPortBounds(BetterScrollPane);
        return new BetterPoint2D(viewport.getMaxX(), viewport.getMaxY());
    }

    public static BetterPoint2D getZoomedTopLeft(BetterScrollPane betterScrollPane) {
        double height = betterScrollPane.getHeight();
        double width = betterScrollPane.getWidth();

        double heightTimesZoom = height * getZoomFactor();
        double widthTimesZoom = width * getZoomFactor();

        double widthDiff = widthTimesZoom - width;
        double heightDiff = heightTimesZoom - height;
        return new BetterPoint2D(abs(widthDiff), abs(heightDiff));
    }

//    public static Rectangle2D scaleRectangle(Rectangle2D original, double zoomFactor) {
//        // Extract the original rectangle's top-left corner and dimensions
//        double minX = original.getMinX();
//        double minY = original.getMinY();
//        double originalWidth = original.getWidth();
//        double originalHeight = original.getHeight();
//
//        // Calculate the scale factor for the viewport
//        double scaleFactor = (zoomFactor < 1) ? 1 / zoomFactor : zoomFactor;
//
//        // Apply the scale factor to the dimensions of the rectangle
//        double newWidth = originalWidth * scaleFactor;
//        double newHeight = originalHeight * scaleFactor;
//
//        // Return the new Rectangle2D with updated dimensions, keeping the top-left corner the same
//        return new Rectangle2D(minX, minY, newWidth, newHeight);
//    }

    public static Rectangle2D scaleRectangle(Rectangle2D original, double zoomFactor) {
        // Extract the original rectangle's top-left corner and dimensions
        double minX = original.getMinX();
        double minY = original.getMinY();
        double originalWidth = original.getWidth();
        double originalHeight = original.getHeight();

        // Apply the inverse scale factor for zooming in (zoomFactor > 1)
        // and direct scale factor for zooming out (zoomFactor < 1)
//        double scaleFactor = (zoomFactor < 1) ? 1 / zoomFactor : zoomFactor;
        double scaleFactor = 1.0 / zoomFactor;

        // Apply the scale factor to the dimensions of the rectangle
        double newWidth = originalWidth * scaleFactor;
        double newHeight = originalHeight * scaleFactor;

        // Return the new Rectangle2D with updated dimensions, keeping the top-left corner the same
        return new Rectangle2D(minX, minY, newWidth, newHeight);
    }


}
