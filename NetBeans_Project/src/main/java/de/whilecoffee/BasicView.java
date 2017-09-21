package de.whilecoffee;

/**
 * Created by jd on 19.09.17.
 *  Copyright © 2017 whileCoffee Software Development - Johannes Dürr. All rights reserved.
 *
 *
 *             .__    .__.__         _________         _____  _____
 *     __  _  _|  |__ |__|  |   ____ \_   ___ \  _____/ ____\/ ____\____   ____
 *     \ \/ \/ /  |  \|  |  | _/ __ \/    \  \/ /  _ \   __\\   __\/ __ \_/ __ \
 *      \     /|   Y  \  |  |_\  ___/\     \___(  <_> )  |   |  | \  ___/\  ___/
 *       \/\_/ |___|  /__|____/\___  >\______  /\____/|__|   |__|  \___  >\___  >
 *                  \/             \/        \/                        \/     \/
 *     Released under MIT License for Hansenhof _electronic
 *
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import de.whilecoffee.geoapi.GeoEvent;
import de.whilecoffee.geoapi.Odokus3rdPartyGeoAPI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
public class BasicView extends View implements Odokus3rdPartyGeoAPI.OdokusGeoApiListener{
   
    // UI Elements
    private VBox controls = null;
    private VBox controls_top = null;
    private HBox buttons = null;
    private TextField userTextField = null;
    private PasswordField passTextField = null;
    private Button loginButton = null;
    private Button fetchButton = null;
    private Button addButton = null;
    private TextArea resTextArea = null;
    
    // odokus API
    Odokus3rdPartyGeoAPI geoApi = null;
    
    public BasicView(String name) {
        super(name);
        
        // Setup User Interface
        // Login
        userTextField = new TextField();
        userTextField.setTooltip(new Tooltip("Enter odokus user name"));
        passTextField = new PasswordField();
        passTextField.setTooltip(new Tooltip("Enter odokus password"));
        loginButton = new Button("Login / Init API");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                init_api();
            }
        });
        
        // Fetch and Add Button
        fetchButton = new Button("Fetch Events");
        fetchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                fetch();
            }
        });
        addButton = new Button("Add Event");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                add();
            }
        });
        
        // Result Text View
        resTextArea = new TextArea("Fetch-results show up here...");
        
        // Bind Elements in boxes and add to Layout
        buttons = new HBox(10.0, fetchButton, addButton);
        buttons.setAlignment(Pos.CENTER);
        controls_top = new VBox(10.0, userTextField, passTextField, loginButton);
        controls = new VBox(10.0, resTextArea);
        controls_top.setAlignment(Pos.CENTER);
        controls.setAlignment(Pos.CENTER);
        setTop(controls_top);
        setCenter(controls);
        setBottom(buttons);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> System.out.println("Menu")));
        appBar.setTitleText("Odokus Geo Api Demo");
    }
    
    
    private void init_api()
    {
        geoApi = new Odokus3rdPartyGeoAPI(userTextField.getText(),passTextField.getText());
        geoApi.setOdokusGeoApiListener(this);
    }
    
    private void fetch()
    {
        if(geoApi != null)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -3);
            Date start = cal.getTime();
            Date end = new Date();
            geoApi.getGeoEvents(start, end);
        }
    }
    
    private void add()
    {
        // Create a custom event
        Map<String, String> extensions = new HashMap<>();
        extensions.put("note", "Note from Desktop Client");
        GeoEvent evt = new GeoEvent(48.135125f,11.581980f, "Desktop Event", "Generated Demo Data", new Date(), extensions);

        // Send to odokus using api
        geoApi.setGeoEvent(evt, "geo-type-3");
    }

    @Override
    public void receivedGeoEvents(List<GeoEvent> events) {
        if(events != null && events.size()>0)
        {
            String resString = String.format("Received : %d\n",events.size());
            for (GeoEvent e : events){
                resString = resString + String.format("%s \n%s \n%s \n\n",
                        e.getEventDate().toString(), e.getEventName(), e.getEventDescription());
            }
            resTextArea.setText(resString);
        }
    }
}
