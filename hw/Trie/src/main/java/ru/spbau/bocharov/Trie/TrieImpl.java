package ru.spbau.bocharov.Trie;

import java.io.*;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

/**
 * Created by fyodor on 2/17/16.
 */
public class TrieImpl implements Trie, StreamSerializable {

    private static class Node implements StreamSerializable {
        private static final char NODE_TERMINAL = '&';

        private int m_subtreeSize = 0;
        private boolean m_isWordEnding = false;
        private Map<Character, Node> m_children = new HashMap<>();

        @Override
        public void serialize(OutputStream out) throws IOException {
            DataOutputStream dos = new DataOutputStream(out);

            dos.writeInt(m_subtreeSize);
            dos.writeBoolean(m_isWordEnding);

            dos.writeInt(m_children.size());
            for (Map.Entry<Character, Node> e : m_children.entrySet()) {
                dos.writeChar(e.getKey());
                e.getValue().serialize(out);
            }

            dos.writeChar(NODE_TERMINAL);
        }

        @Override
        public void deserialize(InputStream in) throws IOException {
            DataInputStream dis = new DataInputStream(in);

            m_subtreeSize = dis.readInt();
            m_isWordEnding = dis.readBoolean();

            int childCount = dis.readInt();
            Map<Character, Node> children = new HashMap<>();
            for (int i = 0; i < childCount; i++) {
                char c = dis.readChar();
                Node node = new Node();
                node.deserialize(in);
                children.put(c, node);
            }
            if (NODE_TERMINAL != dis.readChar()) {
                throw new IllegalArgumentException("Trie.Node.deserialize: " +
                        "can't deserialize node: invalid stream format");
            }
            m_children = children;
        }
    }

    private final static short SIGNATURE = (short) 0b1100110011001100;

    private Node m_root = new Node();

    @Override
    public boolean add(String element) {
        if (contains(element)) {
            return false;
        }

        Node node = m_root;
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            if (!node.m_children.containsKey(c)) {
                node.m_children.put(c, new Node());
            }
            node.m_subtreeSize++;
            node = node.m_children.get(c);
        }
        if (node.m_isWordEnding) {
            return false;
        }

        node.m_isWordEnding = true;
        node.m_subtreeSize++;

        return true;
    }

    @Override
    public boolean contains(String element) {
        Node node = m_root;
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            if (!node.m_children.containsKey(c)) {
                return false;
            }
            node = node.m_children.get(c);
        }
        return node.m_isWordEnding;
    }

    private boolean removeRecursive(Node node, String element, int pos) {
        if (pos == element.length()) {
            if (node.m_isWordEnding) {
                node.m_subtreeSize--;
                node.m_isWordEnding = false;

                return true;
            }
            return false;
        }

        char c = element.charAt(pos);
        if (!node.m_children.containsKey(c)) {
            return false;
        }

        Node child = node.m_children.get(c);
        boolean contained = removeRecursive(child, element, pos + 1);
        if (contained) {
            node.m_subtreeSize--;
            if (child.m_children.isEmpty() && !child.m_isWordEnding) {
                node.m_children.remove(c);
            }
        }

        return contained;
    }

    @Override
    public boolean remove(String element) {
        return removeRecursive(m_root, element, 0);
    }

    @Override
    public int size() {
        return m_root.m_subtreeSize;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        Node node = m_root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!node.m_children.containsKey(c)) {
                return 0;
            }
            node = node.m_children.get(c);
        }
        return node.m_subtreeSize;
    }

    @Override
    public void serialize(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeShort(SIGNATURE);
        m_root.serialize(out);
    }

    @Override
    public void deserialize(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        short signature = dis.readShort();
        if (SIGNATURE != signature) {
            throw new IllegalArgumentException("Trie.deserialize: can't deserialize trie: stream signature mismatch.");
        }
        // To prevent trie from inconsistent state we need to deserialize it first and then reassign root.
        Node newRoot = new Node();
        newRoot.deserialize(in);
        m_root = newRoot;
    }
}
