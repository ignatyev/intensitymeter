package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
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

    private static final double OPACITY = .6;
    private AtomicInteger keyCounter = new AtomicInteger(0);
    private static GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
    private static GlobalMouseHook mouseHook = new GlobalMouseHook(false);
    private String MUSIC_FILE = "SynthChime1.mp3";

    @Override
    public void start(Stage primaryStage) {

        GridPane root = new GridPane();
        BarChart<String, Number> barChart = new BarChart<>(
                new CategoryAxis(),
                new NumberAxis(0, 100, 1));

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyReleased(GlobalKeyEvent event) {
                keyCounter.incrementAndGet();
               /* if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                    keyboardHook.shutdownHook();
                    mouseHook.shutdownHook();
                }*/
            }
        });
        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mouseReleased(GlobalMouseEvent event) {
                keyCounter.incrementAndGet();
            }

            @Override
            public void mouseWheel(GlobalMouseEvent event) {
                // TODO: 11/20/2018 support scrolls
//                System.out.println(event);
//                mouseCounter.incrementAndGet();
            }
        });
        root.add(barChart, 0, 0);

        barChart.setAnimated(true);
        barChart.setLegendVisible(false);
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data<>("", 0));
        barChart.getData().setAll(series1);
        Timeline tl = new Timeline();
        Media sound = new Media(new File(MUSIC_FILE).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                actionEvent -> {
                    for (XYChart.Series<String, Number> series : barChart.getData()) {
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            LocalDateTime now = LocalDateTime.now();
                            if (now.getMinute() % 10 == 0 && now.getSecond() == 0) {
                                keyCounter.set(0);
                            }
                            int currentIntensity = (keyCounter.get() * 100) / (300 + 50);
                            if (currentIntensity < 75 && now.getMinute() % 10 >= 8 && now.getSecond() == 0) {
                                blink(primaryStage, mediaPlayer);
                            }
                            data.setYValue(currentIntensity);
                        }
                    }
                }));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();

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
}