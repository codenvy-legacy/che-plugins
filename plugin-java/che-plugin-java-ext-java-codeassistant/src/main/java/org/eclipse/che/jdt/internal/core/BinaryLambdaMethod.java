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

import org.eclipse.jdt.core.IJavaElement;

public class BinaryLambdaMethod extends LambdaMethod {

	BinaryLambdaMethod(JavaElement parent, String name, String key, int sourceStart, String [] parameterTypes, String [] parameterNames, String returnType, SourceMethodElementInfo elementInfo) {
		super(parent, name, key, sourceStart, parameterTypes, parameterNames, returnType, elementInfo);
	}

	/*
	 * @see JavaElement#getPrimaryElement(boolean)
	 */
	public IJavaElement getPrimaryElement(boolean checkOwner) {
		return this;
	}

	/*
	 * @see IMember#isBinary()
	 */
	public boolean isBinary() {
		return true;
	}
}
