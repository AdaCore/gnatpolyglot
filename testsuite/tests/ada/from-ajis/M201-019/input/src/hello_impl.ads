package Hello_Impl is

   type Parent is tagged null record;

   procedure P (Obj : Parent; F : access procedure) is null;

end Hello_Impl;
