package edu.clysman.unifor.filesystem;

import java.io.*;
import java.util.*;

public class Main {
    private static final int TOTAL_CLUSTERS = 1024;

    private static final String FILESYSTEM_FILE = "filesystem.unifor";

    private static final int[] filesystem = new int[TOTAL_CLUSTERS];

    private static final Directory root = new Directory("/");
    private static Directory currentDirectory = root;

    private static FileNode journalFile;

    public static void main(String[] args) {
        try {
            initializeFileSystem();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print(currentDirectory.getPath() + " > ");
                String commandLine = scanner.nextLine();
                String[] parts = commandLine.split(" ", 2);
                String command = parts[0];
                String argument = parts.length > 1 ? parts[1] : "";

                switch (command) {
                    case "mkdir" -> makeDirectory(argument);
                    case "cd" -> changeDirectory(argument);
                    case "ls" -> listDirectory();
                    case "touch" -> createFile(argument);
                    case "write" -> writeFile(argument);
                    case "cat" -> readFile(argument);
                    case "rm" -> deleteFile(argument);
                    case "rmdir" -> deleteDirectory(argument);
                    case "rename" -> rename(argument);
                    case "log" -> showLog();
                    case "help" -> showHelp();
                    case "exit" -> {
                        saveFileSystem();
                        System.exit(0);
                    }
                    default -> System.out.println("Comando não reconhecido. Digite 'help' para ajuda.");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no sistema de arquivos: " + e.getMessage());
        }
    }

    private static void initializeFileSystem() throws IOException {
        File fsFile = new File(FILESYSTEM_FILE);
        if (fsFile.exists()) {
            loadFileSystem();
        } else {
            Arrays.fill(filesystem, -1);
            root.addFile("journal.log");
            journalFile = root.getFile("journal.log");
            saveFileSystem();
        }

        if (journalFile == null) {
            journalFile = root.getFile("journal.log");
            if (journalFile == null) {
                journalFile = new FileNode("journal.log");
                root.addFile("journal.log");
            }
        }

        processLog();
    }

    private static void listDirectory() {
        currentDirectory.listContents();
    }

    private static void readFile(String name) {
        FileNode file = currentDirectory.getFile(name);
        if (file != null) {
            System.out.println(file.getContent());
        } else {
            System.out.println("Arquivo não encontrado.");
        }
    }

    private static void makeDirectory(String name) throws IOException {
        if (name.isEmpty()) {
            System.out.println("Nome do diretório não pode estar vazio.");
            return;
        }
        String action = "MKDIR " + currentDirectory.getPath() + name;
        logPendingAction(action);
        if (currentDirectory.addSubdirectory(name)) {
            logCommittedAction(action);
            saveFileSystem();
            System.out.println("Diretório criado com sucesso.");
        } else {
            System.out.println("Erro ao criar diretório. Talvez já exista.");
        }
    }

    private static void changeDirectory(String name) throws IOException {
        if (name.equals("..")) {
            if (currentDirectory.getParent() != null) {
                currentDirectory = currentDirectory.getParent();
            }
        } else {
            Directory subDir = currentDirectory.getSubdirectory(name);
            if (subDir != null) {
                currentDirectory = subDir;
            } else {
                System.out.println("Diretório não encontrado.");
            }
        }
    }

    private static void createFile(String name) throws IOException {
        if (name.isEmpty()) {
            System.out.println("Nome do arquivo não pode estar vazio.");
            return;
        }
        String action = "TOUCH " + currentDirectory.getPath() + name;
        logPendingAction(action);
        if (currentDirectory.addFile(name)) {
            logCommittedAction(action);
            saveFileSystem();
            System.out.println("Arquivo criado com sucesso.");
        } else {
            System.out.println("Erro ao criar arquivo. Talvez já exista.");
        }
    }

    private static void writeFile(String input) throws IOException {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("Uso: write <texto> <arquivo>");
            return;
        }
        String fileName = parts[0];
        String content = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        String action = "WRITE " + fileName + " " + content;

        logPendingAction(action);
        FileNode file = currentDirectory.getFile(fileName);
        if (file != null) {
            file.appendContent(content);
            logCommittedAction(action);
            saveFileSystem();
            System.out.println("Conteúdo gravado no arquivo.");
        } else {
            System.out.println("Arquivo não encontrado.");
        }
    }

    private static void deleteFile(String name) throws IOException {
        String action = "RM " + currentDirectory.getPath() + name;
        logPendingAction(action);
        if (currentDirectory.removeFile(name)) {
            logCommittedAction(action);
            saveFileSystem();
            System.out.println("Arquivo removido com sucesso.");
        } else {
            System.out.println("Erro ao remover arquivo. Talvez não exista.");
        }
    }

    private static void deleteDirectory(String name) throws IOException {
        String action = "RMDIR " + currentDirectory.getPath() + name;
        logPendingAction(action);
        if (currentDirectory.removeSubdirectory(name)) {
            logCommittedAction(action);
            saveFileSystem();
            System.out.println("Diretório removido com sucesso.");
        } else {
            System.out.println("Erro ao remover diretório. Talvez não exista ou não esteja vazio.");
        }
    }

    private static void rename(String input) throws IOException {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("Uso: rename <atual> <novo>");
            return;
        }
        String oldName = parts[0];
        String newName = parts[1];
        String action = "RENAME " + oldName + " " + newName;

        logPendingAction(action);
        if (currentDirectory.rename(oldName, newName)) {
            logCommittedAction(action);
            saveFileSystem();
            System.out.println("Renomeado com sucesso.");
        } else {
            System.out.println("Erro ao renomear. Verifique se o item existe.");
        }
    }

    private static void showLog() {
        if (journalFile != null) {
            System.out.println(journalFile.getContent());
        } else {
            System.out.println("Log não encontrado.");
        }
    }

    private static void saveFileSystem() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILESYSTEM_FILE))) {
            oos.writeObject(root);
            oos.writeObject(filesystem);
        }
    }

    private static void loadFileSystem() throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILESYSTEM_FILE))) {
            Directory loadedRoot = (Directory) ois.readObject();
            int[] loadedFilesystem = (int[]) ois.readObject();

            System.arraycopy(loadedFilesystem, 0, filesystem, 0, loadedFilesystem.length);
            root.copyFrom(loadedRoot);
            currentDirectory = root;

            journalFile = root.getFile("journal.log");
        } catch (ClassNotFoundException e) {
            throw new IOException("Erro ao carregar o sistema de arquivos.", e);
        }
    }

    private static void logAction(String action, String status) throws IOException {
        if (journalFile != null) {
            journalFile.appendContent(status + " " + action + "\n");
        }
    }

    private static void logPendingAction(String action) throws IOException {
        logAction(action, "PENDING");
    }

    private static void logCommittedAction(String action) throws IOException {
        logAction(action, "COMMITTED");
    }

    private static void processLog() throws IOException {
        if (journalFile == null || journalFile.getContent().isEmpty()) return;

        String[] logEntries = journalFile.getContent().split("\n");
        List<String> committedActions = new ArrayList<>();
        List<String> pendingActions = new ArrayList<>();

        for (String entry : logEntries) {
            if (entry.startsWith("PENDING: ")) {
                pendingActions.add(entry.substring(9));
            } else if (entry.startsWith("COMMITTED: ")) {
                committedActions.add(entry.substring(11));
            }
        }

        for (String action : pendingActions) {
            if (!committedActions.contains(action)) {
                executeAction(action);
                committedActions.add(action);
            }
        }

        journalFile.clearContent();
        for (String committedAction : committedActions) {
            journalFile.appendContent("COMMITTED: " + committedAction + "\n");
        }
    }


    private static void executeAction(String action) throws IOException {
        String[] parts = action.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "MKDIR" -> makeDirectory(argument);
            case "TOUCH" -> createFile(argument);
            case "WRITE" -> {
                String[] writeParts = argument.split(" ", 2);
                if (writeParts.length == 2) {
                    String content = writeParts[0];
                    String fileName = writeParts[1];
                    writeFile(content + " " + fileName);
                }
            }
            case "RM" -> deleteFile(argument);
            case "RMDIR" -> deleteDirectory(argument);
            case "RENAME" -> {
                String[] renameParts = argument.split(" ", 2);
                if (renameParts.length == 2) {
                    String oldName = renameParts[0];
                    String newName = renameParts[1];
                    rename(oldName + " " + newName);
                }
            }
            default -> System.out.println("Ação não reconhecida no log: " + action);
        }
    }

    private static void showHelp() {
        System.out.println("""
                Comandos disponíveis:
                mkdir <nome>        - Cria um diretório
                cd <nome>           - Altera o diretório atual
                ls                  - Lista o conteúdo do diretório atual
                touch <nome>        - Cria um arquivo vazio
                write <arq> <texto> - Escreve no arquivo especificado
                cat <nome>          - Lê o conteúdo do arquivo
                rm <nome>           - Remove um arquivo
                rmdir <nome>        - Remove um diretório vazio
                rename <velho> <novo> - Renomeia um arquivo ou diretório
                log                 - Exibe o log de ações
                help                - Mostra esta ajuda
                exit                - Salva e encerra o sistema de arquivos
                """);
    }
}