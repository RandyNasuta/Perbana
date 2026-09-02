package com.example.perbana.util.earthquake;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Membedakan guncangan atau getaran antara noise gerakan biasa dengan gunccangan tajam (gelombang gempa)
 * STA mengukur rata-rata guncangan dalam waktu singkat
 * LTA mengukur rata-rata guncangan dalam waktu panjang
 * Jika STA/LTA melonjak melewati ambang batas tertentu, maka akan dianggap sebagai gempa
 */
public class StaLtaDetector {
    private final int staWindowSize;
    private final int ltaWindowSize;
    private final double triggerThreshold;
    private int consecutiveTriggerCount = 0;
    private final int requiredConsecutiveTriggers = 60;
    private boolean isCalibrated = false;


    private final Queue<Double> staQueue = new LinkedList<>();
    private final Queue<Double> ltaQueue = new LinkedList<>();

    private double staSum = 0.0;
    private double ltaSum = 0.0;

    /**
     *
     * @param staWindowSize jumlah sample untuk short-time average (misal 50 sample untuk 1 detik)
     * @param ltaWindowSize jumlah sample untuk long-time average (misal 500 sample untuk 10 detik)
     * @param triggerThreshold rasio lonjakan (misal 3.5 atau 4.0) untuk memicu deteksi
     */
    public StaLtaDetector(int staWindowSize, int ltaWindowSize, double triggerThreshold) {
        this.staWindowSize = staWindowSize;
        this.ltaWindowSize = ltaWindowSize;
        this.triggerThreshold = triggerThreshold;
    }

    public boolean processAccelaration(double acceleration) {
        //Linear Acceleration sudah tanpa gravitasi, kita ambil nilai absolutnya
        double val = Math.abs(acceleration);

        // Hitung Moving Average untuk STA
        staQueue.add(val);
        staSum += val;
        if (staQueue.size() > staWindowSize) {
            staSum -= staQueue.poll();
        }

        // Hitung Moving Average untuk LTA
        ltaQueue.add(val);
        ltaSum += val;
        if (ltaQueue.size() > ltaWindowSize) {
            ltaSum -= ltaQueue.poll();
        }

        if (!isCalibrated && ltaQueue.size() >= ltaWindowSize) {
            isCalibrated = true;
        }

        if (!isCalibrated) {
            return false;
        }

        double sta = staSum / staWindowSize;
        double lta = ltaSum / ltaWindowSize;

        if (lta < 0.01) {
            lta = 0.01;
        }

        double ratio = sta / lta;

        if (ratio >= triggerThreshold) {
            consecutiveTriggerCount++;
        } else {
            consecutiveTriggerCount = Math.max(0, consecutiveTriggerCount-  1);
        }

        if (consecutiveTriggerCount >= requiredConsecutiveTriggers) {
            consecutiveTriggerCount = 0;
            return true;
        }

        return false;
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }
}
