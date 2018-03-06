package dad.correo.view;

import dad.correo.controller.CorreoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CorreoApp extends Application {
	CorreoController control = new CorreoController();
	static Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		CorreoApp.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("CorreoView.fxml"));
		loader.load();
		BorderPane root = loader.getRoot();
		control = loader.getController();
		primaryStage.setTitle("Enviar email");
		primaryStage.getIcons().add(new Image("file:email-send-icon-32x32.png"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}

}
