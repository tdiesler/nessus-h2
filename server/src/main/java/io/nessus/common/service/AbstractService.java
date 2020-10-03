package io.nessus.common.service;

import io.nessus.common.Config;
import io.nessus.common.ConfigSupport;

public abstract class AbstractService extends ConfigSupport implements Service {

    protected AbstractService(Config config) {
        super(config);
    }
}
