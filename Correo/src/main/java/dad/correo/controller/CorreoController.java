package dad.correo.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.mail.EmailException;

import dad.correo.client.CorreoLogica;
import dad.correo.view.CorreoApp;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CorreoController implements Initializable {

	private CorreoLogica logica;
	private StringProperty hostFieldProp, passwordFieldProp, fromFieldProp, toFieldProp, subjectFieldProp,
			mssgFieldProp, portFieldProp;
	private BooleanProperty sslCheck;
	private final FileChooser fileChooser;
	public static IntegerProperty progresoProp;

	@FXML
	Button sendButton, clearButton, closeButton, imgButton;
	@FXML
	TextField hostField, portField, fromField, toField, subjectField;
	@FXML
	CheckBox SSLCheck;
	@FXML
	PasswordField passwordField;
	@FXML
	TextArea mssgField;
	@FXML
	HBox archivosTextFlow;
    @FXML
    ProgressIndicator progress;

	public CorreoController() {
		progresoProp = new SimpleIntegerProperty(this, "progresoProp");
		archivosTextFlow = new HBox();
		fileChooser = new FileChooser();
		logica = new CorreoLogica();
		hostFieldProp = new SimpleStringProperty(this, "hostFieldProp");
		passwordFieldProp = new SimpleStringProperty(this, "passwordFieldProp");
		fromFieldProp = new SimpleStringProperty(this, "fromFieldProp");
		toFieldProp = new SimpleStringProperty(this, "toFieldProp");
		subjectFieldProp = new SimpleStringProperty(this, "subjectFieldProp");
		mssgFieldProp = new SimpleStringProperty(this, "mssgFieldProp");
		portFieldProp = new SimpleStringProperty(this, "portFieldProp");
		sendButton = new Button();
		imgButton = new Button();
		sslCheck = new SimpleBooleanProperty();
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
		// BINDS
		progress.progressProperty().bind(progresoProp.divide(100.0));
		Bindings.bindBidirectional(sslCheck, SSLCheck.selectedProperty());
		Bindings.bindBidirectional(portField.textProperty(), portFieldProp);
		Bindings.bindBidirectional(hostField.textProperty(), hostFieldProp);
		Bindings.bindBidirectional(fromField.textProperty(), fromFieldProp);
		Bindings.bindBidirectional(toField.textProperty(), toFieldProp);
		Bindings.bindBidirectional(subjectField.textProperty(), subjectFieldProp);
		Bindings.bindBidirectional(passwordField.textProperty(), passwordFieldProp);
		Bindings.bindBidirectional(mssgField.textProperty(), mssgFieldProp);

		// INICIALIZO LOS PROP CON LOS SIGUIENTES CAMPOS PORQUE ME ABURRÍ DE PONERLOS
		// TOL RATO PA TESTEAR =(
		hostFieldProp.set("smtp.gmail.com");
		fromFieldProp.set("dad.iesdpm@gmail.com");

		sendButton.setOnAction(e -> onSend(e));
		clearButton.setOnAction(e -> onClear(e));
		closeButton.setOnAction(e -> onClose(e));
		imgButton.setOnAction(e -> {
			try {
				onImg(e);
			} catch (EmailException e1) {
				e1.printStackTrace();
			}
		});
		
		progresoProp.set(0);
	}

	private void onImg(ActionEvent e) throws EmailException {
		File file = fileChooser.showOpenDialog(CorreoApp.getPrimaryStage());
		if (file != null) {
			adjuntarArchivo(file);
		}
	}

	private void adjuntarArchivo(File file) throws EmailException {
		logica.enviarArchivo(file.getAbsolutePath(), "", file.getName());
		addImg(file);
	}

	private void addImg(File file) {
		String format;
		format = String.format("%.15s", file.getName());
		ImageView imageView = new ImageView(new Image("file:imagen.png"));
		imageView.setFitHeight(35);
		imageView.setFitWidth(30);
		Label imagenLabel = new Label();
		Label nombre = new Label(format);
		imagenLabel.setGraphic(imageView);
		VBox archivoBox = new VBox();
		archivoBox.getChildren().setAll(imagenLabel, nombre);
		archivoBox.setAlignment(Pos.CENTER);
		archivosTextFlow.getChildren().addAll(archivoBox);
	}

	private void onClose(ActionEvent e) {
		Stage stage = CorreoApp.getPrimaryStage();
		stage.close();
	}

	public void onClear(ActionEvent e) {
		portFieldProp.set("");
		hostFieldProp.set("");
		fromFieldProp.set("");
		toFieldProp.set("");
		subjectFieldProp.set("");
		passwordFieldProp.set("");
		mssgFieldProp.set("");
		sslCheck.set(false);
		archivosTextFlow.getChildren().setAll();
	}

	private void onSend(ActionEvent e) {
		try {
			logica.enviar(hostFieldProp.getValue(), Integer.parseInt(portFieldProp.getValue()), sslCheck.get(),
					fromFieldProp.getValue(), toFieldProp.getValue(), mssgFieldProp.getValue(),
					subjectFieldProp.getValue(), fromFieldProp.getValue(), passwordFieldProp.getValue());
		} catch (Exception e1) {
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Error");
			error.setHeaderText("No se pudo enviar el e-mail");
			error.setContentText("Invalid message supplied");
			Stage stage = (Stage) error.getDialogPane().getScene().getWindow();
			stage.initOwner(CorreoApp.getPrimaryStage());
			stage.getIcons().setAll(CorreoApp.getPrimaryStage().getIcons());
			error.showAndWait();
		}
	}
}
