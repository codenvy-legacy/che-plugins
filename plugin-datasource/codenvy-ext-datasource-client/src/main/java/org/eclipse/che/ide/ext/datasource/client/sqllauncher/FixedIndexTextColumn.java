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
package org.eclipse.che.ide.ext.datasource.client.sqllauncher;

import java.util.List;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

class FixedIndexTextColumn extends TextColumn<List<String>> {

    private final int columnIndex;

    public FixedIndexTextColumn(final int columnIndex, HasHorizontalAlignment.HorizontalAlignmentConstant alignment) {
        this.columnIndex = columnIndex;
        setHorizontalAlignment(alignment);
    }

    @Override
    public String getValue(final List<String> line) {
        return line.get(this.columnIndex);
    }


}
