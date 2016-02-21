package ru.spbau.bocharov.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fyodor on 2/17/16.
 */
public class TrieImpl implements Trie {

    private class Node {
        int subtreeSize_ = 0;
        boolean isWordEnding_ = false;
        Map<Character, Node> children_ = new HashMap<>();
    }

    private Node root_ = new Node();

    @Override
    public boolean add(String element) {
        if (contains(element))
            return false;
        Node node = root_;
        for (int i = 0; i < element.length(); ++i) {
            char c = element.charAt(i);
            if (!node.children_.containsKey(c)) {
                node.children_.put(c, new Node());
            }
            node.subtreeSize_++;
            node = node.children_.get(c);
        }
        if (node.isWordEnding_) {
            return false;
        }

        node.isWordEnding_ = true;
        node.subtreeSize_++;

        return true;
    }

    @Override
    public boolean contains(String element) {
        Node node = root_;
        for (int i = 0; i < element.length(); ++i) {
            char c = element.charAt(i);
            if (!node.children_.containsKey(c)) {
                return false;
            }
            node = node.children_.get(c);
        }
        return node.isWordEnding_;
    }

    private boolean removeRecursive(Node node, String element, int pos) {
        if (pos == element.length()) {
            node.subtreeSize_--;
            boolean wordEnding = node.isWordEnding_;
            node.isWordEnding_ = false;
            return wordEnding;
        }

        char c = element.charAt(pos);
        if (!node.children_.containsKey(c)) {
            return false;
        }

        Node child = node.children_.get(c);
        boolean contained = removeRecursive(child, element, pos + 1);
        if (contained) {
            node.subtreeSize_--;
            if (child.children_.isEmpty() && !child.isWordEnding_) {
                node.children_.remove(c);
            }
        }

        return contained;
    }

    @Override
    public boolean remove(String element) {
        return removeRecursive(root_, element, 0);
    }

    @Override
    public int size() {
        return root_.subtreeSize_;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        Node node = root_;
        for (int i = 0; i < prefix.length(); ++i) {
            char c = prefix.charAt(i);
            if (!node.children_.containsKey(c)) {
                return 0;
            }
            node = node.children_.get(c);
        }
        return node.subtreeSize_;
    }
}
