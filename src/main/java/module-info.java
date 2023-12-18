module me.vlad.banksimulation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    opens me.vlad.banksimulation to javafx.fxml;
    exports me.vlad.banksimulation;
}