package ru.spbau.bocharov.vcs2.repo.impl;

import ru.spbau.bocharov.vcs2.storage.Storage;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

import static ru.spbau.bocharov.vcs2.util.IOUtils.copy;

class Snapshot implements Serializable {

    private final String dataPath;

    static Snapshot createSnapshot(Set<String> filesToInclude, Path path, Storage storage) throws IOException {
        try (DataOutputStream out = new DataOutputStream(storage.openForWrite(path))) {
            out.writeInt(filesToInclude.size());

            for (String file: filesToInclude) {
                Path filePath = Paths.get(file);
                long size = storage.fileSize(filePath);

                out.writeUTF(file);
                out.writeLong(size);
                try (InputStream in = storage.openForRead(filePath)) {
                    copy(in, out, size);
                }
            }
        }

        return new Snapshot(path.toString());
    }

    void restoreAll(Storage storage) throws IOException {
        try (DataInputStream in = new DataInputStream(storage.openForRead(Paths.get(dataPath)))) {
            int fileCount = in.readInt();
            for (int i = 0; i < fileCount; ++i) {
                String path = in.readUTF();
                long size = in.readLong();
                try (OutputStream out = storage.openForWrite(Paths.get(path))) {
                    copy(in, out, size);
                }
            }
        }
    }

    void restoreOne(Path path, Storage storage) throws IOException {
        try (DataInputStream in = new DataInputStream(storage.openForRead(Paths.get(dataPath)))) {
            int fileCount = in.readInt();
            for (int i = 0; i < fileCount; ++i) {
                String savedPath = in.readUTF();
                long size = in.readLong();
                if (!Objects.equals(savedPath, path.toString())) {
                    copy(in, null, size);
                } else {
                    try (OutputStream out = storage.openForWrite(path)) {
                        copy(in, out, size);
                    }
                    return;
                }
            }
        }

        throw new IOException("snapshot doesn't contain path " + path.toString());
    }

    private Snapshot(String path) {
        dataPath = path;
    }

}
