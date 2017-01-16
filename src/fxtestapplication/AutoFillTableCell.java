/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxtestapplication;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import javafx.util.StringConverter;

/**
 *
 * @author bluroe
 */
public class AutoFillTableCell<S,T> extends TableCell<S,T> {

    // --- converter
    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");

    private TextField textField;
    private ObservableList<SaleItem> items;
    
    public AutoFillTableCell(ObservableList<SaleItem> items) {
        this.items = items;
        getStyleClass().add("editing-cell");
    }
    
    public TextField createTextField() {
        TextField textField = new TextField(getItemText());
        
        textField.setOnAction(event -> {
            if (converter.get() == null) {
                throw new IllegalStateException(
                        "Attempting to convert text input into Object, but provided "
                                + "StringConverter is null. Be sure to set a StringConverter "
                                + "in your cell factory.");
            }
            commitEdit(converter.get().fromString(textField.getText()));
            event.consume();
        });
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
                t.consume();
            }
        });
        
        // auto complete feature
        ContextMenu menu = new ContextMenu();
        textField.setContextMenu(menu);
        textField.textProperty().addListener((o, ov, nv) -> {
            String pattern = "(?i:.*" + nv + ".*)";
            List<SaleItem> matching = items.stream().filter(i -> i.getCode().matches(pattern)).collect(Collectors.toList());
            System.out.println("matching " + matching.size());
            if(matching.isEmpty() || nv.isEmpty()) {
                menu.hide();
            } else {
                List<MenuItem> menuItems = matching.stream().map(i -> {
                    MenuItem item = new MenuItem(i.getCode());
                    item.setOnAction(e -> {
                        
                    });
                    return item;
                }).collect(Collectors.toList());
                menu.getItems().setAll(menuItems);
                menu.show(textField.getScene().getWindow());
            }
        });
        
        return textField;
    }

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }
    
    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }

    @Override public void startEdit() {
        if (! isEditable()
                || ! getTableView().isEditable()
                || ! getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            if (textField == null) {
                textField = createTextField();
            }
            textField.setText(getItemText());
            setText(null);
            
            setGraphic(textField);

            textField.selectAll();

            textField.requestFocus();
        }
    }

    @Override public void cancelEdit() {
        super.cancelEdit();
        setText(getItemText());
        setGraphic(null);
    }

    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        
        if (isEmpty()) {
            setText(null);
            setGraphic(null);
        } else {
             if (isEditing()) {
                textField.setText(getItemText());
                setText(null);

                setGraphic(textField);
            } else {
                setText(getItemText());
                setGraphic(null);
            }
        }
    }
    
    private String getItemText() {
        return converter.get() == null ? getItem() == null ? "" : getItem().toString() : converter.get().toString(getItem());
    }
    
}
