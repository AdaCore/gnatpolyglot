package body Test is

   X : A_T;

   procedure Set (B : Boolean; V : A_T) is
   begin
      X := V;
   end Set;

   function Get_Reference return A_T is
   begin
      return X;
   end Get_Reference;

   function Get_Copy return T'Class is
   begin
      return X.all;
   end Get_Copy;

end Test;