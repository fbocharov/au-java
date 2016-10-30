package ru.spbau.bocharov.torrent.common;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class FileInfo implements Serializable {

    @Getter
    private int fileId;
    @Getter
    private String fileName;
    @Getter
    private long size;

    public FileInfo() {}

    public FileInfo(int id, String name, long sz) {
        fileId = id;
        fileName = name;
        size = sz;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(fileId);
        out.writeUTF(fileName);
        out.writeLong(size);
    }

    public void deserialize(DataInputStream in) throws IOException {
        fileId = in.readInt();
        fileName = in.readUTF();
        size = in.readLong();
    }
}
