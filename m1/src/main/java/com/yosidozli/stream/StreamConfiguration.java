package com.yosidozli.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.apache.kafka.common.protocol.types.Field;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class StreamConfiguration extends Configuration {

    @Valid
    @NotNull
    private DataSourceFactory database;

    public StreamConfiguration() {
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    @NotEmpty
    private String table;

    @JsonProperty("table")
    public String getTable() {
        return table;
    }

    @JsonProperty("table")
    public void setTable(String table) {
        this.table = table;
    }

    @NotEmpty
    private String applicationId;

    @JsonProperty("applicationId")
    public String getApplicationId() {
        return applicationId;
    }

    @JsonProperty("applicationId")
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @NotEmpty
    private String bootstrapServers;

    @JsonProperty("bootstrap.servers")
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    @JsonProperty("bootstrap.servers")
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @NotEmpty
    private String schemeRegistry;

    @JsonProperty("scheme.registry.url")
    public String getSchemeRegistry() {
        return schemeRegistry;
    }

    @JsonProperty("scheme.registry.url")
    public void setSchemeRegistry(String schemeRegistry) {
        this.schemeRegistry = schemeRegistry;
    }

    @NotEmpty
    private String sourceTopic;

    @JsonProperty("src-topic")
    public String getSourceTopic() {
        return sourceTopic;
    }

    @JsonProperty("src-topic")
    public void setSourceTopic(String sourceTopic) {
        this.sourceTopic = sourceTopic;
    }
}
