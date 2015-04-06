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
package org.eclipse.che.jdt.internal.core;

import org.eclipse.jdt.core.JavaModelException;

public class ResolvedLambdaExpression extends LambdaExpression {

    private String uniqueKey;
    LambdaExpression unresolved;

    public ResolvedLambdaExpression(JavaElement parent, LambdaExpression unresolved, String uniqueKey) {
        super(parent, unresolved.interphase, unresolved.sourceStart, unresolved.sourceEnd, unresolved.arrowPosition,
              unresolved.lambdaMethod);
        this.uniqueKey = uniqueKey;
        this.unresolved = unresolved;
    }

    public String getFullyQualifiedParameterizedName() throws JavaModelException {
        return getFullyQualifiedParameterizedName(getFullyQualifiedName('.'), this.uniqueKey);
    }

    /* (non-Javadoc)
     * @see SourceType#getKey()
     */
    public String getKey() {
        return this.uniqueKey;
    }

    @Override
    public boolean equals(Object o) {
        return this.unresolved.equals(o);
    }

    /* (non-Javadoc)
     * @see SourceType#isResolved()
     */
    public boolean isResolved() {
        return true;
	}

	/**
	 * @private Debugging purposes
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, Object info, boolean showResolvedInfo) {
		super.toStringInfo(tab, buffer, info, showResolvedInfo);
		if (showResolvedInfo) {
			buffer.append(" {key="); //$NON-NLS-1$
			buffer.append(this.getKey());
			buffer.append("}"); //$NON-NLS-1$
		}
	}

	public JavaElement unresolved() {
		return this.unresolved;
	}
}