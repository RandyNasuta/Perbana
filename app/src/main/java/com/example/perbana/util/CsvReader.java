package com.example.perbana.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CsvReader {
    private final InputStream inputStream;

    public CsvReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ArrayList<String[]> read() {
        ArrayList<String[]> resultList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                resultList.add(row);
            }
        } catch(IOException iO){
            throw new RuntimeException("Kesalahan IOException ketika membaca data: " + iO.getMessage());
        } catch(Exception e) {
            throw new RuntimeException("Kesalahan ketika membaca data: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException iO) {
                throw new RuntimeException("Kesalahan IOException ketika menutup stream: " + iO.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Kesalahan ketika menutup stream: " + e.getMessage());
            }
        }
        return resultList;
    }
}
