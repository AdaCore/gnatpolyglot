package body P is

   procedure Call (C : Callback) is
      Param : T := (V => 99);
   begin
      C.all (Param);
   end Call;

end P;