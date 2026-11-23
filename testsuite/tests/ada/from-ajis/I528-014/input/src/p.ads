
package P is

   procedure A (V : Integer);

   function A (V : Integer) return Integer;

   type R is record
      null;
   end record;

   type A_R is access all R;

   procedure B (V : R);

   procedure B (V : A_R);

   type T is tagged null record;

   procedure C (V : T);

   type T_Child is new T with null record;

   procedure C (V : T_Child);

   procedure D (V : Integer);

   type D_Type is new Integer;

   procedure D (V : D_Type);


end P;
