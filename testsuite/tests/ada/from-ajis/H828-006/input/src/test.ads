package Test is

   type Root_Type is tagged null record;

   generic
   package X is
      type T is null record;
   end X;

   package I is new X;

   type Some_Type is record
      A : I.T;
   end record;

   function Primitive
     (V : access Root_Type) return Some_Type;

end Test;
