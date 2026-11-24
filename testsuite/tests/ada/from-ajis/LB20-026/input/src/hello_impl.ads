package Hello_Impl is

   type T_Impl is abstract tagged private;

   procedure P (Obj : T_Impl) is null;

private

   type T_Impl is abstract tagged null record;

end Hello_Impl;
