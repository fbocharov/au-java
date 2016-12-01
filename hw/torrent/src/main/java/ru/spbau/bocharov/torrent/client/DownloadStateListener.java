package ru.spbau.bocharov.torrent.client;

import ru.spbau.bocharov.torrent.common.FileInfo;

public interface DownloadStateListener {

    void startDownload(FileInfo file);

    void updateState(int fileId, double percentage);
}
