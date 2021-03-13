package com.yosidozli.api;

import io.dropwizard.lifecycle.Managed;

import javax.validation.constraints.NotNull;
import java.io.Closeable;

public class CloseableManaged implements Managed {

    @NotNull
    private Closeable closeable;

    public CloseableManaged(Closeable closeable) {
        this.closeable = closeable;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        this.closeable.close();
    }
}
