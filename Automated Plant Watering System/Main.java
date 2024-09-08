package sample;
import com.eecs1021.DataController;
import com.eecs1021.SerialPortService;
import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    private final static int maxPotentiometerValue = 1 << 10;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var sp = SerialPortService.getSerialPort("COM3");
        var outputStream = sp.getOutputStream();

        var panel = new BorderPane();
        panel.setStyle("-fx-base: orange");

        var slider = new Slider();
        slider.setMin(0.0);
        slider.setMax(100.0);

        slider.valueProperty().addListener((observableValue, oldValue, newValue) ->{

            try
            {
                outputStream.write(newValue.byteValue());
            }
            catch (IOException e)
            {
                System.out.println("There is a problem within the countdown handler class");
                e.printStackTrace();
            }

        });

        var label = new Label();
        label.setText("Value: 0");
        var button = new Button("Turn Pump On");
        button.setStyle("-fx-base: darkorange");

        button.setOnMouseReleased(value -> {
            try {
                outputStream.write(0);
                button.setText("Turn Pump On");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        button.setOnMousePressed(value -> {
            try {
                outputStream.write(255);
                button.setText("Turn Pump Off");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        slider.valueProperty().addListener((observableValue, oldValue, newValue) ->{

            label.setText("Value: " + String.valueOf(newValue.intValue()));
        });
        var data = new DataController();
        sp.addDataListener(data);
        var now = System.currentTimeMillis();
        var xAxis = new NumberAxis("Time", now, now + 50000, 1500); // creates the x-axis (which automatically updates)
        var yAxis = new NumberAxis("Moisture Value", 0, maxPotentiometerValue, 10); // creates the y-axis

        var dataSeries = new XYChart.Series<>(data.getDataPoints()); // creates the series (all the data)
        var graph = new LineChart<>(xAxis, yAxis, FXCollections.singletonObservableList(dataSeries)); // creates the chart
        graph.setTitle("Moisture Value vs Time");

        panel.setCenter(slider);
        panel.setBottom(graph);
        panel.setLeft(button);
        panel.setPadding(new Insets(0, 20, 0, 20));

        var scene = new Scene(panel, 1000, 395);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}