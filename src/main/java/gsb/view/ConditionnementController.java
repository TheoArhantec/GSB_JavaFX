package gsb.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import gsb.MainApp;
import gsb.model.Conditionnement;
import gsb.model.Praticien;
import gsb.model.PraticienWithVisiteur;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class ConditionnementController implements Controller {
	
	MainApp mainApp;
	@FXML
	private TextField nameVisitor;
	@FXML
	private TableView<Conditionnement> tableView;
	@FXML
	private TableColumn<Conditionnement, String> columMed_depotlegale;
	@FXML
	private TableColumn<Conditionnement, String> columMed_nomcommerciale;
	@FXML
	private TableColumn<Conditionnement, String> columBoite_qte;
	@FXML
	private TableColumn<Conditionnement, String> columNb_boite_a_commander;
	@FXML
	private TableColumn<Conditionnement, String> columMedicament_qte;	
	@FXML
	private Button buttonConfirm; 
	@FXML
	private ProgressBar loader;
	
	private ObservableList<Conditionnement> conditionnementData = FXCollections.observableArrayList();

	@Override
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
		this.buttonConfirm.setOnAction((ActionEvent e) -> {
			this.loader.setVisible(true);
			Timeline task = new Timeline(
				        new KeyFrame(
				                Duration.ZERO,       
				                new KeyValue(this.loader.progressProperty(), 0)
				        ),
				        new KeyFrame(
				                Duration.seconds(2), 
				                new KeyValue(this.loader.progressProperty(), 1)
				        )
					  );  
			  task.setOnFinished(event -> {
				  this.loader.setVisible(false);
				  initData();
			  });
			  
			  task.playFromStart();
			  
			  
			 
		});
	}
	
	@FXML
	public void initData() {
		this.tableView.getItems().clear();
		List<Conditionnement> reqHttp = this.mainApp.getLesConditionnements().getListeConditionnementWithVisiteurName(this.nameVisitor.getText());
		
		if (!(reqHttp == null) ){
			for(Conditionnement unPra: reqHttp) {
	
				this.conditionnementData.add(unPra);
			}
		} 
			
			this.tableView.setItems(this.conditionnementData);
			
			this.columMed_depotlegale.setCellValueFactory(cellData -> cellData.getValue().getMed_depotlegaleProperty());
			this.columMed_nomcommerciale.setCellValueFactory(cellData -> cellData.getValue().getMed_nomcommercialeProperty());
			this.columBoite_qte.setCellValueFactory(cellData -> cellData.getValue().getBoite_qteProperty());
			this.columNb_boite_a_commander.setCellValueFactory(cellData -> cellData.getValue().getNb_boite_a_commanderProperty());
			this.columMedicament_qte.setCellValueFactory(cellData -> cellData.getValue().getMedicament_qteProperty());
			
	}
		
	@FXML
	public void bonDeCommande() throws IOException, DocumentException {
		FileChooser fileChooser = new FileChooser();
		
		  // Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
		          "Pdf files (*.pdf)", "*.pdf");
		  
		fileChooser.getExtensionFilters().add(extFilter);
		 
		fileChooser.getExtensionFilters().add(extFilter);
		  // Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
	
		// Get a Calendar and set it to the current time.
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(Date.from(Instant.now()));
			
		String dateNameFile = String.format("file-%1$tY-%1$tm-%1$td-%1$tk-%1$tS-%1$tp", cal);
		
		String absolutePath = file.getAbsolutePath();
		String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator))+ "\\" + dateNameFile + file.getName();

		
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(filePath));
		 
		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
		Font fontContent = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);

		document.addTitle("Bon de commande");
		document.add(new Paragraph("Bon de commande", font));
		document.add(new Paragraph(" "));

		document.add(new Paragraph("Nom: " +  this.mainApp.getLesPraticien().getListeConditionnementWithVisiteurName(this.nameVisitor.getText()).get(0).getNom(), fontContent));
		document.add(new Paragraph("Adresse: " + " " + this.mainApp.getLesPraticien().getListeConditionnementWithVisiteurName(this.nameVisitor.getText()).get(0).getAdresse()+ ", " 
									+ this.mainApp.getLesPraticien().getListeConditionnementWithVisiteurName(this.nameVisitor.getText()).get(0).getVille()  + " " 
									+ this.mainApp.getLesPraticien().getListeConditionnementWithVisiteurName(this.nameVisitor.getText()).get(0).getCp(), fontContent));
		document.add(new Paragraph(" "));
		
		for (Conditionnement conditionnement : this.tableView.getItems())  {
		
			document.add(new Paragraph("Nom commerciale: " +  conditionnement.getMed_depotlegale(), fontContent));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Nom commerciale: " +  conditionnement.getMed_depotlegale(), fontContent));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Quantité boite: "+ conditionnement.getBoite_qte(), fontContent));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Quantité de médicament: " + conditionnement.getMedicament_qte(), fontContent));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Nombre de boite à commander: " + conditionnement.getNb_boite_a_commander(), fontContent));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));

					
		}
		
		document.close();
		
		Alert alert = new Alert(AlertType.INFORMATION);
      	alert.setTitle("Bon de commande sauvegardé");
      	alert.setHeaderText("Bon de commande sauvegardé");
      	alert.setContentText("Chemin du fichier: "+ filePath);
      	alert.showAndWait();
		
	}
	
	@Override
	public String getNOMMETHODE() {
		// TODO Auto-generated method stub
		return " conditionnement";
	}
	
}
