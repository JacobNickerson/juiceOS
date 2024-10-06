package jbash;

import jbash.environment.JKernel;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        JKernel kernel = JKernel.getInstance();
        kernel.exec("jbash", List.of());
    }
}