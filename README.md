# Eclipse Che Plugins
[![Join the chat at https://gitter.im/eclipse/che](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/eclipse/che?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/codenvy/che-plugins.svg?branch=master)](https://travis-ci.org/codenvy/che-plugins)

[![Eclipse License](http://img.shields.io/badge/license-Eclipse-brightgreen.svg)](https://github.com/codenvy/che/blob/master/LICENSE)
[![latest 3.x](https://img.shields.io/badge/latest stable-3.x-green.svg)](https://github.com/codenvy/che-plugins/tree/master)
[![latest 4.x](https://img.shields.io/badge/latest dev-4.x-yellowgreen.svg)](https://github.com/codenvy/che-plugins/tree/4.0)


## About Eclipse Che
High performance, open source developer environments in the cloud.

* **che**:                     [Main assembly repo - start here] (https://github.com/codenvy/che)
* **che-core**:                [Platform APIs and commons libraries] (http://github.com/codenvy/che-core)
* **che-plugins**:             [Language & tooling extensions] (http://github.com/codenvy/che-plugins)
* **che-depmgt**:              [Maven dependency management POM] (http://github.com/codenvy/che-depmgt)
* **che-parent**:              [Maven parent POM] (http://github.com/codenvy/che-parent)
* **cli**:                     [CLI for interacting with Che remotely] (http://github.com/codenvy/cli)
* **eclipse-plugin**:          [Eclipse IDE plug-in for Che projects] (http://github.com/codenvy/eclipse-plugin)

## About This Module
This module contains plug-ins that are distributed with Che. The default assembly includes Java, Maven, and SDK plug-ins. Other plug-ins can be built and installed into Che with the CLI.

## License
Che is open sourced under the Eclipse Public License 1.0.

## Clone
```sh
git clone https://github.com/codenvy/che-plugins.git
```
If master is unstable, checkout the latest tagged version.

## Build
```sh
cd che-plugins
mvn clean install
```

## What's Inside?
[Plug-in List] (https://eclipse-che.readme.io/docs/plug-ins)

