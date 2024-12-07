package edu.clysman.unifor.filesystem;

import java.io.Serial;
import java.io.Serializable;

class FileNode implements Serializable {
    private String name;
    private final StringBuilder content;

    FileNode(String name) {
        this.name = name;
        this.content = new StringBuilder();
    }

    String getName() {
        return name;
    }

    void setName(String newName) {
        this.name = newName;
    }

    String getContent() {
        return content.toString();
    }

    void appendContent(String newContent) {
        content.append(newContent).append(System.lineSeparator());
    }

    public void clearContent() {
        content.setLength(0);
    }

    @Serial
    private static final long serialVersionUID = 1L;
}
