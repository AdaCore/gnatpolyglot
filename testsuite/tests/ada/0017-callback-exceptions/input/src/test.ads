package Test is

    type Callback is access procedure;

    procedure Call (C : Callback);

    function Get_Callback return Callback;

end Test;
