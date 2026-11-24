generic
package G is
   procedure Add
     (V1 : Integer;
      V2 : Integer;
      Process   : not null access procedure (V3 : Integer));

   type A is access procedure (V3 : Integer);


   procedure Add2
     (V1 : Integer;
      V2 : Integer;
      Process   : A);
end G;