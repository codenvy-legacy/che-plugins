#
# Copyright (c) 2012-2014 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Codenvy, S.A. - initial API and implementation
#

LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
CODENVY_CONFIG_OPTS="-Xshare:auto -Xms512m -Xmx1024m -XX:MaxPermSize=256m"
CODENVY_OPTS="-Dcodenvy.local.conf.dir=${CATALINA_HOME}/conf"

#REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

JAVA_OPTS="$JAVA_OPTS $LOG_OPTS $CODENVY_CONFIG_OPTS $CODENVY_OPTS $REMOTE_DEBUG"

export SERVER_PORT=${PORT}
export JAVA_OPTS
export CLASSPATH="${CATALINA_HOME}/conf/:${CATALINA_HOME}/lib/jul-to-slf4j.jar:${CATALINA_HOME}/lib/slf4j-api.jar:${CATALINA_HOME}/lib/logback-classic.jar:${CATALINA_HOME}/lib/logback-core.jar:${JAVA_HOME}/lib/tools.jar"
