package com.yosidozli.api;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface DeviceDao  {


//        @SqlUpdate("create table devices (uuid String primary key, arrive_ts long)")
//        void createSomethingTable();

        @SqlUpdate("insert into <table> (uuid, state) values (:uuid, :state)")
        void insertDeviceState(@Define("table") String table,@Bind("uuid") String uuid, @Bind("state") boolean state);

        @SqlQuery("select isCharging from <table> where uuid = :uuid")
        boolean deviceState(@Define("table") String table, @Bind("uuid") String uuid);


}
