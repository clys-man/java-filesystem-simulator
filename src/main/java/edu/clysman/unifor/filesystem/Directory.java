package edu.clysman.unifor.filesystem;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Directory implements Serializable {
    private String name;
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

    boolean removeSubdirectory(String path) {
        if (path == null || path.isEmpty()) return false;

        String[] parts = path.split("/");
        String currentPart = parts[0];

        if (parts.length == 1) {
            return subdirectories.remove(currentPart) != null;
        }

        Directory subdirectory = subdirectories.get(currentPart);
        if (subdirectory != null) {
            String newPath = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            return subdirectory.removeSubdirectory(newPath);
        }

        return false;
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

    boolean removeFile(String path) {
        if (path == null || path.isEmpty()) return false;

        String[] parts = path.split("/");
        String currentPart = parts[0];

        if (parts.length == 1) {
            return files.remove(currentPart) != null;
        }

        Directory subdirectory = subdirectories.get(currentPart);
        if (subdirectory != null) {
            String newPath = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            return subdirectory.removeFile(newPath);
        }

        return false;
    }

    boolean rename(String path, String newName) {
        if (path == null || path.isEmpty() || newName == null || newName.isEmpty()) return false;

        String[] parts = path.split("/");
        String currentPart = parts[0];

        if (parts.length == 1) {
            if (files.containsKey(currentPart)) {
                FileNode file = files.remove(currentPart);
                file.setName(newName);
                files.put(newName, file);
                return true;
            }
            if (subdirectories.containsKey(currentPart)) {
                Directory dir = subdirectories.remove(currentPart);
                dir.name = newName;
                subdirectories.put(newName, dir);
                return true;
            }
            return false;
        }

        Directory subdirectory = subdirectories.get(currentPart);
        if (subdirectory != null) {
            String newPath = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            return subdirectory.rename(newPath, newName);
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
