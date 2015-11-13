/*
 * Copyright (c) 2014-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
window.onload = function () {
    var proposalsURL = window.document.location.search.replace('?proposals=', '');
    window.keywords = httpGet(proposalsURL);

    /**
     * Plug-in headers
     */
    var headers = {
        name: "External JavaScript proposals support for Orion",
        version: "1.0",
        description: "This plugin provides loading JavaScript content-assist proposals over HTTP."
    };
    var provider = new orion.PluginProvider(headers);

    var contentAssistServiceProvider = {
        computeProposals: function (buffer, offset, context) {
            var newLineDelimiterRegExp = new RegExp(context.delimiter, 'g');
            var proposals = [];
            var keywords = JSON.parse(window.keywords);
            for (var i = 0; i < keywords.length; i++) {
                var keyword = keywords[i];
                //if (keyword.proposal.indexOf(context.prefix) === 0) {
                var proposal = {
                    proposal: keyword.proposal,
                    description: keyword.description,
                    overwrite: keyword.overwrite,
                    doc: keyword.doc
                };
                if (keyword.group == true) {
                    proposal.unselectable = true;
                    proposal.style = "noemphasis_title";
                }
                // indent multiline block
                if (keyword.proposal !== undefined && keyword.proposal.indexOf(context.delimiter) > -1) {
                    proposal.proposal = keyword.proposal.replace(newLineDelimiterRegExp, context.delimiter + context.indentation)
                }
                if (keyword.escapePosition > 0) {
                    proposal.escapePosition = keyword.escapePosition + offset + context.indentation.length;
                }
                if (keyword.positions !== undefined) {
                    for (var j = 0; j < keyword.positions.length; j++) {
                        var pos = keyword.positions[j];
                        pos.offset = pos.offset + offset;
                    }
                    proposal.positions = keyword.positions;
                }
                proposals.push(proposal);
                //}
            }
            return proposals;
        }
    };

    var hoverServiceProvider = {
        computeHoverInfo: function (editorContext, context) {
            if (context.proposal !== undefined && context.proposal.doc !== undefined) {
                return {
                    type: "markdown",
                    content: context.proposal.doc
                };
            } else {
                return null;
            }
        }
    };

    var serviceProviderProps = {
        name: "External proposals for JavaScript content assist",
        contentType: ["application/javascript"]
    };

    provider.registerServiceProvider("orion.edit.contentAssist", contentAssistServiceProvider, serviceProviderProps);
    provider.registerServiceProvider("orion.edit.hover", hoverServiceProvider, serviceProviderProps);
    provider.connect();
};

function httpGet(theUrl) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", theUrl, false); // false for synchronous request
    xmlHttp.send(null);
    return xmlHttp.responseText;
}
