package Jtp_Sensor is

    type Sensor_Type is abstract tagged null record;
    type Sensor_Access is access all Sensor_Type'Class;

    function Get_Slant_Range (Sensor : access Sensor_Type)
                              return Float is abstract;

   function Non_Abstract (Sensor : access Sensor_Type) return Integer;

end Jtp_Sensor;
