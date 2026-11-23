generic
   type T (<>) is private;
package Gen is

   type R is private;
   
   procedure Set (This : in out R; Val : T);
   
   function Get (This : R) return T;
   
private

   type Acc is access all T;
   
   type R is record
      Val : Acc;
   end record;
   
end Gen;