import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class FileTool {

    private static final Scanner input = new Scanner(System.in);
    // Auther: Layan Alaboudi
    public static void main(String[] args) {

        String command = "";

        // If no arguments were entered, show banner + help and exit
        if (args.length == 0) {
            showBanner();
            help();
            return;
        }
        // If arguments exist, first argument is the command
        else if (args.length != 0)
            command = args[0];

        // Process commands
        switch (command) {

            case "help":
                help();
                break;

            case "list":
                // list requires exactly 2 arguments: list "<path>"
                if (args.length == 2) {
                    list(args[1]);
                    break;
                } else {
                    System.out.println("Usage: list \"<directory>\"");
                    return;
                }

            case "copy":
                // copy requires: copy "<source>" "<destination>"
                if (args.length == 3) {
                    copy(args[1], args[2]);
                    break;
                } else {
                    System.out.println("Usage: copy \"<source>\" \"<destination>\"");
                    return;
                }

            case "move":
                // move requires: move <source> <destination>
                if (args.length == 3) {
                    move(args[1], args[2]);
                    break;
                } else {
                    System.out.println("Usage: move \"<source>\" \"<destination>\"");
                    return;
                }

            case "delete":
                // delete requires: delete <path>
                if (args.length == 2) {
                    delete(args[1]);
                    break;
                } else {
                    System.out.println("Usage: delete \"<path>\"");
                    return;
                }

            case "rename":
                // rename requires: rename <old> <new>
                if (args.length == 3) {
                    rename(args[1], args[2]);
                    break;
                } else {
                    System.out.println("Usage: rename \"<path>\" \"<newname>\"");
                    return;
                }

            default:
                // Unknown command
                System.out.println("Error: Unknown command \"" + command + "\". Type help.");
                return;

        }
        return;
    }

    // Prints the tool banner at the start
    private static void showBanner() {
        String GREEN = "\u001B[32m";
        System.out.println(GREEN + " _____  _  _          _______             _      ");
        System.out.println("|  ___|(_)| |        |__   __|           | |     ");
        System.out.println("| |_   | || |  ___      | |  ___    ___  | |");
        System.out.println("|  _|  | || | / _ \\     | | / _ \\  / _ \\ | |");
        System.out.println("| |    | || ||  __/     | || (_) || (_) || |");
        System.out.println("\\_|    |_||_| \\___|     |_| \\___/  \\___/ |_|");
        System.out.println("\n                  FILE TOOL\n");
    }

    // This method list available commands on
    // the project
     // Auther: Hanin
    public static void help() {
        String RESET = "\u001B[0m";
        System.out.println("Available Commands:");
        System.out.println("help                                 - Show this help message");
        System.out
                .println("list \"<directory_path>\"              - List all files and folders in the given directory");
        System.out
                .println("copy \"<source>\" \"<destination>\"      - Copy a file from source path to destination path");
        System.out
                .println("move \"<source>\" \"<destination>\"      - Move a file from source path to destination path");
        System.out.println("delete \"<path>\"                      - Delete the specified file or directory");
        System.out.println("rename \"<old_name>\" \"<new_name>\"     - Rename the specified file or directory" + RESET);
    }

    // Validates a path string and returns a Path object or null if invalid
     // Auther: Layan Alaboudi
    private static Path validatePath(String input) {
        try {
            return Paths.get(input);
        } catch (InvalidPathException e) {
            System.out.println("Error: invalid path.");
            return null;
        }
    }

    // This method lists the content of a given
    // directory if it exists and is valid
     // Auther: Hanin
    public static void list(String directoryPath) {

        // Convert the raw string input into a Path object.
        // validatePath() ensures the path syntax is valid.
        // If validation fails, it returns null and the method exits.
        Path dir = validatePath(directoryPath).toAbsolutePath();
        if (dir == null)
            return;

        // Check if the path exists in the file system
        if (!Files.exists(dir))
            System.out.println("ERROR: Folder doesn't exist");

        // Check if the path is actually a directory (not a regular file)
        else if (!Files.isDirectory(dir))
            System.out.println("ERROR: Given path isn't for a directory");

        else {
            // Attempt to open the directory stream to read its contents.
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {

                boolean isEmpty = true; // Track whether we find any entries

                // Iterate through items inside the directory.
                // If at least one item exists, print it.
                for (Path entry : stream) {
                    isEmpty = false;
                    System.out.println(entry.getFileName());
                }

                // If no entries were found, directory is empty
                if (isEmpty) {
                    System.out.println("ERROR: The directory is empty.");
                }

            }
            // Thrown when the user does not have permission to read the folder
            catch (AccessDeniedException e) {
                System.out.println("ERROR: Permission denied.");
            }
            // Handles other I/O failures
            catch (IOException e) {
                System.out.println("ERROR: Unable to read directory: " + e.getMessage());
            }
        }
    }

    // Copy Command
    // Auther: Lama
    public static void copy(String sourcePath, String destinationPath) {
        Path srcPath = validatePath(sourcePath);
        Path desPath = validatePath(destinationPath);

        if (srcPath == null || desPath == null)
            return;

        // Source Path Validation
        // 1- if the source path is a directory -- ask the user for the preferred action
        boolean agreeCopyAll = false;
        boolean isSrcDirectory = Files.isDirectory(srcPath);

        if (isSrcDirectory) {
            System.out.println("Are you sure you want to copy all the files in the specified directory ? (Yes/No)");
            String answer = input.next().trim().toLowerCase();
            if (answer.equals("yes") || answer.equals("y")) {
                agreeCopyAll = true;
            } else {
                System.out.println("Exit Operation.. no files copied");
                return;
            }
            // 2- if the source path does not exist
        } else if (!Files.exists(srcPath)) {
            System.out.println("ERROR: the target source path doesn't exist");
            return;
        } // end if

        // Destination Path Validation
        // 1- if the dest directory doesn't exist
        if (Files.isDirectory(desPath) && !Files.exists(desPath)) {
            System.out.println("ERROR: The destination path does not exist");
            return;
            // 2- if the destination path exist but is not a directory
        } else if (!Files.isDirectory(desPath)) {
            System.out.println("ERROR: The destination path is not a directory");
            return;
        } // end if

        try {
            if (!isSrcDirectory) {
                // Build the full destination path including the source file name
                Path finalDesPath = desPath.resolve(srcPath.getFileName());

                // Check if a file with the same name already exist in the destination directory
                boolean IsAlreadyExists = Files.exists(finalDesPath);

                if (IsAlreadyExists) {
                    if (srcPath.equals(finalDesPath)) {
                        // moving to the exact same file path
                        System.out.printf(
                                "%s already exists in the current path. \nDo you want to replace it? (Yes/No) ",
                                finalDesPath);
                    } else {
                        // if it's in different directories
                        System.out.printf(
                                "Another file exists with the same name as %s. \nAre you sure you want to replace it? (Yes/No) ",
                                finalDesPath);
                    } // end if

                    String answer = input.next().trim().toLowerCase();
                    if (answer.equals("yes") || answer.equals("y")) {
                        Files.copy(srcPath, finalDesPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("File copied successfully " + srcPath.getFileName());
                        return;
                    } else {
                        System.out.println("Exit operation.. no files copied");
                        return;
                    } // end if
                }

                // if the file doesn't exist on the destination Directory → copy normally
                else {
                    Files.copy(srcPath, finalDesPath);
                    System.out.println("File copied successfully " + srcPath.getFileName());
                } // end if
            }

            // if the user agreed on copying all files in a src dir
            if (isSrcDirectory && agreeCopyAll) {
                DirectoryStream<Path> filesInDirectory = Files.newDirectoryStream(srcPath);
                for (Path file : filesInDirectory) {
                    // Skip directories
                    if (Files.isDirectory(file)) {
                        System.out.println("Skipping directory: " + file.getFileName());
                        continue;
                    }

                    Path finalDesPath = desPath.resolve(file.getFileName());
                    if (Files.exists(finalDesPath)) {
                        if (file.equals(finalDesPath)) {
                            // moving to the exact same file path
                            System.out.printf(
                                    "%s already exists in the current path. \nDo you want to replace it? (Yes/No) ",
                                    finalDesPath);
                        } else {
                            // if it's in different directories
                            System.out.printf(
                                    "Another file exists with the same name as %s. \nAre you sure you want to replace it? (Yes/No) ",
                                    finalDesPath);
                        } // end if

                        String answer = input.next().trim().toLowerCase();

                        if (answer.equals("yes") || answer.equals("y")) {
                            Files.copy(file, finalDesPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("File copied successfully " + file.getFileName());
                        } else {
                            System.out.println("File Skipped: " + file.getFileName());
                        } // end if

                        continue;
                    }
                    // If the file does NOT exist in the destination → copy normally
                    Files.copy(file, finalDesPath);
                    System.out.println("File copied successfully " + file.getFileName());
                } // end for
            } // end if

        } catch (AccessDeniedException e) {
            System.out.println("ERROR: Permission denied.");
        } catch (IOException e) {
            System.out.println("ERROR: Unable to copy file:" + e.getMessage());
        }
    }

    // Move Command
    // Auther: Lama
    public static void move(String sourcePath, String destinationPath) {
        Path srcPath = validatePath(sourcePath);
        Path desPath = validatePath(destinationPath);

        if (srcPath == null || desPath == null)
            return;

        // Source Path Validation
        // 1- if the source path is a directory
        if (Files.isDirectory(srcPath)) {
            System.out.println("ERROR: Moving directories is not supported");
            return;
            // 2- if the source file does not exist
        } else if (!Files.exists(srcPath)) {
            System.out.println("ERROR: the target source path doesn't exist");
            return;
        } // end if

        // Destination Path Validation
        // 1- if the dest directory doesn't exist
        if (!Files.exists(desPath)) {
            System.out.println("ERROR: The destination path does not exist");
            return;
            // 2- if the destination path exists but is not a directory
        } else if (!Files.isDirectory(desPath)) {
            System.out.println("ERROR: The destination path is not a directory");
            return;
        } // end if

        try {
            // Build the full destination path including the source file name
            Path finalDesPath = desPath.resolve(srcPath.getFileName());

            // Check if a file with the same name already exists in the destination
            // directory
            boolean IsAlreadyExists = Files.exists(finalDesPath);

            if (IsAlreadyExists) {
                if (srcPath.equals(finalDesPath)) {
                    // moving to the exact same file path
                    System.out.printf(
                            "%s already exists in the current path. \nDo you want to replace it? (Yes/No) ",
                            finalDesPath);
                } else {
                    // if it's in different directories
                    System.out.printf(
                            "Another file exists with the same name as %s. \nAre you sure you want to replace it? (Yes/No) ",
                            finalDesPath);
                } // end if

                String answer = input.next().trim().toLowerCase();
                if (answer.equals("yes") || answer.equals("y")) {
                    Files.move(srcPath, finalDesPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File moved successfully " + srcPath.getFileName());
                    return;
                } else {
                    System.out.println("Exit operation.. no files moved");
                    return;
                } // end if
            }

            // if the file doesn't exist on the destination Directory → move normally
            else {
                Files.move(srcPath, finalDesPath);
                System.out.println("File moved successfully " + srcPath.getFileName());
            } // end if

        } catch (AccessDeniedException e) {
            System.out.println("ERROR: Permission denied.");
        } catch (IOException e) {
            System.out.println("ERROR: Unable to move file:" + e.getMessage());
        }
    }

    // rename method
    // Auther: Shouq
    public static void rename(String oldName, String newName) {
        oldName = oldName.trim();

        // Use validatePath to convert the string into a Path object.
        Path oldPath = validatePath(oldName);
        if (oldPath == null) {
            // validatePath will print "Error: invalid path." and stop operation.
            return;
        }

        // Check if the source file does not exist.
        if (Files.notExists(oldPath)) {
            System.out.println("Error: file not found");
            return;
        }

        // check if file not directory (only rename files)
        if (Files.isDirectory(oldPath)) {
            System.out.println("Error: cannot rename a directory");
            return;
        }

        // If the path was given a (relative path) getParent() will return null so we
        // use the current working directory.
        Path currentDir = oldPath.getParent();
        if (currentDir == null) {
            // current directory
            currentDir = Paths.get(".");
        }

        // New name File
        // Remove any extra spaces at the beginning or end of the new name
        String cleanName = newName.trim();

        if (cleanName.isEmpty()) {
            System.out.println("Error: the new name cannot be empty.");
            return;
        }

        // ensure that the new name does not contain illegal characters
        try {
            Paths.get(cleanName);
        } catch (InvalidPathException exp) {
            System.out.println("Error: invalid characters in the new file name.");
            return;
        }

        // Ensure the new name does not contain path separators (to keep rename
        // operation inside the same directory)
        if (cleanName.contains("/") || cleanName.contains("\\")) {
            System.out.println("Error: the new name must not contain path separators.");
            return;
        }

        // build new path object in the same directory with the new name
        Path newPath = Paths.get(currentDir.toString(), cleanName);

        try {
            Files.move(oldPath, newPath);
            System.out.println("File renamed successfully.");

            // when another file with new name exists already
        } catch (FileAlreadyExistsException exp) {

            System.out.println("Error: a file with the new name already exists.");

            // when user is not authorized to access and rename the file
        } catch (AccessDeniedException exp) {

            System.out.println("Error: you do not have permission to rename this file");

            // when input/output problems happens during operation.
        } catch (IOException exp) {

            System.out.println("Error: cannot rename this file");

        }

    }

    // delete method
    // Auther: Shouq
    public static void delete(String path) {
        path = path.trim();

        // make sure the user did not pass an empty string
        if (path.isEmpty()) {
            System.out.println("Error: path cannot be empty.");
            return;
        }

        Path targetPath = validatePath(path);
        if (targetPath == null) {
            // validatePath will print "Error: invalid path."
            return;
        }

        // Check if the file or directory actually exists
        if (Files.notExists(targetPath)) {
            System.out.println("Error: file or directory not found");
            return;
        }

        // Handle the directory case
        if (Files.isDirectory(targetPath)) { // If the target is a directory, first check if it is empty.
            try (DirectoryStream<Path> emptyDirectory = Files.newDirectoryStream(targetPath)) {
                if (emptyDirectory.iterator().hasNext()) {

                    System.out.println("Error: directory is not empty");
                    return;
                }

                // problem while reading the directory content.
            } catch (IOException exp) {
                System.out.println("Error: cannot read the content of the directory");
                return;
            }
        }

        // delete operation
        try {
            Files.delete(targetPath);
            System.out.println("File or directory deleted successfully.");

            // in case the directory is not empty at delete time.
        } catch (DirectoryNotEmptyException exp) {
            System.out.println("Error: directory is not empty");

            // user does not have permission to delete this file or directory
        } catch (AccessDeniedException exp) {
            System.out.println("Error: you don't have permission to delete this file / directory");

            // when input/output problems happens during operation.
        } catch (IOException exp) {
            System.out.println("Error: unable to delete file or directory");

        }

    }
}
