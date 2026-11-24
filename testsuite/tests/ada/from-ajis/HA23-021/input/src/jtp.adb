package body Jtp is
    
    Tp : Tp_Access := null;

    function Get_Tp return Tp_Access is
    begin
        return Tp;
    end Get_Tp;

    procedure Set_Tp (Tp : Tp_Access) is
    begin
        Jtp.Tp := Tp;
    end Set_Tp;

end Jtp;
