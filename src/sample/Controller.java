package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;

public class Controller {

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
    private  ContextMenu menuMarket ;
    @FXML
    private MenuItem itemMarket;
    @FXML
    private  ContextMenu menuSection ;
    @FXML
    private MenuItem itemSection;
    @FXML
    private  ContextMenu menuProduct ;
    @FXML
    private MenuItem itemProduct;
    private HashSet<String> marketSet;
    private HashSet<String> sectionSet;
    private HashSet<String> productSet;

    //public CheckNode node = new CheckNode(1);
    @FXML
    public void initialize(){

        marketSet = setName("market");

        sectionSet = setName("section");

        productSet = setName("product");

        purchaseDate.setValue(LocalDate.now());

        clearPosition();

        sum.setEditable(false);

        market.addEventHandler(Event.ANY, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                handlerProduct(event);
            }
        });

        section.addEventHandler(Event.ANY, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                handlerProduct(event);
            }
        });

        product.addEventHandler(Event.ANY, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                handlerProduct(event);
            }
        });

        price.addEventHandler(Event.ANY, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                handlerField(event);
            }
        });

        number.addEventHandler(Event.ANY, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                handlerField(event);
            }
        });


    }

    private void handlerProduct(Event event){

        TextField field = (TextField) event.getSource();

        ContextMenu menu = field.getContextMenu();

        System.out.println(menu.getItems());
        
        MenuItem item = menu.getItems().get(0);

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
        
        System.out.println(item.getText());

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
        }
    }



    public void handlerField(Event event){

        TextField field = (TextField) event.getSource();

        if(event.getEventType().getName().equals("KEY_RELEASED")){

            try{
                calcSum();
            }catch (Exception e){
                System.out.println(e);
                field.setText(field.getText().substring(0, field.getText().length()-1));
                field.end();
            }
        }
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
        transaction.setText("0");
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
            node.setPurchaseDate(purchaseDate.getValue());
            System.out.println(node);
            node.addInBase();
        }catch (Exception e){
            System.out.println("Не установлена цена!");
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
}
