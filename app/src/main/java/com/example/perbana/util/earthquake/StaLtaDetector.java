package com.example.perbana.util.earthquake;

import android.util.Log;

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
    private final int requiredConsecutiveTriggers = 125; //butuh total sample 125 buah untuk valiasi gempa
    private boolean isCalibrated = false;
    private final String TAG = "StaLtaDetector";


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

    //Status kalibrasi
    public enum CalibrationState {
        NOT_CALIBRATED,
        LOADING,
        CALIBRATED
    }

    private CalibrationState calibrationState = CalibrationState.NOT_CALIBRATED;

    public CalibrationState getCalibrationState() {
        if (isCalibrated) {
            return CalibrationState.CALIBRATED;
        } else if (!ltaQueue.isEmpty()) {
            return CalibrationState.LOADING;
        } else {
            return CalibrationState.NOT_CALIBRATED;
        }
    }

    public boolean processAcceleration(double acceleration) {
        //Linear Acceleration sudah tanpa gravitasi, kita ambil nilai absol utnya
        double val = Math.abs(acceleration);

        //Filter melempar hp
//        if (!isCalibrated && val > 2.5) {
//            resetDetector();
//            return false;
//        }

        //Reset detector ketika ada hantaman instan yang ekstrem
        if (val > 14.0) {
            consecutiveTriggerCount = 0;
            return false;
        }

        // Hitung Moving Average untuk STA
        staQueue.add(val);
        staSum += val;
        if (staQueue.size() > staWindowSize) {
            staSum -= staQueue.poll();
        }

        //Sta saat ini
        double sta = staSum / staWindowSize;

        //lta saat ini
        double lta = 0.1;
        if (!ltaQueue.isEmpty()) {
            lta = ltaSum / ltaQueue.size();
        }

        if (lta < 0.1) {
            lta = 0.1;
        }

        //ratio saat ini
        double ratio = sta / lta;
        double minStaThreshold = 0.6;
        boolean isSpiking = false;

        if (ratio >= triggerThreshold && (sta >= minStaThreshold)) {
            isSpiking = true;
        }

        if (!isSpiking && val < 0.8) {
            // Hitung Moving Average untuk LTA
            ltaQueue.add(val);
            ltaSum += val;
            if (ltaQueue.size() > ltaWindowSize) {
                ltaSum -= ltaQueue.poll();
            }
        }

        //Cek status kalibrasi awal
        if (!isCalibrated && ltaQueue.size() >= ltaWindowSize) {
            isCalibrated = true;
        }

        if (!isCalibrated) {
            return false;
        }

        Log.i(TAG, "processAccelaration: Ratio: " + ratio + " | Threshold: " + triggerThreshold + " | Count: " + consecutiveTriggerCount);

        if (isSpiking) {
            consecutiveTriggerCount++;
        } else {
            if (consecutiveTriggerCount > 0) {
                consecutiveTriggerCount = Math.max(0, consecutiveTriggerCount - 3);
            }
        }

        //Validasi gempa terdeteksi
        if (consecutiveTriggerCount >= requiredConsecutiveTriggers) {
            consecutiveTriggerCount = 0;
            return true;
        }

        return false;
    }

    public void resetDetector() {
        staQueue.clear();
        ltaQueue.clear();
        staSum = 0.0;
        ltaSum = 0.0;
        consecutiveTriggerCount = 0;
        isCalibrated = true;
        calibrationState = CalibrationState.NOT_CALIBRATED;
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }
}
