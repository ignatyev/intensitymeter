package sample;

import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class IntensityReportView {
    public static void show() {
        Stage stage = new Stage();
        stage.setTitle("Intensity Drop Report");
        GridPane root = new GridPane();
        TableView<IntensityDrop> tableView = new TableView<>();
        tableView.getColumns().addAll(
                createColumn("time", "date", 220),
                createColumn("intensity", "intensityScore", 75),
                createColumn("window", "windowTitle", 1300)
        );
        tableView.getItems().setAll(Stats.get());
        root.addColumn(0,
                new Text("Today you had intensity drops at:"),
                tableView,
                new PieChart()
        );
        tableView.setPrefWidth(1450);
        tableView.setPrefHeight(1000);

        stage.setScene(new Scene(root, 1450, 450));
        stage.show();
    }

    private static TableColumn<IntensityDrop, String> createColumn(String name, String fieldName, int prefWidth) {
        TableColumn<IntensityDrop, String> column = new TableColumn<>(name);
        column.setPrefWidth(prefWidth);
        column.setCellValueFactory(
                new PropertyValueFactory<>(fieldName));
        return column;
    }
}
