module me.vlad.banksimulation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;

    opens me.vlad.banksimulation to javafx.fxml;
    exports me.vlad.banksimulation;
    opens me.vlad.banksimulation.simulation to com.google.gson;
    opens me.vlad.banksimulation.util to com.google.gson;
    opens me.vlad.banksimulation.core to com.google.gson;
    opens me.vlad.banksimulation.core.human to com.google.gson;
}