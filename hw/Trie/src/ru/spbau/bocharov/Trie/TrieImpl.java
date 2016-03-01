package ru.spbau.bocharov.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fyodor on 2/17/16.
 */
public class TrieImpl implements Trie {

    private static class Node {
        private int m_subtreeSize = 0;
        private boolean m_isWordEnding = false;
        private final Map<Character, Node> m_children = new HashMap<>();
    }

    private final Node m_root = new Node();

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
            }
            boolean wordEnding = node.m_isWordEnding;
            node.m_isWordEnding = false;
            return wordEnding;
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
}
