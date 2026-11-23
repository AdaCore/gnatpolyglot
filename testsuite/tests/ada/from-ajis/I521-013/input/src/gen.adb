package body Gen is

   procedure Store (Rep : in out R; X : T) is
   begin
      Rep.X := X;
   end Store;

   function Get (Rep : R) return T is
   begin
      return Rep.X;
   end Get;

   procedure Gen_P is
   begin
      null;
   end Gen_P;

end Gen;