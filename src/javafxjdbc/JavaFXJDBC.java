/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxjdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author sohof
 */
public class JavaFXJDBC extends Application {
    // DB connection variables
    static protected Connection con; // We will keep the connection through out the program instead of closing/reopenin..
    // DB access variables
    private static final String SQL_DATABASE_URL = "jdbc:mysql:///labb";  // labb is the name of a database at local mysql server 
    // "jdbc:mysql://hostname:port/databaseName", is how to to acces a remote db. hostnamecan be an ip/name (and maybe port nr)
    private static String ACCESS_DATABASE_URL = "jdbc:ucanaccess://";//
    // ACCESS_DATABASE_URL must be concataned with the filename of access file 

    private static final String SQL_DRIVER ="com.mysql.jdbc.Driver"; 
    // NO DRIVER FOR SQL OR ACCESS NEEDED SINCE AUTOMATIC DRIVER DISCOVERY SEEMED TO WORK
   
    private static String userID = "root";
    private static String passWord = "Mazdak123";
    
    private Stage primaryStage;
    private Group root;
    @Override
    public void start(Stage stage) { // state object is created for you by javaFX Runtime
        this.primaryStage = stage;
        primaryStage.setTitle("Java DB Connectivity with JavaFX GUI");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        
        Text sceneTitle = new Text("Click on the Database you which to use");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
        grid.add(sceneTitle, 0, 0, 3, 1);
          
        try( 
            FileInputStream input1 = new FileInputStream("mysql_logo.png");
            FileInputStream input2 = new FileInputStream("msaccess_logo.png");
            )
        {
   
            ImageView imageView1 = new ImageView(new Image(input1));
            ImageView imageView2 = new ImageView(new Image(input2));
            imageView1.setFitWidth(250); imageView1.setFitHeight(150); imageView1.setPreserveRatio(true);
            imageView2.setFitWidth(250); imageView2.setFitHeight(150); imageView2.setPreserveRatio(false);

            grid.add(imageView1, 0, 2);
            grid.add(imageView2, 2, 2);

            Scene scene = new Scene(grid,700,300);
            primaryStage.setScene(scene);

            imageView1.setOnMouseClicked((MouseEvent e) -> {
    
            loginSceneSQL("MySQL") ;
            });
               
            imageView2.setOnMouseClicked((MouseEvent e) -> {           
            loginSceneAccess("MS Access") ;
            });
             
        }
        catch(IOException e){
            e.printStackTrace();
        }
     primaryStage.show();
  
    }
    public void loginSceneAccess(String selected_DB){
        FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle("Open Resource File");
          fileChooser.getExtensionFilters().addAll(
         new ExtensionFilter("Access Files", "*.accdb"),
         new ExtensionFilter("All Files", "*.*"));
         File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    ACCESS_DATABASE_URL=ACCESS_DATABASE_URL + selectedFile.getAbsolutePath();
                   System.out.println(ACCESS_DATABASE_URL);                    
                    
                if(connectToDB(ACCESS_DATABASE_URL, "Sohof", "DUMMY STRING")){
                   // The user name and  pass word is not 
                  userID = "Sohof"; // login was a success so set global user id string
                  loginSuccessScene("MS Access", userID);
                 }
                    
            }
    }

 
    public void loginSceneSQL(String selectedDB) {
        
        primaryStage.setTitle(selectedDB + " Labb Database");     
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        
        Scene scene = new Scene(grid,700,300, Color.GRAY);
        primaryStage.setScene(scene);
        
        Text scenetitle = new Text("Welcome to the " + selectedDB +" Labb Database");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        
        grid.add(new Label("User Name:"), 0, 1);
        grid.add(new Label("Password:"), 0, 2);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
                
        PasswordField userPWField = new PasswordField();
        grid.add(userPWField, 1, 2);
             
        Button btn = new Button("Login");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        
        primaryStage.show();

       btn.setOnAction(action -> {      
       
            if(connectToDB(SQL_DATABASE_URL, userTextField.getText(),userPWField.getText())){
                userID = userTextField.getText(); // login was a success so set global user id string
                loginSuccessScene(selectedDB, userID);
            }
            else{
                loginFailedSceneSQL(selectedDB);
            }
        }); 
       
       scene.setOnKeyPressed(e -> {      
       
           if (e.getCode()== KeyCode.ENTER){
               
               if(connectToDB(SQL_DATABASE_URL, userTextField.getText(),userPWField.getText())){
                userID = userTextField.getText();
                loginSuccessScene(selectedDB, userID);
                }
                else{
                loginFailedSceneSQL(selectedDB);
                }  
           }
        });
    }// End loginSceneSQL
  
    
    public void loginSuccessScene(String selectedDB, String userID){
       
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10,10,10,10));
        Text sceneTitle = new Text("Logged into " + selectedDB + " as user " + userID);
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        border.setTop(sceneTitle);
        BorderPane.setAlignment(sceneTitle, Pos.TOP_CENTER);

        Button btnShowCarBrands = new Button("Show Car Brands");
        Button btnShowCarBrandByCity = new Button("Show Cars In City");
        Button btnChangeCarColor = new Button("Change Car Color");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(btnShowCarBrands, btnShowCarBrandByCity,btnChangeCarColor);
        border.setLeft(vbox);
        BorderPane.setAlignment(vbox, Pos.TOP_CENTER);

        Scene scene = new Scene(border,700,300, Color.GRAY);
        primaryStage.setScene(scene);
        primaryStage.show(); 
        
        btnShowCarBrands.setOnAction(action -> {      
       
            simpleSelect(scene);
        }); 
        
        btnShowCarBrandByCity.setOnAction(action -> {      
       
            parameterizedSelectView(scene,false);
        });  
        
        btnChangeCarColor.setOnAction(action -> {      
       
            parameterizedUpdateView(scene,false);
        });  
        
        
    }
    
    public void loginFailedSceneSQL(String selectedDB){
        
        Text sceneTitle = new Text("Login Failed Please Try again or Quit");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        
        Button btnTryAgain = new Button("Login");
        Button btnQuit = new Button("Quit");

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.getChildren().addAll(btnTryAgain,btnQuit);
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(sceneTitle,hbox);
        
        Scene scene = new Scene(vbox,700,300, Color.GRAY);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        btnTryAgain.setOnAction(action -> {      
            loginSceneSQL(selectedDB);
        }); 
        btnQuit.setOnAction(action -> {  
            System.exit(1);
        });
    
       
    }
    
    public void parameterizedUpdateView(Scene scene, Boolean warning){
    
      Label noSuchPlate = new Label("Sorry no cars with given plate nr. Please try again.");
      noSuchPlate.setTextFill(Color.RED);
      noSuchPlate.setVisible(warning);

      Label label1 = new Label(" Enter Plate ");
      Label label2 = new Label(" Enter Color ");

      TextField textfield1 = new TextField();
      TextField textfield2 = new TextField();
      Button btn = new Button("Update");
      HBox hbox = new HBox();
      HBox hbox2 = new HBox();

      hbox.setSpacing(5); hbox2.setSpacing(5);
      hbox.setPadding(new Insets(5,5,5,5)); hbox2.setPadding(new Insets(5,5,5,5));
      hbox.getChildren().addAll(label1,textfield1);
      hbox2.getChildren().addAll(label2,textfield2,btn);

      VBox vbox = new VBox();
      vbox.setSpacing(5);
      vbox.setPadding(new Insets(10,10,5,5));
      vbox.getChildren().addAll(noSuchPlate,hbox,hbox2);      
      vbox.setAlignment(Pos.TOP_LEFT);
      ((BorderPane)scene.getRoot()).setCenter(vbox);
        
      btn.setOnAction(e -> {
      String plateNr = textfield1.getText();
        String color = textfield2.getText();
        textfield1.clear();  
        textfield2.clear();
        if(plateNr != null && color != null )
            parameterizedUpdate(scene, plateNr, color);      
      });
      
      scene.setOnKeyPressed(e -> {      
          String plateNr = textfield1.getText();
          String color = textfield2.getText();
          if (e.getCode()== KeyCode.ENTER && plateNr != null && color != null) {      
                textfield1.clear();  
                textfield2.clear();
                parameterizedUpdate(scene, plateNr, color); 
          }
      });
      
      
    }
        
    public void parameterizedUpdate(Scene scene, String plateNr, String color){
    
      String query = "UPDATE bil set farg=? where regnr = ?";
      ResultSet rs;  
      int antal =0;
      try(PreparedStatement stmt = con.prepareStatement(query))
        {
            stmt.setString(1 , color);
            stmt.setString(2 , plateNr);

            antal = stmt.executeUpdate();

            if(antal == 0) {   // no such plate nr found.. try again
                parameterizedUpdateView(scene,true);
            }
          con.commit();
        } // AutoCloseable objects' close methods are called now        
        catch(SQLException e){
            e.printStackTrace();
        }  
    }
      
      
 


    public void parameterizedSelectView(Scene scene, Boolean warning){
    
      Label noSuchCityLabel = new Label("Sorry no cars in that city or city not valid. Please try again.");
      noSuchCityLabel.setTextFill(Color.RED);
      noSuchCityLabel.setVisible(warning);

      Label whichCityLabel = new Label(" Enter City name ");
      TextField cityTextField = new TextField();
      Button btn = new Button("Check");
      HBox hbox = new HBox();
      hbox.setSpacing(5);
      hbox.setPadding(new Insets(5,5,5,5));
      hbox.getChildren().addAll(cityTextField,btn);
      
      VBox vbox = new VBox();
      vbox.setSpacing(5);
      vbox.setPadding(new Insets(10,10,5,5));
      vbox.getChildren().addAll(noSuchCityLabel,whichCityLabel,hbox);      
      vbox.setAlignment(Pos.TOP_LEFT);
      ((BorderPane)scene.getRoot()).setCenter(vbox);

     btn.setOnAction(e -> {
        String city = cityTextField.getText();
        if(city != null)
            parameterizedSelect(scene, city);
     });
     scene.setOnKeyPressed(e -> {      
          String city = cityTextField.getText();
         
          if (e.getCode()== KeyCode.ENTER && city != null) {      
                cityTextField.clear();  
                parameterizedSelect(scene, city);
          }
      });
     
               
    }
    
    public void parameterizedSelect(Scene scene, String city) {
    
        TableView table = new TableView();
        Label resultLabel = new Label("Results ");
        resultLabel.setFont(new Font("Arial", 20));
        
        TableColumn brandCol = new TableColumn("Brand");
        brandCol.setMinWidth(30);
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        
        TableColumn ownerCol = new TableColumn("Owner");
        ownerCol.setMinWidth(30);
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        
        TableColumn plateNrCol = new TableColumn("Plate Nr");
        plateNrCol.setMinWidth(30);
        plateNrCol.setCellValueFactory(new PropertyValueFactory<>("plateNr"));
        
        TableColumn colorCol = new TableColumn("Color");
        colorCol.setMinWidth(30);
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        
        
      ObservableList<Car> data = FXCollections.observableArrayList();
      
      String query = "SELECT regnr, marke, farg FROM bil WHERE agare IN (SELECT id FROM person WHERE stad = ?)";
      ResultSet rs;  
     
      try(PreparedStatement stmt = con.prepareStatement(query))
        {
            stmt.setString(1 , city);
            rs = stmt.executeQuery();

            if(rs.next()) {  
               do
                {                
                data.add(new Car(rs.getString("marke"),"", rs.getString("regnr"), rs.getString("farg") ));

                }while (rs.next());  
               
                table.setItems(data);
                VBox vbox = new VBox();
                vbox.setSpacing(5);
                vbox.setPadding(new Insets(10,25,25,25));
                vbox.getChildren().addAll(resultLabel,table);
                ((BorderPane)scene.getRoot()).setCenter(vbox);
                table.getColumns().addAll(brandCol,plateNrCol,colorCol);
            }
            else { // no such city found or no cars.. try again
                System.out.println("No such city");
                parameterizedSelectView(scene,true);
            }          

        } // AutoCloseable objects' close methods are called now        
        catch(SQLException e){
            e.printStackTrace();
        }  
    }
    
    public void simpleSelect(Scene scene) {
        
        // Local variables
        String query;
        // Set the SQL statement into the query variable
        query = "SELECT DISTINCT marke FROM bil";
 
        TableView table = new TableView();
        Label resultLabel = new Label("Results ");
        resultLabel.setFont(new Font("Arial", 20));
        
        TableColumn brandCol = new TableColumn("Brand");
        brandCol.setMinWidth(30);
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        
        TableColumn ownerCol = new TableColumn("Owner");
        ownerCol.setMinWidth(30);
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        
        TableColumn plateNrCol = new TableColumn("Plate Nr");
        plateNrCol.setMinWidth(30);
        plateNrCol.setCellValueFactory(new PropertyValueFactory<>("plateNr"));
        
        TableColumn colorCol = new TableColumn("Color");
        colorCol.setMinWidth(30);
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10,25,25,25));
        vbox.getChildren().addAll(resultLabel,table);
        ((BorderPane)scene.getRoot()).setCenter(vbox);
        
        ObservableList<Car> data = FXCollections.observableArrayList();

        // use try-with-resources to connect to and query the database
        // res. created shoul be separated with ";" except the last one. 
        try(Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query))
        {
            while (rs.next())
            {                
                data.add(new Car(rs.getString("marke"),"","",""));

            }
           
        table.setItems(data);
        table.getColumns().add(brandCol); // we add and show only one of the columns!
       // table.getColumns().addAll(brandCol,ownerCol,plateNrCol,colorCol);

        } // AutoCloseable objects' close methods are called now        
        catch(SQLException e){
            e.printStackTrace();
        }
               
    }
    public boolean connectToDB(String DATABASE_URL, String userID, String passWord) {
        // If connecting to access then no user ID or pass is required. Dummy values are sent here.
        boolean result = false;
        try      
        {     
            con = DriverManager.getConnection(DATABASE_URL,userID,passWord);
            con.setAutoCommit(false);   
            System.out.println("Connected to " + DATABASE_URL);
            
            if (con != null) {result = true;}
        }
        catch(SQLException e){ 
            //netbeans gives tip because printint the trace is not really handling
            //the exception. In industry this probably should be logged to some file
            // java does have a looging framework to use. 
            e.printStackTrace();
        }
        return result;
    }
    
    public static class Car {
    
    private final SimpleStringProperty owner;
    private final SimpleStringProperty brand;
    private final SimpleStringProperty plateNr;
    private final SimpleStringProperty color;

    public Car(String brand,String owner,String plateNr, String color){
    
        this.brand = new SimpleStringProperty(brand);
        this.color = new SimpleStringProperty(color);
        this.plateNr = new SimpleStringProperty(plateNr);
        this.owner = new SimpleStringProperty(owner);
    }

    public String getOwner(){
        return owner.get();
    }
    public void setOwner(String s){
        owner.set(s);
    }
    public String getBrand(){
        return brand.get();
    }
    public void setBrand(String s){
        brand.set(s);
    }
    
    public String getPlateNr(){
        return plateNr.get();
    }
    public void setPlateNr(String s){
        plateNr.set(s);
    }
  
    public String getColor(){
        return color.get();
    }
    public void setColor(String s){
        color.set(s);
    }
      
       
}
   
    /**
     * @param args the command line arguments
     * There is no need for main in java FX, but useful if you want to pass
     * command line parameters. Main calls static launch() method which launches
     * javaFX runtime and the javaFX application. 
     */
    public static void main(String[] args) {

      Application.launch(args);
    }
    
}
