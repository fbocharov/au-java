package ru.spbau.bocharov.torrent.tracker;

import java.util.Objects;
import java.util.Scanner;

public class Tracker {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("you should specify path to save (load if exists) tracker state");
            return;
        }

        try {
            TorrentTracker tracker = new TorrentTracker(args[0]);
            tracker.start();

            String input;
            Scanner sc = new Scanner(System.in);
            do {
                input = sc.nextLine();
            } while (!Objects.equals(input, ":q"));

            tracker.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
