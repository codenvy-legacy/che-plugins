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
            var proposalsURL = window.document.location.search.replace('?proposals=', '');
            if (proposalsURL == undefined) {
                return [];
            }

            return getContent(proposalsURL).then(function (proposalsContent) {
                var newLineDelimiterRegExp = new RegExp(context.delimiter, 'g');

                var keywords;
                try {
                    keywords = JSON.parse(proposalsContent);
                } catch(e) {
                    throw Error("Unable to parse proposals.json: " + e.message);
                }

                var prefixLength = context.prefix.length;
                var proposals = [];
                for (var i = 0; i < keywords.length; i++) {
                    var keyword = keywords[i];

                    // skip inappropriate proposals
                    if (keyword.proposal !== undefined && keyword.proposal.indexOf(context.prefix) != 0) {
                        continue;
                    }

                    var proposal = {
                        proposal: keyword.proposal,
                        description: keyword.description,
                        overwrite: true,
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
                        proposal.escapePosition = keyword.escapePosition + offset + context.indentation.length - prefixLength;
                    }
                    if (keyword.positions !== undefined) {
                        for (var j = 0; j < keyword.positions.length; j++) {
                            var pos = keyword.positions[j];
                            pos.offset = pos.offset + offset - prefixLength;
                        }
                        proposal.positions = keyword.positions;
                    }
                    proposals.push(proposal);
                    //}
                }
                return proposals;
            }).catch(function (err) {
                console.log(err.message);
            });
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

function getContent(url) {
    return new Promise(function (resolve, reject) {
        var request = new XMLHttpRequest();
        request.open('GET', url);

        request.onload = function () {
            if (this.status == 200) {
                resolve(this.response);
            } else {
                reject(Error("Unable to get " + url + ": " + this.statusText));
            }
        };
        // handle network errors
        request.onerror = function () {
            reject(Error("Unable to get " + url + ": " + this.statusText));
        };

        request.send();
    });
}
