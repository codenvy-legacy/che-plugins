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
package org.eclipse.che.ide.ext.datasource.server;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import org.eclipse.che.ide.ext.datasource.server.ssl.KeyStoreObject;
import org.eclipse.che.ide.ext.datasource.server.ssl.SslKeyStoreService;
import org.eclipse.che.ide.ext.datasource.server.ssl.TrustStoreObject;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseType;
import org.eclipse.che.ide.ext.datasource.shared.DefaultDatasourceDefinitionDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreRequestDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreTableType;

/**
 * Test the datasource service getDatabase() method that is used to retrieve a database catalog information. Tests are ignored as they needs
 * exiting and running database. Customize the database configuration with yours for testing.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDatasourceServiceDatabaseDTO {
    @Mock
    protected DefaultDatasourceDefinitionDTO databaseConfig;

    @Mock
    protected ExploreRequestDTO              exploreRequest;

    @Mock
    protected JdbcConnectionFactory          jdbcConnectionFactory;

    @Ignore
    @Test
    public void testPostgresDTOgeneration() throws Exception {
        when(databaseConfig.getDatabaseType()).thenReturn(DatabaseType.POSTGRES);
        when(databaseConfig.getDatabaseName()).thenReturn("wafa");
        when(databaseConfig.getHostName()).thenReturn("localhost");
        when(databaseConfig.getPort()).thenReturn(5432);
        when(databaseConfig.getUsername()).thenReturn("postgres");
        when(databaseConfig.getPassword()).thenReturn("nuxeospirit");

        when(exploreRequest.getDatasourceConfig()).thenReturn(databaseConfig);
        when(exploreRequest.getExploreTableType()).thenReturn(ExploreTableType.SIMPLE);

        String json = getDatabaseJsonDTOFromDatasourceService(exploreRequest);
        System.out.println(json);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("\"databaseProductName\":\"PostgreSQL\""));
    }

    protected String getDatabaseJsonDTOFromDatasourceService(ExploreRequestDTO exploreRequest) throws Exception {
        DatabaseExploreService dsService = new DatabaseExploreService(jdbcConnectionFactory);
        return dsService.getDatabase(exploreRequest);
    }

    @Ignore
    @Test
    public void testMySqlDTOgeneration() throws Exception {
        when(databaseConfig.getDatabaseType()).thenReturn(DatabaseType.MYSQL);
        when(databaseConfig.getDatabaseName()).thenReturn("aucoffre_db");
        when(databaseConfig.getHostName()).thenReturn("localhost");
        when(databaseConfig.getPort()).thenReturn(3306);
        when(databaseConfig.getUsername()).thenReturn("root");
        when(databaseConfig.getPassword()).thenReturn("selucreh");


        when(exploreRequest.getDatasourceConfig()).thenReturn(databaseConfig);
        when(exploreRequest.getExploreTableType()).thenReturn(ExploreTableType.SIMPLE);

        String json = getDatabaseJsonDTOFromDatasourceService(exploreRequest);
        System.out.println(json);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("\"databaseProductName\":\"MySQL\""));
    }

    @Ignore
    @Test
    public void testOracleDTOgeneration() throws Exception {
        when(databaseConfig.getDatabaseType()).thenReturn(DatabaseType.ORACLE);
        when(databaseConfig.getDatabaseName()).thenReturn("xe");
        when(databaseConfig.getHostName()).thenReturn("192.168.86.191");
        when(databaseConfig.getPort()).thenReturn(1521);
        when(databaseConfig.getUsername()).thenReturn("admin");
        when(databaseConfig.getPassword()).thenReturn("admin");


        when(exploreRequest.getDatasourceConfig()).thenReturn(databaseConfig);
        when(exploreRequest.getExploreTableType()).thenReturn(ExploreTableType.SIMPLE);

        String json = getDatabaseJsonDTOFromDatasourceService(exploreRequest);
        System.out.println(json);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("\"databaseProductName\":\"Oracle\""));
    }

    @Ignore
    @Test
    public void testSqlserverDTOgeneration() throws Exception {
        when(databaseConfig.getDatabaseType()).thenReturn(DatabaseType.JTDS);
        when(databaseConfig.getDatabaseName()).thenReturn("master");
        when(databaseConfig.getHostName()).thenReturn("192.168.56.101");
        when(databaseConfig.getPort()).thenReturn(1433);
        when(databaseConfig.getUsername()).thenReturn("sa");
        when(databaseConfig.getPassword()).thenReturn("admin");


        when(exploreRequest.getDatasourceConfig()).thenReturn(databaseConfig);
        when(exploreRequest.getExploreTableType()).thenReturn(ExploreTableType.SIMPLE);

        String json = getDatabaseJsonDTOFromDatasourceService(exploreRequest);
        System.out.println(json);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("\"databaseProductName\":\"Microsoft SQL Server\""));
    }

    @Mock
    protected FileItem clientCertFileItem;

    @Mock
    protected FileItem clientKeyFileItem;

    @Mock
    protected FileItem serverCAFileItem;

    @Mock
    KeyStoreObject     keystoreObject;
    @Mock
    TrustStoreObject   truststoreObject;

    @Test
    @Ignore
    public void testMySqlSSL() throws Exception {
        Path tmpFolder = Files.createTempDirectory("ssl-keystore-test");
        System.setProperty("javax.net.ssl.trustStore", tmpFolder.toString() + "/truststore");
        System.setProperty("javax.net.ssl.keyStore", tmpFolder.toString() + "/keystore");

        SslKeyStoreService keystoreService = new SslKeyStoreService(keystoreObject, truststoreObject);

        when(databaseConfig.getDatabaseType()).thenReturn(DatabaseType.MYSQL);
        when(databaseConfig.getDatabaseName()).thenReturn("");
        when(databaseConfig.getHostName()).thenReturn("173.194.80.59");
        when(databaseConfig.getPort()).thenReturn(3306);
        when(databaseConfig.getUsername()).thenReturn("root");
        when(databaseConfig.getPassword()).thenReturn("serlii");
        when(databaseConfig.getUseSSL()).thenReturn(true);
        when(databaseConfig.getVerifyServerCertificate()).thenReturn(true);
        when(exploreRequest.getDatasourceConfig()).thenReturn(databaseConfig);
        when(exploreRequest.getExploreTableType()).thenReturn(ExploreTableType.SIMPLE);

        final File certFile = new File("/home/sunix/ssl/demo-codenvy-datasource/client-cert.pem");
        when(clientCertFileItem.getInputStream()).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new FileInputStream(certFile);
            }
        });
        when(clientCertFileItem.getFieldName()).thenReturn("certFile");
        when(clientCertFileItem.get()).thenReturn(IOUtils.toByteArray(new FileInputStream(certFile)));

        final File keyFile = new File("/home/sunix/ssl/demo-codenvy-datasource/client-key.der.pem");
        when(clientKeyFileItem.getInputStream()).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new FileInputStream(keyFile);
            }
        });
        when(clientKeyFileItem.getFieldName()).thenReturn("keyFile");
        when(clientKeyFileItem.get()).thenReturn(IOUtils.toByteArray(new FileInputStream(keyFile)));


        final File serverCAFile = new File("/home/sunix/ssl/demo-codenvy-datasource/server-ca.pem");
        when(serverCAFileItem.getInputStream()).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new FileInputStream(serverCAFile);
            }
        });
        when(serverCAFileItem.getFieldName()).thenReturn("certFile");
        when(serverCAFileItem.get()).thenReturn(IOUtils.toByteArray(new FileInputStream(serverCAFile)));

        List<FileItem> keyFileItems = new ArrayList<>();
        keyFileItems.add(clientCertFileItem);
        keyFileItems.add(clientKeyFileItem);
        List<FileItem> serverCAFileItems = new ArrayList<>();
        serverCAFileItems.add(serverCAFileItem);


        // test without keystore, should fail
        try {
            getDatabaseJsonDTOFromDatasourceService(exploreRequest);
            fail("should have an exception, key and certificate are not added yet");
        } catch (Exception e) { // all right
        }


        // test with keystore but without keys
        keystoreService.getClientKeyStore().addNewKey("test", keyFileItems.iterator());
        keystoreService.getClientKeyStore().deleteKey("test", "");
        keystoreService.getTrustStore().addNewServerCACert("yes", serverCAFileItems.iterator());
        keystoreService.getTrustStore().deleteKey("yes", "");
        try {
            getDatabaseJsonDTOFromDatasourceService(exploreRequest);
            fail("should not work as keys and cert removed");
        } catch (Exception e) {
        }


        // Test with keys should work
        keystoreService.getClientKeyStore().addNewKey("test", keyFileItems.iterator());
        keystoreService.getTrustStore().addNewServerCACert("yes", serverCAFileItems.iterator());

        String json = getDatabaseJsonDTOFromDatasourceService(exploreRequest);
        System.out.println(json);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("\"databaseProductName\":\"MySQL\""));


    }
}
