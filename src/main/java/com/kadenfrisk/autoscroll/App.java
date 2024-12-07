package com.kadenfrisk.autoscroll;

import com.kadenfrisk.autoscroll.custom.BetterPoint2D;
import com.kadenfrisk.autoscroll.custom.BetterScrollPane;
import com.kadenfrisk.autoscroll.custom.DraggableLabel;
import com.kadenfrisk.autoscroll.custom.Grid;
import com.kadenfrisk.autoscroll.util.Geometry;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final double ZOOM_IN_BOUND = 2.0;
    private static final double ZOOM_OUT_BOUND = 0.5;
    private static Grid grid;
    private static BetterScrollPane scrollPane;

    public static void main(String[] args) {
        launch();
    }

    private static boolean withinZoomBounds(double newZoom) {
        return newZoom > ZOOM_IN_BOUND || newZoom < ZOOM_OUT_BOUND;
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();

        ToolBar toolBar = new ToolBar();
        Button zoomIn = new Button("Zoom In");
        zoomIn.setOnAction(this::handleZoomIn);
        Button zoomOut = new Button("Zoom Out");
        zoomOut.setOnAction(this::handleZoomOut);
        Button drawViewport = new Button("Draw Viewport");
        drawViewport.setOnAction(event -> grid.drawViewport(getViewportBounds()));
        toolBar.getItems().addAll(zoomIn, zoomOut, drawViewport);

        scrollPane = new BetterScrollPane();

        Group group = new Group();

        grid = new Grid(6400, 4800);
        group.getChildren().add(grid);

        DraggableLabel label = new DraggableLabel("Hello!", scrollPane, grid);
        label.setLayoutX((640 - label.prefWidth(-1)) / 2);
        label.setLayoutY((480 - label.prefHeight(-1)) / 2);
        grid.getChildren().add(label); // Add label to grid

        scrollPane.setContent(group);

        Scene scene = new Scene(root, 640, 480);

        scene.setOnMouseDragged(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            System.out.println("Dragging inside Scene at: " + mouseX + ", " + mouseY);
        });

        scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            System.out.println("Dragging inside ScrollPane at: " + mouseX + ", " + mouseY);
        });

        ChangeListener<Number> scrollListener = (observable, oldValue, newValue) -> {
            double hValue = scrollPane.getHvalue();
            double vValue = scrollPane.getVvalue();
            double hDistance = hValue * grid.getHeight();
            double vDistance = vValue * grid.getWidth();
            logger.log(Level.INFO, "Scrolled distance - Horizontal: {0}, Vertical: {1}", new Object[]{hDistance, vDistance});
        };

        scrollPane.hvalueProperty().addListener(scrollListener);
        scrollPane.vvalueProperty().addListener(scrollListener);

        root.getChildren().addAll(toolBar, scrollPane);

        stage.setTitle("Zoom-able Content");
        stage.setScene(scene);
        stage.show();
    }

    private void handleZoomOut(ActionEvent actionEvent) {
        zoomOut();
        postZoom();
        actionEvent.consume();
    }

    private void handleZoomIn(ActionEvent actionEvent) {
        zoomIn();
        postZoom();
        actionEvent.consume();
    }

    private void postZoom() {
        fixScrollPosition();
        grid.drawGrid();
    }

    private void fixScrollPosition() {
        BetterPoint2D topLeft = Geometry.getTopLeft(scrollPane);
        BetterPoint2D bottomRight = Geometry.getBottomRight(scrollPane);

        System.out.printf("topLeft: %s, bottomRight: %s\n", topLeft, bottomRight);

        if (topLeft.getX() < 0) {
            scrollPane.setHvalue(0);
        }
        if (topLeft.getY() < 0) {
            scrollPane.setVvalue(0);
        }
        if (bottomRight.getX() > grid.getWidth()) {
            scrollPane.setHvalue(1);
        }
        if (bottomRight.getY() > grid.getHeight()) {
            scrollPane.setVvalue(1);
        }
    }

    public static void zoomIn() {
        setZoom(Geometry.getZoomFactor() + 0.05);
        System.out.printf("Width * Zoom: %f\n", grid.getWidth());
        System.out.printf("Height * Zoom: %f\n", grid.getHeight());
    }

    public static void zoomOut() {
        setZoom(Geometry.getZoomFactor() - 0.05);
        System.out.printf("Width * Zoom: %f\n", grid.getWidth());
        System.out.printf("Height * Zoom: %f\n", grid.getHeight());
    }

    private static void setZoom(double newZoom) {
        if (!withinZoomBounds(newZoom)) {
            Geometry.setZoomFactor(newZoom);
            Scale scale = new Scale(newZoom, newZoom);  // Apply scaling to both axes
            grid.getTransforms().setAll(scale);  // Apply scale to the grid
            grid.drawGrid();
        }
    }

    public static Rectangle2D getViewportBounds() {
        return Geometry.scaleRectangle(Geometry.getViewPortBounds(scrollPane), Geometry.getZoomFactor());
    }
}
