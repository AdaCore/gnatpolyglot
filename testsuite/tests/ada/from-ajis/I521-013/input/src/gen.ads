with Gen2;

generic
   type T is private;
package Gen is

   type R is record
      X : T;
   end record;

   procedure Store (Rep : in out R; X : T);

   function Get (Rep : R) return T;

   procedure Gen_P;

   package Gen_Inst is new Gen2 (Gen_P, T);

end Gen;