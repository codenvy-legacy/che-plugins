[![Stories in Ready](https://badge.waffle.io/codenvy/plugin-datasource.png?label=ready&title=Ready)](https://waffle.io/codenvy/plugin-datasource)
[![Build Status](https://travis-ci.org/codenvy/plugin-datasource.svg?branch=master)](https://travis-ci.org/codenvy/plugin-datasource)
Datasource-Plug-In for Codenvy IDE 3
====================================

Description
-----------

This project is a plugin for [Codenvy IDE 3](http://docs.codenvy.com/sdk/introduction/) that allows to work with databases directly from within the IDE.

What it can do
--------------

With this plugin, you can :

- create and store "datasources", which is a configuration for a database connection
- explore your datasources, with a tree representation of the schemas, tales and columns in the database
- edit SQL files, with syntax highlighting and completion help
- execute your SQL request on one of your datasources and see the result

Screenshots
-----------

![Datasource creation wizard](../screenshots/screenshots/datasource_plugin_creation.png?raw=true, "Datasource creation wizard")

![Datasource explorer](../screenshots/screenshots/datasource_plugin_explorer.png?raw=true, "Datasource explorer")

![SQL edition and execution](../screenshots/screenshots/datasource_plugin_sql_exec.png?raw=true, "SQL edition and execution")
How to test it
--------------

This plugin is bundled in the [Codenvy IDE SDK](https://github.com/codenvy/sdk). See the
[Clone the repository...](https://github.com/codenvy/sdk#clone-the-repository--checkout-latest-stable-branch) and
[Build the project](https://github.com/codenvy/sdk#build-the-project) for instructions.


Supported database types
------------------------

The following database servers are supported :

- [PostgreSQL](https://en.wikipedia.org/wiki/Postgres)
- [MySQL](https://en.wikipedia.org/wiki/Mysql)
- [MS SQL Server](https://en.wikipedia.org/wiki/Microsoft_SQL_Server)
- [NuoDB](https://en.wikipedia.org/wiki/NuoDB)
- [Oracle](https://en.wikipedia.org/wiki/Oracle_Database)[1]

[1] Not activated in the unmodified SDK

The following cloud databases have some support :

- Google Cloud SQL
- Amazon RDS PostgreSQL/MySQL/SqlServer [2]

[2] RDS support for Oracle is present if the Oracle DB support is present


Bundled JDBC drivers
--------------------

The datasource plugin relies on JDBC drivers.
Some JDBC drivers can't be bundled with the tomcat instance that is packaged in the SDK.

The following drivers are bundled by default :

- PostgreSQL
- JTDS (for MS SQLServer)
- MySQL
- NuoDB

This JDBC driver is supported but not bundled :

- Oracle DB


If an unbundled driver is needed, you must

1. check the driver is in the maven local repository or is available on a remote repository you maven installation will
use
2. force their inclusion in the datasource-plugin by activating the relevant profile at compilation
3. generate a new SDK bundle with this custom datasource-plugin

To force the bundling at compilation the driver must be present in you maven local repository or be available in a
repository where your maven installation can find it.


For example, to add support for oracle, you will compile the datasource plugin with :

```
    mvn clean install -DenableOracle
```

Then you will build a new SDK bundle as explained in [Build the project](https://github.com/codenvy/sdk#build-the-project).


How to add Oracle JDBC Driver in your Maven local repository:
-------------------------------------------------------------

1. Get the appropriate oracle JDBC Driver. 2 ways:
  - Get it from wwww.oracle.com
    For example: You can get Oracle Database 11g Release 2 JDBC Drivers from:
    http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
  - Get it from Oracle database installed folder, for example, `{ORACLE_HOME}\jdbc\lib\ojdbc6.jar`

2. Install it.

To install your Oracle jdbc driver, issue following command :

```
    mvn install:install-file -Dfile={path/to/your/ojdbc.jar} -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar
```
