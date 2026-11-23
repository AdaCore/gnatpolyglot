package body Test is

   function F (V1 : T; V2 : T) return T is
   begin
      return (V => V1.V + V2.V);
   end F;

   type T_Child is new T with record
      null;
   end record;

   function F (V1 : T_Child; V2 : T_Child) return T_Child;

   function F (V1 : T_Child; V2 : T_Child) return T_Child is
   begin
      return (V => (V1.V + V2.V) * 10);
   end F;

   function Get_T_Child (X : Integer) return T'Class is
      V : T_Child := (V => X);
   begin
      return V;
   end Get_T_Child;

end Test;