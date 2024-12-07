package edu.clysman.unifor.filesystem;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Directory implements Serializable {
    private final String name;
    private final Directory parent;
    private final Map<String, Directory> subdirectories = new HashMap<>();
    private final Map<String, FileNode> files = new HashMap<>();

    Directory(String name) {
        this(name, null);
    }

    Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    String getPath() {
        return parent == null ? name : parent.getPath() + name + "/";
    }

    Directory getParent() {
        return parent;
    }

    boolean addSubdirectory(String name) {
        if (subdirectories.containsKey(name)) return false;
        subdirectories.put(name, new Directory(name, this));
        return true;
    }

    Directory getSubdirectory(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        String[] parts = path.split("/");
        String currentPart = parts[0];

        if (parts.length == 1) {
            return subdirectories.get(currentPart);
        }

        Directory subdirectory = subdirectories.get(currentPart);
        if (subdirectory != null) {
            String newPath = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            return subdirectory.getSubdirectory(newPath);
        }

        return null;
    }

    boolean removeSubdirectory(String name) {
        return subdirectories.remove(name) != null;
    }

    boolean addFile(String name) {
        if (files.containsKey(name)) return false;
        files.put(name, new FileNode(name));
        return true;
    }

    FileNode getFile(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        String[] parts = path.split("/");
        String currentPart = parts[0];

        if (parts.length == 1) {
            return files.get(currentPart);
        }

        Directory subdirectory = subdirectories.get(currentPart);
        if (subdirectory != null) {
            String newPath = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            return subdirectory.getFile(newPath);
        }

        return null;
    }

    boolean removeFile(String name) {
        return files.remove(name) != null;
    }

    boolean rename(String oldName, String newName) {
        if (files.containsKey(oldName)) {
            FileNode file = files.remove(oldName);
            file.setName(newName);
            files.put(newName, file);
            return true;
        }
        if (subdirectories.containsKey(oldName)) {
            Directory dir = subdirectories.remove(oldName);
            subdirectories.put(newName, dir);
            return true;
        }
        return false;
    }

    void listContents() {
        subdirectories.keySet().forEach(dir -> System.out.println("[D] " + dir));
        files.keySet().forEach(file -> System.out.println("[F] " + file));
    }

    void copyFrom(Directory other) {
        subdirectories.clear();
        files.clear();
        subdirectories.putAll(other.subdirectories);
        files.putAll(other.files);
    }

    @Serial
    private static final long serialVersionUID = 1L;
}
