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
({

    /*
     * By default, all modules are located relative to this path. If baseUrl
     * is not explicitly set, then all modules are loaded relative to
     * the directory that holds the build file. If appDir is set, then
     * baseUrl should be specified as relative to the appDir.
   */
    baseUrl: "${codemirror.extract.location}",
    /*
     * The directory path to save the output. If not specified, then
     * the path will default to be a directory called "build" as a sibling
     * to the build file. All relative paths are relative to the build file.
     */
    dir: "${codemirror.final.location}",
    /*
     *  How to optimize all the JS files in the build output directory.
     *  Right now only the following values
     *  are supported:
     *  - "uglify": (default) uses UglifyJS to minify the code.
     *  - "closure": uses Google's Closure Compiler in simple optimization
     *  mode to minify the code. Only available if running the optimizer using
     *  Java.
     *  - "closure.keepLines": Same as closure option, but keeps line returns
     *  in the minified files.
     *  - "none": no minification will be done.
     */
    optimize: "uglify2",

    //As of RequireJS 2.0.2, the dir above will be deleted before the
    //build starts again. If you have a big build and are not doing
    //source transforms with onBuildRead/onBuildWrite, then you can
    //set keepBuildDir to true to keep the previous dir. This allows for
    //faster rebuilds, but it could lead to unexpected errors if the
    //built code is transformed in some way.
    keepBuildDir: false,

    preserveLicenseComments: false,

    //Introduced in 2.1.2 and considered experimental.
    //If the minifier specified in the "optimize" option supports generating
    //source maps for the minified code, then generate them. The source maps
    //generated only translate minified JS to non-minified JS, it does not do
    //anything magical for translating minified JS to transpiled source code.
    //Currently only optimize: "uglify2" is supported when running in node or
    //rhino, and if running in rhino, "closure" with a closure compiler jar
    //build after r1592 (20111114 release).
    //The source files will show up in a browser developer tool that supports
    //source maps as ".js.src" files.
    generateSourceMaps: true,

    // If using UglifyJS2 for script optimization, these config options can be
    // used to pass configuration values to UglifyJS2.
    // For possible `output` values see:
    // https://github.com/mishoo/UglifyJS2#beautifier-options
    // For possible `compress` values see:
    // https://github.com/mishoo/UglifyJS2#compressor-options
    uglify2 : {
        // Example of a specialized config. If you are fine
        // with the default options, no need to specify
        // any of these properties.
        output : { beautify : false},
        compress : { sequences : true, conditionals:true,
                     comparisons:true, booleans: true, loops: true, global_defs : { DEBUG : false } },
        warnings : true,
        mangle : false,
        screw_ie8: true
    },

    /*
     * If set to true, any files that were combined into a build layer will be
     * removed from the output folder.
     */
    removeCombined: false,

    fileExclusionRegExp: /^\./,

    optimizeCss: false
})