package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {

    private static final int KEYS_NUM_THRESHOLD = 300;
    private static final int MOUSE_NUM_THRESHOLD = 50;
    private static final double OPACITY = .6;
    static final int INTENSITY_THRESHOLD = 75;
    private static final int MINUTE_STARTING_ALERT = 8;
    private AtomicInteger keyAndClickCounter = new AtomicInteger(0);
    private AtomicInteger scrollCounter = new AtomicInteger(0);
    private static GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
    private static GlobalMouseHook mouseHook = new GlobalMouseHook(false);
    private String MUSIC_FILE = "SynthChime1.mp3";

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        BarChart<String, Number> barChart = new BarChart<>(
                new CategoryAxis(),
                new NumberAxis(0, 100, 1));

        keyboardHook.addKeyListener(new MyGlobalKeyAdapter());
        mouseHook.addMouseListener(new MyGlobalMouseAdapter());

        Button dayBtn = new Button("Day");
        dayBtn.onMouseClickedProperty().setValue(event -> IntensityReportView.show());
        root.addColumn(0, barChart, dayBtn);
        GridPane.setMargin(dayBtn, new Insets(0, 0, 10, 25));

        barChart.setAnimated(true);
        barChart.setLegendVisible(false);
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data<>("", 0));
        barChart.getData().setAll(series1);
        playTimeLine(primaryStage, barChart);

        primaryStage.setTitle("Intensity Meter");
        primaryStage.setScene(new Scene(root, 100, 275));
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setOpacity(OPACITY);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setX(1833);
        primaryStage.setY(352);
        primaryStage.show();
    }

    private void playTimeLine(Stage stage, BarChart<String, Number> barChart) {
        Timeline tl = new Timeline();
        Media sound = new Media(new File(MUSIC_FILE).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        Duration duration = Duration.seconds(1);
        tl.getKeyFrames().add(
                new KeyFrame(duration,
                getActionEventHandler(stage, barChart, mediaPlayer))
        );
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }

    private EventHandler<ActionEvent> getActionEventHandler(Stage stage, BarChart<String, Number> barChart, MediaPlayer mediaPlayer) {
        return actionEvent -> {
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    LocalDateTime now = LocalDateTime.now();
                    int currentIntensity =
                            (keyAndClickCounter.get() * 100) / (KEYS_NUM_THRESHOLD + MOUSE_NUM_THRESHOLD);
                    if (now.getMinute() % 10 == 0 && now.getSecond() == 0) {
                        System.out.println(now.toString() + " " + currentIntensity);
                        keyAndClickCounter.set(0);
                        currentIntensity = 0;
                    }
                    if (currentIntensity < INTENSITY_THRESHOLD &&
                            now.getMinute() % 10 >= MINUTE_STARTING_ALERT && now.getSecond() == 0) {
                        blink(stage, mediaPlayer);
                    }
                    data.setYValue(currentIntensity);
                }
            }
        };
    }

    private void blink(Stage stage, MediaPlayer mediaPlayer) {
        mediaPlayer.play();
        for (int ignored : Arrays.asList(1, 2, 3)) {
            stage.setOpacity(1);
            try {
                Thread.sleep(800);
                stage.setOpacity(OPACITY);
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.stop();
    }


    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown Hook is running !");
            keyboardHook.shutdownHook();
            mouseHook.shutdownHook();
        }));

        launch(args);

    }

    private class MyGlobalKeyAdapter extends GlobalKeyAdapter {
        @Override
        public void keyReleased(GlobalKeyEvent event) {
            keyAndClickCounter.incrementAndGet();
           /* if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                keyboardHook.shutdownHook();
                mouseHook.shutdownHook();
            }*/
        }
    }

    private class MyGlobalMouseAdapter extends GlobalMouseAdapter {
        @Override
        public void mouseReleased(GlobalMouseEvent event) {
            keyAndClickCounter.incrementAndGet();
        }

        @Override
        public void mouseWheel(GlobalMouseEvent event) {
            scrollCounter.incrementAndGet();
            int wheelThreshold = 100;
            if (scrollCounter.get() > wheelThreshold) {
                keyAndClickCounter.incrementAndGet();
                scrollCounter.set(0);
            }
        }
    }
}
