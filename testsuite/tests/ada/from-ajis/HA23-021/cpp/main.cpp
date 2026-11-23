#include "jtp.h"
#include "jtp_sensor.h"
#include <iostream>

// Non_Abstract and Get_Slant_Range both take `access Sensor_Type` — not yet bound.
// Sensor_Access / Tp_Access (access to classwide) also not bound.

class MySensor : public jtp_sensor::SensorType {
public:
    MySensor() : jtp_sensor::SensorType(this) {}
    float get_slant_range() const override { return 1.2f; }
};

int main() {
    MySensor s;
    std::cout << jtp_sensor::non_abstract(s) << "\n";
    std::cout << s.get_slant_range() << "\n";
    return 0;
}
