package ui.cui;

import algorithm.BubbleSort;
import algorithm.SortAlgorithm;

public class CliApp {
    public static void main(String[] args) {
        SortAlgorithm bs = new BubbleSort();
        int[] array = {5, 3, 8, 4, 2};
        
        System.out.println("Before sorting:");
        printArray(array);
        
        bs.sort(array);
        
        System.out.println("After sorting using " + bs.getName() + ":");
        printArray(array);
    }
    
    private static void printArray(int[] array) {
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}