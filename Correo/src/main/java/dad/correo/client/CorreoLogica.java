package dad.correo.client;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import dad.correo.controller.CorreoController;
import dad.correo.view.CorreoApp;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class CorreoLogica {

	Task<Void> tarea;
	MultiPartEmail email;

	public CorreoLogica() {
		email = new MultiPartEmail();
	}

	public void enviar(String host, int port, boolean ssl, String from, String to, String msg, String asunto,
			String user, String password) throws EmailException {

		tarea = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				int MAX = 1000;
				email.setHostName(host);
				email.setSmtpPort(port);
				email.setAuthenticator(new DefaultAuthenticator(user, password));
				email.setSSLOnConnect(ssl);
				email.setFrom(from);
				email.setSubject(asunto);
				email.setMsg(msg);
				email.addTo(to);
				for (int i = 0; i < MAX; i++) {
					updateProgress(i, MAX);
					Thread.sleep(3L);
				}
				updateProgress(MAX, MAX);

				email.send();
				return null;
			}

		};
		
		tarea.setOnSucceeded(e -> {
			Alert correcto = new Alert(AlertType.INFORMATION);
			correcto.setTitle("Mensaje Enviado");
			correcto.setHeaderText("Mensaje enviado con éxito a \'" + to.toString() + "\'");
			correcto.setContentText("");
			Stage stage = (Stage) correcto.getDialogPane().getScene().getWindow();
			stage.initOwner(CorreoApp.getPrimaryStage());
			stage.getIcons().setAll(CorreoApp.getPrimaryStage().getIcons());
			correcto.showAndWait();
			CorreoController.progresoProp.unbind();
			CorreoController.progresoProp.set(0);
		});
		CorreoController.progresoProp.bind(tarea.progressProperty().multiply(100));
		new Thread(tarea).start();
	}

	public void enviarArchivo(String path, String description, String name) throws EmailException {
		EmailAttachment attachment = new EmailAttachment();
		attachment.setPath(path);
		attachment.setDisposition(EmailAttachment.ATTACHMENT);
		attachment.setDescription(description);
		attachment.setName(name);
		email.attach(attachment);
	}
}
