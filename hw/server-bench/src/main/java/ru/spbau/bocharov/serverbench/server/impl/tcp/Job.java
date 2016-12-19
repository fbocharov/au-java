package ru.spbau.bocharov.serverbench.server.impl.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.common.ProtocolIO;
import ru.spbau.bocharov.serverbench.server.algo.Sort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

class Job {

    private static final Logger log = LogManager.getLogger(Job.class);

    void execute(InputStream in, OutputStream out) throws IOException {
        int[] array = ProtocolIO.read(in);

        log.info("sorting array...");
        Sort.insertionSort(array);

        log.info("sending response...");
        ProtocolIO.write(out, array);
    }
}
