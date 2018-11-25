package sample;

import javafx.beans.property.SimpleStringProperty;

public class IntensityDrop {
    private final SimpleStringProperty date;
    private final SimpleStringProperty windowTitle;
    private final SimpleStringProperty intensityScore;

    IntensityDrop(String date, String windowTitle, String intensityScore) {
        this.date = new SimpleStringProperty(date);
        this.windowTitle = new SimpleStringProperty(windowTitle);
        this.intensityScore = new SimpleStringProperty(intensityScore);
    }

    @Override
    public String toString() {
        return "IntensityDrop{" +
                "date='" + date + '\'' +
                ", windowTitle='" + windowTitle + '\'' +
                ", intensityScore='" + intensityScore + '\'' +
                '}';
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getWindowTitle() {
        return windowTitle.get();
    }

    public SimpleStringProperty windowTitleProperty() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle.set(windowTitle);
    }

    public String getIntensityScore() {
        return intensityScore.get();
    }

    public SimpleStringProperty intensityScoreProperty() {
        return intensityScore;
    }

    public void setIntensityScore(String intensityScore) {
        this.intensityScore.set(intensityScore);
    }
}
