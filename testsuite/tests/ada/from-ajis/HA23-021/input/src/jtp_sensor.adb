package body Jtp_Sensor is

   function Non_Abstract (Sensor : access Sensor_Type) return Integer is
   begin
      return 666;
   end Non_Abstract;

end Jtp_Sensor;