with Jtp_Sensor;

package Jtp is

    type Tp_Type is abstract tagged null record;
    type Tp_Access is access all Tp_Type'Class;
    
    function Get_Sensor (Tp : access Tp_Type) return Jtp_Sensor.Sensor_Access is abstract;

    function Get_Tp return Tp_Access;

    procedure Set_Tp (Tp : Tp_Access);

end Jtp;
