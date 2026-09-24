package Test is

    type Callback is access function (I: Integer) return Integer;
    subtype ST is Callback;

    function Call (C : ST) return Integer;
    function Get_Callback return ST;

end Test;
