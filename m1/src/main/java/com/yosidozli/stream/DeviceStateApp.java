package com.yosidozli.stream;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DeviceStateApp extends Application<StreamConfiguration> {

    @Override
    public void initialize(Bootstrap<StreamConfiguration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addCommand(new DeviceStreamProcessor("stream","store device data to db"));
    }

    @Override
    public void run(StreamConfiguration configuration, Environment environment) throws Exception {
        // leave empty the login is in DeviceSteamProcessor
    }


    public static void main(String[] args) throws Exception {
        new DeviceStateApp().run(args);
    }
}
