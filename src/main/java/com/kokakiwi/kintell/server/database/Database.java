package com.kokakiwi.kintell.server.database;

import java.io.File;
import java.util.List;

import org.sqlite.JDBC;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.google.common.collect.Lists;

public class Database
{
    private final File        databaseFile;
    private final EbeanServer ebean;
    
    public Database(File databaseFile)
    {
        this.databaseFile = databaseFile;
        
        final boolean createDb = !databaseFile.exists();
        
        final ServerConfig config = createConfig();
        ebean = EbeanServerFactory.create(config);
        
        if (createDb)
        {
            final SpiEbeanServer ebeanServer = (SpiEbeanServer) ebean;
            final DdlGenerator gen = ebeanServer.getDdlGenerator();
            
            gen.runScript(false, gen.generateCreateDdl());
        }
    }
    
    private ServerConfig createConfig()
    {
        final ServerConfig config = new ServerConfig();
        
        config.setName("KintellDatabase");
        config.setClasses(getClasses());
        config.setDefaultServer(false);
        config.setRegister(false);
        config.setDatabasePlatform(new SQLitePlatform());
        config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        
        final DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(JDBC.PREFIX + databaseFile.getAbsolutePath());
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("root");
        dataSourceConfig.setDriver("org.sqlite.JDBC");
        dataSourceConfig.setIsolationLevel(Transaction.SERIALIZABLE);
        
        config.setDataSourceConfig(dataSourceConfig);
        
        return config;
    }
    
    private List<Class<?>> getClasses()
    {
        final List<Class<?>> classes = Lists.newLinkedList();
        
        classes.add(UserEntry.class);
        classes.add(RankEntry.class);
        
        return classes;
    }
    
    public File getDatabaseFile()
    {
        return databaseFile;
    }
    
    public EbeanServer getEbean()
    {
        return ebean;
    }
}
