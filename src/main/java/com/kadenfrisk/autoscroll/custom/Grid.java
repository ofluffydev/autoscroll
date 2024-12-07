package com.kadenfrisk.autoscroll.custom;

import com.kadenfrisk.autoscroll.App;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Grid extends Pane {
    private final Canvas gridCanvas;

    public Grid(double gridWidth, double gridHeight) {
        super();

        gridCanvas = new Canvas(gridWidth, gridHeight);
        getChildren().add(gridCanvas);

        drawGrid();

        widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());

        setOnScroll(this::handleScroll);
    }

    private void handleScroll(ScrollEvent event) {
        if (event.isControlDown()) {
            // Use workspace zoom in / out depending on scroll direction
            if (event.getDeltaY() > 0) {
                App.zoomIn();
            } else {
                App.zoomOut();
            }

            event.consume();
        }
    }

    public void drawGrid() {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        gc.setStroke(Color.rgb(100, 100, 100, 1.0));
        gc.setLineWidth(0.5);

        double gridSize = 20;
        for (double x = 0; x < gridCanvas.getWidth(); x += gridSize) {
            gc.strokeLine(x, 0, x, gridCanvas.getHeight());
        }

        for (double y = 0; y < gridCanvas.getHeight(); y += gridSize) {
            gc.strokeLine(0, y, gridCanvas.getWidth(), y);
        }
    }

    public void drawViewport(Rectangle2D viewport) {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();

        gc.setStroke(Color.RED);
        gc.setLineWidth(1.0);

        gc.strokeRect(viewport.getMinX(), viewport.getMinY(), viewport.getWidth(), viewport.getHeight());
    }

    private void resizeCanvas() {
        gridCanvas.setWidth(getWidth());
        gridCanvas.setHeight(getHeight());

        drawGrid();
    }
}
