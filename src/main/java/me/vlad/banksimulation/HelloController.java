package me.vlad.banksimulation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import me.vlad.banksimulation.simulation.SimulationController;
import me.vlad.banksimulation.util.ValueRange;

import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        SimulationController simulationController = new SimulationController(123, Duration.ofDays(30), 3600, 2000, new ValueRange<>(0L, 600L), new ValueRange<>(120L, 1800L), new ValueRange<>(3000d, 50000d));
        simulationController.simulate();
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}