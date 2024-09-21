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
        FSAPI = new FileSystemAPI();
        root = FSAPI.getRoot();
    }

    @Test
    void testMakeFileNoName() {
        assertFalse(FSAPI.createFile("", root), "File created with no name");
    }

    @Test
    void testMakeFileDuplicateFile() {
        FSAPI.createFile("test", root);
        assertFalse(FSAPI.createFile("test", root), "File created with duplicate name");
    }

    @Test
    void testMakeFile() {
        Assertions.assertTrue(FSAPI.createFile("test", root), "File not created");
    }

    @Test
    void testFindChildFile() {
        FSAPI.createFile("test", root);
        Optional<FileSystemObject> child = root.findChild("test");
        Assertions.assertTrue(child.isPresent(), "Child not found");
        assertInstanceOf(File.class, child.get(), "Child is not a File");
    }

    @Test
    void testGetPath() {
        Assertions.assertEquals(root.getPath(), "/", "Root file path does not equal '/'");
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

    @Nested
    class testFileSystemObjects {
    }
}
