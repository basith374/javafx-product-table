/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxtestapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 *
 * @author bluroe
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private TableView tableView;
    
    private ObservableList<SaleItem> items;
    
    TableColumn<SaleItem, String> itemCodeColumn;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        items = FXCollections.observableArrayList(
                new SaleItem("MEM016", 50, 250),
                new SaleItem("MEM025", 80, 150),
                new SaleItem("MEM034", 30, 390)
        );
//        tableView.setFocusTraversable(false);
        tableView.setEditable(true);
//        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.ENTER) return;
            if(tableView.getEditingCell() == null) {
                if(e.getCode().isLetterKey() || e.getCode().isDigitKey()) {
                    TablePosition tp = tableView.getFocusModel().getFocusedCell();
                    tableView.edit(tp.getRow(), tp.getTableColumn());
                }
            }
        });
        itemCodeColumn = (TableColumn) tableView.getColumns().get(0);
        itemCodeColumn.setCellFactory(c -> {
            AutoFillTableCell textFieldCell = new AutoFillTableCell(items);
            textFieldCell.setConverter(new DefaultStringConverter());
            textFieldCell.getStyleClass().add("editing-cell");
            return textFieldCell;
        });
        itemCodeColumn.setOnEditCommit(e -> {
            SaleItem item = e.getRowValue();
            items.stream().filter(p -> p.getCode().equalsIgnoreCase(e.getNewValue())).findFirst().ifPresent(c -> {
                item.setCode(c.getCode());
                item.setPrice(c.getPrice());
            });
            String code = e.getNewValue();
            item.setCode(code);
            Platform.runLater(() -> {
                tableView.requestFocus();
            });
        });
        TableColumn<SaleItem, Integer> itemQtyColumn = (TableColumn) tableView.getColumns().get(1);
        itemQtyColumn.setCellFactory(p -> {
            TextFieldTableCell textFieldCell = new TextFieldTableCell<>();
            textFieldCell.setConverter(new StringConverter<Number>() {
                @Override
                public Number fromString(String string) {
                    return Integer.parseInt(string);
                }

                @Override
                public String toString(Number number) {
                    return number.toString();
                }
            }); 
            textFieldCell.getStyleClass().add("editing-cell");
            return textFieldCell;
        });
        itemQtyColumn.setOnEditCommit(e -> {
            SaleItem item = e.getRowValue();
            int qty = e.getNewValue();
            item.setQty(qty);
            Platform.runLater(() -> {
                tableView.requestFocus();
            });
        });
        tableView.setItems(FXCollections.observableArrayList(new SaleItem()));
        tableView.addEventFilter(KeyEvent.KEY_RELEASED, e -> { // fires after KEY_PRESSED
            if(e.getCode() == KeyCode.ENTER) {
                TablePosition focusPosition = tableView.getFocusModel().getFocusedCell();
                if(focusPosition.getRow() == -1) {
                    tableView.getSelectionModel().select(0);
                } else {
                    if(focusPosition.getColumn() == 1) {
                        if(focusPosition.getRow() == tableView.getItems().size() - 1) {
                            addRow();
                        } else if(focusPosition.getRow() < tableView.getItems().size() - 1) {
                            tableView.getSelectionModel().clearAndSelect(focusPosition.getRow() + 1, itemCodeColumn);
                            tableView.edit(focusPosition.getRow() + 1, itemCodeColumn);
                        }
                    } else if(focusPosition.getColumn() == 0) {
                        SaleItem item = (SaleItem) tableView.getItems().get(focusPosition.getRow());
                        items.stream()
                                .filter(i -> i.getCode().equals(item.getCode()))
                                .findFirst()
                                .ifPresent(i -> item.setPrice(i.getPrice()));
                        tableView.getSelectionModel().clearAndSelect(focusPosition.getRow(), itemQtyColumn); // declared on top
                        tableView.edit(focusPosition.getRow(), itemQtyColumn); // declared on top
                    }
                }
            }
        });
    }
    
    private void addRow() {
        TablePosition tp = tableView.getFocusModel().getFocusedCell();
        
        tableView.getSelectionModel().clearSelection();
        
        SaleItem item = new SaleItem("", 0, 0);
        tableView.getItems().add(item);
        
        int row = tableView.getItems().size() - 1;
        tableView.getSelectionModel().clearAndSelect(row, itemCodeColumn);
        
//        tableView.scrollTo(item);
        tableView.edit(row, itemCodeColumn);
    }
    
    @FXML
    private void getSelection() {
        System.out.println(tableView.getSelectionModel().getSelectedCells());
    }
    
    @FXML
    private void getFocused() {
        System.out.println(tableView.getFocusModel().getFocusedCell());
    }
    
    @FXML
    private void getEditing() {
        System.out.println(tableView.getEditingCell());
    }
    
}
