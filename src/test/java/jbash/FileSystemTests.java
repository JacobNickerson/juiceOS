package jbash;

import jbash.filesystem.Directory;
import jbash.filesystem.File;
import jbash.filesystem.FileSystemAPI;
import jbash.filesystem.FileSystemObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static jbash.parser.JBashParser.parseCommand;
import static org.junit.jupiter.api.Assertions.*;

public class FileSystemTests {
    FileSystemAPI FSAPI;
    Directory root;

    @BeforeEach
    public void setupFSAPI() {
        FSAPI = FileSystemAPI.getInstance();
        FSAPI.reset();
        root = FSAPI.getRoot();
    }

    @Test
    void testMakeFileNoName() {
        Assertions.assertFalse(FSAPI.createFile("", root), "File created with no name");
    }

    @Test
    void testMakeFileDuplicateFile() {
        FSAPI.createFile("test", root);
        Assertions.assertFalse(FSAPI.createFile("test", root), "File created with duplicate name");
    }

    @Test
    void testMakeFile() {
        Assertions.assertTrue(FSAPI.createFile("test", root), "File not created");
    }

    @Test
    void testMakeDirectory() {
        Assertions.assertTrue(FSAPI.createDirectory("testFolder", root), "Directory not created");
    }

    @Test
    void testMakeDirectoryWithPathAndName() {
       Assertions.assertTrue(FSAPI.createDirectory("testFolder", "/"), "Directory not created in root");
       Assertions.assertTrue(FSAPI.createDirectory("testFolder", "/testFolder"), "Directory not created inside testFolder");
    }

    @Test
    void testMakeDirectoryWithPath() {
        Assertions.assertTrue(FSAPI.createDirectory("/testFolder"));
        Optional<FileSystemObject> testDirectory = FSAPI.getFileSystemObject("/testFolder");
        Assertions.assertTrue(testDirectory.isPresent(), "Test folder in root not found.");
        Assertions.assertEquals(testDirectory.get().getPath(), "/testFolder", "Test folder path incorrect");

        Assertions.assertTrue(FSAPI.createDirectory("/testFolder/testFolder2/"));
        Optional<FileSystemObject> testDirectory2 = FSAPI.getFileSystemObject("/testFolder/testFolder2");
        Assertions.assertTrue(testDirectory2.isPresent(), "Test folder in test folder 1 not found.");
        Assertions.assertEquals(testDirectory2.get().getPath(), "/testFolder/testFolder2", "Test folder 2 path incorrect");
    }

    @Test
    void testMakeFileWithPath() {
        Assertions.assertTrue(FSAPI.createFile("testFile", "/"), "File not created in root");
        Assertions.assertTrue(FSAPI.createDirectory("testFolder", "/"), "Directory not created in root");
        Assertions.assertTrue(FSAPI.createFile("testFile2", "/"), "File not created in testFolder");
    }

    @Test
    void testFindChildFile() {
        FSAPI.createFile("test", root);
        Optional<FileSystemObject> child = root.findChild("test");
        Assertions.assertTrue(child.isPresent(), "Child not found");
        Assertions.assertInstanceOf(File.class, child.get(), "Child is not a File");
    }

    @Test
    void testGetPath() {
        Assertions.assertEquals(root.getPath(), "/", "Root file path is not  '/'");
        FSAPI.createFile("test", root);
        Optional<FileSystemObject> child = root.findChild("test");
        Assertions.assertTrue(child.isPresent(), "Child not found");
        Assertions.assertEquals(child.get().getPath(), "/test", "Child file path is not '/test'");
    }

    @Test
    void testMakeFileContents() {
        FSAPI.createFile("test", root, "Nice contents!");
        Optional<FileSystemObject> child = root.findChild("test");
        Assertions.assertTrue(child.isPresent(), "File not found.");
        File file = (File) child.get();
        Assertions.assertEquals(file.getContents(), "Nice contents!", "File contents not equal");
    }

    @Test
    void testFindChildDirectory() {
        FSAPI.createDirectory("test", root);
        Optional<FileSystemObject> child = root.findChild("test");
        Assertions.assertTrue(child.isPresent(), "Child not found");
        Assertions.assertInstanceOf(Directory.class, child.get(), "Child is not a Directory");
    }

    @Test
    void testMakeFileInDirectory() {
        FSAPI.createDirectory("test", root);
        Optional<FileSystemObject> testDirectory = root.findChild("test");
        Assertions.assertTrue(testDirectory.isPresent(), "Test directory not found");
        Assertions.assertInstanceOf(Directory.class, testDirectory.get(), "Test directory is not a directory");
        Directory directory = (Directory) testDirectory.get();
        FSAPI.createFile("testFile", directory);
        Optional<FileSystemObject> child2 = directory.findChild("testFile");
        Assertions.assertTrue(child2.isPresent(), "Child not found");
        Assertions.assertInstanceOf(File.class, child2.get(), "Child is not a File");
    }

    @Test
    void testMoveFile() {
       FSAPI.createDirectory("testDirectory", root);
        Optional<FileSystemObject> testDirectoryOptional = root.findChild("testDirectory");
        Assertions.assertTrue(testDirectoryOptional.isPresent(), "Test directory not found");
        Directory testDirectory = (Directory) testDirectoryOptional.get();
        Assertions.assertInstanceOf(Directory.class, testDirectory, "Test directory is not a directory");
        FSAPI.createFile("testFile", root);
        Optional<FileSystemObject> testFile = root.findChild("testFile");
        FSAPI.moveFSO(testFile.get().getPath(), testDirectory.getPath());
        Assertions.assertEquals(testFile.get().getPath(), "/testDirectory/testFile", "Path incorrect");
        Assertions.assertEquals(testFile.get().getParent(), testDirectory, "Test file does not belong to testDirectory");
        Optional<FileSystemObject> testFileFound = testDirectory.findChild("testFile");
        Assertions.assertTrue(testFileFound.isPresent(), "Test file not found in new directory children");
        Optional<FileSystemObject> testFileFound2 = root.findChild("testFile");
        Assertions.assertFalse(testFileFound2.isPresent(), "Test file is still found in root directory");
    }

    @Test
    void testWriteContentsToFile() {
        FSAPI.createFile("testFile", root, "Hello world!");
        Optional<FileSystemObject> testFileFound = root.findChild("testFile");
        Assertions.assertTrue(testFileFound.isPresent() && testFileFound.get() instanceof File, "File not found");
        File testFile = (File) testFileFound.get();
        Assertions.assertEquals(testFile.getContents(), "Hello world!", "Contents not set correctly");
        testFile.setContents("New contents!");
        Assertions.assertEquals(testFile.getContents(), "New contents!", "Contents not changed");
    }

    @Test
    void testGetFileSystemObjectWithPath() {
        FSAPI.createFile("testFile", root, "Hello world!");
        FSAPI.createDirectory("testFolder", root);

        Optional<FileSystemObject> testFileOptional = FSAPI.getFileSystemObject("/testFile");
        Assertions.assertTrue(testFileOptional.isPresent(), "Test file not found");
        Assertions.assertInstanceOf(File.class, testFileOptional.get(), "Test file is not a file");

        Optional<FileSystemObject> testDirectoryOptional = FSAPI.getFileSystemObject("/testFolder");
        Assertions.assertTrue(testDirectoryOptional.isPresent(), "Test folder not found");
        Assertions.assertInstanceOf(Directory.class, testDirectoryOptional.get(), "Test folder is not a directory");

        FSAPI.moveFSO("/testFile", "/testFolder");
        Optional<FileSystemObject> testFileOptional2 = FSAPI.getFileSystemObject("/testFolder/testFile");
        Assertions.assertTrue(testFileOptional2.isPresent(), "Test file not found after move");
        Assertions.assertInstanceOf(File.class, testFileOptional.get(), "Test file is not a file after move");
    }

    @Test
    void testGetFileSystemObjectPathParsing() {
        FSAPI.createDirectory("testFolder", root);
        Optional<FileSystemObject> testFolderOptional = FSAPI.getFileSystemObject("/testFolder");
        Assertions.assertTrue(testFolderOptional.isPresent() && testFolderOptional.get() instanceof Directory, "Test directory not found");
        Directory testFolder = (Directory) testFolderOptional.get();
        FSAPI.createDirectory("testFolder2", testFolder);
        FSAPI.createDirectory("test Folder 3", testFolder);
        FSAPI.createFile("testFile", testFolder);
        Optional<FileSystemObject> testFolder3Optional = FSAPI.getFileSystemObject("/testFolder/test Folder 3");
        Assertions.assertTrue(testFolder3Optional.isPresent() && testFolder3Optional.get() instanceof Directory, "Test directory 3 not found");
        Directory testFolder3 = (Directory) testFolder3Optional.get();
        FSAPI.createFile("testFile2", testFolder3);

        Optional<FileSystemObject> emptyPath = FSAPI.getFileSystemObject("");
        Assertions.assertFalse(emptyPath.isPresent(), "FSO found with empty path");

        Optional<FileSystemObject> rootOptional = FSAPI.getFileSystemObject("/");
        Assertions.assertTrue(rootOptional.isPresent(), "Root not found by path");

        Optional<FileSystemObject> rootOptional2 = FSAPI.getFileSystemObject("///////////////////////////");
        Assertions.assertTrue(rootOptional2.isPresent(), "Root not found by multi slash path");

        Optional<FileSystemObject> rootOptional3 = FSAPI.getFileSystemObject("./");
        Assertions.assertTrue(rootOptional3.isPresent(), "Root not found by current directory");

        Optional<FileSystemObject> testFileOptional = FSAPI.getFileSystemObject("/testFolder/testFile");
        Assertions.assertTrue(testFileOptional.isPresent(), "Test file not found with absolute path");

        Optional<FileSystemObject> testFileOptional2 = FSAPI.getFileSystemObject("./testFolder/testFile");
        Assertions.assertTrue(testFileOptional2.isPresent(), "Test file not found with path starting at current directory");

        Optional<FileSystemObject> testFileOptional3 = FSAPI.getFileSystemObject("//testFolder//testFile");
        Assertions.assertTrue(testFileOptional3.isPresent(), "Test file not found with path containing double slashes");

        Optional<FileSystemObject> testFileOptional4 = FSAPI.getFileSystemObject("/testFolder/../testFolder/testFile");
        Assertions.assertTrue(testFileOptional4.isPresent(), "Test file not found with path traveling back with ..");

        Optional<FileSystemObject> testFileOptional5 = FSAPI.getFileSystemObject("/testFolder/testFolder2/../testFile");
        Assertions.assertTrue(testFileOptional5.isPresent(), "Test file not found traveling forward two directories then backwards one using ..");

        Optional<FileSystemObject> testFileOptional6 = FSAPI.getFileSystemObject("./../../testFolder/testFolder2/..//.//.//.//.//testFile");
        Assertions.assertTrue(testFileOptional6.isPresent(), "Test file not found doing some nonsense");

        Optional<FileSystemObject> testFileOptional7 = FSAPI.getFileSystemObject("/testFolder/test Folder 3/testFile2");
        Assertions.assertTrue(testFileOptional7.isPresent(), "Test file not found with path containing spaces");

        Optional<FileSystemObject> testFileOptional8 = FSAPI.getFileSystemObject("/testFolder/testFile/");
        Assertions.assertFalse(testFileOptional8.isPresent(), "Test file found with path ending in slash");

        Optional<FileSystemObject> testDirectoryOptional = FSAPI.getFileSystemObject("/testFolder");
        Assertions.assertFalse(testDirectoryOptional.isPresent(), "Test directory not found with path not ending in a slash");

        Optional<FileSystemObject> testDirectoryOptional2 = FSAPI.getFileSystemObject("/testFolder");
        Assertions.assertFalse(testDirectoryOptional2.isPresent(), "Test directory not found with path ending in a slash");
    }

    @Test
    void testGetFileSystemObjectQuotes() {
        FSAPI.createDirectory("testFolder", root);
        FSAPI.createDirectory("test Folder 2", root);
        Assertions.assertTrue(FSAPI.createFile("testFile", "/testFolder"), "testFile not created");
        Assertions.assertTrue(FSAPI.createFile("testFile2", "/test Folder 2"), "testFile2 not created");

        Optional<FileSystemObject> testFileOptional = FSAPI.getFileSystemObject("/testFolder/\"testFile\"");
        Assertions.assertTrue(testFileOptional.isPresent(), "Test file not found with surround quotations and no spaces");

        Optional<FileSystemObject> testFileOptional2 = FSAPI.getFileSystemObject("/testFolder/\"test\"File");
        Assertions.assertTrue(testFileOptional2.isPresent(), "Test file not found with mid string quotations and no spaces");

        Optional<FileSystemObject> testFileOptional3 = FSAPI.getFileSystemObject("/\"test Folder 2\"/testFile");
        Assertions.assertTrue(testFileOptional3.isPresent(), "Test file not found with surrounding quotations and spaces");

        Optional<FileSystemObject> testFileOptional4 = FSAPI.getFileSystemObject("/\"test\" Folder 2/testFile");
        Assertions.assertTrue(testFileOptional4.isPresent(), "Test file not found with mid string quotations and spaces");
    }

    @Test
    void testMoveCurrentDirectory() {
        FSAPI.createDirectory("testFolder", "/");
        FSAPI.createDirectory("testFolder2", "/testFolder");
        FSAPI.createDirectory("testFolder3", "/testFolder/testFolder2");
        Assertions.assertEquals(root, FSAPI.getFileSystemObject("/").get(), "Root does not equal root when found with path");
        Assertions.assertEquals(root, FSAPI.getFileSystemObject("./").get(), "Root does not equal current directory before moving");

        Optional<FileSystemObject> testDirectoryOptional = FSAPI.getFileSystemObject("/testFolder/");
        Assertions.assertTrue(testDirectoryOptional.isPresent() && testDirectoryOptional.get() instanceof Directory, "testFolder not found");
        Directory testDirectory = (Directory) testDirectoryOptional.get();

        Assertions.assertTrue(FSAPI.moveCurrentDirectory(""), "Failed to move to home directory");
        Assertions.assertFalse(FSAPI.moveCurrentDirectory("sdahfk"), "Moved to a non-existing folder");
        Assertions.assertTrue(FSAPI.moveCurrentDirectory("/testFolder"), "Failed to move to testFolder");
        Assertions.assertEquals(testDirectory, FSAPI.getFileSystemObject("./").get(), "Current directory not equal to testFolder after moving to testFolder");
    }
}
