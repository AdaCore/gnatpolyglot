import com.adacore.libtest.jtp_sensor.SensorType;
import com.adacore.libtest.jtp_sensor.JtpSensorPackage;

public class Main {
    static class MySensor extends SensorType {
        public float getSlantRange() { return 1.2f; }
    }

    public static void main(String[] args) {
        MySensor s = new MySensor();
        System.out.println(JtpSensorPackage.nonAbstract(s));
        System.out.println(s.getSlantRange());
    }
}
