package com.example.perbana.util.earthquake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Mendeteksi guncangan atau getaran menggunakan sensor perangkat
 * Menggunakan TYPE_LINEAR_ACCELERATION untuk mengukur percepatan murni tanpa pengaruh gravitasi bumi
 */
public class EarthquakeSensorDetector implements SensorEventListener {
    private static final String TAG = "EarthquakeSensorDetector";
    private final SensorManager sensorManager;
    private final Sensor linearAccelerometer;
    private OnVibrationDetectedListener listener;

    public interface OnVibrationDetectedListener {
        /**
         * Digunakan secara konstan setiap kali sensor mendeteksi perubahan nilai percepatan
         * @param acceleration Magnitudo percepatan total dalam m/s^2 (akar kuadrat dari x, y, z)
         * @param x Percepatan pada sumbu X (kiri/kanan layar)
         * @param y Percepatan pada sumbu Y (atas/bawah layar)
         * @param z Percepatan pada sumbu Z (depan/belakang layar)
         */
        void onVibrationDetected(double acceleration ,float x, float y, float z);
    }

    /**
     * Konstruktor untuk inisialisasi SensorManager dan mendaftarkan perangkat keras sensor
     * @param context Konteks aplikasi atau activity yang memanggil kelas ini
     */
    public EarthquakeSensorDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        } else {
            linearAccelerometer = null;
        }
    }

    /**
     * Memulai pemantauan sensor
     *
     * @param listener Callback untuk menerima pembaruan data sensor secara real-time.
     */
    public void startListening(OnVibrationDetectedListener listener) {
        this.listener = listener;
        if (sensorManager != null && linearAccelerometer != null) {
            // SENSOR_DELAY_GAME (~20ms) memberikan tingkatan pembaruan yang cukup cepat
            // untuk menangkap gelombang frekuensi gempa tenda terlalu membebani CPU (baterai)
            sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.e(TAG, "Sensor LINEAR_ACCELERATION tidak tersedia diperangkat ini");
        }
    }

    /**
     * Menghentikan pemantauan sensor dalam mencegah memory leaks dan menghemat daya
     * Wajib dipanggil ketika aplikasi tidak digunakan lagi.
     */
    public void stopListening() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            // Nilai percepatan pada masing-masing sumbu 3D dalam satuan m/s^2
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Menghitung Magnitudo Vektor (Euclidean Norm) dari percepatan total 3 sumbu
            // Rumus fisika: a = sqrt(x^2 + y^2 + z^2)
            double acceleration = Math.sqrt(x * x + y * y + z * z);

            // Meneruskan data yang sudah di-filter dan dikalkulasi ke pemanggil (Activity/Service)
            if (listener != null) {
                listener.onVibrationDetected(acceleration, x, y, z);
            }
        }
    }
}
