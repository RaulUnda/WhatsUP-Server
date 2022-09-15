module com.example.whatsupserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.whatsupserver to javafx.fxml;
    exports com.example.whatsupserver;
}