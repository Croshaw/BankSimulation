package me.vlad.banksimulation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import me.vlad.banksimulation.simulation.SimulationController;
import me.vlad.banksimulation.util.DurationUtils;
import me.vlad.banksimulation.util.FileHelper;
import me.vlad.banksimulation.util.ValueRange;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class MyController implements Initializable {
    @FXML
    private Canvas simulationCanvas;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button defSettingsButton;
    @FXML
    private Slider speedSlider;
    @FXML
    private Label speedSliderInfo;
    @FXML
    private TextField seedField;
    @FXML
    private TextField simulationDurationField;
    @FXML
    private TextField dailySalaryOfClerksField;
    @FXML
    private TextField requestDistributionFromField;
    @FXML
    private TextField requestDistributionToField;
    @FXML
    private TextField durationOfRequestServicingFromField;
    @FXML
    private TextField durationOfRequestServicingToField;
    @FXML
    private TextField profitOfRequestServicingFromField;
    @FXML
    private TextField profitOfRequestServicingToField;
    @FXML
    private Slider secondsStepSlider;
    @FXML
    private Label secondsStepSliderInfo;

    private boolean isPause;
    private SimulationController simulationController;
    private void makeTextFieldForOnlyNumber(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        speedSlider.valueProperty().addListener((observableValue, number, t1) -> {
            if(simulationController != null)
                simulationController.setSpeed(t1.doubleValue());
            speedSliderInfo.setText(t1.toString());
        });
        speedSliderInfo.setText(speedSlider.valueProperty().getValue().toString());

        secondsStepSlider.valueProperty().addListener((observableValue, number, t1) -> {
            secondsStepSliderInfo.setText(DurationUtils.toString(Duration.ofSeconds(t1.longValue())));
            if(simulationController != null) {
                simulationController.setSecondsStep(t1.longValue());
            }
        });
        secondsStepSliderInfo.setText(DurationUtils.toString(Duration.ofSeconds(secondsStepSlider.valueProperty().getValue().longValue())));

        makeTextFieldForOnlyNumber(seedField);
        makeTextFieldForOnlyNumber(simulationDurationField);
        makeTextFieldForOnlyNumber(dailySalaryOfClerksField);
        makeTextFieldForOnlyNumber(requestDistributionFromField);
        makeTextFieldForOnlyNumber(requestDistributionToField);
        makeTextFieldForOnlyNumber(durationOfRequestServicingFromField);
        makeTextFieldForOnlyNumber(durationOfRequestServicingToField);
        makeTextFieldForOnlyNumber(profitOfRequestServicingFromField);
        makeTextFieldForOnlyNumber(profitOfRequestServicingToField);
    }
    @FXML
    protected void onDefSettingsButtonClick() {
        seedField.setText("0");
        secondsStepSlider.setValue(600);
        simulationDurationField.setText("30");
        dailySalaryOfClerksField.setText("2000");
        requestDistributionFromField.setText("60");
        requestDistributionToField.setText("600");
        durationOfRequestServicingFromField.setText("120");
        durationOfRequestServicingToField.setText("1800");
        profitOfRequestServicingFromField.setText("3000");
        profitOfRequestServicingToField.setText("50000");
    }
    @FXML
    protected void onSaveButtonClick() {
        if(simulationController != null) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(simulationCanvas.getScene().getWindow());
            if(file != null)
                FileHelper.writeToFile(file.getPath(), SimulationController.serialize(simulationController));
        }
    }
    @FXML
    protected void onStartButtonClick() {
        if(seedField.getText().isEmpty() || simulationDurationField.getText().isEmpty() || dailySalaryOfClerksField.getText().isEmpty() || requestDistributionFromField.getText().isEmpty()
                || requestDistributionToField.getText().isEmpty() || durationOfRequestServicingFromField.getText().isEmpty() || durationOfRequestServicingToField.getText().isEmpty()
                || profitOfRequestServicingFromField.getText().isEmpty() || profitOfRequestServicingToField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            alert.setTitle("Внимание");
            alert.setHeaderText("Заполните поля!");
            alert.show();
            return;
        }
        int seed = Integer.parseInt(seedField.getText());
        Duration durSim = Duration.ofDays(Integer.parseInt(simulationDurationField.getText()));
        long secStep = secondsStepSlider.valueProperty().getValue().longValue();
        double dailyClerkSalary = Double.parseDouble(dailySalaryOfClerksField.getText());
        long requestDistributionFrom = Long.parseLong(requestDistributionFromField.getText());
        long requestDistributionTo = Long.parseLong(requestDistributionFromField.getText());

        long durationOfRequestServicingFrom = Long.parseLong(durationOfRequestServicingFromField.getText());
        long durationOfRequestServicingTo = Long.parseLong(durationOfRequestServicingToField.getText());

        double profitOfRequestServicingFrom = Double.parseDouble(profitOfRequestServicingFromField.getText());
        double profitOfRequestServicingTo = Double.parseDouble(profitOfRequestServicingToField.getText());
        simulationController = new SimulationController(seed, durSim, secStep, dailyClerkSalary, new ValueRange<>(Math.min(requestDistributionFrom, requestDistributionTo), Math.max(requestDistributionFrom, requestDistributionTo)),
                new ValueRange<>(Math.min(durationOfRequestServicingFrom, durationOfRequestServicingTo), Math.max(durationOfRequestServicingFrom, durationOfRequestServicingTo)),
                new ValueRange<>(Math.min(profitOfRequestServicingFrom, profitOfRequestServicingTo), Math.max(profitOfRequestServicingFrom, profitOfRequestServicingTo)), simulationCanvas);
        simulationController.setSpeed(speedSlider.valueProperty().getValue());
        simulationController.simulate();
        stopButton.disableProperty().setValue(false);
        pauseButton.disableProperty().setValue(false);
        saveButton.disableProperty().setValue(false);
        startButton.disableProperty().setValue(true);
        defSettingsButton.disableProperty().setValue(true);
        seedField.disableProperty().setValue(true);
        simulationDurationField.disableProperty().setValue(true);
        dailySalaryOfClerksField.disableProperty().setValue(true);
        requestDistributionFromField.disableProperty().setValue(true);
        requestDistributionToField.disableProperty().setValue(true);
        durationOfRequestServicingFromField.disableProperty().setValue(true);
        durationOfRequestServicingToField.disableProperty().setValue(true);
        profitOfRequestServicingFromField.disableProperty().setValue(true);
        profitOfRequestServicingToField.disableProperty().setValue(true);
    }
    @FXML
    protected void onStopButtonClick() {
        stopButton.disableProperty().setValue(true);
        pauseButton.disableProperty().setValue(true);
        saveButton.disableProperty().setValue(true);
        pauseButton.setText("Пауза");
        isPause = false;
        startButton.disableProperty().setValue(false);
        defSettingsButton.disableProperty().setValue(false);
        seedField.disableProperty().setValue(false);
        simulationDurationField.disableProperty().setValue(false);
        dailySalaryOfClerksField.disableProperty().setValue(false);
        requestDistributionFromField.disableProperty().setValue(false);
        requestDistributionToField.disableProperty().setValue(false);
        durationOfRequestServicingFromField.disableProperty().setValue(false);
        durationOfRequestServicingToField.disableProperty().setValue(false);
        profitOfRequestServicingFromField.disableProperty().setValue(false);
        profitOfRequestServicingToField.disableProperty().setValue(false);
        simulationController.stopSimulation();
    }

    @FXML
    protected void onPauseButtonClick() {
        isPause = !isPause;
        if(isPause) {
            pauseButton.setText("Возобновить");
            simulationController.pause();
        } else {
            pauseButton.setText("Пауза");
            simulationController.resume();
        }
    }
}