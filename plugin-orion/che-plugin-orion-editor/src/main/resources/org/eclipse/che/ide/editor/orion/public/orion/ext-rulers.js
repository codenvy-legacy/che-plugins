/*******************************************************************************
 * @license
 * Copyright (c) 2010, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 *
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/

/**
 * @class Provides the various rulers that show up on the left and right sides of the editor.  The rulers
 * 			contain annotations with varying styles, hovers and on click behaviours.
 */
/*eslint-env browser, amd*/
define("orion/editor/ext-rulers", [ //$NON-NLS-0$
     'orion/editor/eventTarget', //$NON-NLS-0$
     'orion/editor/rulers' //$NON-NLS-0$
], function(mEventTarget, mRulers) {

	/**
	 * <b>See:</b><br/>
	 * {@link orion.editor.Ruler}<br/>
	 */
	function ExtRuler (annotationModel, rulerLocation, rulerOverview, rulerStyle) {
		mRulers.Ruler.call(this, annotationModel, rulerLocation, rulerOverview, rulerStyle); //$NON-NLS-0$
	}
	ExtRuler.prototype = new mRulers.Ruler();
	ExtRuler.prototype.onClick = function(lineIndex, e) {
		this.dispatchEvent({type: "RulerClick", lineIndex: lineIndex});
	};

	mEventTarget.EventTarget.addMixin(ExtRuler.prototype);

	return {
    	ExtRuler: ExtRuler
    };
});