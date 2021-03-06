package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Proj62 {
    static double items[][];
    static int capacity;   // capacity of the knapsack
    static int nodeCount = 1;
    static ArrayList<Node> availableNodes = new ArrayList<>();

    public static void main(String[] args) {
        fillList();

        System.out.println("Capacity of knapsack is " + capacity +
                "\nItems are:");
        for (int i = 0; i < items[2].length; i++) {
            System.out.println((i + 1) + ": " + (int) items[0][i] + " " + (int)items[1][i] +
                    " " + items[2][i]);
        }
        GenerateRootNode();

    }

    public static void BranchAndBound(Node parent) {

        if (parent.level == (items[2].length) && parent == FindBest()) {
            PrintNodes(parent, "Winner");
        }
        else if (parent.weight == capacity && parent == FindBest()){
            PrintNodes(parent, "Winner");
        }
        else {
            PrintNodes(parent, "Exploring");
            GenerateChildren(parent);
            BranchAndBound(FindBest());
        }
    }


    public static void GenerateRootNode() {
        if (availableNodes.size() == 0) {
            List<Integer> empty = new ArrayList<>();
            Node node = new Node();
            node.nodeNum = nodeCount;
            node.level = 0;
            node.items = empty;         // Initializing so we can compare in GetBound
            node.cantUse = new ArrayList<>();
            node = GetBound(node, false);
            availableNodes.add(node);
            BranchAndBound(node);
        } else {
            System.out.println("Tree not empty");
        }

    }


    public static void GenerateChildren(Node parent) {

        // Left Child
        Node node = new Node();

        node.items = new ArrayList<Integer>(parent.items);
        node.level = parent.level + 1;
        node.profit = parent.profit;
        node.weight = parent.weight;
        nodeCount++;
        node.nodeNum = nodeCount;
        node.cantUse = new ArrayList<Integer>(parent.cantUse);
        node.cantUse.add(node.level);
        node = GetBound(node, true);
        availableNodes.add(node);
        PrintNodes(node, "    Left");

        // Right Child
        Node nodeR = new Node();

        nodeR.items = new ArrayList<Integer>(parent.items);
        nodeR.level = parent.level + 1;
        nodeR.profit = parent.profit;
        nodeR.weight = parent.weight;
        nodeR.cantUse = new ArrayList<Integer>(parent.cantUse);

        nodeCount++;
        nodeR.nodeNum = nodeCount;
        nodeR.items.add(node.level);
        nodeR.profit += items[0][nodeR.level - 1];
        nodeR.weight += items[1][nodeR.level - 1];
        if (nodeR.weight > capacity){
            nodeR.profit = -1;
            nodeR.bound = -1;
            PrintNodes(nodeR, "    Right");
            System.out.println("Pruned because to heavy");
            availableNodes.remove(nodeR);
        }
        else {
            nodeR = GetBound(nodeR, false);
            availableNodes.add(nodeR);
            PrintNodes(nodeR, "    Right");
        }
        availableNodes.remove(parent);
        System.out.println("");


        }



    public static Node GetBound(Node node, Boolean left) {
        int i = 0;
        int PLoad = node.weight;      // Plausible load
        int load = node.weight;
        int cantUse = -1;

        while (PLoad <= capacity && i < items[2].length) {

            for (int k = 0; k < node.items.size(); k++ ) {
                if ((i + 1) == node.items.get(k)) {
                    cantUse = node.items.get(k);
                    //TODO im i the one?????????
                    node.bound += items[0][cantUse - 1];
                }
            }
            for (int k = 0; k < node.cantUse.size(); k++ ) {
                    if ((i + 1) == node.cantUse.get(k)) {
                        cantUse = node.cantUse.get(k);
                        break;
                    }
                }
            if ((i + 1) != cantUse) {
                 PLoad += items[1][i];
                // Add profit to the bound if its weight still under cap
                if (PLoad <= capacity) {
                    load += items[1][i];
                    node.bound += items[0][i];
                }
                if (PLoad >= capacity) {
                    break;
                }
                else if ((i + 1) >= items[2].length){
                    i++;
                    break;
                }
            }
            i++;
        }
        if (load != capacity && (i + 1) <= items[2].length) {
            int remainingLoad = capacity - load;
            node.bound += remainingLoad * items[2][i];
        }
        return node;
    }

    public static Node FindBest() {
        Node best = null;
        for (int i = 0; i < availableNodes.size(); i++) {
            if (best == null) {
                best = availableNodes.get(i);
            }
            else if (best.bound < availableNodes.get(i).bound) {
                best = availableNodes.get(i);
            }
        }
        return best;

    }


    public static void fillList() {
        System.out.print("Please enter the name of the file you would like processed: ");
        Scanner keyboard = new Scanner(System.in);
        String fileName = keyboard.nextLine();
        int numItems;
        double profit;
        double weight;
        double profPerKilogram;    // It's just no unit weight but kilo for fun.


        try {
            File file =
                    new File(fileName);

            Scanner fileScanner =
                    new Scanner(file);
            capacity = fileScanner.nextInt();
            numItems = fileScanner.nextInt();
            items = new double[3][numItems];
            int j = 0;
            while (fileScanner.hasNext()) {
                profit = fileScanner.nextInt();
                weight = fileScanner.nextInt();
                profPerKilogram = profit / weight;
                items[0][j] = profit;
                items[1][j] = weight;
                items[2][j] = profPerKilogram;
                j++;
            }
            fileScanner.close();

        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
    }

    public static void PrintNodes(Node node, String who){
        String itemString = "[";
        if (node.items.size() != 0) {
            itemString += node.items.get(0);
        }
        for (int i = 1; i < node.items.size(); i++) {
            itemString += ", " +node.items.get(i);
        }
        itemString += "]";

        String noUse = "[";
        if (node.cantUse.size() != 0) {
            noUse += node.cantUse.get(0);
        }

        for (int i = 1; i < node.cantUse.size(); i++) {
            noUse += ", " + node.cantUse.get(i);
        }
        noUse += "]";
        System.out.println(who + " <Node: " + node.nodeNum +
                ":   items: " + itemString +
                " level: " + node.level +
                " profit: " + node.profit +
                " weight: " + node.weight +
                " bound: " + node.bound + " >" + " cantUse: " + noUse);
    }
}


