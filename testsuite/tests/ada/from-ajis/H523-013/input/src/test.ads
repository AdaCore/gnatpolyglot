package Test is

   --  This test checks that the binding works fine if there are multiple
   --  dispatching parameters.

   type T is tagged record
      V : Integer;
   end record;

   function F (V1 : T; V2 : T) return T;

   function Get_T_Child (X : Integer) return T'Class;

end Test;