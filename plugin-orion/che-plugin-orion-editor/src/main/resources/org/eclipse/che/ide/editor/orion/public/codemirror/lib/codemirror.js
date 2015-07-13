define("codemirror/lib/codemirror", ["orion/editor/mirror"], function(mMirror) {
    var codeMirror = new mMirror.Mirror();
    window.CodeMirror = codeMirror;
    return codeMirror;
});
