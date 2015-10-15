/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.openshift.client.project.wizard.page.template;

import elemental.dom.Element;
import elemental.html.SpanElement;
import elemental.html.TableElement;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.ui.list.SimpleList;
import org.eclipse.che.ide.util.dom.Elements;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of view {@link SelectTemplateView}
 *
 * @author Vlad Zhukovskiy
 */
@Singleton
public class SelectTemplateViewImpl implements SelectTemplateView {

    private static SelectTemplateViewImplUiBinder uiBinder = GWT.create(SelectTemplateViewImplUiBinder.class);

    interface SelectTemplateViewImplUiBinder extends UiBinder<DockPanel, SelectTemplateViewImpl> {
    }

    @UiField
    ScrollPanel templatePanel;

    private SimpleList<Template> templateList;

    private ActionDelegate delegate;
    private DockPanel      widget;

    @Inject
    public SelectTemplateViewImpl(Resources resources) {
        widget = uiBinder.createAndBindUi(this);

        TableElement breakPointsElement = Elements.createTableElement();
        breakPointsElement.setAttribute("style", "width: 100%");

        templateList = SimpleList.create((SimpleList.View)breakPointsElement, resources.defaultSimpleListCss(),
                                         new SimpleList.ListItemRenderer<Template>() {
                                             @Override
                                             public void render(Element listItemBase, Template itemData) {
                                                //TODO refactor this renderer to proper display template item
                                                 String tags = itemData.getMetadata().getAnnotations().get("tags");

                                                 StringBuilder sb = new StringBuilder();
                                                 sb.append(itemData.getMetadata().getName())
                                                   .append("\n")
                                                   .append(itemData.getMetadata().getAnnotations().get("description"))
                                                   .append("\n")
                                                   .append("Namespace: ")
                                                   .append(itemData.getMetadata().getNamespace())
                                                   .append("\nTags: ")
                                                   .append(tags);

                                                 SpanElement container = Elements.createSpanElement();
                                                 container.setInnerText(sb.toString());

                                                 listItemBase.appendChild(container);
                                             }
                                         },
                                         new SimpleList.ListEventDelegate<Template>() {
                                             @Override
                                             public void onListItemClicked(Element listItemBase, Template itemData) {
                                                 templateList.getSelectionModel().setSelectedItem(itemData);
                                                 delegate.onTemplateSelected(itemData);
                                             }

                                             @Override
                                             public void onListItemDoubleClicked(Element listItemBase, Template itemData) {

                                             }
                                         });

        templatePanel.add(templateList);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return widget;
    }

    /** {@inheritDoc} */
    @Override
    public void setTemplates(List<Template> templates, boolean keepExisting) {
        templateList.render(templates);
    }
}
