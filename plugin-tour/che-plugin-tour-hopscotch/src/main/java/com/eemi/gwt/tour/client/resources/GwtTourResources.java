package com.eemi.gwt.tour.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface GwtTourResources extends ClientBundle {

    @Source("hopscotch.min.css")
    TextResource css();

    @Source("hopscotch.min.js")
    TextResource js();

}
