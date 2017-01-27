package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;

public class Controller {

    public CheckBox byDay;
    public CheckBox byMonths;
    public CheckBox brief;
    public CheckBox byMarket;
    public CheckBox bySection;
    public CheckBox byProduct;
    public TextArea screen;
    @FXML
    private TextField market;
    @FXML
    private TextField section;
    @FXML
    private TextField product;
    @FXML
    private TextField price;
    @FXML
    private TextField number;
    @FXML
    private TextField sum;
    @FXML
    private TextField transaction;
    @FXML
    private DatePicker purchaseDate;
    @FXML
    private DatePicker intervalStart;
    @FXML
    private DatePicker intervalEnd;

    private HashSet<String> marketSet;
    private HashSet<String> sectionSet;
    private HashSet<String> productSet;

    @FXML
    public void initialize(){

        marketSet = setName("market");

        sectionSet = setName("section");

        productSet = setName("product");

        purchaseDate.setValue(LocalDate.now());

        clearPosition();

        sum.setEditable(false);

        market.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) { handlerProduct(event); }
        });


        section.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) { handlerProduct(event); }
        });

        product.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                handlerProduct(event);
            }
        });

        price.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                handlerField(event);
            }
        });

        number.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                handlerField(event);
            }
        });


    }


    private void handlerProduct(KeyEvent event){
        System.out.println(event.getEventType());
        System.out.println(event.getCode().getName());
        System.out.println(event.getCharacter());

        System.out.println("==================================================");



        /*Field[] fields = event.getClass().getDeclaredFields();

        for(int i = 0; i < fields.length; i++){
            System.out.println(fields[i]);
        }*/

        TextField field = (TextField) event.getSource();

        ContextMenu menu = field.getContextMenu();

        //System.out.println(field.getParent());

        MenuItem item = menu.getItems().get(0);

        if(event.getCode().getName().equals("Enter")|event.getCode().getName().equals("Tab")){
            if(!item.getText().equals("")){
                field.setText(item.getText());
            }

            return;
        }

        HashSet<String> hashSet = getHashSet(field);

        //System.out.println(item.getText());//Отладка

        if(event.getEventType().getName().equals("KEY_RELEASED")) {

            for (String s : hashSet) {

                if ((s.indexOf(field.getText()) == 0) && (field.getText().length() > 1)) {

                    item.setText(s);

                    item.setOnAction(new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent e) {
                            field.setText(s);
                        }
                    });
                    menu.show(field, Side.BOTTOM, 0, 0);
                }
            }
        }/*else if(event.getEventType().equals("ACTION")|event.getEventType().equals("TAB")){
            field.setText(item.getText());
            field.getParent().getStylesheets();

        }*/
    }

    private HashSet<String> getHashSet(TextField field) {

        HashSet<String> hashSet = null;

        if(field.getId().equals("market")){
            hashSet = marketSet;
        }
        else if(field.getId().equals("section")){
            hashSet = sectionSet;
        }
        else if(field.getId().equals("product")){
            hashSet = productSet;
        }

        return hashSet;
    }


    public void handlerField(KeyEvent event){

        /*TextField field = (TextField) event.getSource();

        System.out.println(event.getCode());*/

        calcSum();

    }

    @FXML
    private void calcSum() {
        int numb = Integer.parseInt(number.getText());
        double price = Double.parseDouble(this.price.getText());
        sum.setText(String.valueOf(numb * price));
        //System.out.println("Работает");
    }

    @FXML
    public void onClickMarket(){
        //System.out.println(market.getOnMouseClicked());
        clearPosition();
    }

    @FXML
    public void setTransaction(){
        CheckNode node = new CheckNode(Integer.parseInt(transaction.getText()));

        market.setText(node.getMarket());
        section.setText(node.getSection());
        product.setText(node.getProduct());
        price.setText(node.getPrice().toString());
        number.setText("1");
        calcSum();
        purchaseDate.setValue(node.getPurchaseDate().toLocalDate());

    }

    @FXML
    public void savePosition(){
        try {
            CheckNode node = new CheckNode(Integer.parseInt(transaction.getText()));
            node.setMarket(market.getText());
            node.setSection(section.getText());
            node.setProduct(product.getText());
            node.setPrice(Double.parseDouble(price.getText()));
            node.setNumber(Integer.parseInt(number.getText()));
            node.setPurchaseDate(purchaseDate.getValue().atStartOfDay());
            System.out.println(node);
            node.addInBase();
        }catch (Exception e){
            e.printStackTrace();
        }
        marketSet = setName("market");

        sectionSet = setName("section");

        productSet = setName("product");
    }

    @FXML
    public void clearPosition(){
        price.setText("0");
        number.setText("1");
        calcSum();
    }

    private HashSet setName(String name){

        String query = "SELECT `" + name + "` FROM `test`";

        ResultSet resultSet = DataBaseManager.getResult(query);

        HashSet setName = new HashSet();
        try {
            while (resultSet.next()) {

                setName.add(resultSet.getString(1));

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return setName;
    }

    @FXML
    public void toForm(){

        String query = "SELECT * FROM `TEST` WHERE `time` >= " +
                intervalStart.getValue().atTime(0,0,0).toEpochSecond(ZoneOffset.ofHours(6))
                + " AND `time` < " + intervalEnd.getValue().plusDays(1).atTime(0,0,0).toEpochSecond(ZoneOffset.ofHours(6))
                + " ORDER BY `time`";

        System.out.println(query);

        ResultSet resultSet = DataBaseManager.getResult(query);



        try {
            Double sumPrice = 0.0;
            while(resultSet.next()){

                String string = resultSet.getString(1);
                string += "\t\t";
                string += resultSet.getString(2);
                string += "\t\t";
                string += resultSet.getString(3);
                string += "\t\t";
                string += resultSet.getString(4);
                string += "\t\t";
                string += resultSet.getString(5);
                string += "\t\t";
                string += LocalDateTime.ofEpochSecond(resultSet.getLong(6),0 , ZoneOffset.ofHours(6));

                sumPrice += Double.parseDouble(resultSet.getString(5));

                System.out.println(string);
                screen.appendText(string + "\n");
            }
            screen.appendText("=============================================================================================" +
                    "\n\t\t\t\t\t\t\t\t\t\tИтог:\t" + sumPrice +"\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
