package ru.spbau.bocharov.serverbench.server.impl.tcp;

import ru.spbau.bocharov.serverbench.common.ProtocolIO;
import ru.spbau.bocharov.serverbench.server.algo.Sort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

class Job {

    void execute(InputStream in, OutputStream out) throws IOException {
        int[] array = ProtocolIO.read(in);
        Sort.insertionSort(array);
        ProtocolIO.write(out, array);
    }
}
